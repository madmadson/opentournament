package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.db.warmachine.WarmachineTournamentGameTable;
import madson.org.opentournament.domain.Tournament;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentServiceImpl implements TournamentService {

    private OpenTournamentDBHelper openTournamentDBHelper;

    private String[] allColumns = {
        TournamentTable.COLUMN_ID, TournamentTable.COLUMN_NAME, TournamentTable.COLUMN_LOCATION,
        TournamentTable.COLUMN_DATE, TournamentTable.COLUMN_MAX_NUMBER_OF_PLAYERS, TournamentTable.COLUMN_ACTUAL_ROUND,
        TournamentTable.COLUMN_ONLINE_UUID, TournamentTable.COLUMN_CREATOR, TournamentTable.COLUMN_CREATOR_EMAIL
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

        createTournament(1, "Coin of Evil", "Ludwigsburg", new DateTime(2016, 3, 10, 10, 8).toDate(), 32, null,
            "Harry Hirsch", "harry@gmail.com");
        createTournament(2, "HMDZ", "Oberhausen", new DateTime(2016, 5, 20, 10, 0).toDate(), 0, null, "Tim Grubi",
            "grubbi@gmail.com");
        createTournament(3, "Dead Fish", "Heidelberg", new DateTime(2016, 7, 15, 10, 0).toDate(), 16, null,
            "Bernhard Fishi", "fishi@gmail.com");
    }


    private void createTournament(int id, String name, String location, Date date, int maxPlayer, String onlineUUID,
        String creator, String creatorEmail) {

        ContentValues contentValues = new ContentValues();

        if (id != 0) {
            contentValues.put(TournamentTable.COLUMN_ID, id);
        }

        contentValues.put(TournamentTable.COLUMN_NAME, name);
        contentValues.put(TournamentTable.COLUMN_LOCATION, location);
        contentValues.put(TournamentTable.COLUMN_DATE, date == null ? null : date.getTime());
        contentValues.put(TournamentTable.COLUMN_MAX_NUMBER_OF_PLAYERS, maxPlayer);
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, 0);
        contentValues.put(TournamentTable.COLUMN_ONLINE_UUID, onlineUUID);
        contentValues.put(TournamentTable.COLUMN_CREATOR, creator);
        contentValues.put(TournamentTable.COLUMN_CREATOR_EMAIL, creatorEmail);
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

        Tournament tournament = new Tournament();
        tournament.set_id(cursor.getInt(0));
        tournament.setName(cursor.getString(1));
        tournament.setLocation(cursor.getString(2));

        String timeInMillis = cursor.getString(3);

        if (timeInMillis != null) {
            DateTime dateTime = new DateTime(Long.parseLong(timeInMillis));

            tournament.setDateOfTournament(dateTime.toDate());
        }

        tournament.setMaxNumberOfPlayers(cursor.getInt(4));
        tournament.setActualRound(cursor.getInt(5));
        tournament.setOnlineUUID(cursor.getString(6));
        tournament.setCreatorName(cursor.getString(7));
        tournament.setCreatorEmail(cursor.getString(8));

        return tournament;
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


    @Override
    public void createTournament(Tournament tournament) {

        Log.i(this.getClass().getName(), "create tournament: " + tournament);

        createTournament(0, tournament.getName(), tournament.getLocation(), tournament.getDateOfTournament(),
            tournament.getMaxNumberOfPlayers(), tournament.getOnlineUUID(), tournament.getCreatorName(),
            tournament.getCreatorEmail());
    }


    @Override
    public void pushTournamentToFirebase(Tournament tournament) {

        Log.i(this.getClass().getName(), "pushes tournament to online: " + tournament);

        UUID uuid = UUID.randomUUID();

        tournament.setOnlineUUID(uuid.toString());

        DatabaseReference firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseDatabaseReference.child("tournaments").push().setValue(tournament);

        // kill local stored tournament
        if (tournament.get_id() != 0) {
            SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();
            writableDatabase.delete(TournamentTable.TABLE_TOURNAMENTS, "_id  = ?",
                new String[] { String.valueOf(tournament.get_id()) });
        }
    }
}
