package madson.org.opentournament.online;

import android.annotation.SuppressLint;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Typeface;

import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;

import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;

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
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Dialog for adding new tournament_uuid players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RegisterTournamentPlayerDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_PLAYER = "player";
    public static final String BUNDLE_TOURNAMENT_PLAYER = "tournament_player";

    private EditText firstNameEditText;
    private EditText nickNameEditText;
    private EditText lastNameEditText;
    private Spinner factionSpinner;
    private Spinner teamNameSpinner;
    private ImageButton addNewTeamNameButton;

    private Tournament tournament;
    private Player player;

    private ArrayAdapter<String> team_adapter;
    private TextView labelTeamName;
    private TextInputLayout lastNameParent;
    private TextInputLayout firstNameParent;
    private TextInputLayout nickNameParent;

    private TextView labelTeamMembers;
    private Map<String, Integer> teamNameMap;

    private BaseActivity baseActivity;
    private TournamentPlayer tournamentPlayer;
    private EditText affiliationEditText;
    private TextInputLayout affiliationParent;

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
            final LayoutInflater inflater = getActivity().getLayoutInflater();

            // its an alert dialog no chance to attach it to parent
            @SuppressLint("InflateParams")
            View dialogView = inflater.inflate(R.layout.dialog_add_tournament_player, null);

            firstNameEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_firstname);
            nickNameEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_nickname);
            lastNameEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_lastname);
            affiliationEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_affiliation);

            firstNameParent = (TextInputLayout) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_firstname_parent);
            nickNameParent = (TextInputLayout) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_nickname_parent);
            lastNameParent = (TextInputLayout) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_lastname_parent);
            affiliationParent = (TextInputLayout) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_affiliation_parent);

            factionSpinner = (Spinner) dialogView.findViewById(R.id.dialog_add_tournament_player_faction_spinner);

            labelTeamName = (TextView) dialogView.findViewById(R.id.label_teamname);
            teamNameSpinner = (Spinner) dialogView.findViewById(R.id.teamname_spinner);
            labelTeamMembers = (TextView) dialogView.findViewById(R.id.team_members);

            addNewTeamNameButton = (ImageButton) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_add_new_team);

            ArrayAdapter<CharSequence> faction_adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.factions, android.R.layout.simple_spinner_item);
            faction_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            factionSpinner.setAdapter(faction_adapter);

            team_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    new ArrayList<String>());
            team_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference(FirebaseReferences.TOURNAMENT_REGISTRATIONS + "/" + tournament.getGameOrSportTyp()
                        + "/" + tournament.getUuid());

            teamNameMap = new HashMap<>();

            reference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                            TournamentPlayer player = playerSnapshot.getValue(TournamentPlayer.class);

                            if (player != null) {
                                String teamName = player.getTeamName();

                                if (teamName != null && !teamName.isEmpty()) {
                                    if (!teamNameMap.containsKey(teamName)) {
                                        teamNameMap.put(teamName, 1);
                                    } else {
                                        teamNameMap.put(teamName, teamNameMap.get(teamName) + 1);
                                    }
                                }
                            }
                        }

                        if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                            team_adapter.add(baseActivity.getString(R.string.no_team));

                            for (String key : teamNameMap.keySet()) {
                                team_adapter.add(key);
                            }
                        } else {
                            for (String key : teamNameMap.keySet()) {
                                if (teamNameMap.get(key) < tournament.getTeamSize()) {
                                    team_adapter.add(key);
                                }
                            }
                        }

                        if (team_adapter.isEmpty()) {
                            teamNameSpinner.setVisibility(View.GONE);
                        } else {
                            teamNameSpinner.setVisibility(View.VISIBLE);
                        }

                        team_adapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            teamNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String teamName = (String) parent.getItemAtPosition(position);
                        int teamMembers;

                        if (teamNameMap.get(teamName) == null) {
                            teamMembers = 0;
                        } else {
                            teamMembers = teamNameMap.get(teamName);
                        }

                        if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                            labelTeamMembers.setText("(" + teamMembers + ")");
                        } else {
                            labelTeamMembers.setText("(" + teamMembers + "/" + tournament.getTeamSize() + ")");
                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

            teamNameSpinner.setAdapter(team_adapter);

            firstNameEditText.setText(player.getFirstName());
            nickNameEditText.setText(player.getNickName());
            lastNameEditText.setText(player.getLastName());
            affiliationEditText.setText(player.getMeta());
            firstNameEditText.setEnabled(false);
            nickNameEditText.setEnabled(false);
            lastNameEditText.setEnabled(false);
            firstNameEditText.setTypeface(Typeface.DEFAULT_BOLD);
            nickNameEditText.setTypeface(Typeface.DEFAULT_BOLD);
            lastNameEditText.setTypeface(Typeface.DEFAULT_BOLD);

            if (tournamentPlayer != null && tournamentPlayer.getFaction() != null) {
                factionSpinner.setSelection(faction_adapter.getPosition(tournamentPlayer.getFaction()));
            }

            addNewTeamNameButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        final View dialogAddTeam = inflater.inflate(R.layout.dialog_add_team, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        final AlertDialog newTeamNameDialog = builder.setView(dialogAddTeam)
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

                                    EditText newTeamNameEditText = (EditText) dialogAddTeam.findViewById(
                                            R.id.new_team_name);

                                    String newTeamName = newTeamNameEditText.getText().toString();

                                    boolean valid = true;

                                    if (teamNameMap.get(newTeamName) != null) {
                                        newTeamNameEditText.setError(getString(R.string.team_name_already_taken));
                                        valid = false;
                                    }

                                    if (newTeamName.toLowerCase().equals(getString(R.string.no_team))) {
                                        newTeamNameEditText.setError(getString(R.string.validation_error_no_team));
                                        valid = false;
                                    }

                                    if (newTeamName.length() == 0) {
                                        newTeamNameEditText.setError(getString(R.string.validation_error_empty));
                                        valid = false;
                                    }

                                    if (valid) {
                                        teamNameSpinner.setVisibility(View.VISIBLE);
                                        team_adapter.add(newTeamName);
                                        team_adapter.notifyDataSetChanged();
                                        teamNameSpinner.setSelection(team_adapter.getCount());

                                        if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                                            labelTeamMembers.setText("(0)");
                                        } else {
                                            labelTeamMembers.setText("(0/" + tournament.getTeamSize() + ")");
                                        }

                                        labelTeamName.setError(null);

                                        newTeamNameDialog.dismiss();
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

                        final String firstName = firstNameEditText.getText().toString();
                        final String nickName = nickNameEditText.getText().toString();
                        final String lastName = lastNameEditText.getText().toString();
                        final String affiliation = affiliationEditText.getText().toString();
                        String teamName = null;

                        if (teamNameSpinner.getSelectedItem() != null) {
                            teamName = teamNameSpinner.getSelectedItem().toString();
                        }

                        final String faction = factionSpinner.getSelectedItem().toString();

                        if (validateForm(firstName, nickName, lastName, teamName)) {
                            TournamentPlayer tournamentPlayer = new TournamentPlayer();
                            tournamentPlayer.setPlayerUUID(player.getUUID());

                            tournamentPlayer.setFirstName(firstName);
                            tournamentPlayer.setNickName(nickName);
                            tournamentPlayer.setLastName(lastName);
                            tournamentPlayer.setMeta(affiliation);
                            tournamentPlayer.setElo(player.getElo());
                            tournamentPlayer.setGamesCounter(player.getGamesCounter());

                            if (teamName != null && !teamName.equals(baseActivity.getString(R.string.no_team))) {
                                tournamentPlayer.setTeamName(teamName);
                            }

                            tournamentPlayer.setFaction(faction);

                            DatabaseReference reference = FirebaseDatabase.getInstance()
                                .getReference(
                                    FirebaseReferences.TOURNAMENT_REGISTRATIONS + "/" + tournament.getGameOrSportTyp()
                                    + "/" + tournament.getUuid() + "/" + player.getUUID());

                            reference.setValue(tournamentPlayer);

                            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                    R.string.success_registration, Snackbar.LENGTH_LONG);
                            snackbar.getView()
                            .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));
                            snackbar.show();

                            dialog.dismiss();
                        }
                    }


                    private boolean validateForm(String firstname, String nickname, String lastname, String teamname) {

                        boolean valid = true;

                        if (firstname.isEmpty()) {
                            valid = false;
                            firstNameParent.setError(getContext().getString(R.string.validation_error_empty));
                        } else {
                            firstNameParent.setError(null);
                        }

                        if (nickname.isEmpty()) {
                            valid = false;
                            nickNameParent.setError(getContext().getString(R.string.validation_error_empty));
                        } else {
                            nickNameParent.setError(null);
                        }

                        if (lastname.isEmpty()) {
                            valid = false;
                            lastNameParent.setError(getContext().getString(R.string.validation_error_empty));
                        } else {
                            lastNameParent.setError(null);
                        }

                        if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                            if (teamname == null) {
                                valid = false;
                                labelTeamName.setError(getContext().getString(R.string.validation_error_empty));
                            } else if (teamname.equals(getString(R.string.no_team))) {
                                valid = false;
                                labelTeamName.setError(getContext().getString(R.string.validation_team_no_team));
                            } else {
                                labelTeamName.setError(null);
                            }
                        }

                        return valid;
                    }
                });
        }
    }
}
