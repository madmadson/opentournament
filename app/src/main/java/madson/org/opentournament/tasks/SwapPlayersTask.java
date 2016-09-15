package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.view.View;

import android.widget.ProgressBar;

import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SwapPlayersTask extends AsyncTask<Void, Void, Void> {

    private Game game1;
    private Game game2;

    private ProgressBar progressBar;
    private Snackbar snackbar;
    private BaseApplication application;

    public SwapPlayersTask(BaseApplication application, Game game1, Game game2, ProgressBar progressBar,
        Snackbar snackbar) {

        this.application = application;

        this.game1 = game1;
        this.game2 = game2;

        this.progressBar = progressBar;
        this.snackbar = snackbar;
    }

    @Override
    protected void onPreExecute() {

        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected Void doInBackground(Void... params) {

        OngoingTournamentService ongoingTournamentService = application.getOngoingTournamentService();

        game1 = ongoingTournamentService.saveGameResult(game1);
        game2 = ongoingTournamentService.saveGameResult(game2);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        progressBar.setVisibility(View.GONE);

        application.notifyPairingChanged(game1, game2);

        snackbar.show();
    }
}
