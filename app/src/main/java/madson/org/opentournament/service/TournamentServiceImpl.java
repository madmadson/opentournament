package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.db.warmachine.GameTable;
import madson.org.opentournament.db.warmachine.TournamentRankingTable;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentTyp;

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

    public TournamentServiceImpl(Context context) {

        Log.i(TournamentServiceImpl.class.getName(), "TournamentServiceImpl Constructor");

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        deleteAllTournaments();
        deleteAllGamesOfTournament();
        deleteAllRankingsOfTournament();
        createMockTournaments();
    }

    @Override
    public void updateTournamentInFirebase(Tournament tournament) {

        Log.i(this.getClass().getName(), "update online tournament in firebase: " + tournament);

        DatabaseReference referenceForTournament = FirebaseDatabase.getInstance()
                .getReference(FirebaseReferences.TOURNAMENTS + "/" + tournament.getGameOrSportTyp() + "/"
                    + tournament.getOnlineUUID());

        referenceForTournament.setValue(tournament);
    }


    @Override
    public Tournament createTournamentInFirebase(Tournament tournament) {

        Log.i(this.getClass().getName(), "pushes tournament to online: " + tournament);

        UUID uuid = UUID.randomUUID();

        tournament.setOnlineUUID(uuid.toString());

        insertOnlineUUID(tournament);

        DatabaseReference referenceForNewTournament = FirebaseDatabase.getInstance()
                .getReference(FirebaseReferences.TOURNAMENTS + "/" + tournament.getGameOrSportTyp() + "/" + uuid);

        referenceForNewTournament.setValue(tournament);

        return tournament;
    }


    private void deleteAllGamesOfTournament() {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(GameTable.TABLE_TOURNAMENT_GAME, null, null);
        writableDatabase.close();
    }


    private void deleteAllRankingsOfTournament() {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(TournamentRankingTable.TABLE_TOURNAMENT_RANKING, null, null);
        writableDatabase.close();
    }


    private void createMockTournaments() {

        insertTournament(1, "Coin of Evil", "Ludwigsburg", new DateTime(2016, 3, 10, 10, 8).toDate(), 32, null, null,
            null, 8, TournamentTyp.SOLO.name());
        insertTournament(2, "HMDZ", "Oberhausen", new DateTime(2016, 5, 20, 10, 0).toDate(), 0, null, null, null, 32,
            TournamentTyp.TEAM.name());
        insertTournament(3, "Dead Fish", "Heidelberg", new DateTime(2016, 7, 15, 10, 0).toDate(), 16, null, null, null,
            0, TournamentTyp.SOLO.name());
    }


    private Tournament insertTournament(int id, String name, String location, Date date, int maxPlayer,
        String onlineUUID, String creator, String creatorEmail, int actualPlayers, String tournmanetType) {

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
        contentValues.put(TournamentTable.COLUMN_TOURNAMENT_TYPE, tournmanetType);
        contentValues.put(TournamentTable.COLUMN_GAME_OR_SPORT_TYPE, "WARMACHINE");
        contentValues.put(TournamentTable.COLUMN_ACTUAL_PLAYERS, actualPlayers);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        long newId = writableDatabase.insert(TournamentTable.TABLE_TOURNAMENTS, null, contentValues);

        writableDatabase.close();

        Tournament newTournament = getTournamentForId(newId);

        return newTournament;
    }


    @Override
    public Tournament getTournamentForId(Long id) {

        Tournament tournament = new Tournament();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentTable.TABLE_TOURNAMENTS,
                TournamentTable.ALL_COLS_FOR_TOURNAMENT, "_id  = ?", new String[] { Long.toString(id) }, null, null,
                null);

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
        writableDatabase.close();
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

        tournament.setActualRound(cursor.getInt(4));
        tournament.setMaxNumberOfPlayers(cursor.getInt(5));
        tournament.setOnlineUUID(cursor.getString(6));
        tournament.setCreatorName(cursor.getString(7));
        tournament.setCreatorEmail(cursor.getString(8));
        tournament.setTournamentTyp(cursor.getString(9));
        tournament.setActualPlayers(cursor.getInt(10));
        tournament.setGameOrSportTyp(cursor.getString(11));

        return tournament;
    }


    @Override
    public void updateTournament(Tournament tournament) {

        Log.i(this.getClass().getName(), "update tournament: " + tournament);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_NAME, tournament.getName());
        contentValues.put(TournamentTable.COLUMN_LOCATION, tournament.getLocation());
        contentValues.put(TournamentTable.COLUMN_DATE,
            tournament.getDateOfTournament() == null ? null : tournament.getDateOfTournament().getTime());
        contentValues.put(TournamentTable.COLUMN_MAX_NUMBER_OF_PLAYERS, tournament.getMaxNumberOfPlayers());

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, "_id = ?",
            new String[] { String.valueOf(tournament.get_id()) });

        writableDatabase.close();
    }


    private void insertOnlineUUID(Tournament tournament) {

        Log.i(this.getClass().getName(), "insert tournament uuid : " + tournament);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ONLINE_UUID, tournament.getOnlineUUID());

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, TournamentTable.COLUMN_ID + " = ?",
            new String[] { String.valueOf(tournament.get_id()) });

        writableDatabase.close();
    }


    @Override
    public void updateActualRound(Long tournamentId, int round) {

        Log.i(this.getClass().getName(), "update actual round for tournament: " + tournamentId);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, round);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, "_id = ?",
            new String[] { String.valueOf(tournamentId) });

        writableDatabase.close();
    }


    @Override
    public void increaseActualPlayerForTournament(Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        Cursor cursor = db.query(TournamentTable.TABLE_TOURNAMENTS,
                new String[] { TournamentTable.COLUMN_ACTUAL_PLAYERS }, "_id  = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        int actualPlayers = cursor.getInt(0);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ACTUAL_PLAYERS, actualPlayers + 1);
        db.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, "_id = ?",
            new String[] { String.valueOf(tournament.get_id()) });

        db.close();
    }


    @Override
    public void decreaseActualPlayerForTournament(Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        Cursor cursor = db.query(TournamentTable.TABLE_TOURNAMENTS,
                new String[] { TournamentTable.COLUMN_ACTUAL_PLAYERS }, "_id  = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        int actualPlayers = cursor.getInt(0);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ACTUAL_PLAYERS, actualPlayers - 1);
        db.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, "_id = ?",
            new String[] { String.valueOf(tournament.get_id()) });

        db.close();
    }


    @Override
    public List<Tournament> getTournaments() {

        ArrayList<Tournament> tournaments = new ArrayList<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentTable.TABLE_TOURNAMENTS,
                TournamentTable.ALL_COLS_FOR_TOURNAMENT, null, null, null, null, null);

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
    public Tournament createTournament(Tournament tournament) {

        Log.i(this.getClass().getName(), "create tournament: " + tournament);

        return insertTournament(0, tournament.getName(), tournament.getLocation(), tournament.getDateOfTournament(),
                tournament.getMaxNumberOfPlayers(), tournament.getOnlineUUID(), tournament.getCreatorName(),
                tournament.getCreatorEmail(), tournament.getActualPlayers(), tournament.getTournamentTyp());
    }


    @Override
    public void deleteTournament(long id) {

        Log.i(this.getClass().getName(), "delete  tournament for id: " + id);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();
        writableDatabase.delete(TournamentTable.TABLE_TOURNAMENTS, "_id  = ?", new String[] { String.valueOf(id) });
        writableDatabase.close();
    }


    @Override
    public void removeTournamentInFirebase(Tournament tournament) {

        Log.i(this.getClass().getName(), "delete online tournament in firebase: " + tournament);

        DatabaseReference referneceForTournamentToDelete = FirebaseDatabase.getInstance()
                .getReference("tournaments/" + tournament.getOnlineUUID());

        referneceForTournamentToDelete.removeValue();
    }
}
