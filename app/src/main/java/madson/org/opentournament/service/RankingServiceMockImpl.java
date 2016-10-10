package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.db.GameTable;
import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentRankingTable;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.service.warmachine.TournamentRankingComparator;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Service for get rankings for tournaments.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RankingServiceMockImpl implements RankingService {

    private TournamentPlayerService tournamentPlayerService;

    private OpenTournamentDBHelper openTournamentDBHelper;

    public RankingServiceMockImpl(Context context) {

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

            tournamentRanking.setTournamentPlayer(allPlayerMapForTournament.get(tournamentRanking.getPlayerUUID()));

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
            tournamentRanking.setTournament_id(String.valueOf(tournament.get_id()));

            if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                tournamentRanking.setPlayerUUID(tournamentPlayer.getPlayerUUID());
                tournamentRanking.setTournamentPlayer(tournamentPlayer);
            } else if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
            }

            tournamentRanking.setTournament_round(round_for_calculation);

            mapOfRankings.put(tournamentPlayer.getPlayerUUID(), tournamentRanking);
        }

        List<Game> gamesOfPlayerForTournament = getAllGamesForTournamentTillRound(tournament.get_id(),
                round_for_calculation);

        for (Game game : gamesOfPlayerForTournament) {
            TournamentRanking playerOneRanking = mapOfRankings.get(game.getPlayerOneUUID());

            int newPlayerOneScore = playerOneRanking.getScore() + game.getPlayer_one_score();
            playerOneRanking.setScore(newPlayerOneScore);

            playerOneRanking.setControl_points(playerOneRanking.getControl_points()
                + game.getPlayer_one_control_points());
            playerOneRanking.setVictory_points(playerOneRanking.getVictory_points()
                + game.getPlayer_one_victory_points());

            TournamentRanking playerTwoRanking = mapOfRankings.get(game.getPlayerTwoUUID());

            playerTwoRanking.setScore(playerTwoRanking.getScore() + game.getPlayer_two_score());
            playerTwoRanking.setControl_points(playerTwoRanking.getControl_points()
                + game.getPlayer_two_control_points());
            playerTwoRanking.setVictory_points(playerTwoRanking.getVictory_points()
                + game.getPlayer_two_victory_points());

            // overwrite map && SOS

            playerTwoRanking.getListOfOpponentsPlayerIds().add(playerOneRanking.getPlayerUUID());
            playerOneRanking.getListOfOpponentsPlayerIds().add(playerTwoRanking.getPlayerUUID());

            mapOfRankings.put(game.getPlayerOneUUID(), playerOneRanking);
            mapOfRankings.put(game.getPlayerTwoUUID(), playerTwoRanking);
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


    @Override
    public void uploadRankingsForTournament(Tournament tournament) {

        int actualRound = tournament.getActualRound();

        DatabaseReference referenceForRankingToDelete = FirebaseDatabase.getInstance()
                .getReference(FirebaseReferences.TOURNAMENT_RANKINGS + "/" + tournament.getOnlineUUID());
        referenceForRankingToDelete.removeValue();

        for (int i = 1; i <= actualRound; i++) {
            List<TournamentRanking> tournamentRankingForRound = getTournamentRankingForRound(tournament, i);

            Log.i(this.getClass().getName(), "pushes rankings to firebase online: " + tournamentRankingForRound);

            for (int j = 0; j < tournamentRankingForRound.size(); j++) {
                UUID uuid = UUID.randomUUID();

                DatabaseReference referenceForRankings = FirebaseDatabase.getInstance()
                        .getReference(FirebaseReferences.TOURNAMENT_RANKINGS + "/" + tournament.getOnlineUUID()
                            + "/" + i + "/" + uuid);

                // for sorting after insert
                TournamentRanking tournamentRanking = tournamentRankingForRound.get(j);
                tournamentRanking.setRank(j + 1);

                referenceForRankings.setValue(tournamentRanking);
            }
        }
    }


    @Override
    public void deleteRankingsForTournament(Tournament tournament) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(TournamentRankingTable.TABLE_TOURNAMENT_RANKING,
            TournamentRankingTable.COLUMN_TOURNAMENT_ID + " = ? ",
            new String[] { String.valueOf(tournament.get_id()) });
        writableDatabase.close();
    }


    private void calculateSoSForRankingMap(Map<String, TournamentRanking> mapOfRankings) {

        Map<String, TournamentRanking> copyMap = new HashMap<>(mapOfRankings);

        for (TournamentRanking ranking : copyMap.values()) {
            List<String> listOfOpponentsIds = ranking.getListOfOpponentsPlayerIds();

            int sos = 0;

            for (String opponent_id : listOfOpponentsIds) {
                sos = sos + copyMap.get(opponent_id).getScore();
            }

            ranking.setSos(sos);

            mapOfRankings.put(ranking.getPlayerUUID(), ranking);
        }
    }


    private void insertRankingForRound(Map<String, TournamentRanking> newRankingForRoundList) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        for (TournamentRanking ranking : newRankingForRoundList.values()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TournamentRankingTable.COLUMN_TOURNAMENT_ID, ranking.getTournament_id());
            contentValues.put(TournamentRankingTable.COLUMN_TOURNAMENT_ROUND, ranking.getTournament_round());
            contentValues.put(TournamentRankingTable.COLUMN_PLAYER_UUID, ranking.getPlayerUUID());

            contentValues.put(TournamentRankingTable.COLUMN_SCORE, ranking.getScore());
            contentValues.put(TournamentRankingTable.COLUMN_SOS, ranking.getSos());
            contentValues.put(TournamentRankingTable.COLUMN_CONTROL_POINTS, ranking.getControl_points());
            contentValues.put(TournamentRankingTable.COLUMN_VICTORY_POINTS, ranking.getVictory_points());

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

        tournamentRanking.setTournament_id(cursor.getString(1));
        tournamentRanking.setTournament_round(cursor.getInt(2));

        tournamentRanking.setPlayerUUID(cursor.getString(3));

        tournamentRanking.setScore(cursor.getInt(4));
        tournamentRanking.setSos(cursor.getInt(5));
        tournamentRanking.setControl_points(cursor.getInt(6));
        tournamentRanking.setVictory_points(cursor.getInt(7));

        return tournamentRanking;
    }
}
