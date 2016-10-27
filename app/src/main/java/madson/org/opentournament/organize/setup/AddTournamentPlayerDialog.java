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

    private EditText firstnameEditText;
    private EditText nicknameEditText;
    private EditText lastnameEditText;
    private Spinner factionSpinner;
    private Spinner teamnameSpinner;
    private ImageButton addNewTeamNameButton;

    private Tournament tournament;
    private Player player;

    private ArrayAdapter<String> team_adapter;
    private TextView labelTeamName;
    private TextInputLayout lastnameParent;
    private TextInputLayout firstnameParent;
    private TextInputLayout nicknameParent;
    private TextView labelTeamMembers;
    private Map<TournamentTeam, List<TournamentPlayer>> teamNameMap;
    private TournamentPlayer tournament_player;
    private BaseActivity baseActivity;

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

            labelTeamName = (TextView) dialogView.findViewById(R.id.label_teamname);
            teamnameSpinner = (Spinner) dialogView.findViewById(R.id.teamname_spinner);
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
                        teamnameSpinner, tournament_player).execute().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            teamnameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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

            teamnameSpinner.setAdapter(team_adapter);

            if (bundle.getParcelable(BUNDLE_PLAYER) != null) {
                player = bundle.getParcelable(BUNDLE_PLAYER);

                if (player != null) {
                    firstnameEditText.setText(player.getFirstName());
                    nicknameEditText.setText(player.getNickName());
                    lastnameEditText.setText(player.getLastName());
                    firstnameEditText.setEnabled(false);
                    nicknameEditText.setEnabled(false);
                    lastnameEditText.setEnabled(false);
                    firstnameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                    nicknameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                    lastnameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }

            if (tournament_player != null) {
                firstnameEditText.setText(tournament_player.getFirstName());
                nicknameEditText.setText(tournament_player.getNickName());
                lastnameEditText.setText(tournament_player.getLastName());
                firstnameEditText.setEnabled(false);
                nicknameEditText.setEnabled(false);
                lastnameEditText.setEnabled(false);
                firstnameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                nicknameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                lastnameEditText.setTypeface(Typeface.DEFAULT_BOLD);

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
                                        teamnameSpinner.setVisibility(View.VISIBLE);
                                        team_adapter.add(newTeamName);
                                        team_adapter.notifyDataSetChanged();
                                        teamnameSpinner.setSelection(team_adapter.getCount());

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

                        final String firstname = firstnameEditText.getText().toString();
                        final String nickname = nicknameEditText.getText().toString();
                        final String lastname = lastnameEditText.getText().toString();
                        String teamname = null;

                        if (teamnameSpinner.getSelectedItem() != null) {
                            teamname = teamnameSpinner.getSelectedItem().toString();
                        }

                        final String faction = factionSpinner.getSelectedItem().toString();

                        if (validateForm(firstname, nickname, lastname, teamname)) {
                            String uuid = UUID.randomUUID().toString();

                            // added local or online Player
                            if (tournament_player == null) {
                                TournamentPlayer tournamentPlayer = new TournamentPlayer();

                                tournamentPlayer.setFirstName(firstname);
                                tournamentPlayer.setNickName(nickname);
                                tournamentPlayer.setLastName(lastname);

                                tournamentPlayer.setTournamentUUID(tournament.getUuid());
                                tournamentPlayer.setFaction(faction);

                                if (!baseActivity.getString(R.string.no_team).equals(teamname)) {
                                    tournamentPlayer.setTeamName(teamname);
                                } else {
                                    tournamentPlayer.setTeamName("");
                                }

                                if (player != null) {
                                    tournamentPlayer.setPlayerUUID(player.getUUID());

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
                                tournament_player.setFirstName(firstname);
                                tournament_player.setNickName(nickname);
                                tournament_player.setLastName(lastname);

                                tournament_player.setTournamentUUID(tournament.getUuid());
                                tournament_player.setFaction(faction);

                                String oldTeamName = tournament_player.getTeamName();

                                if (!baseActivity.getString(R.string.no_team).equals(teamname)) {
                                    tournament_player.setTeamName(teamname);
                                } else {
                                    tournament_player.setTeamName("");
                                }

                                new UpdateTournamentPlayerTask(baseActivity, tournament_player, tournament, dialog,
                                    oldTeamName).execute();
                            }

                            if (player == null && tournament_player == null) {
                                Player newLocalPlayer = new Player();
                                newLocalPlayer.setFirstName(firstname);
                                newLocalPlayer.setNickName(nickname);
                                newLocalPlayer.setLastName(lastname);

                                newLocalPlayer.setUUID(uuid);

                                new SaveLocalPlayerTask(baseActivity, newLocalPlayer).execute();
                            }
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
                                labelTeamName.setError(getContext().getString(R.string.validation_error_empty));
                            } else if (teamname.equals(getString(R.string.no_team))) {
                                valide = false;
                                labelTeamName.setError(getContext().getString(R.string.validation_team_no_team));
                            } else {
                                labelTeamName.setError(null);
                            }
                        }

                        return valide;
                    }
                });
        }
    }
}
