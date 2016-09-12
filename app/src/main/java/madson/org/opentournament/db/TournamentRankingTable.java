package madson.org.opentournament.db;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Represent ranking of tournament for specific round.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentRankingTable {

    /*
     * 0: id
     * 1: online_uuid
     * 2: tournament_id
     * 3: tournament round
     * 4: player_id
     * 5: player_online_uuid
     * 6: score
     * 7: sos
     * 8: cp
     * 9 :vp
     */
    public static final String[] ALL_COLS_FOR_TOURNAMENT_RANKING = {
        TournamentRankingTable.COLUMN_ID, TournamentRankingTable.COLUMN_ONLINE_UUID,
        TournamentRankingTable.COLUMN_TOURNAMENT_ID, TournamentRankingTable.COLUMN_TOURNAMENT_ROUND,
        TournamentRankingTable.COLUMN_PLAYER_ID, TournamentRankingTable.COLUMN_PLAYER_ONLINE_UUID,
        TournamentRankingTable.COLUMN_SCORE, TournamentRankingTable.COLUMN_SOS,
        TournamentRankingTable.COLUMN_CONTROL_POINTS, TournamentRankingTable.COLUMN_VICTORY_POINTS,
    };

    public static final String TABLE_TOURNAMENT_RANKING = "tournament_ranking";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ONLINE_UUID = "onlineUUID";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_TOURNAMENT_ROUND = "tournament_round";

    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_PLAYER_ONLINE_UUID = "player_online_uuid";

    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_SOS = "sos";
    public static final String COLUMN_CONTROL_POINTS = "control_points";
    public static final String COLUMN_VICTORY_POINTS = "victory_points";

    public static void createTable(SQLiteDatabase db) {

        Log.i(TournamentRankingTable.class.getName(), "create tournament ranking table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_RANKING
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ONLINE_UUID + " TEXT, "
            + COLUMN_TOURNAMENT_ID + " INTEGER, " + COLUMN_PLAYER_ID
            + " INTEGER, " + COLUMN_PLAYER_ONLINE_UUID + " TEXT, " + COLUMN_TOURNAMENT_ROUND + " INTEGER, "
            + COLUMN_SCORE + " INTEGER, " + COLUMN_SOS + " INTEGER, " + COLUMN_CONTROL_POINTS + " INTEGER, "
            + COLUMN_VICTORY_POINTS + " INTEGER)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TournamentPlayerTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_RANKING);
        createTable(db);
    }
}
