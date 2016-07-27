package madson.org.opentournament;

import android.app.Application;

import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.OngoingTournamentServiceImpl;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.service.PlayerServiceImpl;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.service.TournamentServiceImpl;

import net.danlew.android.joda.JodaTimeAndroid;


/**
 * Holds all available services.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OpenTournamentApplication extends Application {

    private static OngoingTournamentService ongoingTournamentService;
    private static TournamentService tournamentService;
    private static PlayerService playerService;

    @Override
    public void onCreate() {

        super.onCreate();
        JodaTimeAndroid.init(this);

        if (playerService == null) {
            playerService = new PlayerServiceImpl(getApplicationContext());
        }

        if (tournamentService == null) {
            tournamentService = new TournamentServiceImpl(getApplicationContext());
        }

        if (ongoingTournamentService == null) {
            ongoingTournamentService = new OngoingTournamentServiceImpl(getApplicationContext());
        }
    }


    public TournamentService getTournamentService() {

        return tournamentService;
    }


    public PlayerService getPlayerService() {

        return playerService;
    }


    public OngoingTournamentService getOngoingTournamentService() {

        return ongoingTournamentService;
    }
}
