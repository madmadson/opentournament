package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.db.warmachine.WarmachineTournamentGameTable;
import madson.org.opentournament.db.warmachine.WarmachineTournamentPlayerTable;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentGame;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentPlayer;
import madson.org.opentournament.service.warmachine.WarmachinePlayerComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OngoingTournamentServiceImpl implements OngoingTournamentService {

    private OpenTournamentDBHelper openTournamentDBHelper;
    private PlayerService playerService;
    private TournamentService tournamentService;

    public OngoingTournamentServiceImpl(Context context) {

        Log.w(OngoingTournamentServiceImpl.class.getName(), "WarmachineTournamentServiceImpl Constructor");

        OpenTournamentApplication application = (OpenTournamentApplication) context;

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        if (playerService == null) {
            playerService = application.getPlayerService();
        }

        if (tournamentService == null) {
            tournamentService = application.getTournamentService();
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
        db.insert(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER, null, contentValues);
    }


    private void deleteAllTournamentPlayers() {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        db.delete(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER, null, null);
    }


    @Override
    public List<WarmachineTournamentPlayer> getPlayersForWarmachineTournament(Long tournamentId) {

        ArrayList<WarmachineTournamentPlayer> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER,
                WarmachineTournamentPlayerTable.ALL_COLS_FOR_WARMACHINE_TOURNAMENT_PLAYER, "tournament_id  = ?",
                new String[] { Long.toString(tournamentId) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Player player = playerService.getPlayerForId((long) cursor.getInt(2));
            WarmachineTournamentPlayer warmachineTournamentPlayer = new WarmachineTournamentPlayer(player);
            warmachineTournamentPlayer.setScore(cursor.getInt(3));
            warmachineTournamentPlayer.setSos(cursor.getInt(4));
            warmachineTournamentPlayer.setControl_points(cursor.getInt(5));
            warmachineTournamentPlayer.setVictory_points(cursor.getInt(6));
            players.add(warmachineTournamentPlayer);
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
        db.insert(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER, null, contentValues);
    }


    @Override
    public void removePlayerFromTournament(Player player, Long tournamentId) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("tournament_id", tournamentId);
        contentValues.put("player_id", player.getId());
        db.delete(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER,
            "tournament_id  = ?  AND  player_id = ? ",
            new String[] { String.valueOf(tournamentId), String.valueOf(player.getId()) });
    }


    @Override
    public List<WarmachineTournamentGame> getPairingsForTournament(Long tournamentId, int round) {

        List<WarmachineTournamentGame> warmachineTournamentGames = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineTournamentGameTable.TABLE_TOURNAMENT_GAME,
                WarmachineTournamentGameTable.ALL_COLS_FOR_TOURNAMENT_GAME, "tournament_id  = ? AND round = ?",
                new String[] { String.valueOf(tournamentId), String.valueOf(round) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            WarmachineTournamentGame pairing = cursorToTournamentPairing(cursor);
            warmachineTournamentGames.add(pairing);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return warmachineTournamentGames;
    }


    @Override
    public List<WarmachineTournamentGame> createPairingForRound(Long tournamentId, int round) {

        List<WarmachineTournamentPlayer> playersForTournament = getPlayersForWarmachineTournament(tournamentId);

        // uneven number of players
        if (playersForTournament.size() % 2 == 1) {
            WarmachineTournamentPlayer warmachineTournamentDummyPlayer = new WarmachineTournamentPlayer(new Player(
                        "dummy", "bye", "player"));
            warmachineTournamentDummyPlayer.setScore(0);
            warmachineTournamentDummyPlayer.setSos(0);
            warmachineTournamentDummyPlayer.setControl_points(0);
            warmachineTournamentDummyPlayer.setVictory_points(0);
            playersForTournament.add(warmachineTournamentDummyPlayer);
        }

        Collections.shuffle(playersForTournament);
        Collections.sort(playersForTournament, new WarmachinePlayerComparator());

        ArrayList<WarmachineTournamentGame> warmachineTournamentGames = new ArrayList<>(playersForTournament.size());

        for (int i = 0; i <= (playersForTournament.size() - 1); i = i + 2) {
            WarmachineTournamentGame warmachineTournamentGame = new WarmachineTournamentGame();
            warmachineTournamentGame.setTournament_id(tournamentId.intValue());
            warmachineTournamentGame.setRound(round);

            WarmachineTournamentPlayer player_one = playersForTournament.get(i);
            warmachineTournamentGame.setPlayer_one_id(player_one.getId());
            warmachineTournamentGame.setPlayer_one_full_name(player_one.getFirstname() + " \""
                + player_one.getNickname() + "\" " + player_one.getLastname());

//            warmachineTournamentGame.setPlayer_one_score(player_one.getScore());
//            warmachineTournamentGame.setPlayer_one_control_points(player_one.getControl_points());
//            warmachineTournamentGame.setPlayer_one_victory_points(player_one.getVictory_points());

            WarmachineTournamentPlayer player_two = playersForTournament.get(i + 1);
            warmachineTournamentGame.setPlayer_two_id(player_two.getId());
            warmachineTournamentGame.setPlayer_two_full_name(player_two.getFirstname() + " \""
                + player_two.getNickname() + "\" " + player_two.getLastname());
//            warmachineTournamentGame.setPlayer_two_score(player_two.getScore());
//            warmachineTournamentGame.setPlayer_two_control_points(player_two.getControl_points());
//            warmachineTournamentGame.setPlayer_two_victory_points(player_two.getVictory_points());

            warmachineTournamentGames.add(warmachineTournamentGame);

            // persist pairing
            insertWarmachineTournamentPairing(warmachineTournamentGame);
        }

        makeTournamentRoundActual(tournamentId, round);

        return warmachineTournamentGames;
    }


    private void makeTournamentRoundActual(Long tournamentId, int round) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, round);
        tournamentService.editTournament(tournamentId, contentValues);
    }


    @Override
    public List<Player> getPlayersForTournament(long tournamentId) {

        ArrayList<Player> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER,
                new String[] { WarmachineTournamentPlayerTable.COLUMN_PLAYER_ID }, "tournament_id  = ?",
                new String[] { Long.toString(tournamentId) }, null, null, null);

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
    public WarmachineTournamentGame getPairingsForTournament(long pairing_id) {

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineTournamentGameTable.TABLE_TOURNAMENT_GAME,
                WarmachineTournamentGameTable.ALL_COLS_FOR_TOURNAMENT_GAME, "_id  = ? ",
                new String[] { String.valueOf(pairing_id) }, null, null, null);

        cursor.moveToFirst();

        WarmachineTournamentGame pairing = cursorToTournamentPairing(cursor);

        cursor.close();
        readableDatabase.close();

        return pairing;
    }


    @Override
    public void saveGameResult(WarmachineTournamentGame game) {

        Log.i(this.getClass().getName(), "save game: " + game);

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_ONE_SCORE, game.getPlayer_one_score());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_TWO_SCORE, game.getPlayer_two_score());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_ONE_CONTROL_POINTS,
            game.getPlayer_one_control_points());
        db.update(WarmachineTournamentGameTable.TABLE_TOURNAMENT_GAME, contentValues, "_id = ? ",
            new String[] { String.valueOf(game.get_id()) });
    }


    private void insertWarmachineTournamentPairing(WarmachineTournamentGame pairing) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(WarmachineTournamentGameTable.COLUMN_TOURNAMENT_ID, pairing.getTournament_id());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_ROUND, pairing.getRound());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_ONE_ID, pairing.getPlayer_one_id());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_TWO_ID, pairing.getPlayer_two_id());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_ONE_FULL_NAME, pairing.getPlayer_one_full_name());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_TWO_FULL_NAME, pairing.getPlayer_two_full_name());
        db.insert(WarmachineTournamentGameTable.TABLE_TOURNAMENT_GAME, null, contentValues);
    }


    private WarmachineTournamentGame cursorToTournamentPairing(Cursor cursor) {

        WarmachineTournamentGame pairing = new WarmachineTournamentGame(cursor.getLong(0));

        pairing.setTournament_id(cursor.getInt(1));
        pairing.setRound(cursor.getInt(2));

        pairing.setPlayer_one_id(cursor.getInt(3));
        pairing.setPlayer_one_full_name(cursor.getString(4));
        pairing.setPlayer_one_score(cursor.getInt(5));
        pairing.setPlayer_one_control_points(cursor.getInt(6));
        pairing.setPlayer_one_victory_points(cursor.getInt(7));

        pairing.setPlayer_two_id(cursor.getInt(8));
        pairing.setPlayer_two_full_name(cursor.getString(9));
        pairing.setPlayer_two_score(cursor.getInt(10));
        pairing.setPlayer_two_control_points(cursor.getInt(11));
        pairing.setPlayer_two_victory_points(cursor.getInt(12));

        return pairing;
    }
}
