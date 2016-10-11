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
     * 1: tournament_id
     * 2: tournament round
     * 3: player_uuid
     * 4: score
     * 5: sos
     * 6: cp
     * 7: vp
     */
    public static final String[] ALL_COLS_FOR_TOURNAMENT_RANKING = {
        TournamentRankingTable.COLUMN_ID, TournamentRankingTable.COLUMN_TOURNAMENT_ID,
        TournamentRankingTable.COLUMN_TOURNAMENT_ROUND, TournamentRankingTable.COLUMN_PARTICIPANT_UUID,
        TournamentRankingTable.COLUMN_SCORE, TournamentRankingTable.COLUMN_SOS,
        TournamentRankingTable.COLUMN_CONTROL_POINTS, TournamentRankingTable.COLUMN_VICTORY_POINTS,
    };

    public static final String TABLE_TOURNAMENT_RANKING = "tournament_ranking";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_TOURNAMENT_ROUND = "tournament_round";

    public static final String COLUMN_PARTICIPANT_UUID = "participant_uuid";

    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_SOS = "sos";
    public static final String COLUMN_CONTROL_POINTS = "control_points";
    public static final String COLUMN_VICTORY_POINTS = "victory_points";

    public static void createTable(SQLiteDatabase db) {

        Log.i(TournamentRankingTable.class.getName(), "create tournament ranking table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_RANKING
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TOURNAMENT_ID + " TEXT, "
            + COLUMN_PARTICIPANT_UUID + " TEXT, " + COLUMN_TOURNAMENT_ROUND + " INTEGER, " + COLUMN_SCORE + " INTEGER, "
            + COLUMN_SOS + " INTEGER, " + COLUMN_CONTROL_POINTS + " INTEGER, " + COLUMN_VICTORY_POINTS + " INTEGER)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 3 && newVersion == 4) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_RANKING);
            createTable(db);
        }
    }
}
