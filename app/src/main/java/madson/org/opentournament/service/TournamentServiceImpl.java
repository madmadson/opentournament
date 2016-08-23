package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.db.warmachine.WarmachineTournamentGameTable;
import madson.org.opentournament.domain.Tournament;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentServiceImpl implements TournamentService {

    private OpenTournamentDBHelper openTournamentDBHelper;

    private String[] allColumns = {
        TournamentTable.COLUMN_ID, TournamentTable.COLUMN_NAME, TournamentTable.COLUMN_DESCRIPTION,
        TournamentTable.COLUMN_DATE, TournamentTable.COLUMN_NUMBER_OF_PLAYERS, TournamentTable.COLUMN_ACTUAL_ROUND
    };

    public TournamentServiceImpl(Context context) {

        Log.i(TournamentServiceImpl.class.getName(), "TournamentServiceImpl Constructor");

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        deleteAllTournaments();
        deleteAllTournamentsPairings();
        createMockTournaments();
    }

    private void deleteAllTournamentsPairings() {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(WarmachineTournamentGameTable.TABLE_TOURNAMENT_GAME, null, null);
    }


    private void createMockTournaments() {

        createTournament(1, "Tournament1", "description1", new DateTime(2016, 3, 10, 10, 8).toDate(), 8);
        createTournament(2, "Tournament2", "description2", new DateTime(2016, 5, 20, 10, 0).toDate(), 0);
        createTournament(3, "Tournament3", "description3", new DateTime(2016, 7, 15, 10, 0).toDate(), 0);
    }


    private void createTournament(int _id, String name, String description, Date date, int numberOfPlayers) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ID, _id);
        contentValues.put(TournamentTable.COLUMN_NAME, name);
        contentValues.put(TournamentTable.COLUMN_DESCRIPTION, description);
        contentValues.put(TournamentTable.COLUMN_DATE, date.toString());
        contentValues.put(TournamentTable.COLUMN_NUMBER_OF_PLAYERS, numberOfPlayers);
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, 0);
        createTournament(contentValues);
    }


    @Override
    public Tournament getTournamentForId(Long id) {

        Tournament tournament = new Tournament();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentTable.TABLE_TOURNAMENTS, Tournament.ALL_COLS_FOR_TOURNAMENT,
                "_id  = ?", new String[] { Long.toString(id) }, null, null, null);

        if (cursor.moveToFirst()) {
            tournament = cursorToTournament(cursor);
        }

        cursor.close();
        readableDatabase.close();

        return tournament;
    }


    private void deleteAllTournaments() {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(TournamentTable.TABLE_TOURNAMENTS, null, null);
    }


    private Tournament cursorToTournament(Cursor cursor) {

        return new Tournament(cursor.getInt(0), cursor.getString(1), cursor.getString(2), new Date(cursor.getLong(3)),
                cursor.getInt(4), cursor.getInt(5));
    }


    @Override
    public void createTournament(ContentValues contentValues) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.insert(TournamentTable.TABLE_TOURNAMENTS, null, contentValues);

        writableDatabase.close();
    }


    @Override
    public void editTournament(Long tournamentId, ContentValues contentValues) {

        Log.i(this.getClass().getName(), "edit tournament: " + tournamentId + " with: " + contentValues);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, "_id = ?",
            new String[] { String.valueOf(tournamentId) });

        writableDatabase.close();
    }


    @Override
    public List<Tournament> getTournaments() {

        ArrayList<Tournament> tournaments = new ArrayList<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentTable.TABLE_TOURNAMENTS, allColumns, null, null, null, null,
                null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Tournament tournament = cursorToTournament(cursor);

            tournaments.add(tournament);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return tournaments;
    }
}
