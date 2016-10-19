package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.PlayerTable;
import madson.org.opentournament.domain.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerServiceImpl implements PlayerService {

    private OpenTournamentDBHelper openTournamentDBHelper;

    public PlayerServiceImpl(Context context) {

        Log.w(PlayerServiceImpl.class.getName(), "PlayerServiceImpl Constructor");

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }
    }

    @Override
    public void createLocalPlayer(Player player) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerTable.COLUMN_FIRSTNAME, player.getFirstName());
        contentValues.put(PlayerTable.COLUMN_NICKNAME, player.getNickName());
        contentValues.put(PlayerTable.COLUMN_LASTNAME, player.getLastName());
        contentValues.put(PlayerTable.COLUMN_UUID, player.getUUID());

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.insert(PlayerTable.TABLE_PLAYER, null, contentValues);
    }


    @Override
    public Player getPlayerForId(String playerId) {

        Player player = null;
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(PlayerTable.TABLE_PLAYER, PlayerTable.ALL_COLS_FOR_PLAYER_TABLE,
                PlayerTable.COLUMN_ID + " = ?", new String[] { playerId }, null, null, null, null);

        if (cursor.moveToFirst()) {
            player = cursorToPlayer(cursor);
        }

        cursor.close();

        return player;
    }


    @Override
    public List<Player> getAllLocalPlayers() {

        ArrayList<Player> players = new ArrayList<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(PlayerTable.TABLE_PLAYER, PlayerTable.ALL_COLS_FOR_PLAYER_TABLE, null,
                null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Player player = cursorToPlayer(cursor);
            player.setLocal(true);
            players.add(player);
            cursor.moveToNext();
        }

        cursor.close();

        return players;
    }


    @Override
    public void deleteLocalPlayer(Player player) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(PlayerTable.TABLE_PLAYER, PlayerTable.COLUMN_UUID + " = ? ",
            new String[] { player.getUUID() });
    }


    private Player cursorToPlayer(Cursor cursor) {

        Player player = new Player();

        player.set_id(cursor.getLong(0));
        player.setUUID(cursor.getString(1));
        player.setFirstName(cursor.getString(2));
        player.setNickName(cursor.getString(3));
        player.setLastName(cursor.getString(4));

        return player;
    }
}
