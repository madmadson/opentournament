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
    public void createRankingForRound(Tournament tournament, int round_for_calculation) {

        List<TournamentPlayer> allPlayersForTournament = tournamentPlayerService.getAllPlayersForTournament(tournament);

        Map<Long, TournamentRanking> mapOfRankings = new HashMap<>(allPlayersForTournament.size());

        for (TournamentPlayer tournamentPlayer : allPlayersForTournament) {
            TournamentRanking tournamentRanking = new TournamentRanking(tournament.get_id(),
                    tournamentPlayer.getPlayer_id(), round_for_calculation);
            tournamentRanking.setTournamentPlayer(tournamentPlayer);
            mapOfRankings.put(tournamentPlayer.getPlayer_id(), tournamentRanking);
        }

        List<Game> gamesOfPlayerForTournament = getAllGamesForTournamentTillRound(tournament.get_id(),
                round_for_calculation);

        for (Game game : gamesOfPlayerForTournament) {
            TournamentRanking playerOneRanking = mapOfRankings.get(game.getPlayer_one_id());

            playerOneRanking.setScore(playerOneRanking.getScore() + game.getPlayer_one_score());
            playerOneRanking.setControl_points(playerOneRanking.getControl_points()
                + game.getPlayer_one_control_points());
            playerOneRanking.setVictory_points(playerOneRanking.getVictory_points()
                + game.getPlayer_one_victory_points());

            TournamentRanking playerTwoRanking = mapOfRankings.get(game.getPlayer_two_id());

            playerTwoRanking.setScore(playerTwoRanking.getScore() + game.getPlayer_two_score());
            playerTwoRanking.setControl_points(playerTwoRanking.getControl_points()
                + game.getPlayer_two_control_points());
            playerTwoRanking.setVictory_points(playerTwoRanking.getVictory_points()
                + game.getPlayer_two_victory_points());

            // for SOS
            playerOneRanking.getListOfOpponentsPlayerIds().add(playerTwoRanking.getPlayer_id());
            playerTwoRanking.getListOfOpponentsPlayerIds().add(playerOneRanking.getPlayer_id());

            // overwrite map
            mapOfRankings.put(playerOneRanking.getPlayer_id(), playerOneRanking);
            mapOfRankings.put(playerTwoRanking.getPlayer_id(), playerTwoRanking);
        }

        calculateSoSForRankingMap(mapOfRankings);

        insertRankingForRound(mapOfRankings);
    }


    private void calculateSoSForRankingMap(Map<Long, TournamentRanking> mapOfRankings) {

        for (TournamentRanking ranking : mapOfRankings.values()) {
            List<Long> listOfOpponentsIds = ranking.getListOfOpponentsPlayerIds();

            int sos = 0;

            for (long opponent_id : listOfOpponentsIds) {
                sos = sos + mapOfRankings.get(opponent_id).getScore();
            }

            ranking.setSos(sos);

            mapOfRankings.put(ranking.getPlayer_id(), ranking);
        }
    }


    private void insertRankingForRound(Map<Long, TournamentRanking> newRankingForRoundList) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        for (TournamentRanking ranking : newRankingForRoundList.values()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TournamentRankingTable.COLUMN_TOURNAMENT_ID, ranking.getTournament_id());
            contentValues.put(TournamentRankingTable.COLUMN_PLAYER_ID, ranking.getPlayer_id());
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
