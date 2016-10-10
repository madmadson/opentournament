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
import java.util.UUID;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerServiceMockImpl implements PlayerService {

    private OpenTournamentDBHelper openTournamentDBHelper;

    public PlayerServiceMockImpl(Context context) {

        Log.w(PlayerServiceMockImpl.class.getName(), "PlayerServiceImpl Constructor");

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        deleteAllPlayers();
        createMockPlayers();
    }

    private void deleteAllPlayers() {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(PlayerTable.TABLE_PLAYER, null, null);
        writableDatabase.close();
    }


    private void createMockPlayers() {

        createPlayer(1, "Tobias", "Madson", "Matt");
        createPlayer(2, "Christoph", "Zaziboy", "Scholl");
        createPlayer(3, "David", "Wildjack", "Voigt");
        createPlayer(4, "Andreas", "Ragegear", "Neugebauer");
        createPlayer(5, "Andreas", "Raskild", "Tonndorf");
        createPlayer(6, "Martina", "Bazinga", "Haug");
        createPlayer(7, "Tobias", "Zeus", "Rohrauer");
        createPlayer(8, "Yann", "Arcane", "Krehl");
    }


    public void createPlayer(int id, String firstName, String nickName, String lastName) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerTable.COLUMN_ID, id);
        contentValues.put(PlayerTable.COLUMN_FIRSTNAME, firstName);
        contentValues.put(PlayerTable.COLUMN_NICKNAME, nickName);
        contentValues.put(PlayerTable.COLUMN_LASTNAME, lastName);
        contentValues.put(PlayerTable.COLUMN_UUID, id);

        createPlayer(contentValues);
    }


    @Override
    public void createLocalPlayer(Player player) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerTable.COLUMN_FIRSTNAME, player.getFirstName());
        contentValues.put(PlayerTable.COLUMN_NICKNAME, player.getNickName());
        contentValues.put(PlayerTable.COLUMN_LASTNAME, player.getLastName());
        contentValues.put(PlayerTable.COLUMN_UUID, UUID.randomUUID().toString());

        createPlayer(contentValues);
    }


    @Override
    public Player getPlayerForId(String playerId) {

        Player player = null;
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(PlayerTable.TABLE_PLAYER, PlayerTable.ALL_COLS_FOR_PLAYER_TABLE,
                PlayerTable.COLUMN_ID + " = ?", new String[] { playerId }, null, null, null, null);

        if (cursor.moveToFirst()) {
            player = cursorToPlayer(cursor);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return player;
    }


    @Override
    public List<Player> getAllLocalPlayers() {

        List<Player> players = new ArrayList<>();

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
        readableDatabase.close();

        return players;
    }


    private void createPlayer(ContentValues contentValues) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.insert(PlayerTable.TABLE_PLAYER, null, contentValues);

        writableDatabase.close();
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
