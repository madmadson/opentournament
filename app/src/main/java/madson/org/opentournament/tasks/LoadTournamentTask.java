package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.v4.view.ViewPager;

import android.view.View;

import android.widget.ProgressBar;

import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.GameListAdapter;
import madson.org.opentournament.organize.TournamentOrganizeActivity;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadTournamentTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private Tournament tournament;
    private TournamentOrganizeActivity.SectionsPagerAdapter adapter;
    private ViewPager pager;
    private ProgressBar progressBar;
    private Tournament actualTournament;

    public LoadTournamentTask(BaseApplication baseApplication, Tournament tournament,
        TournamentOrganizeActivity.SectionsPagerAdapter adapter, ViewPager pager, ProgressBar progressBar) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.adapter = adapter;
        this.pager = pager;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected Void doInBackground(Void... params) {

        TournamentService tournamentService = baseApplication.getTournamentService();
        actualTournament = tournamentService.getTournamentForId(tournament.get_id());

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
        adapter.setTournamentToOrganize(actualTournament);
        pager.setCurrentItem(actualTournament.getActualRound());
        progressBar.setVisibility(View.GONE);
    }
}
