package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.view.View;

import android.widget.ProgressBar;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentEndTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication application;
    private Tournament tournament;
    private ProgressBar progressBar;

    public TournamentEndTask(BaseApplication application, Tournament tournament, ProgressBar progressBar) {

        this.application = application;
        this.tournament = tournament;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {

        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected Void doInBackground(Void... params) {

        TournamentService tournamentService = application.getTournamentService();
        RankingService rankingService = application.getRankingService();

        rankingService.createRankingForRound(tournament, tournament.getActualRound() + 1);
        tournamentService.endTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        progressBar.setVisibility(View.GONE);

        application.notifyTournamentEvent(OpenTournamentEventTag.NEXT_ROUND_PAIRED, null);
    }
}
