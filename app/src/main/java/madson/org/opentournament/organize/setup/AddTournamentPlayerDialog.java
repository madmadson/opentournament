package madson.org.opentournament.organize.setup;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Typeface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
import java.util.List;


/**
 * Dialog for adding new tournament players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class AddTournamentPlayerDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_PLAYER = "player";

    private TournamentSetupEventListener mListener;

    private EditText firstnameEditText;
    private EditText nicknameEditText;
    private EditText lastnameEditText;
    private Spinner factionSpinner;
    private Spinner teamnameSpinner;
    private ImageButton addNewTeamnameButton;

    private Tournament tournament;
    private Player player;
    private ArrayAdapter<String> team_adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (getParentFragment() instanceof TournamentSetupFragment) {
            mListener = (TournamentSetupFragment) getParentFragment();
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();

        if (mListener != null) {
            mListener = null;
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);

            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_add_tournament_player, null);

            firstnameEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_firstname);
            nicknameEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_nickname);
            lastnameEditText = (EditText) dialogView.findViewById(R.id.dialog_add_tournament_player_lastname);

            factionSpinner = (Spinner) dialogView.findViewById(R.id.dialog_add_tournament_player_faction_spinner);
            teamnameSpinner = (Spinner) dialogView.findViewById(R.id.dialog_add_tournament_player_teamname);

            addNewTeamnameButton = (ImageButton) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_add_new_team);

            ArrayAdapter<CharSequence> faction_adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.factions, android.R.layout.simple_spinner_item);
            faction_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            factionSpinner.setAdapter(faction_adapter);

            team_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    new ArrayList<String>());
            team_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            teamnameSpinner.setAdapter(team_adapter);

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    TournamentPlayerService tournamentPlayerService = ((BaseApplication) getActivity().getApplication())
                        .getTournamentPlayerService();

                    final List<String> listOfAllTeamNames = tournamentPlayerService.getAllTeamNamesForTournament(
                            tournament);

                    listOfAllTeamNames.add(0, getString(R.string.no_team));
                    team_adapter.addAll(listOfAllTeamNames);
                    team_adapter.notifyDataSetChanged();
                }
            };
            runnable.run();

            if (bundle.getParcelable(BUNDLE_PLAYER) != null) {
                player = bundle.getParcelable(BUNDLE_PLAYER);

                if (player != null) {
                    firstnameEditText.setText(player.getFirstname());
                    nicknameEditText.setText(player.getNickname());
                    lastnameEditText.setText(player.getLastname());
                    firstnameEditText.setEnabled(false);
                    nicknameEditText.setEnabled(false);
                    lastnameEditText.setEnabled(false);
                    firstnameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                    nicknameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                    lastnameEditText.setTypeface(Typeface.DEFAULT_BOLD);
                }
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

                                    if (newTeamNameEditText.getText().toString().length() != 0) {
                                        team_adapter.add(newTeamNameEditText.getText().toString());
                                        team_adapter.notifyDataSetChanged();
                                        teamnameSpinner.setSelection(team_adapter.getCount());
                                        newTeamNameDialog.dismiss();
                                    } else {
                                        newTeamNameEditText.setError(getString(R.string.validation_error_empty));
                                    }
                                }
                            });
                    }
                });

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setView(dialogView)
                .setTitle(getString(R.string.new_player_tournament_title))
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
            Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);

            positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(this.getClass().getName(), "addTournamentPlayer new tournament_player");

                        final String firstname = firstnameEditText.getText().toString();
                        final String nickname = nicknameEditText.getText().toString();
                        final String lastname = lastnameEditText.getText().toString();

                        if (!firstname.isEmpty() && !nickname.isEmpty() && !lastname.isEmpty()) {
                            Runnable runnable = new Runnable() {

                                @Override
                                public void run() {

                                    TournamentPlayerService tournamentPlayerService =
                                        ((BaseApplication) getActivity().getApplication()).getTournamentPlayerService();

                                    if (player == null) {
                                        Log.i(this.getClass().getName(), "add new local player.");

                                        Player newLocalPlayer = new Player();
                                        newLocalPlayer.setFirstname(firstname);
                                        newLocalPlayer.setNickname(nickname);
                                        newLocalPlayer.setLastname(lastname);

                                        PlayerService playerService = ((BaseApplication) getActivity().getApplication())
                                            .getPlayerService();
                                        Player newLocalPlayerWithId = playerService.createLocalPlayer(newLocalPlayer);

                                        player = newLocalPlayerWithId;
                                    } else {
                                        if (mListener != null) {
                                            mListener.removeAvailablePlayer(player);
                                        }
                                    }

                                    TournamentPlayer tournamentPlayer = new TournamentPlayer(player, tournament);

                                    tournamentPlayer.setFaction(factionSpinner.getSelectedItem().toString());

                                    if (teamnameSpinner.getSelectedItem() != "-") {
                                        tournamentPlayer.setTeamname(teamnameSpinner.getSelectedItem().toString());
                                    }

                                    tournamentPlayerService.addTournamentPlayerToTournament(tournamentPlayer,
                                        tournament);

                                    ((BaseApplication) getActivity().getApplication()).getTournamentService()
                                    .increaseActualPlayerForTournament(tournament);

                                    if (mListener != null) {
                                        mListener.addTournamentPlayer(tournamentPlayer);
                                    }

                                    Snackbar snackbar = Snackbar.make(
                                            ((BaseActivity) getActivity()).getCoordinatorLayout(),
                                            R.string.success_new_player_inserted, Snackbar.LENGTH_LONG);

                                    snackbar.getView()
                                    .setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));

                                    snackbar.show();

                                    dialog.dismiss();
                                }
                            };
                            runnable.run();
                        } else {
                            Log.i(this.getClass().getName(), "validation failed");

                            if (firstname.isEmpty()) {
                                firstnameEditText.setError(getContext().getString(R.string.validation_error_empty));
                            } else {
                                firstnameEditText.setError(null);
                            }

                            if (nickname.isEmpty()) {
                                nicknameEditText.setError(getContext().getString(R.string.validation_error_empty));
                            } else {
                                nicknameEditText.setError(null);
                            }

                            if (lastname.isEmpty()) {
                                lastnameEditText.setError(getContext().getString(R.string.validation_error_empty));
                            } else {
                                lastnameEditText.setError(null);
                            }
                        }
                    }
                });
        }
    }
}
