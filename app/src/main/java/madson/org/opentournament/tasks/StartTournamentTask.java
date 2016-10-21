package madson.org.opentournament.tasks;

import android.graphics.Color;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;

import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class StartTournamentTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;

    private Tournament tournament;

    private Map<String, PairingOption> pairingOptions;
    private boolean successful;

    public StartTournamentTask(BaseActivity baseActivity, Tournament tournament,
        Map<String, PairingOption> pairingOptions) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;

        this.pairingOptions = pairingOptions;
    }

    @Override
    protected Void doInBackground(Void... params) {

        RankingService rankingService = baseActivity.getBaseApplication().getRankingService();
        OngoingTournamentService ongoingTournamentService = baseActivity.getBaseApplication()
                .getOngoingTournamentService();
        TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();

        Map<String, TournamentRanking> rankingForRound = rankingService.createRankingForRound(tournament, 1);

        successful = ongoingTournamentService.createGamesForRound(tournament, 1, rankingForRound, pairingOptions);
        tournamentService.updateActualRound(tournament, 1);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        baseActivity.getBaseApplication().notifyTournamentEvent(OpenTournamentEventTag.TOURNAMENT_STARTED, null);

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.empty, Snackbar.LENGTH_LONG);

        if (successful) {
            snackbar.setText(R.string.successfully_start_tournament);
            snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));
        } else {
            snackbar.setText(R.string.no_games_created);

            TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.BLACK);
            snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorNeutral));
        }

        snackbar.show();
    }
}
