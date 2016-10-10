package madson.org.opentournament.organize.setup;

import android.app.Dialog;

import android.content.DialogInterface;

import android.graphics.Typeface;

import android.os.Bundle;

import android.support.annotation.Nullable;

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
import madson.org.opentournament.tasks.EditTournamentPlayerTask;
import madson.org.opentournament.tasks.LoadTournamentTeamTask;
import madson.org.opentournament.tasks.SaveTournamentPlayerTask;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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

            labelTeamName = (TextView) dialogView.findViewById(R.id.label_teamname);
            teamnameSpinner = (Spinner) dialogView.findViewById(R.id.teamname_spinner);
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
                        int teamMembers = 0;

                        if (teamNameMap.get(new TournamentTeam(teamName)) != null) {
                            teamMembers = teamNameMap.get(new TournamentTeam(teamName)).size();
                        } else {
                            if (teamNameMap.get(new TournamentTeam("")) == null) {
                                teamMembers = 0;
                            } else {
                                teamMembers = teamNameMap.get(new TournamentTeam("")).size();
                            }
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

                                            if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                                                labelTeamMembers.setText("(0)");
                                            } else {
                                                labelTeamMembers.setText("(0/" + tournament.getTeamSize() + ")");
                                            }

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
                            if (tournament_player == null) {
                                new SaveTournamentPlayerTask((BaseActivity) getActivity(), player, tournament, dialog,
                                    firstname, nickname, lastname, teamname, faction).execute();
                            } else {
                                new EditTournamentPlayerTask((BaseActivity) getActivity(), tournament_player,
                                    tournament, dialog, teamname, faction).execute();
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
