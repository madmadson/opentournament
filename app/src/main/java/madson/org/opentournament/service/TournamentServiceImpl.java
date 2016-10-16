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

    public TournamentServiceImpl(Context context) {

        Log.i(TournamentServiceImpl.class.getName(), "TournamentServiceImpl Constructor");

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }
    }

    @Override
    public void updateTournamentInFirebase(Tournament tournament) {

        Log.i(this.getClass().getName(), "update online tournament in firebase: " + tournament);

        DatabaseReference referenceForTournament = FirebaseDatabase.getInstance()
                .getReference(FirebaseReferences.TOURNAMENTS + "/" + tournament.getGameOrSportTyp() + "/"
                    + tournament.getUUID());

        referenceForTournament.setValue(tournament);
    }


    @Override
    public Tournament uploadTournament(Tournament tournament) {

        Log.i(this.getClass().getName(), "pushes tournament to online: " + tournament);

        DatabaseReference referenceForUpdateTournament = FirebaseDatabase.getInstance()
                .getReference(FirebaseReferences.TOURNAMENTS + "/" + tournament.getGameOrSportTyp() + "/"
                    + tournament.getUUID());

        referenceForUpdateTournament.setValue(tournament);

        return tournament;
    }


    private Tournament insertTournament(int id, String name, String location, Date date, int maxPlayer,
        String onlineUUID, String creator, String creatorEmail, int actualPlayers, String tournmanetType,
        int teamsize) {

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
        contentValues.put(TournamentTable.COLUMN_STATE, Tournament.TournamentState.PLANED.name());
        contentValues.put(TournamentTable.COLUMN_TEAM_SIZE, teamsize);
        contentValues.put(TournamentTable.COLUMN_ONLINE_UUID, UUID.randomUUID().toString());

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        long newId = writableDatabase.insert(TournamentTable.TABLE_TOURNAMENTS, null, contentValues);

        return getTournamentForId(newId);
    }


    @Override
    public Tournament getTournamentForId(Long id) {

        Tournament tournament = new Tournament();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentTable.TABLE_TOURNAMENTS,
                TournamentTable.ALL_COLS_FOR_TOURNAMENT, TournamentTable.COLUMN_ID + " = ?",
                new String[] { Long.toString(id) }, null, null, null);

        if (cursor.moveToFirst()) {
            tournament = cursorToTournament(cursor);
        }

        cursor.close();

        return tournament;
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
        tournament.setMaxNumberOfParticipants(cursor.getInt(5));
        tournament.setUuid(cursor.getString(6));
        tournament.setCreatorName(cursor.getString(7));
        tournament.setCreatorEmail(cursor.getString(8));
        tournament.setTournamentTyp(cursor.getString(9));
        tournament.setActualPlayers(cursor.getInt(10));
        tournament.setGameOrSportTyp(cursor.getString(11));
        tournament.setState(cursor.getString(12));
        tournament.setTeamSize(cursor.getInt(13));

        return tournament;
    }


    @Override
    public void updateTournament(Tournament tournament) {

        Log.i(this.getClass().getName(), "update tournament: " + tournament);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_NAME, tournament.getName());
        contentValues.put(TournamentTable.COLUMN_LOCATION, tournament.getLocation());
        contentValues.put(TournamentTable.COLUMN_TEAM_SIZE, tournament.getTeamSize());
        contentValues.put(TournamentTable.COLUMN_TOURNAMENT_TYPE, tournament.getTournamentTyp());
        contentValues.put(TournamentTable.COLUMN_DATE,
            tournament.getDateOfTournament() == null ? null : tournament.getDateOfTournament().getTime());
        contentValues.put(TournamentTable.COLUMN_MAX_NUMBER_OF_PLAYERS, tournament.getMaxNumberOfParticipants());

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, "_id = ?",
            new String[] { String.valueOf(tournament.get_id()) });
    }


    private void insertOnlineUUID(Tournament tournament) {

        Log.i(this.getClass().getName(), "insert tournament uuid : " + tournament);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ONLINE_UUID, tournament.getUUID());

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, TournamentTable.COLUMN_ID + " = ?",
            new String[] { String.valueOf(tournament.get_id()) });
    }


    @Override
    public Tournament updateActualRound(Tournament tournament, int round) {

        Log.i(this.getClass().getName(), "update actual round for tournament: " + tournament);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, round);
        contentValues.put(TournamentTable.COLUMN_STATE, Tournament.TournamentState.STARTED.name());

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, TournamentTable.COLUMN_ID + " = ?",
            new String[] { String.valueOf(tournament.get_id()) });

        tournament.setActualRound(round);
        tournament.setState(Tournament.TournamentState.STARTED.name());

        return tournament;
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

        cursor.close();
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

        cursor.close();
    }


    @Override
    public void endTournament(Tournament tournament) {

        Log.i(this.getClass().getName(), "update tournament: " + tournament);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_STATE, Tournament.TournamentState.FINISHED.name());
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, tournament.getActualRound() + 1);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues,
            TournamentTable.COLUMN_ONLINE_UUID + " = ?", new String[] { tournament.getUUID() });
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

        return tournaments;
    }


    @Override
    public Tournament createTournament(Tournament tournament) {

        Log.i(this.getClass().getName(), "create tournament: " + tournament);

        return insertTournament(0, tournament.getName(), tournament.getLocation(), tournament.getDateOfTournament(),
                tournament.getMaxNumberOfParticipants(), tournament.getUUID(), tournament.getCreatorName(),
                tournament.getCreatorEmail(), tournament.getActualPlayers(), tournament.getTournamentTyp(),
                tournament.getTeamSize());
    }


    @Override
    public void deleteTournament(Tournament tournament) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();
        writableDatabase.delete(TournamentTable.TABLE_TOURNAMENTS, TournamentTable.COLUMN_ID + "  = ?",
            new String[] { String.valueOf(tournament.get_id()) });
    }
}
