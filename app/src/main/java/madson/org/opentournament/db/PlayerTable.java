package madson.org.opentournament.db;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;


/**
 * Local database for players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerTable {

    public static final String TABLE_PLAYER = "player";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ONLINE_UUID = "onlineUUID";

    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_LASTNAME = "lastname";
    public static final String COLUMN_NICKNAME = "nickname";

    public static void createTable(SQLiteDatabase db) {

        Log.i(PlayerTable.class.getName(), "create player table");

        db.execSQL(" CREATE TABLE " + TABLE_PLAYER
            + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_FIRSTNAME
            + " TEXT, " + COLUMN_LASTNAME + " TEXT, " + COLUMN_NICKNAME + " TEXT, " + COLUMN_ONLINE_UUID + " TEXT)");
    }


    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(PlayerTable.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        createTable(db);
    }
}
