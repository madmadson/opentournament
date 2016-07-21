package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.db.PlayerDBHelper;
import madson.org.opentournament.db.TournamentDBHelper;
import madson.org.opentournament.db.TournamentPlayerDBHelper;
import madson.org.opentournament.domain.Player;
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

    private TournamentDBHelper tournamentDBHelper;
    private PlayerService playerService;
    private String[] allColumns = {
        TournamentDBHelper.COLUMN_ID, TournamentDBHelper.COLUMN_NAME, TournamentDBHelper.COLUMN_DESCRIPTION,
        TournamentDBHelper.COLUMN_DATE, TournamentDBHelper.COLUMN_NUMBER_OF_PLAYERS
    };

    public TournamentServiceImpl(Context context) {

        Log.w(TournamentServiceImpl.class.getName(), "TournamentServiceImpl Constructor");

        OpenTournamentApplication application = (OpenTournamentApplication) context;

        if (playerService == null) {
            playerService = application.getPlayerService();
        }

        if (tournamentDBHelper == null) {
            tournamentDBHelper = new TournamentDBHelper(context);
        }

        createMockTournaments();
    }

    private void createMockTournaments() {

        createTournament("Tournament1", "description1", new DateTime(2016, 3, 10, 10, 0).toDate(), 0);
        createTournament("Tournament2", "description2", new DateTime(2016, 5, 20, 10, 0).toDate(), 0);
        createTournament("Tournament3", "description3", new DateTime(2016, 7, 15, 10, 0).toDate(), 0);
    }


    private void createTournament(String name, String description, Date date, int numberOfPlayers) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentDBHelper.COLUMN_NAME, name);
        contentValues.put(TournamentDBHelper.COLUMN_DESCRIPTION, description);
        contentValues.put(TournamentDBHelper.COLUMN_DATE, date.toString());
        contentValues.put(TournamentDBHelper.COLUMN_NUMBER_OF_PLAYERS, numberOfPlayers);
        createTournament(contentValues);
    }


    @Override
    public Tournament getTournamentForId(Long id) {

        Tournament tournament = new Tournament();
        SQLiteDatabase readableDatabase = tournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentDBHelper.TABLE_TOURNAMENTS, Tournament.ALL_COLS_FOR_TOURNAMENT,
                "_id  = ?", new String[] { Long.toString(id) }, null, null, null);

        if (cursor.moveToFirst()) {
            tournament = cursorToTournament(cursor);
        }

        cursor.close();
        readableDatabase.close();

        return tournament;
    }


    private Tournament cursorToTournament(Cursor cursor) {

        return new Tournament(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3),
                new Date(cursor.getLong(4)));
    }


    @Override
    public void createTournament(ContentValues contentValues) {

        SQLiteDatabase writableDatabase = tournamentDBHelper.getWritableDatabase();

        writableDatabase.insert(TournamentDBHelper.TABLE_TOURNAMENTS, null, contentValues);

        writableDatabase.close();
    }


    @Override
    public void editTournament(Long tournamentId, ContentValues contentValues) {

        SQLiteDatabase writableDatabase = tournamentDBHelper.getWritableDatabase();

        writableDatabase.update(TournamentDBHelper.TABLE_TOURNAMENTS, contentValues, "_id = ?",
            new String[] { String.valueOf(tournamentId) });

        writableDatabase.close();
    }


    @Override
    public List<Player> getPlayersForTournament(Long tournamentId) {

        ArrayList<Player> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = tournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentDBHelper.TABLE_TOURNAMENTS,
                new String[] { "_id", "player_id" }, "tournament_id  = ?", new String[] { Long.toString(tournamentId) },
                null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Player player = playerService.getPlayerForId((long) cursor.getInt(0));
            players.add(player);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return players;
    }


    @Override
    public List<Tournament> getTournaments() {

        ArrayList<Tournament> tournaments = new ArrayList<>();

        SQLiteDatabase readableDatabase = tournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentDBHelper.TABLE_TOURNAMENTS, allColumns, null, null, null, null,
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
