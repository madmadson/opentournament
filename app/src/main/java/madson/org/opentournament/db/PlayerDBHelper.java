package madson.org.opentournament.db;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;


/**
 * Database stuff for tournaments.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_PLAYER = "player";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_LASTNAME = "lastname";
    public static final String COLUMN_NICKNAME = "nickname";

    private static final String DB_NAME = "opentournament.db";
    private static final int DB_VERSION = 1;

    public PlayerDBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(" CREATE TABLE " + TABLE_PLAYER
            + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_FIRSTNAME
            + " TEXT, " + COLUMN_LASTNAME + " TEXT, " + COLUMN_NICKNAME + " TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(PlayerDBHelper.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        onCreate(db);
    }
}
