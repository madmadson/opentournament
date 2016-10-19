package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.organize.RankingListFragment;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadRankingListTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private Tournament tournament;
    private int round;
    private RankingListFragment.RankingListAdapter rankingListAdapter;
    private List<TournamentRanking> rankingList;

    public LoadRankingListTask(BaseApplication baseApplication, Tournament tournament, int round,
        RankingListFragment.RankingListAdapter rankingListAdapter) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.round = round;
        this.rankingListAdapter = rankingListAdapter;
    }

    @Override
    protected Void doInBackground(Void... params) {

        RankingService rankingService = baseApplication.getRankingService();

        rankingList = rankingService.getTournamentRankingForRound(tournament, round);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        rankingListAdapter.setRankings(rankingList);
    }
}
