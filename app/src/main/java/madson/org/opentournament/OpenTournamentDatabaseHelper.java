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

        db.execSQL(
            " CREATE TABLE player (_id INTEGER PRIMARY KEY AUTOINCREMENT, firstname TEXT, lastname TEXT, nickname TEXT)");

        insertPlayer(db, "Tobias", "Matt", "Madson");
        insertPlayer(db, "Christoph", "Scholl", "Zaziboy");
        insertPlayer(db, "David", "Voigt", "Wildjack");

        db.execSQL(" CREATE TABLE tournament_player (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "tournament_id INTEGER, "
            + "player_id INTEGER, "
            + "FOREIGN KEY(tournament_id) REFERENCES tournament(_id),"
            + "FOREIGN KEY(player_id) REFERENCES player(_id))");

        // TODO: does not work :/
//        insertTournamentPlayer(db, 1, 1);
//        insertTournamentPlayer(db, 1, 2);
//        insertTournamentPlayer(db, 1, 3);
//        insertTournamentPlayer(db, 2, 1);
//        insertTournamentPlayer(db, 2, 2);
//        insertTournamentPlayer(db, 2, 3);
//        insertTournamentPlayer(db, 3, 1);
//        insertTournamentPlayer(db, 3, 2);
//        insertTournamentPlayer(db, 3, 3);
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


    private static void insertPlayer(SQLiteDatabase db, String firstname, String lastname, String nickname) {

        ContentValues values = new ContentValues();
        values.put("firstname", firstname);
        values.put("lastname", lastname);
        values.put("nickname", nickname);
        db.insert("player", null, values);
    }


    private static void insertTournamentPlayer(SQLiteDatabase db, int tournament_id, int player_id) {

        ContentValues values = new ContentValues();
        values.put("tournament_id", tournament_id);
        values.put("player_id", player_id);
        db.insert("tournament_player", null, values);
    }
}
