package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.warmachine.GameTable;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.warmachine.Game;
import madson.org.opentournament.organize.setup.TournamentPlayerComparator;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
import java.util.Collections;
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
    private TournamentPlayerService tournamentPlayerService;

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
                game.setPlayer2(allPlayerMapForTournament.get(game.getPlayer_one_online_uuid()));
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
    public List<Game> createGamesForRound(Tournament tournament, int round) {

        List<TournamentPlayer> players = tournamentPlayerService.getAllPlayersForTournament(tournament);

        // uneven number of players
        if (players.size() % 2 == 1) {
            TournamentPlayer warmachineTournamentDummyPlayer = new TournamentPlayer();
            players.add(warmachineTournamentDummyPlayer);
        }

        Collections.shuffle(players);
        Collections.sort(players, new TournamentPlayerComparator());

        List<Game> games = new ArrayList<>(players.size() / 2);

        for (int i = 0; i <= (players.size() - 1); i = i + 2) {
            Log.i(this.getClass().getName(), "player1:" + players.get(i));

            Game game = new Game();
            game.setTournament_id(tournament.get_id());
            game.setTournament_round(round);

            TournamentPlayer player_one = players.get(i);

            game.setPlayer_one_id(player_one.getPlayer_id());

            if (player_one.getPlayer_online_uuid() != null) {
                game.setPlayer_one_online_uuid(player_one.getOnline_uuid());
            }

            game.setPlayer1(player_one);

            Log.i(this.getClass().getName(), "player2:" + players.get(i + 1));

            TournamentPlayer player_two = players.get(i + 1);

            if (player_one.getPlayer_online_uuid() != null) {
                game.setPlayer_one_online_uuid(player_one.getOnline_uuid());
            }

            game.setPlayer2(player_two);

            game.setPlayer_two_id(player_two.getPlayer_id());

            games.add(game);
        }

        Log.i(this.getClass().getName(), "finished pairing for new round");

        // persist pairing and new round
        insertGames(games);

        return games;
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
