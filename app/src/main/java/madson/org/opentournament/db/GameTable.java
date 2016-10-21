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
     * 1: uuid
     * 2: parent_uuid
     * 3: tournament_uuid
     * 4: tournament_round
     * 5: participant_one__uuid
     * 6: PARTICIPANT_one_score
     * 7: PARTICIPANT_one_control_points
     * 8: PARTICIPANT_one_victory_points
     * 9: PARTICIPANT_one_army_list
     * 10: PARTICIPANT_two_uuid
     * 11: PARTICIPANT_two_score
     * 12: PARTICIPANT_two_control_points
     * 13: PARTICIPANT_two_victory_points
     * 14: PARTICIPANT_two_army_list
     * 15: game finished
     * 16: scenario
     * 17: playing_field
     * 18: participant_one_intermediate_score
     * 19: participant_two_intermediate_score
     */
    public static final String[] ALL_COLS_FOR_TOURNAMENT_GAME = {
        GameTable.COLUMN_ID, GameTable.COLUMN_UUID, GameTable.COLUMN_PARENT_UUID, GameTable.COLUMN_TOURNAMENT_UUID,
        GameTable.COLUMN_TOURNAMENT_ROUND, GameTable.COLUMN_PARTICIPANT_ONE_UUID,
        GameTable.COLUMN_PARTICIPANT_ONE_SCORE, GameTable.COLUMN_PARTICIPANT_ONE_CONTROL_POINTS,
        GameTable.COLUMN_PARTICIPANT_ONE_VICTORY_POINTS, GameTable.COLUMN_PARTICIPANT_ONE_ARMY_LIST,
        GameTable.COLUMN_PARTICIPANT_TWO_UUID, GameTable.COLUMN_PARTICIPANT_TWO_SCORE,
        GameTable.COLUMN_PARTICIPANT_TWO_CONTROL_POINTS, GameTable.COLUMN_PARTICIPANT_TWO_VICTORY_POINTS,
        GameTable.COLUMN_PARTICIPANT_TWO_ARMY_LIST, GameTable.COLUMN_FINISHED, GameTable.COLUMN_SCENARIO,
        GameTable.COLUMN_PLAYING_FIELD, GameTable.COLUMN_PARTICIPANT_ONE_INTERMEDIATE_POINTS,
        GameTable.COLUMN_PARTICIPANT_TWO_INTERMEDIATE_POINTS
    };

    public static final String TABLE_TOURNAMENT_GAME = "game";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_PARENT_UUID = "parent_uuid";
    public static final String COLUMN_TOURNAMENT_UUID = "tournament_uuid";
    public static final String COLUMN_TOURNAMENT_ROUND = "tournament_round";

    public static final String COLUMN_PARTICIPANT_ONE_UUID = "participant_one_uuid";

    public static final String COLUMN_PARTICIPANT_ONE_SCORE = "participant_one_score";
    public static final String COLUMN_PARTICIPANT_ONE_CONTROL_POINTS = "participant_one_control_points";
    public static final String COLUMN_PARTICIPANT_ONE_VICTORY_POINTS = "participant_one_victory_points";
    public static final String COLUMN_PARTICIPANT_ONE_ARMY_LIST = "participant_one_army_list";

    public static final String COLUMN_PARTICIPANT_TWO_UUID = "participant_two_uuid";

    public static final String COLUMN_PARTICIPANT_TWO_SCORE = "participant_two_score";
    public static final String COLUMN_PARTICIPANT_TWO_CONTROL_POINTS = "participant_two_control_points";
    public static final String COLUMN_PARTICIPANT_TWO_VICTORY_POINTS = "participant_two_victory_points";
    public static final String COLUMN_PARTICIPANT_TWO_ARMY_LIST = "participant_two_army_list";

    public static final String COLUMN_FINISHED = "finished";
    public static final String COLUMN_SCENARIO = "scenario";
    public static final String COLUMN_PLAYING_FIELD = "playing_field";

    public static final String COLUMN_PARTICIPANT_ONE_INTERMEDIATE_POINTS = "participant_one_intermediate_points";
    public static final String COLUMN_PARTICIPANT_TWO_INTERMEDIATE_POINTS = "participant_two_intermediate_points";

    public static void createTable(SQLiteDatabase db) {

        Log.i(GameTable.class.getName(), "create warmachine_tournament_pairing table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_GAME
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_UUID + " TEXT, " + COLUMN_PARENT_UUID
            + " TEXT, " + COLUMN_TOURNAMENT_UUID + " TEXT, " + COLUMN_TOURNAMENT_ROUND + " INTEGER, "
            + COLUMN_PARTICIPANT_ONE_UUID + " TEXT, " + COLUMN_PARTICIPANT_ONE_SCORE + " INTEGER, "
            + COLUMN_PARTICIPANT_ONE_CONTROL_POINTS + " INTEGER, " + COLUMN_PARTICIPANT_ONE_VICTORY_POINTS
            + " INTEGER, " + COLUMN_PARTICIPANT_ONE_ARMY_LIST + " TEXT, " + COLUMN_PARTICIPANT_TWO_UUID
            + " TEXT, " + COLUMN_PARTICIPANT_TWO_SCORE + " INTEGER, " + COLUMN_PARTICIPANT_TWO_CONTROL_POINTS
            + " INTEGER, " + COLUMN_PARTICIPANT_TWO_VICTORY_POINTS + " INTEGER, " + COLUMN_PARTICIPANT_TWO_ARMY_LIST
            + " TEXT, " + COLUMN_FINISHED + " INTEGER, " + COLUMN_SCENARIO
            + " TEXT, " + COLUMN_PLAYING_FIELD + " INTEGER, " + COLUMN_PARTICIPANT_ONE_INTERMEDIATE_POINTS
            + " INTEGER, " + COLUMN_PARTICIPANT_TWO_INTERMEDIATE_POINTS + " INTEGER)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 3 && newVersion == 4) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_GAME);
            createTable(db);
        }

        if (oldVersion == 4 && newVersion == 5) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_GAME);
            createTable(db);
        }

        if (6 > oldVersion) {
            db.execSQL("ALTER TABLE " + TABLE_TOURNAMENT_GAME + " ADD COLUMN " + COLUMN_PARTICIPANT_ONE_ARMY_LIST
                + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_TOURNAMENT_GAME + " ADD COLUMN " + COLUMN_PARTICIPANT_TWO_ARMY_LIST
                + " TEXT");
        }
    }
}
