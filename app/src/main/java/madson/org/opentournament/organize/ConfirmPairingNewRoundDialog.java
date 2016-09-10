package madson.org.opentournament.organize;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class ConfirmPairingNewRoundDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND_TO_DISPLAY = "round_for_pairing";

    private Tournament tournament;
    private int round_for_pairing;
    private List<PairingOption> pairingOptions;

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

        for (int i = 0; i < pairingOptions.size(); i++) {
            TextView textView = new TextView(getActivity());

            String pairingOptionName = pairingOptions.get(i).getPairingOptionName();
            textView.setText(getStringResourceByName(pairingOptionName));
            textView.setLayoutParams(params);
            container.addView(textView);

            final ToggleButton toggleButton = new ToggleButton(getActivity());
            toggleButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (toggleButton.isChecked()) {
                            pairingOptions.get(v.getId()).setActive(true);
                        } else {
                            pairingOptions.get(v.getId()).setActive(false);
                        }
                    }
                });
            toggleButton.setId(i);
            toggleButton.setChecked(pairingOptions.get(i).isDefaultPairing());
            toggleButton.setLayoutParams(params);
            container.addView(toggleButton);
        }

        String title;

        if (round_for_pairing == 1) {
            title = getString(R.string.confirm_start_tournament_title);
        } else {
            title = getString(R.string.confirm_pairing_title, round_for_pairing);
        }

        builder.setView(dialogView)
            .setTitle(title)
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

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

                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {

                            Log.i(this.getClass().getName(), "confirmed pairing for round " + round_for_pairing);

                            TournamentOrganizeActivity activity = (TournamentOrganizeActivity) getActivity();

                            OngoingTournamentService ongoingTournamentService =
                                ((BaseApplication) getActivity().getApplication()).getOngoingTournamentService();

                            RankingService rankingService = ((BaseApplication) getActivity().getApplication())
                                .getRankingService();

                            TournamentService tournamentService = ((BaseApplication) getActivity().getApplication())
                                .getTournamentService();

                            // first create ranking for complete games
                            Map<String, TournamentRanking> rankingForRound = rankingService.createRankingForRound(
                                    tournament, round_for_pairing);

                            // now we can create pairings for new round
                            ongoingTournamentService.createGamesForRound(tournament, round_for_pairing, rankingForRound,
                                pairingOptions);

                            // last thing update tournament
                            Tournament updatedTournament = tournamentService.updateActualRound(tournament,
                                    round_for_pairing);

                            activity.setTournamentToTabView(updatedTournament);

                            // set visibility of start button
                            TournamentEventListener tournamentEventListener = activity.getBaseApplication()
                                .getTournamentEventListener();

                            tournamentEventListener.startRound(round_for_pairing);

                            dialog.dismiss();
                        }
                    };
                    runnable.run();
                }
            });
    }
}
