package madson.org.opentournament.db;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Database for team in tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentTeamTable {

    /*
     * 0: id
     * 1: tournament_id
     * 2: teamname
     * 3: meta
     */
    public static final String[] ALL_COLS_FOR_TOURNAMENT_TEAM_TABLE = {
        TournamentTeamTable.COLUMN_ID, TournamentTeamTable.COLUMN_TOURNAMENT_ID, TournamentTeamTable.COLUMN_TEAMNAME,
        TournamentTeamTable.COLUMN_META
    };

    public static final String TABLE_TOURNAMENT_TEAM = "tournament_team";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";

    public static final String COLUMN_TEAMNAME = "teamname";

    // META DATA for warmachine -> may extract to sperate tournament specific table
    public static final String COLUMN_META = "meta";

    public static void createTable(SQLiteDatabase db) {

        Log.i(TournamentTeamTable.class.getName(), "create tournament_team table");

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_TEAM
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TOURNAMENT_ID + " INTEGER,"
            + COLUMN_TEAMNAME + " TEXT, " + COLUMN_META + " TEXT)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TournamentTeamTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_TEAM);
        createTable(db);
    }
}
