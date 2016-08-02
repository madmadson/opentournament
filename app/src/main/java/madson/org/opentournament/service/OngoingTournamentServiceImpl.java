package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.PlayerTable;
import madson.org.opentournament.db.TournamentPlayerTable;
import madson.org.opentournament.domain.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OngoingTournamentServiceImpl implements OngoingTournamentService {

    private OpenTournamentDBHelper openTournamentDBHelper;
    private PlayerService playerService;

    public OngoingTournamentServiceImpl(Context context) {

        Log.w(OngoingTournamentServiceImpl.class.getName(), "OngoingTournamentServiceImpl Constructor");

        OpenTournamentApplication application = (OpenTournamentApplication) context;

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        if (playerService == null) {
            playerService = application.getPlayerService();
        }

        deleteAllTournamentPlayers();
        createMockTournamentPlayers();
    }

    private void createMockTournamentPlayers() {

        addPlayerToTournament(1, 1);
        addPlayerToTournament(1, 2);
        addPlayerToTournament(1, 3);
        addPlayerToTournament(1, 4);
        addPlayerToTournament(1, 5);
        addPlayerToTournament(1, 6);
        addPlayerToTournament(1, 7);
        addPlayerToTournament(1, 8);
    }


    private void addPlayerToTournament(int tournament_id, int player_id) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put("tournament_id", tournament_id);
        contentValues.put("player_id", player_id);
        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);
    }


    private void deleteAllTournamentPlayers() {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, null);
    }


    @Override
    public List<Player> getPlayersForTournament(Long tournamentId) {

        ArrayList<Player> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                new String[] { "player_id" }, "tournament_id  = ?", new String[] { Long.toString(tournamentId) }, null,
                null, null);

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
    public void addPlayerToTournament(Player player, Long tournamentId) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("tournament_id", tournamentId);
        contentValues.put("player_id", player.getId());
        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);
    }


    @Override
    public void removePlayerFromTournament(Player player, Long tournamentId) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("tournament_id", tournamentId);
        contentValues.put("player_id", player.getId());
        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, "tournament_id  = ?  AND  player_id = ? ",
            new String[] { String.valueOf(tournamentId), String.valueOf(player.getId()) });
    }
}
