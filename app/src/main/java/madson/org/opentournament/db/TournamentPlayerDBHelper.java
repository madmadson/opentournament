package madson.org.opentournament.db;

import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;


/**
 * Database stuff for tournaments.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_TOURNAMENT_PLAYER = "tournament_player";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
    public static final String COLUMN_PLAYER_ID = "player_id";

    private static final String DB_NAME = "opentournament.db";
    private static final int DB_VERSION = 1;

    public TournamentPlayerDBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENT_PLAYER
            + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TOURNAMENT_ID
            + " INTEGER, " + COLUMN_PLAYER_ID + " INTEGER)");

        ContentValues contentValues = new ContentValues();
        contentValues.put("tournament_id", 1);
        contentValues.put("player_id", 1);
        db.insert(TABLE_TOURNAMENT_PLAYER, null, contentValues);
        contentValues.clear();

        contentValues = new ContentValues();
        contentValues.put("tournament_id", 1);
        contentValues.put("player_id", 2);
        db.insert(TABLE_TOURNAMENT_PLAYER, null, contentValues);
        contentValues.clear();

        contentValues = new ContentValues();
        contentValues.put("tournament_id", 1);
        contentValues.put("player_id", 3);
        db.insert(TABLE_TOURNAMENT_PLAYER, null, contentValues);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TournamentPlayerDBHelper.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENT_PLAYER);
        onCreate(db);
    }
}
