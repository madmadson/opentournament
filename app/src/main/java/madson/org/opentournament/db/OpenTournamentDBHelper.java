package madson.org.opentournament.db;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import madson.org.opentournament.db.warmachine.WarmachineTournamentGameTable;


/**
 * Management for opentournament database.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OpenTournamentDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "opentournament.db";
    private static final int DB_VERSION = 1;

    public OpenTournamentDBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        TournamentTable.createTable(db);
        PlayerTable.createTable(db);
        TournamentPlayerTable.createTable(db);
        WarmachineTournamentGameTable.createTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
