package madson.org.opentournament.online;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Typeface;

import android.os.Bundle;
import android.os.Parcelable;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.tasks.LoadTournamentTeamTask;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * Dialog for adding new tournament_uuid players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RegisterTournamentPlayerDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_PLAYER = "player";
    public static final String BUNDLE_TOURNAMENT_PLAYER = "tournament_player";

    private EditText firstnameEditText;
    private EditText nicknameEditText;
    private EditText lastnameEditText;
    private Spinner factionSpinner;
    private Spinner teamnameSpinner;
    private ImageButton addNewTeamnameButton;

    private Tournament tournament;
    private Player player;

    private ArrayAdapter<String> team_adapter;
    private TextView labelTeamname;
    private TextInputLayout lastnameParent;
    private TextInputLayout firstnameParent;
    private TextInputLayout nicknameParent;
    private TextView labelTeammembers;
    private Map<String, Integer> teamnameMap;

    private BaseActivity baseActivity;
    private TournamentPlayer tournamentPlayer;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
            player = bundle.getParcelable(BUNDLE_PLAYER);
            tournamentPlayer = bundle.getParcelable(BUNDLE_TOURNAMENT_PLAYER);

            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_add_tournament_player, null);

            firstnameEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_firstname);
            nicknameEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_nickname);
            lastnameEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_lastname);

            firstnameParent = (TextInputLayout) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_firstname_parent);
            nicknameParent = (TextInputLayout) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_nickname_parent);
            lastnameParent = (TextInputLayout) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_lastname_parent);

            factionSpinner = (Spinner) dialogView.findViewById(R.id.dialog_add_tournament_player_faction_spinner);

            labelTeamname = (TextView) dialogView.findViewById(R.id.label_teamname);
            teamnameSpinner = (Spinner) dialogView.findViewById(R.id.teamname_spinner);
            labelTeammembers = (TextView) dialogView.findViewById(R.id.team_members);

            addNewTeamnameButton = (ImageButton) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_add_new_team);

            ArrayAdapter<CharSequence> faction_adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.factions, android.R.layout.simple_spinner_item);
            faction_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            factionSpinner.setAdapter(faction_adapter);

            team_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    new ArrayList<String>());
            team_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference(FirebaseReferences.TOURNAMENT_REGISTRATIONS + "/" + tournament.getOnlineUUID());

            teamnameMap = new HashMap<>();

            reference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                            TournamentPlayer player = playerSnapshot.getValue(TournamentPlayer.class);

                            if (player != null) {
                                String teamname = player.getTeamname();

                                if (teamname != null && !teamname.isEmpty()) {
                                    if (!teamnameMap.containsKey(teamname)) {
                                        teamnameMap.put(teamname, 1);
                                    } else {
                                        teamnameMap.put(teamname, teamnameMap.get(teamname) + 1);
                                    }
                                }
                            }
                        }

                        if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                            team_adapter.add(baseActivity.getString(R.string.no_team));

                            for (String key : teamnameMap.keySet()) {
                                team_adapter.add(key);
                            }
                        } else {
                            for (String key : teamnameMap.keySet()) {
                                if (teamnameMap.get(key) < tournament.getTeamSize()) {
                                    team_adapter.add(key);
                                }
                            }
                        }

                        if (team_adapter.isEmpty()) {
                            teamnameSpinner.setVisibility(View.GONE);
                        } else {
                            teamnameSpinner.setVisibility(View.VISIBLE);
                        }

                        team_adapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            teamnameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String teamname = (String) parent.getItemAtPosition(position);
                        int teammembers;

                        if (teamnameMap.get(teamname) == null) {
                            teammembers = 0;
                        } else {
                            teammembers = teamnameMap.get(teamname);
                        }

                        if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                            labelTeammembers.setText("(" + teammembers + ")");
                        } else {
                            labelTeammembers.setText("(" + teammembers + "/" + tournament.getTeamSize() + ")");
                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

            teamnameSpinner.setAdapter(team_adapter);

            firstnameEditText.setText(player.getFirstname());
            nicknameEditText.setText(player.getNickname());
            lastnameEditText.setText(player.getLastname());
            firstnameEditText.setEnabled(false);
            nicknameEditText.setEnabled(false);
            lastnameEditText.setEnabled(false);
            firstnameEditText.setTypeface(Typeface.DEFAULT_BOLD);
            nicknameEditText.setTypeface(Typeface.DEFAULT_BOLD);
            lastnameEditText.setTypeface(Typeface.DEFAULT_BOLD);

            if (tournamentPlayer != null && tournamentPlayer.getFaction() != null) {
                factionSpinner.setSelection(faction_adapter.getPosition(tournamentPlayer.getFaction()));
            }

            addNewTeamnameButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        final EditText newTeamNameEditText = new EditText(getContext());

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        final AlertDialog newTeamNameDialog = builder.setView(newTeamNameEditText)
                            .setTitle(R.string.add_new_team)
                            .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            // nothing
                                        }
                                    })
                            .setNeutralButton(R.string.dialog_cancel, null)
                            .create();
                        newTeamNameDialog.show();

                        newTeamNameDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    if (!newTeamNameEditText.getText()
                                            .toString()
                                            .toLowerCase()
                                            .equals(getString(R.string.no_team))) {
                                        if (newTeamNameEditText.getText().toString().length() != 0) {
                                            teamnameSpinner.setVisibility(View.VISIBLE);
                                            team_adapter.add(newTeamNameEditText.getText().toString());
                                            team_adapter.notifyDataSetChanged();
                                            teamnameSpinner.setSelection(team_adapter.getCount());
                                            newTeamNameDialog.dismiss();
                                        } else {
                                            newTeamNameEditText.setError(getString(R.string.validation_error_empty));
                                        }
                                    } else {
                                        newTeamNameEditText.setError(getString(R.string.validation_error_no_team));
                                    }
                                }
                            });
                    }
                });

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setView(dialogView)
                .setTitle(getString(R.string.regsiter_for_tournament))
                .setPositiveButton(R.string.dialog_save, null)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            RegisterTournamentPlayerDialog.this.getDialog().cancel();
                        }
                    });

            return builder.create();
        }

        return null;
    }


    @Override
    public void onStart() {

        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            final Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);

            positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(this.getClass().getName(), "addTournamentPlayer new tournament_player");

                        final String firstname = firstnameEditText.getText().toString();
                        final String nickname = nicknameEditText.getText().toString();
                        final String lastname = lastnameEditText.getText().toString();
                        String teamname = null;

                        if (teamnameSpinner.getSelectedItem() != null) {
                            teamname = teamnameSpinner.getSelectedItem().toString();
                        }

                        final String faction = factionSpinner.getSelectedItem().toString();

                        if (validateForm(firstname, nickname, lastname, teamname)) {
                            TournamentPlayer tournamentPlayer = new TournamentPlayer();
                            tournamentPlayer.setOnline_uuid(player.getOnlineUUID());
                            tournamentPlayer.setPlayer_online_uuid(player.getOnlineUUID());
                            tournamentPlayer.setFirstname(firstname);
                            tournamentPlayer.setNickname(nickname);
                            tournamentPlayer.setLastname(lastname);

                            if (teamname != null && !teamname.equals(baseActivity.getString(R.string.no_team))) {
                                tournamentPlayer.setTeamname(teamname);
                            }

                            tournamentPlayer.setFaction(faction);

                            DatabaseReference reference = FirebaseDatabase.getInstance()
                                .getReference(
                                    FirebaseReferences.TOURNAMENT_REGISTRATIONS + "/" + tournament.getOnlineUUID() + "/"
                                    + player.getOnlineUUID());

                            reference.setValue(tournamentPlayer);

                            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                    R.string.success_registration, Snackbar.LENGTH_LONG);
                            snackbar.getView()
                            .setBackgroundColor(baseActivity.getResources().getColor(R.color.colorPositive));
                            snackbar.show();

                            dialog.dismiss();
                        }
                    }


                    private boolean validateForm(String firstname, String nickname, String lastname, String teamname) {

                        boolean valide = true;

                        if (firstname.isEmpty()) {
                            valide = false;
                            firstnameParent.setError(getContext().getString(R.string.validation_error_empty));
                        } else {
                            firstnameParent.setError(null);
                        }

                        if (nickname.isEmpty()) {
                            valide = false;
                            nicknameParent.setError(getContext().getString(R.string.validation_error_empty));
                        } else {
                            nicknameParent.setError(null);
                        }

                        if (lastname.isEmpty()) {
                            valide = false;
                            lastnameParent.setError(getContext().getString(R.string.validation_error_empty));
                        } else {
                            lastnameParent.setError(null);
                        }

                        if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                            if (teamname == null) {
                                valide = false;
                                labelTeamname.setError(getContext().getString(R.string.validation_error_empty));
                            } else if (teamname.equals(getString(R.string.no_team))) {
                                valide = false;
                                labelTeamname.setError(getContext().getString(R.string.validation_team_no_team));
                            } else {
                                labelTeamname.setError(null);
                            }
                        }

                        return valide;
                    }
                });
        }
    }
}
