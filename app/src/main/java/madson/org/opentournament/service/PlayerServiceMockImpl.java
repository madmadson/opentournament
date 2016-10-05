package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.PlayerTable;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.TournamentPlayer;

import java.util.ArrayList;
import java.util.List;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerServiceMockImpl implements PlayerService {

    private OpenTournamentDBHelper openTournamentDBHelper;
    private String[] allColumns = {
        PlayerTable.COLUMN_ID, PlayerTable.COLUMN_FIRSTNAME, PlayerTable.COLUMN_NICKNAME, PlayerTable.COLUMN_LASTNAME
    };

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

        createPlayer(1, new Player("Tobias", "Madson", "Matt"));
        createPlayer(2, new Player("Christoph", "Zaziboy", "Scholl"));
        createPlayer(3, new Player("David", "Wildjack", "Voigt"));
        createPlayer(4, new Player("Andreas", "Ragegear", "Neugebauer"));
        createPlayer(5, new Player("Andreas", "Raskild", "Tonndorf"));
        createPlayer(6, new Player("Martina", "Bazinga", "Haug"));
        createPlayer(7, new Player("Tobias", "Zeus", "Rohrauer"));
        createPlayer(8, new Player("Yann", "Arcane", "Krehl"));
    }


    public void createPlayer(int id, Player player) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerTable.COLUMN_ID, id);
        contentValues.put(PlayerTable.COLUMN_FIRSTNAME, player.getFirstname());
        contentValues.put(PlayerTable.COLUMN_NICKNAME, player.getNickname());
        contentValues.put(PlayerTable.COLUMN_LASTNAME, player.getLastname());

        createPlayer(contentValues);
    }


    @Override
    public Player createLocalPlayer(Player player) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerTable.COLUMN_FIRSTNAME, player.getFirstname());
        contentValues.put(PlayerTable.COLUMN_NICKNAME, player.getNickname());
        contentValues.put(PlayerTable.COLUMN_LASTNAME, player.getLastname());

        long player_id = createPlayer(contentValues);
        player.set_id(player_id);

        return player;
    }


    @Override
    public Player getPlayerForId(String playerId) {

        Player player = null;
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(PlayerTable.TABLE_PLAYER, allColumns, "_id  = ?",
                new String[] { playerId }, null, null, null, null);

        if (cursor.moveToFirst()) {
            player = cursorToPlayer(cursor);
        }

        cursor.close();
        readableDatabase.close();

        return player;
    }


    @Override
    public List<Player> getAllLocalPlayers() {

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


    @Override
    public List<Player> getAllLocalPlayersNotInTournament(List<TournamentPlayer> listOfPlayers) {

        ArrayList<Player> players = new ArrayList<>();

        String filterString = "";

        for (TournamentPlayer player : listOfPlayers) {
            String playerId = String.valueOf(player.getPlayerId()) + ",";

            filterString += playerId;
        }

        if (listOfPlayers.size() > 0) {
            filterString = filterString.substring(0, filterString.length() - 1);

            SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

            Cursor cursor = readableDatabase.query(PlayerTable.TABLE_PLAYER, allColumns,
                    "_id  NOT IN (" + filterString + ")", null, null, null, null);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Player player = cursorToPlayer(cursor);

                players.add(player);
                cursor.moveToNext();
            }

            cursor.close();
            readableDatabase.close();

            return players;
        } else {
            return getAllLocalPlayers();
        }
    }


    private long createPlayer(ContentValues contentValues) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        long insertedId = writableDatabase.insert(PlayerTable.TABLE_PLAYER, null, contentValues);

        writableDatabase.close();

        return insertedId;
    }


    private Player cursorToPlayer(Cursor cursor) {

        Player player = new Player();

        player.set_id(cursor.getLong(0));
        player.setOnlineUUID(cursor.getString(1));
        player.setFirstname(cursor.getString(2));
        player.setNickname(cursor.getString(3));
        player.setLastname(cursor.getString(4));

        return player;
    }
}
