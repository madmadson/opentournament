package madson.org.opentournament.db;

import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

import java.util.Calendar;

import static java.util.Calendar.getInstance;


/**
 * Database stuff for tournaments.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_TOURNAMENTS = "tournament";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NUMBER_OF_PLAYERS = "numberOfPlayers";

    private static final String DB_NAME = "opentournament.db";
    private static final int DB_VERSION = 1;

    public TournamentDBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(" CREATE TABLE " + TABLE_TOURNAMENTS
            + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
            + " TEXT, " + COLUMN_DESCRIPTION + " TEXT, " + COLUMN_DATE + " NUMERIC, " + COLUMN_NUMBER_OF_PLAYERS
            + " NUMERIC)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TournamentDBHelper.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURNAMENTS);
        onCreate(db);
    }
}
