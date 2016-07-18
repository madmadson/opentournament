package madson.org.opentournament;

import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

import static java.util.Calendar.getInstance;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OpenTournamentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "open_tournament";
    private static final int DB_VERSION = 1;

    public OpenTournamentDatabaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Calendar instance = getInstance();
        instance.set(2016, 6, 30, 8, 0);
        db.execSQL(
            " CREATE TABLE tournament (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, date NUMERIC, numberOfPlayers NUMERIC)");

        insertTournament(db, "Tournament1", "description1", instance.getTimeInMillis());
        insertTournament(db, "Tournament2", "description2", instance.getTimeInMillis());
        insertTournament(db, "Tournament3", "description4", instance.getTimeInMillis());
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    private static void insertTournament(SQLiteDatabase db, String name, String description, long date) {

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("date", date);
        values.put("numberOfPlayers", 0);
        db.insert("tournament", null, values);
    }
}
