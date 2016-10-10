package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.setup.TournamentPlayerListAdapter;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class AddDummyPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private Tournament tournament;
    private TournamentPlayerListAdapter tournamentPlayerListAdapter;
    private TournamentPlayer dummyTournamentPlayer;

    public AddDummyPlayerTask(BaseApplication baseApplication, Tournament tournament,
        TournamentPlayerListAdapter tournamentPlayerListAdapter) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.tournamentPlayerListAdapter = tournamentPlayerListAdapter;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TournamentPlayerService tournamentPlayerService = baseApplication.getTournamentPlayerService();

        dummyTournamentPlayer = new TournamentPlayer();
        dummyTournamentPlayer.setFirstName("Dummy");
        dummyTournamentPlayer.setNickName("THE HAMMER");
        dummyTournamentPlayer.setLastName("Player");
        dummyTournamentPlayer.setDummy(true);
        dummyTournamentPlayer.setLocal(true);

        tournamentPlayerService.addTournamentPlayerToTournament(dummyTournamentPlayer, tournament);

        baseApplication.getTournamentService().increaseActualPlayerForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        tournamentPlayerListAdapter.addTournamentPlayer(dummyTournamentPlayer);
    }
}
