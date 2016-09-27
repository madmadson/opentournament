package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.util.Log;

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
public class DropTournamentPlayerFromTournamentTask extends AsyncTask<Void, Void, Void> {

    private Tournament tournament;
    private TournamentPlayer player;
    private TournamentPlayerListFragment tournamentPlayerListFragment;
    private BaseApplication baseApplication;

    private TournamentPlayer updatedPlayer;

    public DropTournamentPlayerFromTournamentTask(BaseApplication baseApplication, Tournament tournament,
        TournamentPlayer player, TournamentPlayerListFragment tournamentPlayerListFragment) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.player = player;
        this.tournamentPlayerListFragment = tournamentPlayerListFragment;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.i(this.getClass().getName(), "drop player from tournament ");

        updatedPlayer = baseApplication.getTournamentPlayerService().dropPlayerFromTournament(player, tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        tournamentPlayerListFragment.updatePlayer(updatedPlayer);
    }
}
