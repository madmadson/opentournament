package madson.org.opentournament.organize;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class ConfirmPairRoundAgainDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND_TO_DISPLAY = "round_for_pairing";

    private Tournament tournament;
    private int round_for_pairing;
    private Map<String, PairingOption> pairingOptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    private String getStringResourceByName(String aString) {

        Context context = getContext();
        int resId = context.getResources().getIdentifier(aString, "string", context.getPackageName());

        return getString(resId);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
            round_for_pairing = bundle.getInt(BUNDLE_ROUND_TO_DISPLAY);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm_pairing, null);

        LinearLayout container = (LinearLayout) dialogView.findViewById(R.id.pairing_options_container);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        pairingOptions = ((BaseApplication) getActivity().getApplication()).getPairingOptionsForTournament(tournament);

        for (final PairingOption option : pairingOptions.values()) {
            TextView textView = new TextView(getActivity());

            String pairingOptionName = option.getPairingOptionName();
            textView.setText(getStringResourceByName(pairingOptionName));
            textView.setLayoutParams(params);
            container.addView(textView);

            final ToggleButton toggleButton = new ToggleButton(getActivity());
            toggleButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (toggleButton.isChecked()) {
                            option.setActive(true);
                        } else {
                            option.setActive(false);
                        }
                    }
                });

            toggleButton.setChecked(option.isDefaultPairing());
            toggleButton.setLayoutParams(params);
            container.addView(toggleButton);
        }

        String title = getString(R.string.confirm_pair_round_again_title, round_for_pairing);

        builder.setView(dialogView)
            .setTitle(title)
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ConfirmPairRoundAgainDialog.this.getDialog().cancel();
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

                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {

                            Log.i(this.getClass().getName(), "pairround " + round_for_pairing + " again");

                            TournamentOrganizeActivity activity = (TournamentOrganizeActivity) getActivity();

                            OngoingTournamentService ongoingTournamentService =
                                ((BaseApplication) getActivity().getApplication()).getOngoingTournamentService();

                            RankingService rankingService = ((BaseApplication) getActivity().getApplication())
                                .getRankingService();

                            rankingService.deleteRankingForRound(tournament, round_for_pairing);
                            ongoingTournamentService.deleteGamesForRound(tournament, round_for_pairing);

                            // first create ranking for complete games
                            Map<String, TournamentRanking> rankingForRound = rankingService.createRankingForRound(
                                    tournament, round_for_pairing);

                            // now we can create pairings for new round
                            boolean success = ongoingTournamentService.createGamesForRound(tournament,
                                    round_for_pairing, rankingForRound, pairingOptions);

                            if (!success) {
                                Log.e(this.getClass().getName(),
                                    "pairing failed. Delete pairing, games for round and say something to user :) ");
                            }

                            TournamentEventListener tournamentEventListener = activity.getBaseApplication()
                                .getTournamentEventListener();

                            tournamentEventListener.pairRoundAgain(round_for_pairing);

                            dialog.dismiss();
                        }
                    };
                    runnable.run();
                }
            });
    }
}
