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
    private static final int DB_VERSION = 1;

    TournamentTable tournamentTable;
    PlayerTable playerTable;
    TournamentPlayerTable tournamentPlayerTable;

    public OpenTournamentDBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

        tournamentTable = new TournamentTable();
        playerTable = new PlayerTable();
        tournamentPlayerTable = new TournamentPlayerTable();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        tournamentTable.createTable(db);
        playerTable.createTable(db);
        tournamentPlayerTable.createTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
