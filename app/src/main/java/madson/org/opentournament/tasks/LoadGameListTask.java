package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.view.View;

import android.widget.Button;
import android.widget.TextView;

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
    private TextView noGamesInfo;
    private Button nextRoundButton;
    private Button endTournamentButton;
    private List<Game> gamesForRound;

    public LoadGameListTask(BaseApplication baseApplication, Tournament tournament, int round,
        GameListAdapter gameListAdapter, TextView noGamesInfo, Button nextRoundButton, Button endTournamentButton) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.round = round;
        this.gameListAdapter = gameListAdapter;
        this.noGamesInfo = noGamesInfo;
        this.nextRoundButton = nextRoundButton;
        this.endTournamentButton = endTournamentButton;
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

        gameListAdapter.setGamesForRound(gamesForRound, round);

        if (tournament.getActualRound() == round) {
            if (gamesForRound.isEmpty()) {
                noGamesInfo.setVisibility(View.VISIBLE);
                endTournamentButton.setVisibility(View.GONE);
                nextRoundButton.setVisibility(View.GONE);
            } else {
                noGamesInfo.setVisibility(View.GONE);
                endTournamentButton.setVisibility(View.VISIBLE);
                nextRoundButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
