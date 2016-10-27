package madson.org.opentournament.db;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Management for opentournament database.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OpenTournamentDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "opentournament.db";
    public static final int DB_VERSION = 7;

    public OpenTournamentDBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        TournamentTable.createTable(db);
        PlayerTable.createTable(db);
        TournamentPlayerTable.createTable(db);
        TournamentRankingTable.createTable(db);
        GameTable.createTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        TournamentTable.onUpgrade(db, oldVersion, newVersion);
        PlayerTable.onUpgrade(db, oldVersion, newVersion);
        TournamentPlayerTable.onUpgrade(db, oldVersion, newVersion);
        TournamentRankingTable.onUpgrade(db, oldVersion, newVersion);
        GameTable.onUpgrade(db, oldVersion, newVersion);
    }
}
