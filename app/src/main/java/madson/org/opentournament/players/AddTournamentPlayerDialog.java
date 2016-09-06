package madson.org.opentournament.players;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.jaredrummler.materialspinner.MaterialSpinner;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * Dialog for adding new tournament players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class AddTournamentPlayerDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_PLAYER = "player";

    private CoordinatorLayout coordinatorLayout;
    private TournamentSetupEventListener mListener;

    private EditText firstnameEditText;
    private EditText nicknameEditText;
    private EditText lastnameEditText;
    private MaterialSpinner factionSpinner;
    private MaterialSpinner teamnameSpinner;
    private ImageButton addNewTeamnameButton;

    private Tournament tournament;
    private Player player;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinator_main);
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        // target fragment have to be set and must be TournamentSetupFragment -> both are on etup
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

            factionSpinner = (MaterialSpinner) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_faction_spinner);

            factionSpinner.setItems(getResources().getStringArray(R.array.factions));

            TournamentPlayerService tournamentPlayerService = ((BaseApplication) getActivity().getApplication())
                .getTournamentPlayerService();

            final List<String> listOfAllTeamNames = tournamentPlayerService.getAllTeamNamesForTournament(tournament);

            if (tournament.getOnlineUUID() != null && (((BaseActivity) getActivity()).isConnected())) {
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

                final ValueEventListener tournamentPlayerListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot teamname : dataSnapshot.getChildren()) {
                            if (!listOfAllTeamNames.contains(teamname.getKey())) {
                                listOfAllTeamNames.add(teamname.getKey());
                            }
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };

                DatabaseReference child = mFirebaseDatabaseReference.child("tournaments/" + tournament.getOnlineUUID()
                        + "/teams");

                child.addValueEventListener(tournamentPlayerListener);
            }

            teamnameSpinner = (MaterialSpinner) dialogView.findViewById(R.id.dialog_add_tournament_player_teamname);

            listOfAllTeamNames.add(0, getString(R.string.no_team));

            teamnameSpinner.setItems(listOfAllTeamNames);

            if (bundle.getParcelable(BUNDLE_PLAYER) != null) {
                player = bundle.getParcelable(BUNDLE_PLAYER);

                if (player != null) {
                    firstnameEditText.setText(player.getFirstname());
                    nicknameEditText.setText(player.getNickname());
                    lastnameEditText.setText(player.getLastname());
                }
            }

            addNewTeamnameButton = (ImageButton) dialogView.findViewById(
                    R.id.dialog_add_tournament_player_add_new_team);

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

                                    if (newTeamNameEditText.getText().length() != 0) {
                                        teamnameSpinner.setText(newTeamNameEditText.getText());
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
                .setTitle(getString(R.string.new_player_tournament_title, tournament.getName()))
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

                        String firstname = firstnameEditText.getText().toString();
                        String nickname = nicknameEditText.getText().toString();
                        String lastname = lastnameEditText.getText().toString();

                        if (!firstname.isEmpty() && !nickname.isEmpty() && !lastname.isEmpty()) {
                            TournamentPlayerService tournamentPlayerService =
                                ((BaseApplication) getActivity().getApplication()).getTournamentPlayerService();

                            if (player == null) {
                                Log.i(this.getClass().getName(), "addTournamentPlayer new local player.");

                                Player newLocalPlayer = new Player();
                                newLocalPlayer.setFirstname(firstname);
                                newLocalPlayer.setFirstname(nickname);
                                newLocalPlayer.setFirstname(lastname);

                                PlayerService playerService = ((BaseApplication) getActivity().getApplication())
                                    .getPlayerService();
                                playerService.createLocalPlayer(newLocalPlayer);

                                player = newLocalPlayer;
                            }

                            TournamentPlayer tournamentPlayer = new TournamentPlayer(player, tournament);

                            tournamentPlayer.setFaction(factionSpinner.getText().toString());

                            if (teamnameSpinner.getText() != "-") {
                                tournamentPlayer.setTeamname(teamnameSpinner.getText().toString());
                            }

                            dialog.dismiss();

                            tournamentPlayerService.addTournamentPlayerToTournament(tournamentPlayer, tournament);

                            if (tournament.getOnlineUUID() != null && (((BaseActivity) getActivity()).isConnected())) {
                                // do it online!
                                tournamentPlayerService.setTournamentPlayerToFirebase(tournamentPlayer, tournament);
                            } else {
                                if (mListener != null) {
                                    mListener.addTournamentPlayer(tournamentPlayer);
                                }
                            }

                            if (mListener != null) {
                                mListener.removeAvailablePlayer(player);
                            }

                            Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.success_new_player_inserted,
                                    Snackbar.LENGTH_LONG);

                            snackbar.getView()
                            .setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));

                            snackbar.show();
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
