package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.db.warmachine.GameTable;
import madson.org.opentournament.db.warmachine.TournamentRankingTable;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.domain.warmachine.Game;
import madson.org.opentournament.service.warmachine.TournamentRankingComparator;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service for get rankings for tournaments.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RankingServiceImpl implements RankingService {

    private TournamentPlayerService tournamentPlayerService;

    private OpenTournamentDBHelper openTournamentDBHelper;

    public RankingServiceImpl(Context context) {

        BaseApplication application = (BaseApplication) context;

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        if (tournamentPlayerService == null) {
            tournamentPlayerService = application.getTournamentPlayerService();
        }
    }

    @Override
    public List<TournamentRanking> getTournamentRankingForRound(Tournament tournament, int round) {

        Log.i(this.getClass().getName(), "get ranking for round -> for ranking list/ final standings");

        Map<String, TournamentPlayer> allPlayerMapForTournament = tournamentPlayerService.getAllPlayerMapForTournament(
                tournament);

        List<TournamentRanking> rankingsForRound = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentRankingTable.TABLE_TOURNAMENT_RANKING,
                TournamentRankingTable.ALL_COLS_FOR_TOURNAMENT_RANKING,
                TournamentRankingTable.COLUMN_TOURNAMENT_ID + " = ? AND "
                + TournamentRankingTable.COLUMN_TOURNAMENT_ROUND
                + " = ?", new String[] { Long.toString(tournament.get_id()), String.valueOf(round) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            TournamentRanking tournamentRanking = cursorToTournamentRanking(cursor);

            if (tournamentRanking.getPlayer_online_uuid() != null) {
                tournamentRanking.setTournamentPlayer(allPlayerMapForTournament.get(
                        tournamentRanking.getPlayer_online_uuid()));
            } else {
                tournamentRanking.setTournamentPlayer(allPlayerMapForTournament.get(
                        String.valueOf(tournamentRanking.getPlayer_id())));
            }

            rankingsForRound.add(tournamentRanking);
            cursor.moveToNext();
        }

        // THE RANKING!
        Collections.sort(rankingsForRound, new TournamentRankingComparator());

        cursor.close();
        readableDatabase.close();

        return rankingsForRound;
    }


    @Override
    public Map<String, TournamentRanking> createRankingForRound(Tournament tournament, int round_for_calculation) {

        List<TournamentPlayer> allPlayersForTournament = tournamentPlayerService.getAllPlayersForTournament(tournament);

        Map<String, TournamentRanking> mapOfRankings = new HashMap<>(allPlayersForTournament.size());

        for (TournamentPlayer tournamentPlayer : allPlayersForTournament) {
            TournamentRanking tournamentRanking = new TournamentRanking();
            tournamentRanking.setTournament_id(tournament.get_id());
            tournamentRanking.setPlayer_id(tournamentPlayer.getPlayer_id());
            tournamentRanking.setPlayer_online_uuid(tournamentPlayer.getPlayer_online_uuid());
            tournamentRanking.setTournament_round(round_for_calculation);
            tournamentRanking.setTournamentPlayer(tournamentPlayer);

            if (tournamentPlayer.getPlayer_online_uuid() != null) {
                mapOfRankings.put(tournamentPlayer.getPlayer_online_uuid(), tournamentRanking);
            } else {
                mapOfRankings.put(String.valueOf(tournamentPlayer.getPlayer_id()), tournamentRanking);
            }
        }

        List<Game> gamesOfPlayerForTournament = getAllGamesForTournamentTillRound(tournament.get_id(),
                round_for_calculation);

        for (Game game : gamesOfPlayerForTournament) {
            TournamentRanking playerOneRanking;

            if (game.getPlayer_one_online_uuid() != null) {
                playerOneRanking = mapOfRankings.get(game.getPlayer_one_online_uuid());
            } else {
                playerOneRanking = mapOfRankings.get(String.valueOf(game.getPlayer_one_id()));
            }

            int newPlayerOneScore = playerOneRanking.getScore() + game.getPlayer_one_score();
            playerOneRanking.setScore(newPlayerOneScore);

            playerOneRanking.setControl_points(playerOneRanking.getControl_points()
                + game.getPlayer_one_control_points());
            playerOneRanking.setVictory_points(playerOneRanking.getVictory_points()
                + game.getPlayer_one_victory_points());

            TournamentRanking playerTwoRanking;

            if (game.getPlayer_two_online_uuid() != null) {
                playerTwoRanking = mapOfRankings.get(game.getPlayer_two_online_uuid());
            } else {
                playerTwoRanking = mapOfRankings.get(String.valueOf(game.getPlayer_two_id()));
            }

            playerTwoRanking.setScore(playerTwoRanking.getScore() + game.getPlayer_two_score());
            playerTwoRanking.setControl_points(playerTwoRanking.getControl_points()
                + game.getPlayer_two_control_points());
            playerTwoRanking.setVictory_points(playerTwoRanking.getVictory_points()
                + game.getPlayer_two_victory_points());

            // overwrite map && SOS
            if (game.getPlayer_one_online_uuid() != null) {
                playerTwoRanking.getListOfOpponentsPlayerIds().add(playerOneRanking.getPlayer_online_uuid());
            } else {
                playerTwoRanking.getListOfOpponentsPlayerIds().add(String.valueOf(game.getPlayer_one_id()));
            }

            if (game.getPlayer_two_online_uuid() != null) {
                playerOneRanking.getListOfOpponentsPlayerIds().add(playerTwoRanking.getPlayer_online_uuid());
            } else {
                playerOneRanking.getListOfOpponentsPlayerIds().add(String.valueOf(game.getPlayer_two_id()));
            }

            if (game.getPlayer_one_online_uuid() != null) {
            } else {
                mapOfRankings.put(String.valueOf(game.getPlayer_one_id()), playerOneRanking);
            }

            if (game.getPlayer_two_online_uuid() != null) {
            } else {
                mapOfRankings.put(String.valueOf(game.getPlayer_two_id()), playerTwoRanking);
            }
        }

        calculateSoSForRankingMap(mapOfRankings);

        insertRankingForRound(mapOfRankings);

        return mapOfRankings;
    }


    @Override
    public void deleteRankingForRound(Tournament tournament, int roundToDelete) {

        Log.i(this.getClass().getName(),
            "delete  ranking for round: " + roundToDelete + " in tournament : " + tournament);

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(TournamentRankingTable.TABLE_TOURNAMENT_RANKING,
            TournamentRankingTable.COLUMN_TOURNAMENT_ID + " = ? AND " + TournamentRankingTable.COLUMN_TOURNAMENT_ROUND
            + " = ?", new String[] { String.valueOf(tournament.get_id()), String.valueOf(roundToDelete) });
        writableDatabase.close();
    }


    private void calculateSoSForRankingMap(Map<String, TournamentRanking> mapOfRankings) {

        for (TournamentRanking ranking : mapOfRankings.values()) {
            List<String> listOfOpponentsIds = ranking.getListOfOpponentsPlayerIds();

            int sos = 0;

            for (String opponent_id : listOfOpponentsIds) {
                sos = sos + mapOfRankings.get(opponent_id).getScore();
            }

            ranking.setSos(sos);

            if (ranking.getPlayer_online_uuid() != null) {
                mapOfRankings.put(ranking.getPlayer_online_uuid(), ranking);
            } else {
                mapOfRankings.put(String.valueOf(ranking.getPlayer_id()), ranking);
            }
        }
    }


    private void insertRankingForRound(Map<String, TournamentRanking> newRankingForRoundList) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        for (TournamentRanking ranking : newRankingForRoundList.values()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TournamentRankingTable.COLUMN_TOURNAMENT_ID, ranking.getTournament_id());
            contentValues.put(TournamentRankingTable.COLUMN_PLAYER_ID, ranking.getPlayer_id());
            contentValues.put(TournamentRankingTable.COLUMN_PLAYER_ONLINE_UUID, ranking.getPlayer_online_uuid());
            contentValues.put(TournamentRankingTable.COLUMN_TOURNAMENT_ROUND, ranking.getTournament_round());

            contentValues.put(TournamentRankingTable.COLUMN_SCORE, ranking.getScore());
            contentValues.put(TournamentRankingTable.COLUMN_CONTROL_POINTS, ranking.getControl_points());
            contentValues.put(TournamentRankingTable.COLUMN_VICTORY_POINTS, ranking.getVictory_points());

            contentValues.put(TournamentRankingTable.COLUMN_SOS, ranking.getSos());

            db.insert(TournamentRankingTable.TABLE_TOURNAMENT_RANKING, null, contentValues);
        }

        db.close();
    }


    private List<Game> getAllGamesForTournamentTillRound(long tournament_id, int tournament_round) {

        List<Game> games = new ArrayList<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_TOURNAMENT_ID + "  = ? AND " + GameTable.COLUMN_TOURNAMENT_ROUND
                + " <= ? AND (" + GameTable.COLUMN_PLAYER_ONE_SCORE + " != 0 OR  " + GameTable.COLUMN_PLAYER_TWO_SCORE
                + " != 0) ", new String[] { String.valueOf(tournament_id), String.valueOf(tournament_round) }, null,
                null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = Game.cursorToGame(cursor);
            games.add(game);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return games;
    }


    private TournamentRanking cursorToTournamentRanking(Cursor cursor) {

        TournamentRanking tournamentRanking = new TournamentRanking();
        tournamentRanking.set_id(cursor.getInt(0));
        tournamentRanking.setOnline_uuid(cursor.getString(1));

        tournamentRanking.setTournament_id(cursor.getInt(2));
        tournamentRanking.setTournament_round(cursor.getInt(3));

        tournamentRanking.setPlayer_id(cursor.getInt(4));
        tournamentRanking.setPlayer_online_uuid(cursor.getString(5));

        tournamentRanking.setScore(cursor.getInt(6));
        tournamentRanking.setSos(cursor.getInt(7));
        tournamentRanking.setControl_points(cursor.getInt(8));
        tournamentRanking.setVictory_points(cursor.getInt(9));

        return tournamentRanking;
    }
}
