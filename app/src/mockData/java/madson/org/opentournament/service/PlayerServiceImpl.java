package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.PlayerDBHelper;
import madson.org.opentournament.db.TournamentDBHelper;
import madson.org.opentournament.db.TournamentPlayerDBHelper;
import madson.org.opentournament.domain.Player;

import org.joda.time.DateTime;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerServiceImpl implements PlayerService {

    private PlayerDBHelper playerDBHelper;
    private SQLiteDatabase database;
    private String[] allColumns = {
        PlayerDBHelper.COLUMN_ID, PlayerDBHelper.COLUMN_FIRSTNAME, PlayerDBHelper.COLUMN_LASTNAME
    };

    public PlayerServiceImpl(Context context) {

        Log.w(PlayerServiceImpl.class.getName(), "PlayerServiceImpl Constructor");

        if (playerDBHelper == null) {
            playerDBHelper = new PlayerDBHelper(context);
        }

        open();
        // createMockPlayers();
    }

    private void createMockPlayers() {

        createPlayer(new Player("Tobias", "Madson", "Matt"));
        createPlayer(new Player("Christopf", "Zaziboy", "Scholl"));
        createPlayer(new Player("David", "Wildjack", "Voigt"));
    }


    @Override
    public void createPlayer(Player player) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerDBHelper.COLUMN_FIRSTNAME, player.getFirstname());
        contentValues.put(PlayerDBHelper.COLUMN_NICKNAME, player.getNickname());
        contentValues.put(PlayerDBHelper.COLUMN_LASTNAME, player.getLastname());

        createPlayer(contentValues);
    }


    @Override
    public Player getPlayerForId(Long playerId) {

        Cursor cursor = database.query(PlayerDBHelper.TABLE_PLAYER,
                new String[] {
                    PlayerDBHelper.COLUMN_FIRSTNAME, PlayerDBHelper.COLUMN_NICKNAME, PlayerDBHelper.COLUMN_LASTNAME
                }, null, null, null, null, null);

        Player player = new Player(cursor.getString(0), cursor.getString(1), cursor.getString(2));

        cursor.close();

        return player;
    }


    private void createPlayer(ContentValues contentValues) {

        database.insert(PlayerDBHelper.TABLE_PLAYER, null, contentValues);
    }


    public void open() throws SQLException {

        if (database == null) {
            database = playerDBHelper.getWritableDatabase();
        }
    }
}
