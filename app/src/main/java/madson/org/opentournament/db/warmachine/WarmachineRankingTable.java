package madson.org.opentournament.db.warmachine;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Database stuff for player in tou.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachineRankingTable {

    public static final String[] ALL_COLS_FOR_RANKING_TABLE = {
        "_id", "tournament_id", "player_id", "round", "score", "sos", "control_points", "victory_points"
    };

    public static final String TABLE_WARMACHINE_RANKING = "warmachine_tournament_player_ranking";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_ROUND = "round";

    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_SOS = "sos";
    public static final String COLUMN_CONTROL_POINTS = "control_points";
    public static final String COLUMN_VICTORY_POINTS = "victory_points";

    public static void createTable(SQLiteDatabase db) {

        Log.i(WarmachineRankingTable.class.getName(), "create tournament_player table");

        db.execSQL(" CREATE TABLE " + TABLE_WARMACHINE_RANKING
            + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TOURNAMENT_ID
            + " INTEGER, " + COLUMN_PLAYER_ID + " INTEGER, " + COLUMN_ROUND
            + " INTEGER, " + COLUMN_SCORE + " INTEGER, " + COLUMN_SOS
            + " INTEGER, " + COLUMN_CONTROL_POINTS + " INTEGER, " + COLUMN_VICTORY_POINTS
            + " INTEGER )");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(WarmachineRankingTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WARMACHINE_RANKING);
        createTable(db);
    }
}
