package madson.org.opentournament.organize.setup;

import android.app.Dialog;

import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.TournamentOrganizeActivity;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;


/**
 * dialog that tournament organisator start torunament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class ConfirmStartTournamentDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private Tournament tournament;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm_start_tournament, null);

        builder.setView(dialogView)
            .setTitle(R.string.confirm_start_tournament_title)
            .setPositiveButton(R.string.dialog_confirm, null)
            .setNeutralButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ConfirmStartTournamentDialog.this.getDialog().cancel();
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

                            TournamentOrganizeActivity activity = (TournamentOrganizeActivity) getActivity();

                            OngoingTournamentService ongoingTournamentService =
                                ((BaseApplication) getActivity().getApplication()).getOngoingTournamentService();

                            RankingService rankingService = ((BaseApplication) getActivity().getApplication())
                                .getRankingService();

                            TournamentService tournamentService = ((BaseApplication) getActivity().getApplication())
                                .getTournamentService();

                            // first create ranking for complete games
                            rankingService.createRankingForRound(tournament, 1);

                            // now we can create pairings for new round
                            ongoingTournamentService.createGamesForRound(tournament, 1);

                            // last thing update tournament
                            Tournament tournament = tournamentService.updateActualRound(
                                    ConfirmStartTournamentDialog.this.tournament, 1);

                            // update activity
                            activity.setTournamentToTabView(tournament);

                            dialog.dismiss();
                        }
                    };
                    runnable.run();
                }
            });
    }
}
