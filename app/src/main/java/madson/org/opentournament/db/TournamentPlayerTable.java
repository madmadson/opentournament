package madson.org.opentournament.db;

import android.content.ContentValues;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Database stuff for tournaments.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerTable {

    public static final String TABLE_TOURNAMENT_PLAYER = "tournament_player";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_PLAYER_ID = "player_id";

    public static void createTable(SQLiteDatabase db) {

        Log.i(TournamentPlayerTable.class.getName(), "create tournament_player table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_PLAYER
            + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TOURNAMENT_ID
            + " INTEGER, " + COLUMN_PLAYER_ID + " INTEGER)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TournamentPlayerTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_PLAYER);
        createTable(db);
    }
}
