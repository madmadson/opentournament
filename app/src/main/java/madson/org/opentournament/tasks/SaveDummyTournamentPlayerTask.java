package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.events.AddTournamentPlayerEvent;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.UUID;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveDummyTournamentPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private Tournament tournament;

    private TournamentPlayer dummyTournamentPlayer;

    public SaveDummyTournamentPlayerTask(BaseApplication baseApplication, Tournament tournament) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TournamentPlayerService tournamentPlayerService = baseApplication.getTournamentPlayerService();

        dummyTournamentPlayer = new TournamentPlayer();
        dummyTournamentPlayer.setFirstName("Dummy");
        dummyTournamentPlayer.setNickName("THE HAMMER");
        dummyTournamentPlayer.setLastName("Player");
        dummyTournamentPlayer.setPlayerUUID(UUID.randomUUID().toString());
        dummyTournamentPlayer.setDummy(true);
        dummyTournamentPlayer.setLocal(true);
        dummyTournamentPlayer.setTeamName("");
        dummyTournamentPlayer.setTournamentUUID(tournament.getUuid());

        tournamentPlayerService.addTournamentPlayerToTournament(dummyTournamentPlayer, tournament);

        baseApplication.getTournamentService().increaseActualPlayerForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseApplication.notifyTournamentEvent(OpenTournamentEventTag.ADD_TOURNAMENT_PLAYER,
            new AddTournamentPlayerEvent(dummyTournamentPlayer));
    }
}
