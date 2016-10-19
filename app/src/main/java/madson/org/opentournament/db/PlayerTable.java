package madson.org.opentournament.db;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Local database for players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerTable {

    /*
     * 0: id
     * 1: player_uuid
     * 2: firstname
     * 3: nickname
     * 4: lastname

     */
    public static final String[] ALL_COLS_FOR_PLAYER_TABLE = {
        PlayerTable.COLUMN_ID, PlayerTable.COLUMN_UUID, PlayerTable.COLUMN_FIRSTNAME, PlayerTable.COLUMN_NICKNAME,
        PlayerTable.COLUMN_LASTNAME
    };

    public static final String TABLE_PLAYER = "player";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UUID = "uuid";

    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_LASTNAME = "lastname";
    public static final String COLUMN_NICKNAME = "nickname";

    public static void createTable(SQLiteDatabase db) {

        Log.i(PlayerTable.class.getName(), "create player table");

        db.execSQL(" CREATE TABLE " + TABLE_PLAYER
            + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_FIRSTNAME
            + " TEXT, " + COLUMN_LASTNAME + " TEXT, " + COLUMN_NICKNAME + " TEXT, " + COLUMN_UUID + " TEXT)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 3 && newVersion == 4) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
            createTable(db);
        }
    }
}
