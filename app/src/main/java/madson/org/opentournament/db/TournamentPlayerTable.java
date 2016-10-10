package madson.org.opentournament.db;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Database for player in tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerTable {

    /*
     * 0: id
     * 1: tournament_id
     * 2: player_uuid
     * 3 firstname
     * 4: nickname
     * 5: lastname
     * 6: teamname
     * 7: faction
     * 8: meta
     * 9: dummy
     * 10: dropped_in_round
     * 11: local
     */
    public static final String[] ALL_COLS_FOR_TOURNAMENT_PLAYER_TABLE = {
        TournamentPlayerTable.COLUMN_ID, TournamentPlayerTable.COLUMN_TOURNAMENT_ID,
        TournamentPlayerTable.COLUMN_PLAYER_UUID, TournamentPlayerTable.COLUMN_FIRSTNAME,
        TournamentPlayerTable.COLUMN_NICKNAME, TournamentPlayerTable.COLUMN_LASTNAME,
        TournamentPlayerTable.COLUMN_TEAMNAME, TournamentPlayerTable.COLUMN_FACTION, TournamentPlayerTable.COLUMN_META,
        TournamentPlayerTable.COLUMN_DUMMY, TournamentPlayerTable.COLUMN_DROPPED_IN_ROUND,
        TournamentPlayerTable.COLUMN_LOCAL
    };

    public static final String TABLE_TOURNAMENT_PLAYER = "tournament_player";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_PLAYER_UUID = "player_uuid";

    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_NICKNAME = "nickname";
    public static final String COLUMN_LASTNAME = "lastname";

    // META DATA for warmachine -> may extract to sperate tournament specific table
    public static final String COLUMN_TEAMNAME = "teamname";
    public static final String COLUMN_FACTION = "faction";
    public static final String COLUMN_META = "meta";

    public static final String COLUMN_DUMMY = "dummy";

    public static final String COLUMN_DROPPED_IN_ROUND = "dropped_on_round";
    public static final String COLUMN_LOCAL = "local";

    public static void createTable(SQLiteDatabase db) {

        Log.i(TournamentPlayerTable.class.getName(), "create tournament_player table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_PLAYER
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TOURNAMENT_ID + " TEXT,"
            + COLUMN_PLAYER_UUID + " TEXT," + COLUMN_FIRSTNAME + " TEXT, " + COLUMN_NICKNAME + " TEXT, "
            + COLUMN_LASTNAME + " TEXT, " + COLUMN_TEAMNAME + " TEXT, " + COLUMN_FACTION + " TEXT, " + COLUMN_META
            + " TEXT, " + COLUMN_DUMMY + " INTEGER, " + COLUMN_DROPPED_IN_ROUND + " INTEGER, " + COLUMN_LOCAL
            + " INTEGER)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TournamentPlayerTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");

        if (oldVersion == 3 && newVersion == 4) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_PLAYER);
            createTable(db);
        }
    }
}
