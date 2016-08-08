package madson.org.opentournament.tournament;

import android.app.Dialog;

import android.content.DialogInterface;

import android.os.Bundle;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentPairing;
import madson.org.opentournament.service.OngoingTournamentService;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class EnterResultForPairingDialog extends DialogFragment {

    public static final String BUNDLE_PAIRING_ID = "pairing_id";
    private long pairing_id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            pairing_id = bundle.getLong(BUNDLE_PAIRING_ID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        OngoingTournamentService ongoingTournamentService = ((OpenTournamentApplication) getActivity()
                .getApplication()).getOngoingTournamentService();
        WarmachineTournamentPairing pairing = ongoingTournamentService.getPairingForTournament(pairing_id);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_enter_result_pairing, null);

        builder.setView(dialogView)
            .setPositiveButton(R.string.confirm, null)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        EnterResultForPairingDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
