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

    public static final String[] ALL_COLS_FOR_TOURNAMENT = {
        TournamentTable.COLUMN_ID, "name", "location", "date", "maxNumberOfPlayers", "actualRound", "onlineUUID",
        "creator", "creatorEmail", "tournamentType"
    };

    public static final String TABLE_TOURNAMENTS = "tournament";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ONLINE_UUID = "onlineUUID";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MAX_NUMBER_OF_PLAYERS = "maxNumberOfPlayers";
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
            + " INTEGER, " + COLUMN_ACTUAL_ROUND + " INTEGER, " + COLUMN_ONLINE_UUID + " TEXT, " + COLUMN_CREATOR
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
