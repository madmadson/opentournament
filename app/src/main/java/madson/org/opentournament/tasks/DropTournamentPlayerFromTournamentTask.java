package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.util.Log;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class DropTournamentPlayerFromTournamentTask extends AsyncTask<Void, Void, Void> {

    private Tournament tournament;
    private TournamentPlayer tournamentPlayer;

    private BaseApplication baseApplication;

    public DropTournamentPlayerFromTournamentTask(BaseApplication baseApplication, Tournament tournament,
        TournamentPlayer tournamentPlayer) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.tournamentPlayer = tournamentPlayer;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.i(this.getClass().getName(), "drop tournamentPlayer from tournament ");

        baseApplication.getTournamentPlayerService().dropPlayerFromTournament(tournamentPlayer, tournament);

        return null;
    }
}
