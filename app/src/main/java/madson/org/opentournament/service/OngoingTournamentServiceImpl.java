package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.db.warmachine.WarmachineRankingTable;
import madson.org.opentournament.db.warmachine.WarmachineTournamentGameTable;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentGame;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentRanking;
import madson.org.opentournament.service.warmachine.WarmachinePlayerComparator;
import madson.org.opentournament.service.warmachine.WarmachineRankingComparator;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        BaseApplication application = (BaseApplication) context;

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
        contentValues.put("round", 0);
        db.insert(WarmachineRankingTable.TABLE_WARMACHINE_RANKING, null, contentValues);
    }


    private void deleteAllTournamentPlayers() {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        db.delete(WarmachineRankingTable.TABLE_WARMACHINE_RANKING, null, null);
    }


    @Override
    public List<WarmachineTournamentRanking> getRankingForRound(Long tournamentId, int round) {

        ArrayList<WarmachineTournamentRanking> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineRankingTable.TABLE_WARMACHINE_RANKING,
                WarmachineRankingTable.ALL_COLS_FOR_RANKING_TABLE, "tournament_id  = ? AND round = ?",
                new String[] { Long.toString(tournamentId), String.valueOf(round) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Player player = playerService.getPlayerForId((long) cursor.getInt(2));

            WarmachineTournamentRanking warmachineTournamentPlayer = new WarmachineTournamentRanking();
            warmachineTournamentPlayer.setPlayer_id(cursor.getInt(2));
            warmachineTournamentPlayer.setFirstname(player.getFirstname());
            warmachineTournamentPlayer.setNickname(player.getNickname());
            warmachineTournamentPlayer.setLastname(player.getLastname());
            warmachineTournamentPlayer.setRound(cursor.getInt(3));
            warmachineTournamentPlayer.setScore(cursor.getInt(4));
            warmachineTournamentPlayer.setSos(cursor.getInt(5));
            warmachineTournamentPlayer.setControl_points(cursor.getInt(6));
            warmachineTournamentPlayer.setVictory_points(cursor.getInt(7));
            players.add(warmachineTournamentPlayer);
            cursor.moveToNext();
        }

        Collections.sort(players, new WarmachineRankingComparator());

        cursor.close();
        readableDatabase.close();

        return players;
    }


    @Override
    public void addPlayerToTournament(Player player, Long tournamentId) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("tournament_id", tournamentId);
        contentValues.put("player_id", player.get_id());
        contentValues.put("round", 0);
        db.insert(WarmachineRankingTable.TABLE_WARMACHINE_RANKING, null, contentValues);
    }


    @Override
    public void removePlayerFromTournament(Player player, Long tournamentId) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("tournament_id", tournamentId);
        contentValues.put("player_id", player.get_id());
        db.delete(WarmachineRankingTable.TABLE_WARMACHINE_RANKING, "tournament_id  = ?  AND  player_id = ? ",
            new String[] { String.valueOf(tournamentId), String.valueOf(player.get_id()) });
    }


    @Override
    public List<WarmachineTournamentGame> getGameForRound(Long tournamentId, int round) {

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

        List<WarmachineTournamentRanking> playersWithRankingFromRound = getRankingForRound(tournamentId, round);

        // uneven number of players
        if (playersWithRankingFromRound.size() % 2 == 1) {
            WarmachineTournamentRanking warmachineTournamentDummyPlayer = new WarmachineTournamentRanking();
            playersWithRankingFromRound.add(warmachineTournamentDummyPlayer);
        }

        Collections.shuffle(playersWithRankingFromRound);
        Collections.sort(playersWithRankingFromRound, new WarmachinePlayerComparator());

        List<WarmachineTournamentGame> warmachineTournamentGames = new ArrayList<>(playersWithRankingFromRound.size());

        for (int i = 0; i <= (playersWithRankingFromRound.size() - 1); i = i + 2) {
            Log.i(this.getClass().getName(), "player1:" + playersWithRankingFromRound.get(i));

            WarmachineTournamentGame warmachineTournamentGame = new WarmachineTournamentGame();
            warmachineTournamentGame.setTournament_id(tournamentId.intValue());
            warmachineTournamentGame.setRound(round);

            WarmachineTournamentRanking player_one = playersWithRankingFromRound.get(i);
            warmachineTournamentGame.setPlayer_one_id((int) player_one.getPlayer_id());
            warmachineTournamentGame.setPlayer_one_full_name(player_one.getFirstname() + " \""
                + player_one.getNickname() + "\" " + player_one.getLastname());

            Log.i(this.getClass().getName(), "player2:" + playersWithRankingFromRound.get(i + 1));

            WarmachineTournamentRanking player_two = playersWithRankingFromRound.get(i + 1);
            warmachineTournamentGame.setPlayer_two_id((int) player_two.getPlayer_id());
            warmachineTournamentGame.setPlayer_two_full_name(player_two.getFirstname() + " \""
                + player_two.getNickname() + "\" " + player_two.getLastname());

            warmachineTournamentGames.add(warmachineTournamentGame);
        }

        // persist pairing and new round
        insertGames(warmachineTournamentGames);
        makeTournamentRoundActual(tournamentId, round);

        return warmachineTournamentGames;
    }


    private void makeTournamentRoundActual(Long tournamentId, int round) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentTable.COLUMN_ACTUAL_ROUND, round);
        tournamentService.editTournament(tournamentId, contentValues);
    }


    @Override
    public List<Player> getAllPlayersForTournament(long tournamentId) {

        ArrayList<Player> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineRankingTable.TABLE_WARMACHINE_RANKING,
                new String[] { WarmachineRankingTable.COLUMN_PLAYER_ID }, "tournament_id  = ?",
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
    public WarmachineTournamentGame getGameForId(long game_id) {

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineTournamentGameTable.TABLE_TOURNAMENT_GAME,
                WarmachineTournamentGameTable.ALL_COLS_FOR_TOURNAMENT_GAME, "_id  = ? ",
                new String[] { String.valueOf(game_id) }, null, null, null);

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
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_ONE_CONTROL_POINTS,
            game.getPlayer_one_control_points());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_ONE_VICTORY_POINTS,
            game.getPlayer_one_victory_points());

        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_TWO_SCORE, game.getPlayer_two_score());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_TWO_CONTROL_POINTS,
            game.getPlayer_two_control_points());
        contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_TWO_VICTORY_POINTS,
            game.getPlayer_two_victory_points());

        db.update(WarmachineTournamentGameTable.TABLE_TOURNAMENT_GAME, contentValues, "_id = ? ",
            new String[] { String.valueOf(game.get_id()) });
    }


    @Override
    public void createRankingForRound(long tournament_id, int round_for_calculation) {

        List<Player> allPlayersForTournament = getAllPlayersForTournament(tournament_id);
        Map<Long, WarmachineTournamentRanking> mapOfPlayers = new HashMap<>();

        for (Player player : allPlayersForTournament) {
            mapOfPlayers.put(player.get_id(),
                new WarmachineTournamentRanking(tournament_id, player.get_id(), round_for_calculation));
        }

        List<WarmachineTournamentGame> gamesOfPlayerForTournament = getAllGamesForTournamentTillRound(tournament_id,
                round_for_calculation);

        for (WarmachineTournamentGame game : gamesOfPlayerForTournament) {
            WarmachineTournamentRanking playerOne = mapOfPlayers.get(game.getPlayer_one_id());

            playerOne.setScore(playerOne.getScore() + game.getPlayer_one_score());
            playerOne.setControl_points(playerOne.getControl_points() + game.getPlayer_one_control_points());
            playerOne.setVictory_points(playerOne.getVictory_points() + game.getPlayer_one_victory_points());

            WarmachineTournamentRanking playerTwo = mapOfPlayers.get(game.getPlayer_two_id());

            playerTwo.setScore(playerTwo.getScore() + game.getPlayer_two_score());
            playerTwo.setControl_points(playerTwo.getControl_points() + game.getPlayer_two_control_points());
            playerTwo.setVictory_points(playerTwo.getVictory_points() + game.getPlayer_two_victory_points());

            // for SOS
            playerOne.getListOfOpponents().add(playerTwo.getPlayer_id());
            playerTwo.getListOfOpponents().add(playerOne.getPlayer_id());

            // overwrite map
            mapOfPlayers.put(playerOne.getPlayer_id(), playerOne);
            mapOfPlayers.put(playerTwo.getPlayer_id(), playerTwo);
        }

        calculateSoSForPlayerMap(mapOfPlayers);

        insertWarmachinePlayerRankingForRound(mapOfPlayers);
    }


    private void calculateSoSForPlayerMap(Map<Long, WarmachineTournamentRanking> mapOfPlayers) {

        for (WarmachineTournamentRanking ranking : mapOfPlayers.values()) {
            List<Long> listOfOpponents = ranking.getListOfOpponents();

            int sos = 0;

            for (long opponent_id : listOfOpponents) {
                sos = sos + mapOfPlayers.get(opponent_id).getScore();
            }

            ranking.setSos(sos);

            mapOfPlayers.put(ranking.getPlayer_id(), ranking);
        }
    }


    private List<WarmachineTournamentGame> getAllGamesForTournamentTillRound(long tournament_id, int round) {

        List<WarmachineTournamentGame> warmachineTournamentGames = new ArrayList<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineTournamentGameTable.TABLE_TOURNAMENT_GAME,
                WarmachineTournamentGameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                "tournament_id  = ? AND round <= ? AND (player_one_score != 0 OR player_two_score != 0) ",
                new String[] { String.valueOf(tournament_id), String.valueOf(round) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            WarmachineTournamentGame game = cursorToTournamentPairing(cursor);
            warmachineTournamentGames.add(game);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return warmachineTournamentGames;
    }


    private void insertWarmachinePlayerRankingForRound(Map<Long, WarmachineTournamentRanking> newRankingForRoundList) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        for (WarmachineTournamentRanking ranking : newRankingForRoundList.values()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WarmachineRankingTable.COLUMN_TOURNAMENT_ID, ranking.getTournament_id());
            contentValues.put(WarmachineRankingTable.COLUMN_PLAYER_ID, ranking.getPlayer_id());
            contentValues.put(WarmachineRankingTable.COLUMN_ROUND, ranking.getRound());

            contentValues.put(WarmachineRankingTable.COLUMN_SCORE, ranking.getScore());
            contentValues.put(WarmachineRankingTable.COLUMN_CONTROL_POINTS, ranking.getControl_points());
            contentValues.put(WarmachineRankingTable.COLUMN_VICTORY_POINTS, ranking.getVictory_points());

            contentValues.put(WarmachineRankingTable.COLUMN_SOS, ranking.getSos());

            db.insert(WarmachineRankingTable.TABLE_WARMACHINE_RANKING, null, contentValues);
        }

        db.close();
    }


    private void insertGames(List<WarmachineTournamentGame> pairings) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        for (WarmachineTournamentGame pairing : pairings) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WarmachineTournamentGameTable.COLUMN_TOURNAMENT_ID, pairing.getTournament_id());
            contentValues.put(WarmachineTournamentGameTable.COLUMN_ROUND, pairing.getRound());
            contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_ONE_ID, pairing.getPlayer_one_id());
            contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_TWO_ID, pairing.getPlayer_two_id());
            contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_ONE_FULL_NAME,
                pairing.getPlayer_one_full_name());
            contentValues.put(WarmachineTournamentGameTable.COLUMN_PLAYER_TWO_FULL_NAME,
                pairing.getPlayer_two_full_name());
            db.insert(WarmachineTournamentGameTable.TABLE_TOURNAMENT_GAME, null, contentValues);
        }

        db.close();
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
