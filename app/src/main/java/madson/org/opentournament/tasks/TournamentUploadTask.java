package madson.org.opentournament.tasks;

import android.app.ActionBar;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import android.view.View;

import android.widget.ProgressBar;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentUploadTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication application;
    private Tournament tournament;
    private ProgressBar progressBar;

    public TournamentUploadTask(BaseApplication application, Tournament tournament, ProgressBar progressBar) {

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
        TournamentPlayerService tournamentPlayerService = application.getTournamentPlayerService();
        RankingService rankingService = application.getRankingService();
        OngoingTournamentService ongoingTournamentService = application.getOngoingTournamentService();

        Tournament uploadedTournament = tournamentService.uploadTournament(this.tournament);
        tournamentPlayerService.uploadTournamentPlayers(uploadedTournament);
        rankingService.uploadRankingsForTournament(uploadedTournament);
        ongoingTournamentService.uploadGames(uploadedTournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        progressBar.setVisibility(View.GONE);
    }
}
