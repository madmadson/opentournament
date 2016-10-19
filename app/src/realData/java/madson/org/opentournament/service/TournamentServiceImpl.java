package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import madson.org.opentournament.db.TournamentDBHelper;
import madson.org.opentournament.domain.Tournament;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentServiceImpl implements TournamentService {

    private TournamentDBHelper tournamentDBHelper;
    private SQLiteDatabase database;
    private String[] allColumns = {
        TournamentDBHelper.COLUMN_ID, TournamentDBHelper.COLUMN_NAME, TournamentDBHelper.COLUMN_DESCRIPTION,
        TournamentDBHelper.COLUMN_DATE, TournamentDBHelper.COLUMN_NUMBER_OF_PLAYERS
    };

    public TournamentServiceImpl(Context context) {

        tournamentDBHelper = new TournamentDBHelper(context);
        open();
    }

    private void createTournament(String name, String description, Date date) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("description", description);
        contentValues.put("date", date.toString());
    }


    public void open() throws SQLException {

        database = tournamentDBHelper.getWritableDatabase();
    }


    public void close() {

        tournamentDBHelper.close();
    }


    @Override
    public Tournament getTournamentForId(Long id) {

        Tournament tournament = new Tournament();

        Cursor cursor = database.query(TournamentDBHelper.TABLE_TOURNAMENTS, Tournament.ALL_COLS_FOR_TOURNAMENT,
                "_id  = ?", new String[] { Long.toString(id) }, null, null, null);

        if (cursor.moveToFirst()) {
            tournament = cursorToTournament(cursor);
        }

        return tournament;
    }


    @Override
    public Cursor getCursorForAllTournaments() {

        return database.query(TournamentDBHelper.TABLE_TOURNAMENTS, allColumns, null, null, null, null, null);
    }


    private Tournament cursorToTournament(Cursor cursor) {

        return new Tournament(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                new Date(cursor.getLong(4)));
    }


    @Override
    public Cursor getCursorForPlayersOfTournament(Long tournamentId) {

        return database.query(TournamentDBHelper.TABLE_TOURNAMENTS, new String[] { "_id", "name" }, null, null, null,
                null, null);
    }


    @Override
    public void createTournament(ContentValues contentValues) {

        database.insert(TournamentDBHelper.TABLE_TOURNAMENTS, null, contentValues);
    }


    @Override
    public void editTournament(Long tournamentId, ContentValues contentValues) {

        database.update(TournamentDBHelper.TABLE_TOURNAMENTS, contentValues, "_id = ?",
            new String[] { String.valueOf(tournamentId) });
    }
}
