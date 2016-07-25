package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.PlayerTable;
import madson.org.opentournament.domain.Player;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerServiceImpl implements PlayerService {

    private OpenTournamentDBHelper openTournamentDBHelper;
    private String[] allColumns = { PlayerTable.COLUMN_ID, PlayerTable.COLUMN_FIRSTNAME, PlayerTable.COLUMN_LASTNAME };

    public PlayerServiceImpl(Context context) {

        Log.w(PlayerServiceImpl.class.getName(), "PlayerServiceImpl Constructor");

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        createMockPlayers();
    }

    private void createMockPlayers() {

        createPlayer(new Player("Tobias", "Madson", "Matt"));
        createPlayer(new Player("Christopf", "Zaziboy", "Scholl"));
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

        Cursor cursor = readableDatabase.query(PlayerTable.TABLE_PLAYER,
                new String[] { PlayerTable.COLUMN_FIRSTNAME, PlayerTable.COLUMN_NICKNAME, PlayerTable.COLUMN_LASTNAME },
                null, null, null, null, null);

        Player player = new Player(cursor.getString(0), cursor.getString(1), cursor.getString(2));

        cursor.close();
        readableDatabase.close();

        return player;
    }


    private void createPlayer(ContentValues contentValues) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.insert(PlayerTable.TABLE_PLAYER, null, contentValues);

        writableDatabase.close();
    }
}
