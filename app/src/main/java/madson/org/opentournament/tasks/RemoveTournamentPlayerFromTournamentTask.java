package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.util.Log;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.setup.AvailablePlayerListFragment;
import madson.org.opentournament.organize.setup.TournamentPlayerListFragment;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RemoveTournamentPlayerFromTournamentTask extends AsyncTask<Void, Void, Void> {

    private Tournament tournament;
    private TournamentPlayer player;
    private TournamentPlayerListFragment tournamentPlayerListFragment;
    private AvailablePlayerListFragment availablePlayerListFragment;
    private BaseApplication baseApplication;

    public RemoveTournamentPlayerFromTournamentTask(BaseApplication baseApplication, Tournament tournament,
        TournamentPlayer player, TournamentPlayerListFragment tournamentPlayerListFragment,
        AvailablePlayerListFragment availablePlayerListFragment) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.player = player;
        this.tournamentPlayerListFragment = tournamentPlayerListFragment;
        this.availablePlayerListFragment = availablePlayerListFragment;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.i(this.getClass().getName(), "remove player from tournament ");

        baseApplication.getTournamentPlayerService().removePlayerFromTournament(player, tournament);

        baseApplication.getTournamentService().decreaseActualPlayerForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        tournamentPlayerListFragment.removePlayer(player);

        // dummy player are not persistent
        if (!player.isDummy() && availablePlayerListFragment != null) {
            availablePlayerListFragment.addPlayer(player);
        }
    }
}
