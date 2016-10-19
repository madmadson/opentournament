package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.util.Log;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.RemoveTournamentPlayerEvent;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RemoveTournamentPlayerFromTournamentTask extends AsyncTask<Void, Void, Void> {

    private Tournament tournament;
    private TournamentPlayer player;

    private BaseActivity baseActivity;

    public RemoveTournamentPlayerFromTournamentTask(BaseActivity baseActivity, Tournament tournament,
        TournamentPlayer player) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
        this.player = player;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.i(this.getClass().getName(), "remove player from tournament ");

        baseActivity.getBaseApplication().getTournamentPlayerService().removePlayerFromTournament(player);

        baseActivity.getBaseApplication().getTournamentService().decreaseActualPlayerForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.REMOVE_TOURNAMENT_PLAYER,
                new RemoveTournamentPlayerEvent(player));

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_remove_player,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorPositive));
        snackbar.show();
    }
}
