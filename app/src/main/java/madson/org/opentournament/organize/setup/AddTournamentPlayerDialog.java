package madson.org.opentournament.organize.setup;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Typeface;

import android.os.Bundle;

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

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTeam;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.tasks.LoadTournamentTeamTask;
import madson.org.opentournament.tasks.SaveLocalPlayerTask;
import madson.org.opentournament.tasks.SaveTournamentPlayerTask;
import madson.org.opentournament.tasks.UpdateTournamentPlayerTask;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


/**
 * Dialog for adding new tournament players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class AddTournamentPlayerDialog extends DialogFragment {

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
    private Map<TournamentTeam, List<TournamentPlayer>> teamNameMap;
    private TournamentPlayer tournament_player;
    private BaseActivity baseActivity;
    private EditText affiliationEditText;

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

            if (bundle.getParcelable(BUNDLE_TOURNAMENT_PLAYER) != null) {
                tournament_player = bundle.getParcelable(BUNDLE_TOURNAMENT_PLAYER);
            }

            // Get the layout inflater
            final LayoutInflater inflater = getActivity().getLayoutInflater();

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

            factionSpinner = (Spinner) dialogView.findViewById(R.id.dialog_add_tournament_player_faction_spinner);

            labelTeamName = (TextView) dialogView.findViewById(R.id.label_teamname);
            teamNameSpinner = (Spinner) dialogView.findViewById(R.id.teamname_spinner);
            labelTeamMembers = (TextView) dialogView.findViewById(R.id.team_members);

            addNewTeamNameButton = (ImageButton) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_add_new_team);

            ArrayAdapter<CharSequence> faction_adapter = ArrayAdapter.createFromResource(baseActivity, R.array.factions,
                    android.R.layout.simple_spinner_item);
            faction_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            factionSpinner.setAdapter(faction_adapter);

            team_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    new ArrayList<String>());
            team_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            try {
                teamNameMap = new LoadTournamentTeamTask((BaseActivity) getActivity(), tournament, team_adapter,
                        teamNameSpinner, tournament_player).execute().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            teamNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String teamName = (String) parent.getItemAtPosition(position);
                        int teamMembers;

                        if (teamName.equals(baseActivity.getString(R.string.no_team))
                                && teamNameMap.get(new TournamentTeam("")) != null) {
                            teamMembers = teamNameMap.get(new TournamentTeam("")).size();
                        } else if (teamNameMap.get(new TournamentTeam(teamName)) != null) {
                            teamMembers = teamNameMap.get(new TournamentTeam(teamName)).size();
                        } else {
                            teamMembers = 0;
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

            if (bundle.getParcelable(BUNDLE_PLAYER) != null) {
                player = bundle.getParcelable(BUNDLE_PLAYER);

                if (player != null) {
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
                }
            }

            if (tournament_player != null) {
                firstNameEditText.setText(tournament_player.getFirstName());
                nickNameEditText.setText(tournament_player.getNickName());
                lastNameEditText.setText(tournament_player.getLastName());
                affiliationEditText.setText(tournament_player.getMeta());
                firstNameEditText.setEnabled(false);
                nickNameEditText.setEnabled(false);
                lastNameEditText.setEnabled(false);
                firstNameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                nickNameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                lastNameEditText.setTypeface(Typeface.DEFAULT_BOLD);

                if (tournament_player.getFaction() != null) {
                    factionSpinner.setSelection(faction_adapter.getPosition(tournament_player.getFaction()));
                }
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

                                    if (teamNameMap.get(new TournamentTeam(newTeamName)) != null) {
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

            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);

            builder.setView(dialogView)
                .setTitle(tournament_player == null ? getString(R.string.new_player_tournament_title)
                                                    : getString(R.string.edit_tournament_player))
                .setPositiveButton(R.string.dialog_save, null)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            AddTournamentPlayerDialog.this.getDialog().cancel();
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
                            String uuid = UUID.randomUUID().toString();

                            // added local or online Player
                            if (tournament_player == null) {
                                TournamentPlayer tournamentPlayer = new TournamentPlayer();

                                tournamentPlayer.setFirstName(firstName);
                                tournamentPlayer.setNickName(nickName);
                                tournamentPlayer.setLastName(lastName);
                                tournamentPlayer.setMeta(affiliation);

                                tournamentPlayer.setTournamentUUID(tournament.getUuid());
                                tournamentPlayer.setFaction(faction);

                                if (!baseActivity.getString(R.string.no_team).equals(teamName)) {
                                    tournamentPlayer.setTeamName(teamName);
                                } else {
                                    tournamentPlayer.setTeamName("");
                                }

                                if (player != null) {
                                    tournamentPlayer.setPlayerUUID(player.getUUID());
                                    tournamentPlayer.setElo(player.getElo());
                                    tournamentPlayer.setGamesCounter(player.getGamesCounter());

                                    if (player.isLocal()) {
                                        tournamentPlayer.setLocal(true);
                                    }
                                } else {
                                    tournamentPlayer.setPlayerUUID(uuid);
                                }

                                if (player == null && tournament_player == null) {
                                    tournamentPlayer.setLocal(true);
                                }

                                new SaveTournamentPlayerTask(baseActivity, tournament, tournamentPlayer, dialog)
                                .execute();
                            } else {
                                tournament_player.setFirstName(firstName);
                                tournament_player.setNickName(nickName);
                                tournament_player.setLastName(lastName);

                                tournament_player.setMeta(affiliation);
                                tournament_player.setElo(tournament_player.getElo());
                                tournament_player.setGamesCounter(tournament_player.getGamesCounter());

                                tournament_player.setTournamentUUID(tournament.getUuid());
                                tournament_player.setFaction(faction);

                                String oldTeamName = tournament_player.getTeamName();

                                if (!baseActivity.getString(R.string.no_team).equals(teamName)) {
                                    tournament_player.setTeamName(teamName);
                                } else {
                                    tournament_player.setTeamName("");
                                }

                                new UpdateTournamentPlayerTask(baseActivity, tournament_player, tournament, dialog,
                                    oldTeamName).execute();
                            }

                            if (player == null && tournament_player == null) {
                                Player newLocalPlayer = new Player();
                                newLocalPlayer.setFirstName(firstName);
                                newLocalPlayer.setNickName(nickName);
                                newLocalPlayer.setLastName(lastName);
                                newLocalPlayer.setLastName(affiliation);
                                newLocalPlayer.setElo(0);
                                newLocalPlayer.setGamesCounter(0);

                                newLocalPlayer.setUUID(uuid);

                                new SaveLocalPlayerTask(baseActivity, newLocalPlayer).execute();
                            }
                        }
                    }


                    private boolean validateForm(String firstName, String nickName, String lastName, String teamName) {

                        boolean valid = true;

                        if (firstName.isEmpty()) {
                            valid = false;
                            firstNameParent.setError(getContext().getString(R.string.validation_error_empty));
                        } else {
                            firstNameParent.setError(null);
                        }

                        if (nickName.isEmpty()) {
                            valid = false;
                            nickNameParent.setError(getContext().getString(R.string.validation_error_empty));
                        } else {
                            nickNameParent.setError(null);
                        }

                        if (lastName.isEmpty()) {
                            valid = false;
                            lastNameParent.setError(getContext().getString(R.string.validation_error_empty));
                        } else {
                            lastNameParent.setError(null);
                        }

                        if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                            if (teamName == null) {
                                valid = false;
                                labelTeamName.setError(getContext().getString(R.string.validation_error_empty));
                            } else if (teamName.equals(getString(R.string.no_team))) {
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
