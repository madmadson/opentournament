package madson.org.opentournament.db.warmachine;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachineTournamentPairingTable {

    /*
     * 0: id
     * 1: tournament_id
     * 2: round
     * 3: player_one_id
     * 4 :player_one_full_name
     * 5: player_one_score
     * 6: player_one_sos
     * 7: player_one_control_points
     * 8: player_one_victory_points
     * 9: player_two_id
     * 10: player_two_full_name
     * 11: player_two_score
     * 12: player_two_sos
     * 13: player_two_control_points
     * 14: player_two_victory_points
     */
    public static final String[] ALL_COLS_FOR_TOURNAMENT_PAIRING = {
        "_id", "tournament_id", "round", "player_one_id", "player_one_full_name", "player_one_score", "player_one_sos",
        "player_one_control_points", "player_one_victory_points", "player_two_id", "player_two_full_name",
        "player_two_score", "player_two_sos", "player_two_control_points", "player_two_victory_points"
    };

    public static final String TABLE_TOURNAMENT_PAIRING = "warmachine_tournament_pairing";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_ROUND = "round";

    public static final String COLUMN_PLAYER_ONE_ID = "player_one_id";
    public static final String COLUMN_PLAYER_ONE_FULL_NAME = "player_one_full_name";
    public static final String COLUMN_PLAYER_ONE_SCORE = "player_one_score";
    public static final String COLUMN_PLAYER_ONE_STRENGTH_OF_SCHEDULE = "player_one_sos";
    public static final String COLUMN_PLAYER_ONE_CONTROL_POINTS = "player_one_control_points";
    public static final String COLUMN_PLAYER_ONE_VICTORY_POINTS = "player_one_victory_points";

    public static final String COLUMN_PLAYER_TWO_ID = "player_two_id";
    public static final String COLUMN_PLAYER_TWO_FULL_NAME = "player_two_full_name";
    public static final String COLUMN_PLAYER_TWO_SCORE = "player_two_score";
    public static final String COLUMN_PLAYER_TWO_STRENGTH_OF_SCHEDULE = "player_two_sos";
    public static final String COLUMN_PLAYER_TWO_CONTROL_POINTS = "player_two_control_points";
    public static final String COLUMN_PLAYER_TWO_VICTORY_POINTS = "player_two_victory_points";

    public static void createTable(SQLiteDatabase db) {

        Log.i(WarmachineTournamentPairingTable.class.getName(), "create warmachine_tournament_pairing table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_PAIRING
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TOURNAMENT_ID + " INTEGER, "
            + COLUMN_ROUND + " INTEGER, " + COLUMN_PLAYER_ONE_ID + " INTEGER, " + COLUMN_PLAYER_ONE_FULL_NAME
            + " STRING, " + COLUMN_PLAYER_TWO_ID + " INTEGER, " + COLUMN_PLAYER_TWO_FULL_NAME + " STRING, "
            + COLUMN_PLAYER_ONE_SCORE + " INTEGER, " + COLUMN_PLAYER_ONE_STRENGTH_OF_SCHEDULE + " INTEGER, "
            + COLUMN_PLAYER_ONE_CONTROL_POINTS + " INTEGER, " + COLUMN_PLAYER_ONE_VICTORY_POINTS + " INTEGER, "
            + COLUMN_PLAYER_TWO_SCORE + " INTEGER, " + COLUMN_PLAYER_TWO_STRENGTH_OF_SCHEDULE + " INTEGER, "
            + COLUMN_PLAYER_TWO_CONTROL_POINTS + " INTEGER, " + COLUMN_PLAYER_TWO_VICTORY_POINTS + " INTEGER)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(WarmachineTournamentPlayerTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_PAIRING);
        createTable(db);
    }
}
