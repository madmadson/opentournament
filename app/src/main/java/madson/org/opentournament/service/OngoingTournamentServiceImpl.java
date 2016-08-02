package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.db.warmachine.WarmachineTournamentPairingTable;
import madson.org.opentournament.db.warmachine.WarmachineTournamentPlayerTable;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentPairing;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentPlayer;
import madson.org.opentournament.service.warmachine.WarmachinePlayerComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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

        OpenTournamentApplication application = (OpenTournamentApplication) context;

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
        db.insert(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER, null, contentValues);
    }


    private void deleteAllTournamentPlayers() {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        db.delete(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER, null, null);
    }


    @Override
    public List<WarmachineTournamentPlayer> getPlayersForWarmachineTournament(Long tournamentId) {

        ArrayList<WarmachineTournamentPlayer> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER,
                WarmachineTournamentPlayerTable.ALL_COLS_FOR_WARMACHINE_TOURNAMENT_PLAYER, "tournament_id  = ?",
                new String[] { Long.toString(tournamentId) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Player player = playerService.getPlayerForId((long) cursor.getInt(2));
            WarmachineTournamentPlayer warmachineTournamentPlayer = new WarmachineTournamentPlayer(player);
            warmachineTournamentPlayer.setScore(cursor.getInt(3));
            warmachineTournamentPlayer.setControl_points(cursor.getInt(4));
            warmachineTournamentPlayer.setVictory_points(cursor.getInt(5));
            players.add(warmachineTournamentPlayer);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return players;
    }


    @Override
    public void addPlayerToTournament(Player player, Long tournamentId) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("tournament_id", tournamentId);
        contentValues.put("player_id", player.getId());
        db.insert(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER, null, contentValues);
    }


    @Override
    public void removePlayerFromTournament(Player player, Long tournamentId) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("tournament_id", tournamentId);
        contentValues.put("player_id", player.getId());
        db.delete(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER,
            "tournament_id  = ?  AND  player_id = ? ",
            new String[] { String.valueOf(tournamentId), String.valueOf(player.getId()) });
    }


    @Override
    public List<WarmachineTournamentPairing> getPairingsForTournament(Long tournamentId, int round) {

        List<WarmachineTournamentPairing> warmachineTournamentPairings = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineTournamentPairingTable.TABLE_TOURNAMENT_PAIRING,
                WarmachineTournamentPairing.ALL_COLS_FOR_TOURNAMENT_PAIRING, "tournament_id  = ? AND round = ?",
                new String[] { String.valueOf(tournamentId), String.valueOf(round) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            WarmachineTournamentPairing pairing = new WarmachineTournamentPairing();
            warmachineTournamentPairings.add(pairing);
            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return warmachineTournamentPairings;
    }


    @Override
    public List<WarmachineTournamentPairing> createPairingForRound(Long tournamentId, int round) {

        List<WarmachineTournamentPlayer> playersForTournament = getPlayersForWarmachineTournament(tournamentId);

        // uneven number of players
        if (playersForTournament.size() % 2 == 1) {
            WarmachineTournamentPlayer warmachineTournamentDummyPlayer = new WarmachineTournamentPlayer(new Player(
                        "dummy", "bye", "player"));
            warmachineTournamentDummyPlayer.setScore(0);
            warmachineTournamentDummyPlayer.setControl_points(0);
            warmachineTournamentDummyPlayer.setVictory_points(0);
            playersForTournament.add(warmachineTournamentDummyPlayer);
        }

        Collections.shuffle(playersForTournament);
        Collections.sort(playersForTournament, new WarmachinePlayerComparator());

        ArrayList<WarmachineTournamentPairing> warmachineTournamentPairings = new ArrayList<>(
                playersForTournament.size());

        for (int i = 0; i <= (playersForTournament.size() - 1); i = i + 2) {
            WarmachineTournamentPairing warmachineTournamentPairing = new WarmachineTournamentPairing();
            warmachineTournamentPairing.setTournament_id(tournamentId.intValue());
            warmachineTournamentPairing.setRound(round);
            warmachineTournamentPairing.setPlayer_one_id(playersForTournament.get(i).getId());
            warmachineTournamentPairing.setPlayer_two_id(playersForTournament.get(i + 1).getId());

            warmachineTournamentPairings.add(warmachineTournamentPairing);

            // persist pairing
            insertWarmachineTournamentPairing(warmachineTournamentPairing);
        }

        return warmachineTournamentPairings;
    }


    @Override
    public List<Player> getPlayersForTournament(long tournamentId) {

        ArrayList<Player> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(WarmachineTournamentPlayerTable.TABLE_WARMACHINE_TOURNAMENT_PLAYER,
                new String[] { WarmachineTournamentPlayerTable.COLUMN_PLAYER_ID }, "tournament_id  = ?",
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


    private void insertWarmachineTournamentPairing(WarmachineTournamentPairing pairing) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(WarmachineTournamentPairingTable.COLUMN_TOURNAMENT_ID, pairing.getTournament_id());
        contentValues.put(WarmachineTournamentPairingTable.COLUMN_ROUND, pairing.getRound());
        contentValues.put(WarmachineTournamentPairingTable.COLUMN_PLAYER_ONE_ID, pairing.getPlayer_one_id());
        contentValues.put(WarmachineTournamentPairingTable.COLUMN_PLAYER_TWO_ID, pairing.getPlayer_two_id());
        db.insert(WarmachineTournamentPairingTable.TABLE_TOURNAMENT_PAIRING, null, contentValues);
    }


    private WarmachineTournamentPairing cursorToTournamentPairing(Cursor cursor) {

        return new WarmachineTournamentPairing(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7), cursor.getInt(8),
                cursor.getInt(9), cursor.getInt(10));
    }
}
