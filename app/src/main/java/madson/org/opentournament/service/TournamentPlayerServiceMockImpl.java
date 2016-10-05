package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentPlayerTable;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.setup.TournamentPlayerComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerServiceMockImpl implements TournamentPlayerService {

    private OpenTournamentDBHelper openTournamentDBHelper;

    public TournamentPlayerServiceMockImpl(Context context) {

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        deleteAllTournamentPlayers();
        createMockTournamentPlayers();
    }

    @Override
    public void removePlayerFromTournament(TournamentPlayer player, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        if (player.getPlayerOnlineUUID() == null) {
            db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ?  AND  " + TournamentPlayerTable.COLUMN_PLAYER_ID
                + " = ? ", new String[] { String.valueOf(tournament.get_id()), player.getPlayerId() });
        } else {
            db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ?  AND  "
                + TournamentPlayerTable.COLUMN_PLAYER_ONLINE_UUID
                + " = ? ", new String[] { String.valueOf(tournament.get_id()), player.getPlayerOnlineUUID() });
        }

        db.close();
    }


    @Override
    public Map<String, Integer> getAllTeamsForTournament(Tournament tournament) {

        HashMap<String, Integer> teamNameMap = new HashMap<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                new String[] { TournamentPlayerTable.COLUMN_TEAMNAME }, "tournament_id  = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String teamname = cursor.getString(0);

            if (teamname != null && !teamname.isEmpty()) {
                if (!teamNameMap.containsKey(teamname)) {
                    teamNameMap.put(teamname, 1);
                } else {
                    teamNameMap.put(teamname, teamNameMap.get(teamname) + 1);
                }
            }

            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return teamNameMap;
    }


    @Override
    public List<String> getAllPlayersOnlineUUIDForTournament(Tournament tournament) {

        List<String> playersOnlineUUIDs = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                new String[] { TournamentPlayerTable.COLUMN_PLAYER_ONLINE_UUID }, "tournament_id  = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            playersOnlineUUIDs.add(cursor.getString(0));

            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return playersOnlineUUIDs;
    }


    @Override
    public void addTournamentPlayerToTournament(TournamentPlayer player, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TournamentPlayerTable.COLUMN_TOURNAMENT_ID, tournament.get_id());

        if (player.getPlayerOnlineUUID() == null) {
            contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ONLINE_UUID, UUID.randomUUID().toString());
        } else {
            contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ONLINE_UUID, player.getPlayerOnlineUUID());
        }

        contentValues.put(TournamentPlayerTable.COLUMN_FIRSTNAME, player.getFirstname());
        contentValues.put(TournamentPlayerTable.COLUMN_NICKNAME, player.getNickname());
        contentValues.put(TournamentPlayerTable.COLUMN_LASTNAME, player.getLastname());
        contentValues.put(TournamentPlayerTable.COLUMN_TEAMNAME, player.getTeamname());
        contentValues.put(TournamentPlayerTable.COLUMN_FACTION, player.getFaction());
        contentValues.put(TournamentPlayerTable.COLUMN_DUMMY, player.isDummy());

        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);

        db.close();
    }


    @Override
    public List<TournamentPlayer> getAllPlayersForTournament(Tournament tournament) {

        List<TournamentPlayer> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                TournamentPlayerTable.ALL_COLS_FOR_TOURNAMENT_PLAYER_TABLE,
                TournamentPlayerTable.COLUMN_TOURNAMENT_ID + "  = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            TournamentPlayer tournamentPlayer = cursorToTournamentPlayer(cursor);
            players.add(tournamentPlayer);

            cursor.moveToNext();
        }

        Collections.sort(players, new TournamentPlayerComparator());

        cursor.close();
        readableDatabase.close();

        return players;
    }


    @Override
    public Map<String, TournamentPlayer> getAllPlayerMapForTournament(Tournament tournament) {

        Map<String, TournamentPlayer> players = new HashMap<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                TournamentPlayerTable.ALL_COLS_FOR_TOURNAMENT_PLAYER_TABLE,
                TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            TournamentPlayer tournamentPlayer = cursorToTournamentPlayer(cursor);

            if (tournamentPlayer.getPlayerOnlineUUID() != null) {
                players.put(tournamentPlayer.getPlayerOnlineUUID(), tournamentPlayer);
            } else {
                players.put(tournamentPlayer.getPlayerId(), tournamentPlayer);
            }

            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return players;
    }


    private void createMockTournamentPlayers() {

        addPlayerToTournament(1, new Player(1, "Tobias", "Madson", "Matt"), "48\" AD", "Cygnar", "KA");
        addPlayerToTournament(1, new Player(2, "Christoph", "Zaziboy", "Scholl"), "48\" AD", "Circle", "KA");
        addPlayerToTournament(1, new Player(3, "David", "Wildjack", "Voigt"), "48\" AD", "Cryx", "KA");
        addPlayerToTournament(1, new Player(4, "Andreas", "Ragegear", "Neugebauer"), "48\" AD", "Legion", "KA");
        addPlayerToTournament(1, new Player(5, "Andreas", "Raskild", "Tonndorf"), "Team Karlsruhe", "Cryx", "KA2");
        addPlayerToTournament(1, new Player(6, "Martina", "Bazinga", "Haug"), "Team Karlsruhe", "Menoth", "KA2");
        addPlayerToTournament(1, new Player(7, "Tobias", "Zeus", "Rohrauer"), "Team Karlsruhe", "Cygnar", "KA2");
        addPlayerToTournament(1, new Player(8, "Yann", "Arcane", "Krehl"), "Team Karlsruhe", "Trollbloods", "KA2");
    }


    private void addPlayerToTournament(int tournament_id, Player player, String teamname, String faction, String meta) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentPlayerTable.COLUMN_TOURNAMENT_ID, tournament_id);
        contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ID, player.get_id());
        contentValues.put(TournamentPlayerTable.COLUMN_FIRSTNAME, player.getFirstname());
        contentValues.put(TournamentPlayerTable.COLUMN_NICKNAME, player.getNickname());
        contentValues.put(TournamentPlayerTable.COLUMN_LASTNAME, player.getLastname());
        contentValues.put(TournamentPlayerTable.COLUMN_TEAMNAME, teamname);
        contentValues.put(TournamentPlayerTable.COLUMN_FACTION, faction);
        contentValues.put(TournamentPlayerTable.COLUMN_META, meta);
        contentValues.put(TournamentPlayerTable.COLUMN_DUMMY, false);

        if (player.getOnlineUUID() != null) {
            contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ONLINE_UUID, player.getOnlineUUID());
        } else {
            contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ONLINE_UUID, UUID.randomUUID().toString());
        }

        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);

        db.close();
    }


    private void deleteAllTournamentPlayers() {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, null);

        db.close();
    }


    private TournamentPlayer cursorToTournamentPlayer(Cursor cursor) {

        TournamentPlayer tournamentPlayer = new TournamentPlayer();
        tournamentPlayer.set_id(cursor.getInt(0));
        tournamentPlayer.setTournamentId(cursor.getInt(1));
        tournamentPlayer.setPlayerId(cursor.getString(2));
        tournamentPlayer.setPlayerOnlineUUID(cursor.getString(3));

        tournamentPlayer.setFirstname(cursor.getString(4));
        tournamentPlayer.setNickname(cursor.getString(5));
        tournamentPlayer.setLastname(cursor.getString(6));
        tournamentPlayer.setTeamname(cursor.getString(7));
        tournamentPlayer.setFaction(cursor.getString(8));
        tournamentPlayer.setMeta(cursor.getString(9));
        tournamentPlayer.setDummy(cursor.getInt(10) != 0);
        tournamentPlayer.setDroppedInRound(cursor.getInt(11));

        return tournamentPlayer;
    }


    @Override
    public void uploadTournamentPlayers(Tournament tournament) {

        List<TournamentPlayer> allPlayersForTournament = getAllPlayersForTournament(tournament);
        Log.i(this.getClass().getName(), "pushes tournament players to firebase: " + allPlayersForTournament);

        DatabaseReference referenceFoPlayersToDelete = FirebaseDatabase.getInstance()
                .getReference(FirebaseReferences.TOURNAMENT_PLAYERS + "/" + tournament.getOnlineUUID());
        referenceFoPlayersToDelete.removeValue();

        for (TournamentPlayer player : allPlayersForTournament) {
            String online_uuid = player.getPlayerOnlineUUID();

            DatabaseReference referenceForUpdateTournamentPlayer = FirebaseDatabase.getInstance()
                    .getReference(FirebaseReferences.TOURNAMENT_PLAYERS + "/" + tournament.getOnlineUUID() + "/"
                        + online_uuid);

            referenceForUpdateTournamentPlayer.setValue(player);
        }
    }


    @Override
    public TournamentPlayer dropPlayerFromTournament(TournamentPlayer player, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentPlayerTable.COLUMN_DROPPED_IN_ROUND, tournament.getActualRound());

        db.update(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, contentValues,
            TournamentPlayerTable.COLUMN_ID + " = ?", new String[] { String.valueOf(player.get_id()) });

        db.close();

        player.setDroppedInRound(tournament.getActualRound());

        return player;
    }


    @Override
    public void editTournamentPlayer(TournamentPlayer tournamentPlayer, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TournamentPlayerTable.COLUMN_TEAMNAME, tournamentPlayer.getTeamname());
        contentValues.put(TournamentPlayerTable.COLUMN_FACTION, tournamentPlayer.getFaction());

        db.update(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, contentValues,
            TournamentPlayerTable.COLUMN_ID + " = ?", new String[] { String.valueOf(tournamentPlayer.get_id()) });

        db.close();
    }
}
