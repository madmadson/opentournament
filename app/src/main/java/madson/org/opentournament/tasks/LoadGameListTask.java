package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.GameListAdapter;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadGameListTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private Tournament tournament;
    private int round;
    private GameListAdapter gameListAdapter;
    private List<Game> gamesForRound;

    public LoadGameListTask(BaseApplication baseApplication, Tournament tournament, int round,
        GameListAdapter gameListAdapter) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.round = round;
        this.gameListAdapter = gameListAdapter;
    }

    @Override
    protected Void doInBackground(Void... params) {

        OngoingTournamentService ongoingTournamentService = baseApplication.getOngoingTournamentService();

        gamesForRound = ongoingTournamentService.getGamesForRound(tournament, round);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        gameListAdapter.setGames(gamesForRound);
    }
}
