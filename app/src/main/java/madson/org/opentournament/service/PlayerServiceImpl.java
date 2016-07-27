package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.PlayerTable;
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerServiceImpl implements PlayerService {

    private OpenTournamentDBHelper openTournamentDBHelper;
    private String[] allColumns = {
        PlayerTable.COLUMN_ID, PlayerTable.COLUMN_FIRSTNAME, PlayerTable.COLUMN_NICKNAME, PlayerTable.COLUMN_LASTNAME
    };

    public PlayerServiceImpl(Context context) {

        Log.w(PlayerServiceImpl.class.getName(), "PlayerServiceImpl Constructor");

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        deleteAllPlayers();
        createMockPlayers();
    }

    private void deleteAllPlayers() {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(PlayerTable.TABLE_PLAYER, null, null);
    }


    private void createMockPlayers() {

        createPlayer(new Player("Tobias", "Madson", "Matt"));
        createPlayer(new Player("Christoph", "Zaziboy", "Scholl"));
        createPlayer(new Player("David", "Wildjack", "Voigt"));
    }


    @Override
    public void createPlayer(Player player) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerTable.COLUMN_FIRSTNAME, player.getFirstname());
        contentValues.put(PlayerTable.COLUMN_NICKNAME, player.getNickname());
        contentValues.put(PlayerTable.COLUMN_LASTNAME, player.getLastname());

        createPlayer(contentValues);
    }


    @Override
    public Player getPlayerForId(Long playerId) {

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(PlayerTable.TABLE_PLAYER, allColumns, null, null, null, null, null);

        Player player = cursorToPlayer(cursor);

        cursor.close();
        readableDatabase.close();

        return player;
    }


    @Override
    public List<Player> getAllPlayers() {

        ArrayList<Player> players = new ArrayList<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(PlayerTable.TABLE_PLAYER, allColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Player player = cursorToPlayer(cursor);

            players.add(player);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return players;
    }


    private void createPlayer(ContentValues contentValues) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.insert(PlayerTable.TABLE_PLAYER, null, contentValues);

        writableDatabase.close();
    }


    private Player cursorToPlayer(Cursor cursor) {

        return new Player(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
    }
}
