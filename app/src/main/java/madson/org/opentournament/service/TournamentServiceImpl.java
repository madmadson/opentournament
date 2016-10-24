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
import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentTyp;

import org.joda.time.DateTime;

import java.util.ArrayList;
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


    @Override
    public Tournament getTournamentForId(String uuid) {

        Tournament tournament = new Tournament();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentTable.TABLE_TOURNAMENTS,
                TournamentTable.ALL_COLS_FOR_TOURNAMENT, TournamentTable.COLUMN_UUID + " = ?", new String[] { uuid },
                null, null, null);

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

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, TournamentTable.COLUMN_UUID + " = ?",
            new String[] { tournament.getUUID() });
    }


    @Override
    public Tournament updateActualRound(Tournament tournament, int round) {

        Log.i(this.getClass().getName(), "update actual round for tournament: " + tournament);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, round);

        if (round == 0) {
            contentValues.put(TournamentTable.COLUMN_STATE, Tournament.TournamentState.PLANED.name());
        } else {
            contentValues.put(TournamentTable.COLUMN_STATE, Tournament.TournamentState.STARTED.name());
        }

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, TournamentTable.COLUMN_UUID + " = ?",
            new String[] { tournament.getUUID() });

        tournament.setActualRound(round);

        if (round == 0) {
            tournament.setState(Tournament.TournamentState.PLANED.name());
        } else {
            tournament.setState(Tournament.TournamentState.STARTED.name());
        }

        return tournament;
    }


    @Override
    public void increaseActualPlayerForTournament(Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        Cursor cursor = db.query(TournamentTable.TABLE_TOURNAMENTS,
                new String[] { TournamentTable.COLUMN_ACTUAL_PLAYERS }, TournamentTable.COLUMN_UUID + " = ?",
                new String[] { tournament.getUUID() }, null, null, null);

        cursor.moveToFirst();

        int actualPlayers = cursor.getInt(0);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ACTUAL_PLAYERS, actualPlayers + 1);
        db.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, TournamentTable.COLUMN_UUID + " = ?",
            new String[] { tournament.getUUID() });

        cursor.close();
    }


    @Override
    public void decreaseActualPlayerForTournament(Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        Cursor cursor = db.query(TournamentTable.TABLE_TOURNAMENTS,
                new String[] { TournamentTable.COLUMN_ACTUAL_PLAYERS }, TournamentTable.COLUMN_UUID + " = ?",
                new String[] { tournament.getUUID() }, null, null, null);

        cursor.moveToFirst();

        int actualPlayers = cursor.getInt(0);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ACTUAL_PLAYERS, actualPlayers - 1);
        db.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, TournamentTable.COLUMN_UUID + " = ?",
            new String[] { tournament.getUUID() });

        cursor.close();
    }


    @Override
    public void endTournament(Tournament tournament) {

        Log.i(this.getClass().getName(), "update tournament: " + tournament);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_STATE, Tournament.TournamentState.FINISHED.name());
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, tournament.getActualRound() + 1);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.update(TournamentTable.TABLE_TOURNAMENTS, contentValues, TournamentTable.COLUMN_UUID + " = ?",
            new String[] { tournament.getUUID() });
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

        ContentValues contentValues = new ContentValues();

        String uuid = UUID.randomUUID().toString();

        tournament.setUuid(uuid);

        contentValues.put(TournamentTable.COLUMN_NAME, tournament.getName());
        contentValues.put(TournamentTable.COLUMN_LOCATION, tournament.getLocation());
        contentValues.put(TournamentTable.COLUMN_DATE,
            tournament.getDateOfTournament() == null ? null : tournament.getDateOfTournament().getTime());
        contentValues.put(TournamentTable.COLUMN_MAX_NUMBER_OF_PLAYERS, tournament.getMaxNumberOfParticipants());
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, 0);
        contentValues.put(TournamentTable.COLUMN_UUID, uuid);
        contentValues.put(TournamentTable.COLUMN_CREATOR, tournament.getCreatorName());
        contentValues.put(TournamentTable.COLUMN_CREATOR_EMAIL, tournament.getCreatorEmail());

        String tournamentTyp = tournament.getTournamentTyp();
        contentValues.put(TournamentTable.COLUMN_TOURNAMENT_TYPE, tournamentTyp);

        if (tournamentTyp.equals(TournamentTyp.SOLO.name())) {
            contentValues.put(TournamentTable.COLUMN_GAME_OR_SPORT_TYPE, GameOrSportTyp.WARMACHINE_SOLO.name());
            tournament.setGameOrSportTyp(GameOrSportTyp.WARMACHINE_SOLO.name());
        } else if (tournamentTyp.equals(TournamentTyp.TEAM.name())) {
            contentValues.put(TournamentTable.COLUMN_GAME_OR_SPORT_TYPE, GameOrSportTyp.WARMACHINE_TEAM.name());
            tournament.setGameOrSportTyp(GameOrSportTyp.WARMACHINE_TEAM.name());
        }

        contentValues.put(TournamentTable.COLUMN_ACTUAL_PLAYERS, 0);
        contentValues.put(TournamentTable.COLUMN_STATE, Tournament.TournamentState.PLANED.name());
        tournament.setState(Tournament.TournamentState.PLANED.name());

        contentValues.put(TournamentTable.COLUMN_TEAM_SIZE, tournament.getTeamSize());

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();
        writableDatabase.insert(TournamentTable.TABLE_TOURNAMENTS, null, contentValues);

        return tournament;
    }


    @Override
    public void deleteTournament(Tournament tournament) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();
        writableDatabase.delete(TournamentTable.TABLE_TOURNAMENTS, TournamentTable.COLUMN_UUID + "  = ?",
            new String[] { tournament.getUUID() });
    }
}
