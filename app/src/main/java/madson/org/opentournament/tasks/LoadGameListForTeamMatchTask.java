package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.team.TeamGameListAdapter;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadGameListForTeamMatchTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private Tournament tournament;
    private Game game;
    private TeamGameListAdapter gameListAdapter;
    private List<Game> gamesForRound;

    public LoadGameListForTeamMatchTask(BaseApplication baseApplication, Tournament tournament, Game parent_game,
        TeamGameListAdapter gameListAdapter) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.game = parent_game;
        this.gameListAdapter = gameListAdapter;
    }

    @Override
    protected Void doInBackground(Void... params) {

        OngoingTournamentService ongoingTournamentService = baseApplication.getOngoingTournamentService();

        gamesForRound = ongoingTournamentService.getGamesForTeamMatch(tournament, game);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        gameListAdapter.setGames(gamesForRound);
    }
}
