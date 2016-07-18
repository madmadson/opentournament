package madson.org.opentournament;

import android.app.Application;

import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.service.TournamentServiceImpl;


/**
 * Holds all available services.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OpenTournamentApplication extends Application {

    private static OpenTournamentDatabaseHelper dbHelper;
    private static TournamentService tournamentService;

    public OpenTournamentDatabaseHelper getDBHelper() {

        if (dbHelper == null) {
            dbHelper = new OpenTournamentDatabaseHelper(getApplicationContext());
        }

        return dbHelper;
    }


    public TournamentService getTournamentService() {

        if (tournamentService == null) {
            tournamentService = new TournamentServiceImpl(getDBHelper());
        }

        return tournamentService;
    }
}
