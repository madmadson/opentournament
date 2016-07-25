package madson.org.opentournament.db;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import static java.util.Calendar.getInstance;


/**
 * Database stuff for tournaments.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentTable {

    public static final String TABLE_TOURNAMENTS = "tournament";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NUMBER_OF_PLAYERS = "numberOfPlayers";

    public void createTable(SQLiteDatabase db) {

        Log.i(TournamentTable.class.getName(), "cretae tournament table");
        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENTS
            + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
            + " TEXT, " + COLUMN_DESCRIPTION + " TEXT, " + COLUMN_DATE + " NUMERIC, " + COLUMN_NUMBER_OF_PLAYERS
            + " NUMERIC)");
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TournamentTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENTS);
        createTable(db);
    }
}
