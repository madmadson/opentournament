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
import madson.org.opentournament.domain.TournamentTeam;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.service.warmachine.TournamentRankingComparator;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
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

            if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                tournamentRanking.setTournamentParticipant(allPlayerMapForTournament.get(
                        tournamentRanking.getParticipantUUID()));
            }

            rankingsForRound.add(tournamentRanking);
            cursor.moveToNext();
        }

        // THE RANKING!
        Collections.sort(rankingsForRound, new TournamentRankingComparator());

        cursor.close();

        return rankingsForRound;
    }


    @Override
    public Map<String, TournamentRanking> createRankingForRound(Tournament tournament, int round_for_calculation) {

        if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
            return createRankingForRoundSoloTournament(tournament, round_for_calculation);
        } else if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
            return createRankingForRoundTeamTournament(tournament, round_for_calculation);
        }

        return null;
    }


    private Map<String, TournamentRanking> createRankingForRoundTeamTournament(Tournament tournament,
        int round_for_calculation) {

        Map<TournamentTeam, List<TournamentPlayer>> allTeamsForTournament =
            tournamentPlayerService.getTeamMapForTournament(tournament);

        Map<String, TournamentRanking> mapOfRankings = new HashMap<>();

        for (TournamentTeam team : allTeamsForTournament.keySet()) {
            TournamentRanking tournamentRanking = new TournamentRanking();
            tournamentRanking.setTournament_id(String.valueOf(tournament.get_id()));

            tournamentRanking.setParticipantUUID(team.getTeamName());
            tournamentRanking.setTournamentParticipant(team);

            tournamentRanking.setTournament_round(round_for_calculation);

            mapOfRankings.put(team.getTeamName(), tournamentRanking);
        }

        List<Game> allGamesTillRound = getAllGamesForTournamentTillRound(tournament.get_id(), round_for_calculation);

        for (Game game : allGamesTillRound) {
            // teamName is key
            if (mapOfRankings.containsKey(game.getParticipantOneUUID())) {
                TournamentRanking teamOneRanking = mapOfRankings.get(game.getParticipantOneUUID());

                int newTeamOneScore = teamOneRanking.getScore() + game.getParticipant_one_score();
                teamOneRanking.setScore(newTeamOneScore);

                TournamentRanking teamTwoRanking = mapOfRankings.get(game.getParticipantTwoUUID());

                int newTeamTwoScore = teamTwoRanking.getScore() + game.getParticipant_two_score();
                teamTwoRanking.setScore(newTeamTwoScore);

                teamTwoRanking.getListOfOpponentsPlayerIds().add(teamOneRanking.getParticipantUUID());
                teamOneRanking.getListOfOpponentsPlayerIds().add(teamTwoRanking.getParticipantUUID());

                mapOfRankings.put(game.getParticipantOneUUID(), teamOneRanking);
                mapOfRankings.put(game.getParticipantTwoUUID(), teamTwoRanking);
            } else {
                // game between players

                TournamentRanking teamOneRanking = mapOfRankings.get(((TournamentPlayer) game.getParticipantOne())
                        .getTeamName());

                teamOneRanking.setControl_points(teamOneRanking.getControl_points()
                    + game.getParticipant_one_control_points());
                teamOneRanking.setVictory_points(teamOneRanking.getVictory_points()
                    + game.getParticipant_one_victory_points());

                TournamentRanking teamTwoRanking = mapOfRankings.get(((TournamentPlayer) game.getParticipantTwo())
                        .getTeamName());

                teamTwoRanking.setControl_points(teamTwoRanking.getControl_points()
                    + game.getParticipant_two_control_points());
                teamTwoRanking.setVictory_points(teamTwoRanking.getVictory_points()
                    + game.getParticipant_two_victory_points());

                mapOfRankings.put(game.getParticipantOneUUID(), teamOneRanking);
                mapOfRankings.put(game.getParticipantTwoUUID(), teamTwoRanking);
            }
        }

        calculateSoSForRankingMap(mapOfRankings);

        insertRankingForRound(mapOfRankings);

        return mapOfRankings;
    }


    private Map<String, TournamentRanking> createRankingForRoundSoloTournament(Tournament tournament,
        int round_for_calculation) {

        List<TournamentPlayer> allPlayersForTournament = tournamentPlayerService.getAllPlayersForTournament(tournament);

        Map<String, TournamentRanking> mapOfRankings = new HashMap<>(allPlayersForTournament.size());

        for (TournamentPlayer tournamentPlayer : allPlayersForTournament) {
            TournamentRanking tournamentRanking = new TournamentRanking();
            tournamentRanking.setTournament_id(String.valueOf(tournament.get_id()));

            tournamentRanking.setParticipantUUID(tournamentPlayer.getPlayerUUID());
            tournamentRanking.setTournamentParticipant(tournamentPlayer);

            tournamentRanking.setTournament_round(round_for_calculation);

            mapOfRankings.put(tournamentPlayer.getPlayerUUID(), tournamentRanking);
        }

        List<Game> gamesOfPlayerForTournament = getAllGamesForTournamentTillRound(tournament.get_id(),
                round_for_calculation);

        for (Game game : gamesOfPlayerForTournament) {
            TournamentRanking playerOneRanking = mapOfRankings.get(game.getParticipantOneUUID());

            int newPlayerOneScore = playerOneRanking.getScore() + game.getParticipant_one_score();
            playerOneRanking.setScore(newPlayerOneScore);

            playerOneRanking.setControl_points(playerOneRanking.getControl_points()
                + game.getParticipant_one_control_points());
            playerOneRanking.setVictory_points(playerOneRanking.getVictory_points()
                + game.getParticipant_one_victory_points());

            TournamentRanking playerTwoRanking = mapOfRankings.get(game.getParticipantTwoUUID());

            playerTwoRanking.setScore(playerTwoRanking.getScore() + game.getParticipant_two_score());
            playerTwoRanking.setControl_points(playerTwoRanking.getControl_points()
                + game.getParticipant_two_control_points());
            playerTwoRanking.setVictory_points(playerTwoRanking.getVictory_points()
                + game.getParticipant_two_victory_points());

            // overwrite map && SOS

            playerTwoRanking.getListOfOpponentsPlayerIds().add(playerOneRanking.getParticipantUUID());
            playerOneRanking.getListOfOpponentsPlayerIds().add(playerTwoRanking.getParticipantUUID());

            mapOfRankings.put(game.getParticipantOneUUID(), playerOneRanking);
            mapOfRankings.put(game.getParticipantTwoUUID(), playerTwoRanking);
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

            mapOfRankings.put(ranking.getParticipantUUID(), ranking);
        }
    }


    private void insertRankingForRound(Map<String, TournamentRanking> newRankingForRoundList) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        for (TournamentRanking ranking : newRankingForRoundList.values()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TournamentRankingTable.COLUMN_TOURNAMENT_ID, ranking.getTournament_id());
            contentValues.put(TournamentRankingTable.COLUMN_TOURNAMENT_ROUND, ranking.getTournament_round());
            contentValues.put(TournamentRankingTable.COLUMN_PARTICIPANT_UUID, ranking.getParticipantUUID());

            contentValues.put(TournamentRankingTable.COLUMN_SCORE, ranking.getScore());
            contentValues.put(TournamentRankingTable.COLUMN_SOS, ranking.getSos());
            contentValues.put(TournamentRankingTable.COLUMN_CONTROL_POINTS, ranking.getControl_points());
            contentValues.put(TournamentRankingTable.COLUMN_VICTORY_POINTS, ranking.getVictory_points());

            db.insert(TournamentRankingTable.TABLE_TOURNAMENT_RANKING, null, contentValues);
        }
    }


    private List<Game> getAllGamesForTournamentTillRound(long tournament_id, int tournament_round) {

        List<Game> games = new ArrayList<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_TOURNAMENT_ID + "  = ? A   ND " + GameTable.COLUMN_TOURNAMENT_ROUND
                + " <= ? AND " + GameTable.COLUMN_FINISHED + " != 0  AND " + GameTable.COLUMN_PARENT_UUID + " IS NULL",
                new String[] { String.valueOf(tournament_id), String.valueOf(tournament_round) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = Game.cursorToGame(cursor);
            games.add(game);
            cursor.moveToNext();
        }

        cursor.close();

        return games;
    }


    private TournamentRanking cursorToTournamentRanking(Cursor cursor) {

        TournamentRanking tournamentRanking = new TournamentRanking();
        tournamentRanking.set_id(cursor.getInt(0));

        tournamentRanking.setTournament_id(cursor.getString(1));
        tournamentRanking.setTournament_round(cursor.getInt(2));

        tournamentRanking.setParticipantUUID(cursor.getString(3));

        tournamentRanking.setScore(cursor.getInt(4));
        tournamentRanking.setSos(cursor.getInt(5));
        tournamentRanking.setControl_points(cursor.getInt(6));
        tournamentRanking.setVictory_points(cursor.getInt(7));

        return tournamentRanking;
    }
}
