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
import madson.org.opentournament.domain.TournamentTeam;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Map<TournamentTeam, List<TournamentPlayer>> teamMap = new HashMap<>();

    private BaseActivity baseActivity;
    private TournamentPlayer tournamentPlayer;
    private EditText affiliationEditText;
    private TextInputLayout affiliationParent;
    private TextView teamAffiliation;
    private TextView teamAffiliationLabel;

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
            teamAffiliation = (TextView) dialogView.findViewById(R.id.tv_team_affiliation);
            teamAffiliationLabel = (TextView) dialogView.findViewById(R.id.label_team_affiliation);

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

            if (TournamentTyp.TEAM.name().equals(tournament.getTournamentTyp())) {
                affiliationEditText.setVisibility(View.GONE);
                affiliationParent.setVisibility(View.GONE);
            } else {
                affiliationEditText.setVisibility(View.VISIBLE);
                affiliationParent.setVisibility(View.VISIBLE);
            }

            addNewTeamNameButton = (ImageButton) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_add_new_team);

            ArrayAdapter<CharSequence> faction_adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.factions, android.R.layout.simple_spinner_item);
            faction_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            factionSpinner.setAdapter(faction_adapter);

            team_adapter = new ArrayAdapter<>(baseActivity, android.R.layout.simple_spinner_item,
                    new ArrayList<String>());
            team_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference(FirebaseReferences.TOURNAMENT_REGISTRATIONS + "/" + tournament.getGameOrSportTyp()
                        + "/" + tournament.getUuid());

            teamMap = new HashMap<>();

            reference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                            TournamentPlayer player = playerSnapshot.getValue(TournamentPlayer.class);

                            if (player != null) {
                                String teamName = player.getTeamName();

                                if (teamName != null && !teamName.isEmpty()) {
                                    TournamentTeam tournamentTeam = new TournamentTeam(teamName);

                                    if (!teamMap.containsKey(tournamentTeam)) {
                                        List<TournamentPlayer> playerList = new ArrayList<>();
                                        playerList.add(player);
                                        tournamentTeam.setMeta(player.getMeta());
                                        teamMap.put(tournamentTeam, playerList);
                                    } else {
                                        List<TournamentPlayer> tournamentPlayers = teamMap.get(tournamentTeam);
                                        tournamentPlayers.add(player);
                                        teamMap.put(tournamentTeam, tournamentPlayers);
                                    }
                                }
                            }
                        }

                        if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                            team_adapter.add(baseActivity.getString(R.string.no_team));

                            for (TournamentTeam key : teamMap.keySet()) {
                                team_adapter.add(key.getName());
                            }
                        } else {
                            String firstTeamAffiliation = null;

                            for (TournamentTeam key : teamMap.keySet()) {
                                if (teamMap.get(key).size() < tournament.getTeamSize()) {
                                    team_adapter.add(key.getName());

                                    if (firstTeamAffiliation == null) {
                                        firstTeamAffiliation = key.getMeta();
                                    }
                                }
                            }

                            if (team_adapter.getCount() != 0) {
                                teamAffiliation.setVisibility(View.VISIBLE);
                                teamAffiliationLabel.setVisibility(View.VISIBLE);
                                teamAffiliation.setText(firstTeamAffiliation);
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
                        TournamentTeam tournamentTeam = new TournamentTeam(teamName);

                        if (teamMap.get(tournamentTeam) == null) {
                            teamMembers = 0;
                        } else {
                            teamMembers = teamMap.get(tournamentTeam).size();
                        }

                        if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                            labelTeamMembers.setText("(" + teamMembers + ")");
                        } else {
                            labelTeamMembers.setText("(" + teamMembers + "/" + tournament.getTeamSize() + ")");

                            if (teamMap.get(tournamentTeam) != null) {
                                TournamentPlayer tournamentPlayer = teamMap.get(new TournamentTeam(teamName)).get(0);
                                teamAffiliation.setText(tournamentPlayer.getMeta());
                            }
                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

            teamNameSpinner.setAdapter(team_adapter);

            if (TournamentTyp.TEAM.name().equals(tournament.getTournamentTyp())) {
                affiliationEditText.setVisibility(View.GONE);
                affiliationParent.setVisibility(View.GONE);
            } else {
                affiliationEditText.setVisibility(View.VISIBLE);
                affiliationParent.setVisibility(View.VISIBLE);
            }

            firstNameEditText.setText(player.getFirstName());
            nickNameEditText.setText(player.getNickName());
            lastNameEditText.setText(player.getLastName());

            firstNameEditText.setEnabled(false);
            nickNameEditText.setEnabled(false);
            lastNameEditText.setEnabled(false);
            firstNameEditText.setTypeface(Typeface.DEFAULT_BOLD);
            nickNameEditText.setTypeface(Typeface.DEFAULT_BOLD);
            lastNameEditText.setTypeface(Typeface.DEFAULT_BOLD);

            if (!TournamentTyp.TEAM.name().equals(tournament.getTournamentTyp())) {
                affiliationEditText.setText(player.getMeta());
            }

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

                        final EditText newTeamAffiliationEditText = (EditText) dialogAddTeam.findViewById(
                                R.id.new_team_affiliation);
                        TextInputLayout newTeamAffiliationParent = (TextInputLayout) dialogAddTeam.findViewById(
                                R.id.new_team_affiliation_parent);

                        if (!TournamentTyp.TEAM.name().equals(tournament.getTournamentTyp())) {
                            newTeamAffiliationEditText.setVisibility(View.GONE);
                            newTeamAffiliationParent.setVisibility(View.GONE);
                        }

                        newTeamNameDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    TextInputLayout newTeamNameParent = (TextInputLayout) dialogAddTeam.findViewById(
                                            R.id.new_team_name_parent);
                                    EditText newTeamNameEditText = (EditText) dialogAddTeam.findViewById(
                                            R.id.new_team_name);

                                    String newTeamName = newTeamNameEditText.getText().toString();

                                    String newTeamAffiliation = newTeamAffiliationEditText.getText().toString();

                                    boolean valid = true;

                                    if (teamMap.get(new TournamentTeam(newTeamName)) != null) {
                                        newTeamNameParent.setError(getString(R.string.team_name_already_taken));
                                        valid = false;
                                    }

                                    if (newTeamName.toLowerCase().equals(getString(R.string.no_team))) {
                                        newTeamNameParent.setError(getString(R.string.validation_error_no_team));
                                        valid = false;
                                    }

                                    if (newTeamName.length() == 0) {
                                        newTeamNameParent.setError(getString(R.string.validation_error_empty));
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

                                        newTeamNameParent.setError(null);

                                        if (!newTeamAffiliation.isEmpty()) {
                                            teamAffiliation.setVisibility(View.VISIBLE);
                                            teamAffiliationLabel.setVisibility(View.VISIBLE);
                                            teamAffiliation.setText(newTeamAffiliation);
                                        } else {
                                            teamAffiliation.setVisibility(View.GONE);
                                            teamAffiliationLabel.setVisibility(View.GONE);
                                            teamAffiliation.setText(null);
                                        }

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
                        String affiliation;

                        if (TournamentTyp.TEAM.name().equals(tournament.getTournamentTyp())) {
                            affiliation = teamAffiliation.getText().toString();
                        } else {
                            affiliation = affiliationEditText.getText().toString();
                        }

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

                            DatabaseReference tournamentRegistration = FirebaseDatabase.getInstance()
                                .getReference(
                                    FirebaseReferences.TOURNAMENT_REGISTRATIONS + "/" + tournament.getGameOrSportTyp()
                                    + "/" + tournament.getUuid() + "/" + player.getUUID());

                            tournamentRegistration.setValue(tournamentPlayer);

                            DatabaseReference playerRegistration = FirebaseDatabase.getInstance()
                                .getReference(
                                    FirebaseReferences.PLAYER_REGISTRATIONS + "/" + player.getUUID()
                                    + "/" + tournament.getUuid());

                            playerRegistration.setValue(tournament);

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
