package madson.org.opentournament.tournament;

import android.app.Dialog;

import android.content.DialogInterface;

import android.os.Bundle;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.service.OngoingTournamentService;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class ConfirmPairingNewRoundDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    public static final String BUNDLE_ROUND_TO_DISPLAY = "round_for_pairing";

    private long tournament_id;
    private int round_for_pairing;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament_id = bundle.getLong(BUNDLE_TOURNAMENT_ID);
            round_for_pairing = bundle.getInt(BUNDLE_ROUND_TO_DISPLAY);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm_pairing, null);

        builder.setView(dialogView)
            .setTitle(getString(R.string.confirm_pairing_title, round_for_pairing))
            .setPositiveButton(R.string.confirm, null)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ConfirmPairingNewRoundDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }


    @Override
    public void onStart() {

        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            viewConfirmButton(dialog);
        }
    }


    private void viewConfirmButton(final AlertDialog dialog) {

        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "confirmed pairing");

                    OngoingTournamentActivity activity = (OngoingTournamentActivity) getActivity();

                    OngoingTournamentService ongoingTournamentService =
                        ((OpenTournamentApplication) getActivity().getApplication()).getOngoingTournamentService();

                    ongoingTournamentService.createPairingForRound(tournament_id, round_for_pairing);
                    ongoingTournamentService.createRankingForRound(tournament_id, round_for_pairing);

                    activity.addRoundAfterNewPairing();

                    dialog.dismiss();
                }
            });
    }
}