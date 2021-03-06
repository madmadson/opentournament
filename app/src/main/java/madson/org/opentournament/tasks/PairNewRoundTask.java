package madson.org.opentournament.tasks;

import android.graphics.Color;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v4.content.ContextCompat;

import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.PairRoundAgainEvent;
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
public class PairNewRoundTask extends AsyncTask<Void, Void, Boolean> {

    private BaseApplication application;
    private Tournament tournament;
    private Snackbar snackbar;
    private ProgressBar progressBar;
    private boolean pairAgain;
    private Map<String, PairingOption> pairingOptions;

    public PairNewRoundTask(BaseApplication application, Tournament tournament, Snackbar snackbar,
        ProgressBar progressBar, boolean pairAgain, Map<String, PairingOption> pairingOptions) {

        this.application = application;
        this.tournament = tournament;
        this.snackbar = snackbar;
        this.progressBar = progressBar;
        this.pairAgain = pairAgain;
        this.pairingOptions = pairingOptions;
    }

    @Override
    protected void onPreExecute() {

        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected Boolean doInBackground(Void... params) {

        RankingService rankingService = application.getRankingService();
        OngoingTournamentService ongoingTournamentService = application.getOngoingTournamentService();

        boolean success;

        if (pairAgain) {
            rankingService.deleteRankingForRound(tournament, tournament.getActualRound());
            ongoingTournamentService.deleteGamesForRound(tournament, tournament.getActualRound());

            Map<String, TournamentRanking> rankingForRound = rankingService.createRankingForRound(tournament,
                    tournament.getActualRound());

            success = ongoingTournamentService.createGamesForRound(tournament, tournament.getActualRound(),
                    rankingForRound, pairingOptions);
        } else {
            int next_round = tournament.getActualRound() + 1;
            Map<String, TournamentRanking> rankingForRound = rankingService.createRankingForRound(tournament,
                    next_round);

            success = ongoingTournamentService.createGamesForRound(tournament, next_round, rankingForRound,
                    pairingOptions);

            TournamentService tournamentService = application.getTournamentService();

            tournamentService.updateActualRound(tournament, tournament.getActualRound() + 1);
        }

        return success;
    }


    @Override
    protected void onPostExecute(Boolean successful) {

        progressBar.setVisibility(View.GONE);

        if (pairAgain) {
            application.notifyTournamentEvent(OpenTournamentEventTag.PAIR_ROUND_AGAIN,
                new PairRoundAgainEvent(tournament.getActualRound()));
        } else {
            application.notifyTournamentEvent(OpenTournamentEventTag.NEXT_ROUND_PAIRED, null);
        }

        if (successful) {
            snackbar.setText(R.string.successfully_pair_round_again);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(application, R.color.colorAccent));
        } else {
            snackbar.setText(R.string.no_games_created);

            TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(application, R.color.colorAlmostBlack));
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(application, R.color.colorAccent));
        }

        snackbar.setDuration(Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
