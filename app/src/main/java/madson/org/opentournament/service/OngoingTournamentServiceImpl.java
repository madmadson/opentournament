package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.warmachine.GameTable;
import madson.org.opentournament.db.warmachine.TournamentRankingTable;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.domain.warmachine.Game;
import madson.org.opentournament.players.TournamentPlayerComparator;
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

        List<Game> games = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                "tournament_id  = ? AND round = ?",
                new String[] { String.valueOf(tournament.get_id()), String.valueOf(round) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game pairing = Game.cursorToGame(cursor);
            games.add(pairing);
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
            game.setPlayer_one_full_name(player_one.getFirstname() + " \"" + player_one.getNickname() + "\" "
                + player_one.getLastname());

            Log.i(this.getClass().getName(), "player2:" + players.get(i + 1));

            TournamentPlayer player_two = players.get(i + 1);
            game.setPlayer_two_id(player_two.getPlayer_id());
            game.setPlayer_two_full_name(player_two.getFirstname() + " \"" + player_two.getNickname() + "\" "
                + player_two.getLastname());

            games.add(game);
        }

        Log.i(this.getClass().getName(), "finished pairing for new round");

        // persist pairing and new round
        insertGames(games);
        makeTournamentRoundActual(tournament.get_id(), round);

        return games;
    }


    private void makeTournamentRoundActual(Long tournamentId, int round) {

        tournamentService.updateActualRound(tournamentId, round);
    }


    @Override
    public Game getGameForId(long game_id) {

        Log.i(this.getClass().getName(), "get game for id:" + game_id);

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                "_id  = ? ", new String[] { String.valueOf(game_id) }, null, null, null);

        cursor.moveToFirst();

        Game pairing = null;

        while (!cursor.isAfterLast()) {
            pairing = Game.cursorToGame(cursor);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        Log.i(this.getClass().getName(), "pairing loaded sucessfully: " + pairing);

        return pairing;
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

        db.update(GameTable.TABLE_TOURNAMENT_GAME, contentValues, "_id = ? ",
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

        for (Game pairing : games) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GameTable.COLUMN_TOURNAMENT_ID, pairing.getTournament_id());
            contentValues.put(GameTable.COLUMN_PLAYER_ONE_ID, pairing.getPlayer_one_id());
            contentValues.put(GameTable.COLUMN_PLAYER_TWO_ID, pairing.getPlayer_two_id());
            contentValues.put(GameTable.COLUMN_PLAYER_ONE_FULL_NAME, pairing.getPlayer_one_full_name());
            contentValues.put(GameTable.COLUMN_PLAYER_TWO_FULL_NAME, pairing.getPlayer_two_full_name());
            contentValues.put(GameTable.COLUMN_TOURNAMENT_ROUND, pairing.getTournament_round());
            db.insert(GameTable.TABLE_TOURNAMENT_GAME, null, contentValues);
        }

        db.close();
    }
}
