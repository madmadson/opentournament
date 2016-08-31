package madson.org.opentournament.players;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Build;
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

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.ongoing.TournamentSetupFragment;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Dialog for adding new local players to tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class AddLocalPlayerToTournamentDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    private CoordinatorLayout coordinatorLayout;
    private TournamentPlayerAddListener mListener;

    private EditText firstnameEditText;
    private EditText nicknameEditText;
    private EditText lastnameEditText;

    private Tournament tournament;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinator_main);

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (getTargetFragment() instanceof TournamentSetupFragment) {
            mListener = (TournamentSetupFragment) getTargetFragment();
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_new_player, null);

            firstnameEditText = (EditText) dialogView.findViewById(R.id.new_player_firstname);
            nicknameEditText = (EditText) dialogView.findViewById(R.id.new_player_nickname);
            lastnameEditText = (EditText) dialogView.findViewById(R.id.new_player_lastname);

            TournamentService tournamentService = ((BaseApplication) getActivity().getApplication())
                .getTournamentService();
            Tournament tournamentForId = tournamentService.getTournamentForId(tournament.get_id());

            builder.setView(dialogView)
                .setTitle(getString(R.string.new_player_tournament_title, tournamentForId.getName()))
                .setPositiveButton(R.string.dialog_save, null)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            AddLocalPlayerToTournamentDialog.this.getDialog().cancel();
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

                        Log.i(this.getClass().getName(), "add new player");

                        String firstname = firstnameEditText.getText().toString();
                        String nickname = nicknameEditText.getText().toString();
                        String lastname = lastnameEditText.getText().toString();

                        if (!firstname.isEmpty() && !nickname.isEmpty() && !lastname.isEmpty()) {
                            Player player = new Player(firstname, nickname, lastname);

                            PlayerService playerService = ((BaseApplication) getActivity().getApplication())
                                .getPlayerService();
                            playerService.createPlayer(player);

                            OngoingTournamentService ongoingTournamentService =
                                ((BaseApplication) getActivity().getApplication()).getOngoingTournamentService();

                            ongoingTournamentService.addPlayerToTournament(player, tournament.get_id());

                            Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.success_new_player_inserted,
                                    Snackbar.LENGTH_LONG);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                snackbar.getView()
                                .setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent, null));
                            } else {
                                snackbar.getView()
                                .setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
                            }

                            snackbar.show();

                            if (mListener != null) {
                                // mListener.addTournamentPlayer();
                            }

                            dialog.dismiss();
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

    public interface TournamentPlayerAddListener {

        void addTournamentPlayer(TournamentPlayer tournamentPlayer);
    }
}
