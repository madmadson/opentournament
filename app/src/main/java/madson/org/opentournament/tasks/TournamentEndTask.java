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
import madson.org.opentournament.organize.TournamentOrganizeActivity;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
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
    private BaseActivity activity;

    public TournamentEndTask(BaseApplication application, Tournament tournament, ProgressBar progressBar,
        BaseActivity activity) {

        this.application = application;
        this.tournament = tournament;
        this.progressBar = progressBar;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {

        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected Void doInBackground(Void... params) {

        TournamentService tournamentService = application.getTournamentService();

        tournamentService.endTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        progressBar.setVisibility(View.GONE);

        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
}
