package madson.org.opentournament.tasks;

import android.app.Activity;

import android.content.Intent;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.view.View;

import android.widget.ProgressBar;

import madson.org.opentournament.MainActivity;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.organize.TournamentOrganizeActivity;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.utility.TournamentEventTag;

import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentEndTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication application;
    private Tournament tournament;
    private ProgressBar progressBar;

    private int next_round;

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

        application.notifyTournamentEvent(TournamentEventTag.NEXT_ROUND_PAIRED);
    }
}
