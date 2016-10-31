package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.view.View;

import android.widget.ProgressBar;

import madson.org.opentournament.domain.Game;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.PairingChangedEvent;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class DeleteGamesTask extends AsyncTask<Void, Void, Void> {

    private Game game1;
    private Game game2;

    private BaseApplication application;

    public DeleteGamesTask(BaseApplication application, Game game1, Game game2) {

        this.application = application;

        this.game1 = game1;
        this.game2 = game2;
    }

    @Override
    protected Void doInBackground(Void... params) {

        OngoingTournamentService ongoingTournamentService = application.getOngoingTournamentService();

        ongoingTournamentService.deleteGamesForTeamMatch(game1);
        ongoingTournamentService.deleteGamesForTeamMatch(game2);

        return null;
    }
}
