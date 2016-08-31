package madson.org.opentournament.db;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Database for player in tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerTable {

    public static final String[] ALL_COLS_FOR_TOURNAMENT_PLAYER_TABLE = {
        "_id", "tournament_id", "player_id", "round", "score", "sos", "control_points", "victory_points",
        "player_online_uuid"
    };

    public static final String TABLE_TOURNAMENT_PLAYER = "tournament_player";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_PLAYER_ONLINE_UUID = "player_online_uuid";

    public static final String COLUMN_ROUND = "round";

    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_SOS = "sos";
    public static final String COLUMN_CONTROL_POINTS = "control_points";
    public static final String COLUMN_VICTORY_POINTS = "victory_points";

    public static void createTable(SQLiteDatabase db) {

        Log.i(TournamentPlayerTable.class.getName(), "create tournament_player table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_PLAYER
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TOURNAMENT_ID + " INTEGER,"
            + COLUMN_PLAYER_ID + " INTEGER, " + COLUMN_PLAYER_ONLINE_UUID
            + " TEXT, " + COLUMN_ROUND + " INTEGER, " + COLUMN_SCORE + " INTEGER, " + COLUMN_SOS + " INTEGER, "
            + COLUMN_CONTROL_POINTS + " INTEGER, " + COLUMN_VICTORY_POINTS + " INTEGER )");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TournamentPlayerTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_PLAYER);
        createTable(db);
    }
}
