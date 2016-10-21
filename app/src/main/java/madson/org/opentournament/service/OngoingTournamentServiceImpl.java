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
import madson.org.opentournament.domain.TournamentTeam;
import madson.org.opentournament.domain.TournamentTyp;
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

        if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
            return getGamesForTeamTournamentForRound(tournament, round);
        } else {
            return getGamesForSoloTournamentForRound(tournament, round);
        }
    }


    private List<Game> getGamesForSoloTournamentForRound(Tournament tournament, int round) {

        Map<String, TournamentPlayer> allPlayerMapForTournament = tournamentPlayerService.getAllPlayerMapForTournament(
                tournament);
        List<Game> gamesToReturn = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_TOURNAMENT_UUID + "  = ? ", new String[] { tournament.getUUID() }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = Game.cursorToGame(cursor);

            if (game.getTournament_round() < round) {
                // collect information about opponents in current round
                TournamentPlayer playerOne = allPlayerMapForTournament.get(game.getParticipantOneUUID());
                TournamentPlayer playerTwo = allPlayerMapForTournament.get(game.getParticipantTwoUUID());

                playerOne.getListOfOpponentsIds().add(playerTwo.getPlayerUUID());
                playerTwo.getListOfOpponentsIds().add(playerOne.getPlayerUUID());

                allPlayerMapForTournament.put(game.getParticipantOneUUID(), playerOne);
                allPlayerMapForTournament.put(game.getParticipantTwoUUID(), playerTwo);
            }

            if (game.getTournament_round() == round) {
                game.setParticipantOne(allPlayerMapForTournament.get(game.getParticipantOneUUID()));
                game.setParticipantTwo(allPlayerMapForTournament.get(game.getParticipantTwoUUID()));

                if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                    game.setTournamentPlayerOne(allPlayerMapForTournament.get(game.getParticipantOneUUID()));
                    game.setTournamentPlayerTwo(allPlayerMapForTournament.get(game.getParticipantTwoUUID()));
                }

                gamesToReturn.add(game);
            }

            cursor.moveToNext();
        }

        cursor.close();

        return gamesToReturn;
    }


    private List<Game> getGamesForTeamTournamentForRound(Tournament tournament, int round) {

        Map<String, TournamentTeam> allTeamMapForTournament = tournamentPlayerService.getAllTeamMapForTournament(
                tournament);

        List<Game> gamesToReturn = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_TOURNAMENT_UUID + "  = ? AND " + GameTable.COLUMN_PARENT_UUID + " IS null",
                new String[] { tournament.getUUID() }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = Game.cursorToGame(cursor);

            if (game.getTournament_round() < round) {
                TournamentTeam teamOne = allTeamMapForTournament.get(game.getParticipantOneUUID());
                TournamentTeam teamTwo = allTeamMapForTournament.get(game.getParticipantTwoUUID());

                teamOne.getListOfOpponentsIds().add(teamTwo.getUuid());
                teamTwo.getListOfOpponentsIds().add(teamOne.getUuid());

                allTeamMapForTournament.put(game.getParticipantOneUUID(), teamOne);
                allTeamMapForTournament.put(game.getParticipantTwoUUID(), teamTwo);
            }

            if (game.getTournament_round() == round) {
                game.setParticipantOne(allTeamMapForTournament.get(game.getParticipantOneUUID()));
                game.setParticipantTwo(allTeamMapForTournament.get(game.getParticipantTwoUUID()));

                gamesToReturn.add(game);
            }

            cursor.moveToNext();
        }

        cursor.close();

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


    private boolean match(List<TournamentRanking> players, List<Game> games, Tournament tournament, int round) {

        if (players.isEmpty()) {
            return true;
        }

        for (int i = 0; i < players.size() - 1; i++) {
            TournamentRanking player1 = players.get(i);

            for (int j = i + 1; j < players.size(); j++) {
                TournamentRanking player2 = players.get(j);

                if (playerDontPlayTwiceAgainstEachOther) {
                    if (player1.getListOfOpponentsPlayerIds().contains(player2.getParticipantUUID())) {
                        Log.i(this.getClass().getName(), "" + player1 + " VS " + player2 + " not possible");

                        continue;
                    }
                }

                if (playerWithSameTeamDontPlayAgainstEachOther) {
                    String teamOneName = "";
                    String teamTwoName = "";

                    if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                        teamOneName = ((TournamentPlayer) player1.getTournamentParticipant()).getTeamName();
                        teamTwoName = ((TournamentPlayer) player2.getTournamentParticipant()).getTeamName();
                    } else if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                        teamOneName = player1.getParticipantUUID();
                        teamTwoName = player2.getParticipantUUID();
                    }

                    if (teamOneName != null && teamTwoName != null) {
                        if (teamOneName.equals(teamTwoName)) {
                            Log.i(this.getClass().getName(), "" + player1 + " VS " + player2 + " not possible");

                            continue;
                        }
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

                    game.setTournamentUUID(tournament.getUUID());
                    game.setTournament_round(round);

                    game.setParticipantOne(player1.getTournamentParticipant());
                    game.setParticipantOneUUID(player1.getParticipantUUID());

                    game.setParticipantTwo(player2.getTournamentParticipant());
                    game.setParticipantTwoUUID(player2.getParticipantUUID());

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
                    if (player1.getListOfOpponentsPlayerIds().contains(player2.getParticipantUUID())) {
                        Log.i(this.getClass().getName(), "" + player1 + " VS " + player2 + " not possible");

                        continue;
                    }
                }

                if (playerWithSameTeamDontPlayAgainstEachOther) {
                    String teamOneName = "";
                    String teamTwoName = "";

                    if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                        teamOneName = ((TournamentPlayer) player1.getTournamentParticipant()).getTeamName();
                        teamTwoName = ((TournamentPlayer) player2.getTournamentParticipant()).getTeamName();
                    } else if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                        teamOneName = player1.getParticipantUUID();
                        teamTwoName = player2.getParticipantUUID();
                    }

                    if (!teamOneName.isEmpty() && !teamTwoName.isEmpty()) {
                        if (teamOneName.equals(teamTwoName)) {
                            Log.i(this.getClass().getName(), "" + player1 + " VS " + player2 + " not possible");

                            continue;
                        }
                    }
                }

                ArrayList<TournamentRanking> copyPlayer = new ArrayList<>(players);
                copyPlayer.remove(player1);
                copyPlayer.remove(player2);

                boolean match = match(copyPlayer, games, tournament, round);

                if (match) {
                    Game game = new Game();

                    game.setTournamentUUID(tournament.getUUID());
                    game.setTournament_round(round);

                    game.setParticipantOne(player1.getTournamentParticipant());
                    game.setParticipantOneUUID(player1.getParticipantUUID());

                    game.setParticipantTwo(player2.getTournamentParticipant());
                    game.setParticipantTwoUUID(player2.getParticipantUUID());

                    games.add(game);

                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public Game saveGameResult(Game game) {

        Log.i(this.getClass().getName(), "save game: " + game);

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_UUID, game.getParticipantOne().getUuid());
        game.setParticipantOneUUID(game.getParticipantOne().getUuid());

        contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_UUID, game.getParticipantTwo().getUuid());
        game.setParticipantTwoUUID(game.getParticipantTwo().getUuid());

        contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_SCORE, game.getParticipant_one_score());
        contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_CONTROL_POINTS, game.getParticipant_one_control_points());
        contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_VICTORY_POINTS, game.getParticipant_one_victory_points());

        contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_SCORE, game.getParticipant_two_score());
        contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_CONTROL_POINTS, game.getParticipant_two_control_points());
        contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_VICTORY_POINTS, game.getParticipant_two_victory_points());
        contentValues.put(GameTable.COLUMN_FINISHED, game.isFinished());

        db.update(GameTable.TABLE_TOURNAMENT_GAME, contentValues, GameTable.COLUMN_UUID + " = ? ",
            new String[] { String.valueOf(game.getUUID()) });

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
            GameTable.COLUMN_TOURNAMENT_UUID + " = ? AND " + GameTable.COLUMN_TOURNAMENT_ROUND
            + " = ?", new String[] { tournament.getUUID(), String.valueOf(roundToDelete) });
    }


    @Override
    public void uploadGames(Tournament uploadedTournament) {

        int actualRound = uploadedTournament.getActualRound();

        DatabaseReference referenceForGamesToDelete = FirebaseDatabase.getInstance()
                .getReference(FirebaseReferences.TOURNAMENT_GAMES + "/" + uploadedTournament.getGameOrSportTyp() + "/"
                    + uploadedTournament.getUUID());
        referenceForGamesToDelete.removeValue();

        for (int i = 1; i <= actualRound; i++) {
            List<Game> allGamesForRound = getAllGamesForTeamTournamentForRound(uploadedTournament, i);

            for (Game game : allGamesForRound) {
                UUID uuid = UUID.randomUUID();

                DatabaseReference referenceForGames = FirebaseDatabase.getInstance()
                        .getReference(FirebaseReferences.TOURNAMENT_GAMES + "/" + uploadedTournament
                            .getGameOrSportTyp() + "/" + uploadedTournament.getUUID()
                            + "/" + i + "/" + uuid);

                referenceForGames.setValue(game);
            }
        }
    }


    private List<Game> getAllGamesForTeamTournamentForRound(Tournament tournament, int round) {

        Map<String, TournamentPlayer> allPlayerMapForTournament = tournamentPlayerService.getAllPlayerMapForTournament(
                tournament);

        Map<String, TournamentTeam> allTeamMapForTournament = tournamentPlayerService.getAllTeamMapForTournament(
                tournament);

        List<Game> gamesToReturn = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_TOURNAMENT_UUID + "  = ? ", new String[] { tournament.getUUID() }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = Game.cursorToGame(cursor);

            if (game.getTournament_round() < round) {
                // collect information about opponents in current round
                if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                    TournamentPlayer participantOne = allPlayerMapForTournament.get(game.getParticipantOneUUID());
                    TournamentPlayer participantTwo = allPlayerMapForTournament.get(game.getParticipantTwoUUID());
                    participantOne.getListOfOpponentsUUIDs().add(participantTwo.getUuid());
                    participantTwo.getListOfOpponentsUUIDs().add(participantOne.getUuid());

                    allPlayerMapForTournament.put(game.getParticipantOneUUID(), participantOne);
                    allPlayerMapForTournament.put(game.getParticipantTwoUUID(), participantTwo);
                } else if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())
                        && game.getParent_UUID() == null) {
                    TournamentTeam teamOne = allTeamMapForTournament.get(game.getParticipantOneUUID());
                    TournamentTeam teamTwo = allTeamMapForTournament.get(game.getParticipantTwoUUID());

                    teamOne.getListOfOpponentsIds().add(teamTwo.getUuid());
                    teamTwo.getListOfOpponentsIds().add(teamOne.getUuid());

                    allTeamMapForTournament.put(game.getParticipantOneUUID(), teamOne);
                    allTeamMapForTournament.put(game.getParticipantTwoUUID(), teamTwo);
                }
            }

            if (game.getTournament_round() == round) {
                if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name()) && game.getParent_UUID() == null) {
                    game.setParticipantOne(allTeamMapForTournament.get(game.getParticipantOneUUID()));
                    game.setParticipantTwo(allTeamMapForTournament.get(game.getParticipantTwoUUID()));

                    gamesToReturn.add(game);
                } else {
                    game.setParticipantOne(allPlayerMapForTournament.get(game.getParticipantOneUUID()));
                    game.setParticipantTwo(allPlayerMapForTournament.get(game.getParticipantTwoUUID()));

                    if (game.getParent_UUID() != null
                            || tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                        game.setTournamentPlayerOne(allPlayerMapForTournament.get(game.getParticipantOneUUID()));
                        game.setTournamentPlayerTwo(allPlayerMapForTournament.get(game.getParticipantTwoUUID()));
                    }

                    gamesToReturn.add(game);
                }
            }

            cursor.moveToNext();
        }

        cursor.close();

        return gamesToReturn;
    }


    @Override
    public void deleteGamesForTournament(Tournament tournament) {

        SQLiteDatabase writableDatabase = openTournamentDBHelper.getWritableDatabase();

        writableDatabase.delete(GameTable.TABLE_TOURNAMENT_GAME, GameTable.COLUMN_TOURNAMENT_UUID + " = ? ",
            new String[] { tournament.getUUID() });
    }


    @Override
    public List<Game> getGamesForTeamMatch(Tournament tournament, Game parent_game) {

        Map<String, TournamentPlayer> allPlayerMapForTournament = tournamentPlayerService.getAllPlayerMapForTournament(
                tournament);

        List<Game> gamesToReturn = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_TOURNAMENT_UUID + "  = ? AND " + GameTable.COLUMN_PARENT_UUID + " = ? ",
                new String[] { tournament.getUUID(), parent_game.getUUID() }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = Game.cursorToGame(cursor);

            game.setParticipantOne(allPlayerMapForTournament.get(game.getParticipantOneUUID()));
            game.setParticipantTwo(allPlayerMapForTournament.get(game.getParticipantTwoUUID()));

            gamesToReturn.add(game);

            cursor.moveToNext();
        }

        cursor.close();

        if (gamesToReturn.isEmpty()) {
            SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

            Map<TournamentTeam, List<TournamentPlayer>> allTeamsForTournament =
                tournamentPlayerService.getTeamMapForTournament(tournament);

            List<TournamentPlayer> teamOneMembers = allTeamsForTournament.get(new TournamentTeam(
                        parent_game.getParticipantOneUUID()));

            List<TournamentPlayer> teamTwoMembers = allTeamsForTournament.get(new TournamentTeam(
                        parent_game.getParticipantTwoUUID()));

            for (int i = 0; i < teamOneMembers.size(); i++) {
                Game game = new Game();

                String uuid = UUID.randomUUID().toString();

                game.setUUID(uuid);
                game.setParent_UUID(parent_game.getUUID());
                game.setParticipantOneUUID(teamOneMembers.get(i).getUuid());
                game.setParticipantOne(teamOneMembers.get(i));
                game.setParticipantTwoUUID(teamTwoMembers.get(i).getUuid());
                game.setParticipantTwo(teamTwoMembers.get(i));
                game.setPlaying_field(i + 1);

                ContentValues contentValues = new ContentValues();
                contentValues.put(GameTable.COLUMN_UUID, uuid);
                contentValues.put(GameTable.COLUMN_PARENT_UUID, parent_game.getUUID());

                contentValues.put(GameTable.COLUMN_TOURNAMENT_UUID, tournament.getUUID());
                contentValues.put(GameTable.COLUMN_TOURNAMENT_ROUND, parent_game.getTournament_round());
                contentValues.put(GameTable.COLUMN_PLAYING_FIELD, i + 1);

                contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_UUID, teamOneMembers.get(i).getUuid());
                contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_UUID, teamTwoMembers.get(i).getUuid());

                db.insert(GameTable.TABLE_TOURNAMENT_GAME, null, contentValues);

                gamesToReturn.add(game);
            }
        }

        return gamesToReturn;
    }


    private void insertGames(List<Game> games) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        for (int i = 0; i < games.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GameTable.COLUMN_UUID, UUID.randomUUID().toString());

            contentValues.put(GameTable.COLUMN_TOURNAMENT_UUID, games.get(i).getTournamentUUID());
            contentValues.put(GameTable.COLUMN_TOURNAMENT_ROUND, games.get(i).getTournament_round());
            contentValues.put(GameTable.COLUMN_PLAYING_FIELD, i + 1);

            contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_UUID, games.get(i).getParticipantOneUUID());
            contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_UUID, games.get(i).getParticipantTwoUUID());

            db.insert(GameTable.TABLE_TOURNAMENT_GAME, null, contentValues);
        }
    }


    @Override
    public Game updateTeamMatch(Game singleGameFromTeamEvent, Tournament tournament) {

        Log.i(this.getClass().getName(), "save team match game: " + singleGameFromTeamEvent);

        List<Game> gamesForTeamMatch = getAllGamesForTeamMatch(singleGameFromTeamEvent.getParent_UUID(), tournament);

        Game teamMatch = getGameForUUID(singleGameFromTeamEvent.getParent_UUID());

        teamMatch.setParticipant_one_intermediate_points(0);
        teamMatch.setParticipant_two_intermediate_points(0);
        teamMatch.setParticipant_one_control_points(0);
        teamMatch.setParticipant_one_victory_points(0);
        teamMatch.setParticipant_two_control_points(0);
        teamMatch.setParticipant_two_victory_points(0);

        boolean allGamesAreFinished = true;

        int teamOneScore = 0;
        int teamTwoScore = 0;

        int teamOneControlPoints = 0;
        int teamOneVictoryPoints = 0;
        int teamTwoControlPoints = 0;
        int teamTwoVictoryPoints = 0;

        for (Game game : gamesForTeamMatch) {
            // Player one is team one
            String teamNamePlayerOne = ((TournamentPlayer) game.getParticipantOne()).getTeamName();

            if (teamNamePlayerOne.equals(teamMatch.getParticipantOneUUID())) {
                teamOneControlPoints += game.getParticipant_one_control_points();
                teamOneVictoryPoints += game.getParticipant_one_victory_points();

                teamTwoControlPoints += game.getParticipant_two_control_points();
                teamTwoVictoryPoints += game.getParticipant_two_victory_points();

                teamOneScore += game.getParticipant_one_score();
                teamTwoScore += game.getParticipant_two_score();

                // Player one is team two
            } else {
                teamOneControlPoints += game.getParticipant_two_control_points();
                teamOneVictoryPoints += game.getParticipant_two_victory_points();

                teamTwoControlPoints += game.getParticipant_one_control_points();
                teamTwoVictoryPoints += game.getParticipant_one_victory_points();

                teamOneScore += game.getParticipant_two_score();
                teamTwoScore += game.getParticipant_one_score();
            }

            if (!game.isFinished()) {
                allGamesAreFinished = false;
            }
        }

        teamMatch.setParticipant_one_intermediate_points(teamOneScore);
        teamMatch.setParticipant_two_intermediate_points(teamTwoScore);

        teamMatch.setParticipant_one_control_points(teamOneControlPoints);
        teamMatch.setParticipant_one_victory_points(teamOneVictoryPoints);

        teamMatch.setParticipant_two_control_points(teamTwoControlPoints);
        teamMatch.setParticipant_two_victory_points(teamTwoVictoryPoints);

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (allGamesAreFinished) {
            teamMatch.setParticipant_one_score(teamOneScore > teamTwoScore ? 1 : 0);
            teamMatch.setParticipant_two_score(teamTwoScore > teamOneScore ? 1 : 0);
            teamMatch.setFinished(true);

            contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_SCORE, teamMatch.getParticipant_one_score());
            contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_SCORE, teamMatch.getParticipant_two_score());
            contentValues.put(GameTable.COLUMN_FINISHED, teamMatch.isFinished());
        }

        contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_INTERMEDIATE_POINTS,
            teamMatch.getParticipant_one_intermediate_points());
        contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_CONTROL_POINTS,
            teamMatch.getParticipant_one_control_points());
        contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_VICTORY_POINTS,
            teamMatch.getParticipant_one_victory_points());

        contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_INTERMEDIATE_POINTS,
            teamMatch.getParticipant_two_intermediate_points());
        contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_CONTROL_POINTS,
            teamMatch.getParticipant_two_control_points());
        contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_VICTORY_POINTS,
            teamMatch.getParticipant_two_victory_points());

        db.update(GameTable.TABLE_TOURNAMENT_GAME, contentValues, GameTable.COLUMN_UUID + " = ? ",
            new String[] { teamMatch.getUUID() });

        return teamMatch;
    }


    @Override
    public Game saveArmyList(Game gameToSave, TournamentPlayer tournamentPlayer, String armyList) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (gameToSave.getParticipantOne().equals(tournamentPlayer)) {
            contentValues.put(GameTable.COLUMN_PARTICIPANT_ONE_ARMY_LIST, armyList);
            gameToSave.setParticipant_one_army_list(armyList);
        } else if (gameToSave.getParticipantTwo().equals(tournamentPlayer)) {
            contentValues.put(GameTable.COLUMN_PARTICIPANT_TWO_ARMY_LIST, armyList);
            gameToSave.setParticipant_two_army_list(armyList);
        }

        db.update(GameTable.TABLE_TOURNAMENT_GAME, contentValues, GameTable.COLUMN_UUID + " = ? ",
            new String[] { gameToSave.getUUID() });

        return gameToSave;
    }


    private List<Game> getAllGamesForTeamMatch(String parent_uuid, Tournament tournament) {

        Map<String, TournamentPlayer> allPlayerMapForTournament = tournamentPlayerService.getAllPlayerMapForTournament(
                tournament);

        List<Game> gamesToReturn = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_PARENT_UUID + " = ? ", new String[] { parent_uuid }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Game game = Game.cursorToGame(cursor);

            game.setParticipantOne(allPlayerMapForTournament.get(game.getParticipantOneUUID()));
            game.setParticipantTwo(allPlayerMapForTournament.get(game.getParticipantTwoUUID()));

            gamesToReturn.add(game);

            cursor.moveToNext();
        }

        cursor.close();

        return gamesToReturn;
    }


    private Game getGameForUUID(String uuid) {

        Game game = null;
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(GameTable.TABLE_TOURNAMENT_GAME, GameTable.ALL_COLS_FOR_TOURNAMENT_GAME,
                GameTable.COLUMN_UUID + " = ? ", new String[] { uuid }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            game = Game.cursorToGame(cursor);

            cursor.moveToNext();
        }

        cursor.close();

        return game;
    }
}
