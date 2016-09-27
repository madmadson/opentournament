package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.support.annotation.NonNull;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.config.MapOfPairingConfig;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.db.GameTable;
import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OngoingTournamentServiceImpl implements OngoingTournamentService {

    private OpenTournamentDBHelper openTournamentDBHelper;
    private PlayerService playerService;
    private TournamentService tournamentService;
    private TournamentPlayerService tournamentPlayerService;
    private RankingService rankingService;
    private boolean playerWithSameTeamDontPlayAgainstEachOther;
    private boolean playerDontPlayTwiceAgainstEachOther;

    public OngoingTournamentServiceImpl(Context context) {

        Log.d(OngoingTournamentServiceImpl.class.getName(), "WarmachineTournamentServiceImpl Constructor");

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

        if (tournamentPlayerService == null) {
            tournamentPlayerService = application.getTournamentPlayerService();
        }

        if (rankingService == null) {
            rankingService = application.getRankingService();
        }
    }

    @Override
    public List<Game> getGamesForRound(Tournament tournament, int round) {

        Map<String, TournamentPlayer> allPlayerMapForTournament = tournamentPlayerService.getAllPlayerMapForTournament(
                tournament);

        List<Game> gamesToReturn = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_TOURNAMENT_ID + "  = ? ", new String[] { String.valueOf(tournament.get_id()) }, null,
                null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = Game.cursorToGame(cursor);

            if (game.getTournament_round() < round) {
                TournamentPlayer playerOne = allPlayerMapForTournament.get(game.getRealPlayerOneId());
                TournamentPlayer playerTwo = allPlayerMapForTournament.get(game.getRealPlayerTwoId());

                playerOne.getListOfOpponentsIds().add(playerTwo.getRealPlayerId());
                playerTwo.getListOfOpponentsIds().add(playerOne.getRealPlayerId());

                allPlayerMapForTournament.put(game.getRealPlayerOneId(), playerOne);
                allPlayerMapForTournament.put(game.getRealPlayerTwoId(), playerTwo);
            }

            if (game.getTournament_round() == round) {
                game.setPlayer1(allPlayerMapForTournament.get(game.getRealPlayerOneId()));
                game.setPlayer2(allPlayerMapForTournament.get(game.getRealPlayerTwoId()));

                gamesToReturn.add(game);
            }

            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return gamesToReturn;
    }


    @Override
    public boolean createGamesForRound(Tournament tournament, int round, Map<String, TournamentRanking> rankingForRound,
        Map<String, PairingOption> pairingOptions) {

        List<TournamentRanking> rankings = new ArrayList<>(rankingForRound.values());

        playerDontPlayTwiceAgainstEachOther = pairingOptions.containsKey(
                MapOfPairingConfig.PLAYER_NOT_PLAY_TWICE_AGAINST_EACH_OVER)
            && pairingOptions.get(MapOfPairingConfig.PLAYER_NOT_PLAY_TWICE_AGAINST_EACH_OVER).isActive();

        playerWithSameTeamDontPlayAgainstEachOther = pairingOptions.containsKey(
                MapOfPairingConfig.PLAYER_WITH_SAME_TEAM_NOT_PLAY_AGAINST_EACH_OTHER)
            && pairingOptions.get(MapOfPairingConfig.PLAYER_WITH_SAME_TEAM_NOT_PLAY_AGAINST_EACH_OTHER)
                .isActive();

        List<Game> games = new ArrayList<>(rankings.size() / 2);

        Collections.shuffle(rankings);
        Collections.sort(rankings, new Comparator<TournamentRanking>() {

                @Override
                public int compare(TournamentRanking o1, TournamentRanking o2) {

                    return o2.getScore() - o1.getScore();
                }
            });

        List<TournamentRanking> copyRanking = new ArrayList<>(rankings);

        boolean match = match(copyRanking, games, tournament, round);

        if (!match) {
            return false;
        }

        Collections.reverse(games);

        Log.i(this.getClass().getName(), "finished pairing for new round");

        // persist pairing and new round
        insertGames(games);

        return true;
    }


    public boolean match(List<TournamentRanking> players, List<Game> games, Tournament tournament, int round) {

        if (players.isEmpty()) {
            return true;
        }

        for (int i = 0; i < players.size() - 1; i++) {
            TournamentRanking player1 = players.get(i);

            for (int j = i + 1; j < players.size(); j++) {
                TournamentRanking player2 = players.get(j);

                if (playerDontPlayTwiceAgainstEachOther) {
                    if (player1.getListOfOpponentsPlayerIds().contains(player2.getRealPlayerId())) {
                        Log.i(this.getClass().getName(), "" + player1 + " VS " + player2 + " not possible");

                        continue;
                    }
                }

                if (playerWithSameTeamDontPlayAgainstEachOther) {
                    if (player1.getTournamentPlayer().getTeamname().equals(
                                player2.getTournamentPlayer().getTeamname())) {
                        Log.i(this.getClass().getName(), "" + player1 + " VS " + player2 + " not possible");

                        continue;
                    }
                }

                if (player1.getScore() - 1 > player2.getScore()) {
                    continue;
                }

                ArrayList<TournamentRanking> copyPlayer = new ArrayList<>(players);
                copyPlayer.remove(player1);
                copyPlayer.remove(player2);

                boolean match = match(copyPlayer, games, tournament, round);

                if (match) {
                    Game game = new Game();

                    game.setTournament_id(tournament.get_id());
                    game.setTournament_round(round);

                    game.setPlayer1(player1.getTournamentPlayer());
                    game.setPlayer_one_id(player1.getPlayer_id());
                    game.setPlayer_one_online_uuid(player1.getPlayer_online_uuid());

                    game.setPlayer2(player2.getTournamentPlayer());
                    game.setPlayer_two_id(player2.getPlayer_id());
                    game.setPlayer_two_online_uuid(player2.getPlayer_online_uuid());

                    games.add(game);

                    return true;
                }
            }
        }

        for (int i = 0; i < players.size() - 1; i++) {
            TournamentRanking player1 = players.get(i);

            for (int j = i + 1; j < players.size(); j++) {
                TournamentRanking player2 = players.get(j);

                if (playerDontPlayTwiceAgainstEachOther) {
                    if (player1.getListOfOpponentsPlayerIds().contains(player2.getRealPlayerId())) {
                        Log.i(this.getClass().getName(), "" + player1 + " VS " + player2 + " not possible");

                        continue;
                    }
                }

                if (playerWithSameTeamDontPlayAgainstEachOther) {
                    if (player1.getTournamentPlayer().getTeamname().equals(
                                player2.getTournamentPlayer().getTeamname())) {
                        Log.i(this.getClass().getName(), "" + player1 + " VS " + player2 + " not possible");

                        continue;
                    }
                }

                ArrayList<TournamentRanking> copyPlayer = new ArrayList<>(players);
                copyPlayer.remove(player1);
                copyPlayer.remove(player2);

                boolean match = match(copyPlayer, games, tournament, round);

                if (match) {
                    Game game = new Game();

                    game.setTournament_id(tournament.get_id());
                    game.setTournament_round(round);

                    game.setPlayer1(player1.getTournamentPlayer());
                    game.setPlayer_one_id(player1.getPlayer_id());
                    game.setPlayer_one_online_uuid(player1.getPlayer_online_uuid());

                    game.setPlayer2(player2.getTournamentPlayer());
                    game.setPlayer_two_id(player2.getPlayer_id());
                    game.setPlayer_two_online_uuid(player2.getPlayer_online_uuid());

                    games.add(game);

                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public Game getGameForId(long game_id) {

        Log.i(this.getClass().getName(), "get game for id:" + game_id);

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_ID + " = ? ", new String[] { String.valueOf(game_id) }, null, null, null);

        cursor.moveToFirst();

        Game game = null;

        while (!cursor.isAfterLast()) {
            game = Game.cursorToGame(cursor);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        Log.i(this.getClass().getName(), "game loaded sucessfully: " + game);

        return game;
    }


    @Override
    public Game saveGameResult(Game game) {

        Log.i(this.getClass().getName(), "save game: " + game);

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        if (game.getPlayer1().getPlayer_online_uuid() != null) {
            contentValues.put(GameTable.COLUMN_PLAYER_ONE_ONLINE_UUID, game.getPlayer1().getPlayer_online_uuid());
            game.setPlayer_one_online_uuid(game.getPlayer1().getPlayer_online_uuid());
        } else {
            contentValues.put(GameTable.COLUMN_PLAYER_ONE_ID, game.getPlayer1().getPlayer_id());
            game.setPlayer_one_id(game.getPlayer1().getPlayer_id());
        }

        if (game.getPlayer1().getPlayer_online_uuid() != null) {
            contentValues.put(GameTable.COLUMN_PLAYER_TWO_ONLINE_UUID, game.getPlayer2().getPlayer_online_uuid());
            game.setPlayer_two_online_uuid(game.getPlayer2().getPlayer_online_uuid());
        } else {
            contentValues.put(GameTable.COLUMN_PLAYER_TWO_ID, game.getPlayer2().getPlayer_id());
            game.setPlayer_two_id(game.getPlayer2().getPlayer_id());
        }

        contentValues.put(GameTable.COLUMN_PLAYER_ONE_SCORE, game.getPlayer_one_score());
        contentValues.put(GameTable.COLUMN_PLAYER_ONE_CONTROL_POINTS, game.getPlayer_one_control_points());
        contentValues.put(GameTable.COLUMN_PLAYER_ONE_VICTORY_POINTS, game.getPlayer_one_victory_points());

        contentValues.put(GameTable.COLUMN_PLAYER_TWO_SCORE, game.getPlayer_two_score());
        contentValues.put(GameTable.COLUMN_PLAYER_TWO_CONTROL_POINTS, game.getPlayer_two_control_points());
        contentValues.put(GameTable.COLUMN_PLAYER_TWO_VICTORY_POINTS, game.getPlayer_two_victory_points());
        contentValues.put(GameTable.COLUMN_FINISHED, game.isFinished());

        db.update(GameTable.TABLE_TOURNAMENT_GAME, contentValues, GameTable.COLUMN_ID + " = ? ",
            new String[] { String.valueOf(game.get_id()) });

        db.close();

        return game;
    }


    @Override
    public boolean checkAllGamesAreFinishedForRound(Tournament tournament, int round) {

        List<Game> gamesForRound = getGamesForRound(tournament, round);

        boolean allGamesFinished = true;

        for (Game game : gamesForRound) {
            if (!game.isFinished()) {
                allGamesFinished = false;
            }
        }

        return allGamesFinished;
    }


    @Override
    public void deleteGamesForRound(Tournament tournament, int roundToDelete) {

        Log.i(this.getClass().getName(),
            "delete  ranking for round: " + roundToDelete + " in tournament : " + tournament);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(GameTable.TABLE_TOURNAMENT_GAME,
            GameTable.COLUMN_TOURNAMENT_ID + " = ? AND " + GameTable.COLUMN_TOURNAMENT_ROUND
            + " = ?", new String[] { String.valueOf(tournament.get_id()), String.valueOf(roundToDelete) });
        writableDatabase.close();
    }


    @Override
    public void uploadGames(Tournament uploadedTournament) {

        int actualRound = uploadedTournament.getActualRound();

        DatabaseReference referenceForGamesToDelete = FirebaseDatabase.getInstance()
                .getReference(FirebaseReferences.TOURNAMENT_GAMES + "/" + uploadedTournament.getOnlineUUID());
        referenceForGamesToDelete.removeValue();

        for (int i = 1; i <= actualRound; i++) {
            List<Game> allGamesForRound = getGamesForRound(uploadedTournament, i);

            for (Game game : allGamesForRound) {
                UUID uuid = UUID.randomUUID();

                DatabaseReference referenceForGames = FirebaseDatabase.getInstance()
                        .getReference(FirebaseReferences.TOURNAMENT_GAMES + "/" + uploadedTournament.getOnlineUUID()
                            + "/" + i + "/" + uuid);

                referenceForGames.setValue(game);
            }
        }
    }


    @Override
    public void tournamentPlayerSwap(Game game1, Game game2, String playerOneId, String playerTwoId) {
    }


    private void insertGames(List<Game> games) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        for (Game game : games) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GameTable.COLUMN_TOURNAMENT_ID, game.getTournament_id());
            contentValues.put(GameTable.COLUMN_TOURNAMENT_ROUND, game.getTournament_round());

            contentValues.put(GameTable.COLUMN_PLAYER_ONE_ID, game.getPlayer_one_id());
            contentValues.put(GameTable.COLUMN_PLAYER_ONE_ONLINE_UUID, game.getPlayer_one_online_uuid());

            contentValues.put(GameTable.COLUMN_PLAYER_TWO_ID, game.getPlayer_two_id());
            contentValues.put(GameTable.COLUMN_PLAYER_TWO_ONLINE_UUID, game.getPlayer_two_online_uuid());

            db.insert(GameTable.TABLE_TOURNAMENT_GAME, null, contentValues);
        }

        db.close();
    }
}
