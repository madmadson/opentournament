package madson.org.opentournament.db;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.domain.TournamentPlayer;

import static java.util.Calendar.getInstance;


/**
 * Database stuff for tournaments. Note: date of tournament is stored as TIMESTAMP. Online is a flag for 1: online 0:
 * only local
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentTable {

    /*
     * 0 id
     * 1: name
     * 2: location
     * 3: date
     * 4: actual round
     * 5: max players
     * 6: online uuid
     * 7: creator
     * 8: creator email
     * 9: tournament type
     * 10: actual players
     */
    public static final String[] ALL_COLS_FOR_TOURNAMENT = {
        TournamentTable.COLUMN_ID, TournamentTable.COLUMN_NAME, TournamentTable.COLUMN_LOCATION,
        TournamentTable.COLUMN_DATE, TournamentTable.COLUMN_ACTUAL_ROUND, TournamentTable.COLUMN_MAX_NUMBER_OF_PLAYERS,
        TournamentTable.COLUMN_ONLINE_UUID, TournamentTable.COLUMN_CREATOR, TournamentTable.COLUMN_CREATOR_EMAIL,
        TournamentTable.COLUMN_TOURNAMENT_TYPE, TournamentTable.COLUMN_ACTUAL_PLAYERS
    };

    public static final String TABLE_TOURNAMENTS = "tournament";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ONLINE_UUID = "onlineUUID";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MAX_NUMBER_OF_PLAYERS = "maxNumberOfPlayers";
    public static final String COLUMN_ACTUAL_PLAYERS = "actualPlayers";
    public static final String COLUMN_ACTUAL_ROUND = "actualRound";
    public static final String COLUMN_CREATOR = "creator";
    public static final String COLUMN_CREATOR_EMAIL = "creatorEmail";
    public static final String COLUMN_TOURNAMENT_TYPE = "tournamentType";

    public static void createTable(SQLiteDatabase db) {

        Log.i(TournamentTable.class.getName(), "cretae tournament table");
        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENTS
            + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
            + " TEXT, " + COLUMN_LOCATION + " TEXT, " + COLUMN_DATE + " INTEGER, " + COLUMN_MAX_NUMBER_OF_PLAYERS
            + " INTEGER, " + COLUMN_ACTUAL_ROUND + " INTEGER, " + COLUMN_ACTUAL_PLAYERS + " INTEGER, "
            + COLUMN_ONLINE_UUID + " TEXT, " + COLUMN_CREATOR
            + " TEXT, " + COLUMN_CREATOR_EMAIL + " TEXT, " + COLUMN_TOURNAMENT_TYPE + " TEXT)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TournamentTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENTS);
        createTable(db);
    }
}
