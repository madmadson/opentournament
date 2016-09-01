package madson.org.opentournament.db.warmachine;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.TournamentPlayerTable;


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
     * 3: tournament_online_uuid
     * 4: player_id
     * 5: player_online_uuid
     * 6: tournament round
     * 7: score
     * 8: sos
     * 9: cp
     * 10:vp
     * 11: firstname
     * 12: nickname
     * 13: lastname
     */
    public static final String[] ALL_COLS_FOR_RANKING = {
        TournamentRankingTable.COLUMN_ID, TournamentRankingTable.COLUMN_ONLINE_UUID,
        TournamentRankingTable.COLUMN_TOURNAMENT_ID, TournamentRankingTable.COLUMN_TOURNAMENT_ONLINE_UUID,
        TournamentRankingTable.COLUMN_PLAYER_ID, TournamentRankingTable.COLUMN_PLAYER_ONLINE_UUID,
        TournamentRankingTable.COLUMN_ROUND, TournamentRankingTable.COLUMN_SCORE, TournamentRankingTable.COLUMN_SOS,
        TournamentRankingTable.COLUMN_CONTROL_POINTS, TournamentRankingTable.COLUMN_VICTORY_POINTS,
        TournamentRankingTable.COLUMN_FIRSTNAME, TournamentRankingTable.COLUMN_NICKNAME,
        TournamentRankingTable.COLUMN_LASTNAME
    };

    public static final String TABLE_TOURNAMENT_RANKING = "tournament_ranking";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ONLINE_UUID = "onlineUUID";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_TOURNAMENT_ONLINE_UUID = "tournament_online_uuid";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_PLAYER_ONLINE_UUID = "player_online_uuid";
    public static final String COLUMN_ROUND = "tournament_round";

    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_SOS = "sos";
    public static final String COLUMN_CONTROL_POINTS = "control_points";
    public static final String COLUMN_VICTORY_POINTS = "victory_points";

    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_NICKNAME = "nickname";
    public static final String COLUMN_LASTNAME = "lastname";

    public static void createTable(SQLiteDatabase db) {

        Log.i(TournamentRankingTable.class.getName(), "create tournament ranking table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_RANKING
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ONLINE_UUID + " TEXT, "
            + COLUMN_TOURNAMENT_ID + " INTEGER," + COLUMN_TOURNAMENT_ONLINE_UUID + " TEXT, " + COLUMN_PLAYER_ID
            + " INTEGER, " + COLUMN_PLAYER_ONLINE_UUID + " TEXT, " + COLUMN_ROUND + " INTEGER, " + COLUMN_SCORE
            + " INTEGER, " + COLUMN_SOS + " INTEGER, " + COLUMN_CONTROL_POINTS + " INTEGER, " + COLUMN_VICTORY_POINTS
            + " INTEGER, " + COLUMN_FIRSTNAME + " TEXT, " + COLUMN_NICKNAME + " TEXT, " + COLUMN_LASTNAME + " TEXT)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TournamentPlayerTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_RANKING);
        createTable(db);
    }
}
