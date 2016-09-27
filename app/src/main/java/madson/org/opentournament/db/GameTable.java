package madson.org.opentournament.db;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Represent a game between to two opponents.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameTable {

    /*
     * 0: id
     * 1: online_uuid
     * 2: tournament_id
     * 3: tournament_round
     * 4: player_one_id
     * 5: player_one_online_uuid
     * 6: player_one_score
     * 7: player_one_control_points
     * 8: player_one_victory_points
     * 9: player_two_id
     * 10: player_two_online_uuid
     * 11: player_two_score
     * 12: player_two_control_points
     * 13: player_two_victory_points
     * 14: game finished
     * 15: scenario
     * 16: playing_field
     */
    public static final String[] ALL_COLS_FOR_TOURNAMENT_GAME = {
        GameTable.COLUMN_ID, GameTable.COLUMN_ONLINE_UUID, GameTable.COLUMN_TOURNAMENT_ID,
        GameTable.COLUMN_TOURNAMENT_ROUND, GameTable.COLUMN_PLAYER_ONE_ID, GameTable.COLUMN_PLAYER_ONE_ONLINE_UUID,
        GameTable.COLUMN_PLAYER_ONE_SCORE, GameTable.COLUMN_PLAYER_ONE_CONTROL_POINTS,
        GameTable.COLUMN_PLAYER_ONE_VICTORY_POINTS, GameTable.COLUMN_PLAYER_TWO_ID,
        GameTable.COLUMN_PLAYER_TWO_ONLINE_UUID, GameTable.COLUMN_PLAYER_TWO_SCORE,
        GameTable.COLUMN_PLAYER_TWO_CONTROL_POINTS, GameTable.COLUMN_PLAYER_TWO_VICTORY_POINTS,
        GameTable.COLUMN_FINISHED, GameTable.COLUMN_SCENARIO, GameTable.COLUMN_PLAYING_FIELD
    };

    public static final String TABLE_TOURNAMENT_GAME = "game";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ONLINE_UUID = "onlineUUID";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_TOURNAMENT_ROUND = "tournament_round";

    public static final String COLUMN_PLAYER_ONE_ID = "player_one_id";
    public static final String COLUMN_PLAYER_ONE_ONLINE_UUID = "player_one_online_uuid";
    public static final String COLUMN_PLAYER_ONE_SCORE = "player_one_score";
    public static final String COLUMN_PLAYER_ONE_CONTROL_POINTS = "player_one_control_points";
    public static final String COLUMN_PLAYER_ONE_VICTORY_POINTS = "player_one_victory_points";

    public static final String COLUMN_PLAYER_TWO_ID = "player_two_id";
    public static final String COLUMN_PLAYER_TWO_ONLINE_UUID = "player_two_online_uuid";

    public static final String COLUMN_PLAYER_TWO_SCORE = "player_two_score";
    public static final String COLUMN_PLAYER_TWO_CONTROL_POINTS = "player_two_control_points";
    public static final String COLUMN_PLAYER_TWO_VICTORY_POINTS = "player_two_victory_points";

    public static final String COLUMN_FINISHED = "finished";
    public static final String COLUMN_SCENARIO = "scenario";
    public static final String COLUMN_PLAYING_FIELD = "playing_field";

    public static void createTable(SQLiteDatabase db) {

        Log.i(GameTable.class.getName(), "create warmachine_tournament_pairing table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_GAME
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ONLINE_UUID + " TEXT, "
            + COLUMN_TOURNAMENT_ID
            + " INTEGER, " + COLUMN_TOURNAMENT_ROUND + " INTEGER, " + COLUMN_PLAYER_ONE_ID + " INTEGER, "
            + COLUMN_PLAYER_ONE_ONLINE_UUID + " TEXT, " + COLUMN_PLAYER_ONE_SCORE + " INTEGER, "
            + COLUMN_PLAYER_ONE_CONTROL_POINTS + " INTEGER, " + COLUMN_PLAYER_ONE_VICTORY_POINTS + " INTEGER, "
            + COLUMN_PLAYER_TWO_ID
            + " INTEGER, " + COLUMN_PLAYER_TWO_ONLINE_UUID + " TEXT, " + COLUMN_PLAYER_TWO_SCORE + " INTEGER, "
            + COLUMN_PLAYER_TWO_CONTROL_POINTS + " INTEGER, " + COLUMN_PLAYER_TWO_VICTORY_POINTS + " INTEGER, "
            + COLUMN_FINISHED + " INTEGER, " + COLUMN_SCENARIO
            + " TEXT, " + COLUMN_PLAYING_FIELD + " INTEGER)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TournamentPlayerTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_GAME);
        createTable(db);
    }
}
