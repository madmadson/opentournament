package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

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

        List<Game> games = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_TOURNAMENT_ID + "  = ? AND " + GameTable.COLUMN_TOURNAMENT_ROUND + " = ?",
                new String[] { String.valueOf(tournament.get_id()), String.valueOf(round) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = Game.cursorToGame(cursor);

            if (game.getPlayer_one_online_uuid() != null) {
                game.setPlayer1(allPlayerMapForTournament.get(game.getPlayer_one_online_uuid()));
            } else {
                game.setPlayer1(allPlayerMapForTournament.get(String.valueOf(game.getPlayer_one_id())));
            }

            if (game.getPlayer_two_online_uuid() != null) {
                game.setPlayer2(allPlayerMapForTournament.get(game.getPlayer_two_online_uuid()));
            } else {
                game.setPlayer2(allPlayerMapForTournament.get(String.valueOf(game.getPlayer_two_id())));
            }

            games.add(game);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return games;
    }


    @Override
    public boolean createGamesForRound(Tournament tournament, int round, Map<String, TournamentRanking> rankingForRound,
        Map<String, PairingOption> pairingOptions) {

        List<TournamentRanking> rankings = new ArrayList<>(rankingForRound.values());

        Collections.shuffle(rankings);
        Collections.sort(rankings, new Comparator<TournamentRanking>() {

                @Override
                public int compare(TournamentRanking o1, TournamentRanking o2) {

                    return o2.getScore() - o1.getScore();
                }
            });

        List<Game> games = new ArrayList<>(rankings.size() / 2);

        // make games for every 2 players
        while (rankings.size() > 0) {
            Game game = new Game();
            game.setTournament_id(tournament.get_id());
            game.setTournament_round(round);

            // get first one with highest score
            TournamentRanking player_one = rankings.get(0);
            game.setPlayer1(player_one.getTournamentPlayer());
            game.setPlayer_one_id(player_one.getPlayer_id());
            game.setPlayer_one_online_uuid(player_one.getPlayer_online_uuid());

            rankings.remove(player_one);

            List<String> listOfOpponentsPlayerIds = player_one.getListOfOpponentsPlayerIds();

            TournamentRanking player_two = null;

            if (pairingOptions.containsKey(MapOfPairingConfig.PLAYER_NOT_PLAY_TWICE_AGAINST_EACH_OVER)
                    && pairingOptions.get(MapOfPairingConfig.PLAYER_NOT_PLAY_TWICE_AGAINST_EACH_OVER)
                    .isActive()) {
                // search for first player player one has not played against
                for (TournamentRanking secondPlayer : rankings) {
                    if (secondPlayer.getPlayer_online_uuid() != null) {
                        if (!listOfOpponentsPlayerIds.contains(secondPlayer.getPlayer_online_uuid())) {
                            player_two = secondPlayer;
                            game.setPlayer2(secondPlayer.getTournamentPlayer());
                            game.setPlayer_two_id(secondPlayer.getPlayer_id());
                            game.setPlayer_two_online_uuid(secondPlayer.getPlayer_online_uuid());

                            break;
                        }
                    } else {
                        if (!listOfOpponentsPlayerIds.contains(String.valueOf(secondPlayer.getPlayer_id()))) {
                            player_two = secondPlayer;
                            game.setPlayer2(secondPlayer.getTournamentPlayer());
                            game.setPlayer_two_id(secondPlayer.getPlayer_id());
                            game.setPlayer_two_online_uuid(secondPlayer.getPlayer_online_uuid());

                            break;
                        }
                    }
                }
            } else {
                player_two = rankings.get(0);
                game.setPlayer2(player_two.getTournamentPlayer());
                game.setPlayer_two_id(player_two.getPlayer_id());
                game.setPlayer_two_online_uuid(player_two.getPlayer_online_uuid());
            }

            // pairing failed
            if (player_two == null) {
                return false;
            }

            rankings.remove(player_two);

            Log.i(this.getClass().getName(), "game created: " + game);
            games.add(game);
        }

        Log.i(this.getClass().getName(), "finished pairing for new round");

        // persist pairing and new round
        insertGames(games);

        return true;
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
    public void saveGameResult(Game game) {

        Log.i(this.getClass().getName(), "save game: " + game);

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(GameTable.COLUMN_PLAYER_ONE_SCORE, game.getPlayer_one_score());
        contentValues.put(GameTable.COLUMN_PLAYER_ONE_CONTROL_POINTS, game.getPlayer_one_control_points());
        contentValues.put(GameTable.COLUMN_PLAYER_ONE_VICTORY_POINTS, game.getPlayer_one_victory_points());

        contentValues.put(GameTable.COLUMN_PLAYER_TWO_SCORE, game.getPlayer_two_score());
        contentValues.put(GameTable.COLUMN_PLAYER_TWO_CONTROL_POINTS, game.getPlayer_two_control_points());
        contentValues.put(GameTable.COLUMN_PLAYER_TWO_VICTORY_POINTS, game.getPlayer_two_victory_points());
        contentValues.put(GameTable.COLUMN_FINISHED, game.isFinished());

        db.update(GameTable.TABLE_TOURNAMENT_GAME, contentValues, GameTable.COLUMN_ID + " = ? ",
            new String[] { String.valueOf(game.get_id()) });
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
