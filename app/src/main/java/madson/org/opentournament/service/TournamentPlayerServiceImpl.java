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
import madson.org.opentournament.domain.TournamentPlayerComparator;
import madson.org.opentournament.domain.TournamentTeam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerServiceImpl implements TournamentPlayerService {

    private OpenTournamentDBHelper openTournamentDBHelper;

    public TournamentPlayerServiceImpl(Context context) {

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }
    }

    @Override
    public void removePlayerFromTournament(TournamentPlayer player) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
            TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ?  AND  " + TournamentPlayerTable.COLUMN_PLAYER_UUID
            + " = ? ", new String[] { player.getTournamentId(), player.getPlayerUUID() });
    }


    @Override
    public Map<TournamentTeam, List<TournamentPlayer>> getTeamMapForTournament(Tournament tournament) {

        Map<TournamentTeam, List<TournamentPlayer>> teamMap = new HashMap<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                TournamentPlayerTable.ALL_COLS_FOR_TOURNAMENT_PLAYER_TABLE,
                TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            TournamentPlayer tournamentPlayer = cursorToTournamentPlayer(cursor);

            String teamName = tournamentPlayer.getTeamName();

            if (teamName == null) {
                teamName = "";
            }

            TournamentTeam tournamentTeam = new TournamentTeam(teamName);

            if (!teamMap.containsKey(tournamentTeam)) {
                List<TournamentPlayer> teamMembers = new ArrayList<>();

                teamMembers.add(tournamentPlayer);
                teamMap.put(tournamentTeam, teamMembers);
            } else {
                List<TournamentPlayer> tournamentPlayers = teamMap.get(tournamentTeam);
                tournamentPlayers.add(tournamentPlayer);
                teamMap.put(tournamentTeam, tournamentPlayers);
            }

            cursor.moveToNext();
        }

        cursor.close();

        return teamMap;
    }


    @Override
    public List<TournamentTeam> getTeamListForTournament(Tournament tournament) {

        List<TournamentTeam> teamList = new ArrayList<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                TournamentPlayerTable.ALL_COLS_FOR_TOURNAMENT_PLAYER_TABLE,
                TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            TournamentPlayer tournamentPlayer = cursorToTournamentPlayer(cursor);

            String teamName = tournamentPlayer.getTeamName();

            if (teamName == null) {
                teamName = "";
            }

            TournamentTeam tournamentTeam = new TournamentTeam(teamName);

            if (!teamList.contains(tournamentTeam)) {
                teamList.add(tournamentTeam);
            }

            cursor.moveToNext();
        }

        cursor.close();

        return teamList;
    }


    @Override
    public List<String> getAllPlayersUUIDsForTournament(Tournament tournament) {

        List<String> playersOnlineUUIDs = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                new String[] { TournamentPlayerTable.COLUMN_PLAYER_UUID },
                TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            playersOnlineUUIDs.add(cursor.getString(0));

            cursor.moveToNext();
        }

        cursor.close();

        return playersOnlineUUIDs;
    }


    @Override
    public void addTournamentPlayerToTournament(TournamentPlayer player, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TournamentPlayerTable.COLUMN_TOURNAMENT_ID, tournament.get_id());

        contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_UUID, player.getPlayerUUID());
        contentValues.put(TournamentPlayerTable.COLUMN_FIRSTNAME, player.getFirstName());
        contentValues.put(TournamentPlayerTable.COLUMN_NICKNAME, player.getNickName());
        contentValues.put(TournamentPlayerTable.COLUMN_LASTNAME, player.getLastName());
        contentValues.put(TournamentPlayerTable.COLUMN_TEAMNAME, player.getTeamName());
        contentValues.put(TournamentPlayerTable.COLUMN_FACTION, player.getFaction());
        contentValues.put(TournamentPlayerTable.COLUMN_DUMMY, player.isDummy());
        contentValues.put(TournamentPlayerTable.COLUMN_LOCAL, player.isLocal());

        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);
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

            players.put(tournamentPlayer.getPlayerUUID(), tournamentPlayer);

            cursor.moveToNext();
        }

        cursor.close();

        return players;
    }


    @Override
    public Map<String, TournamentTeam> getAllTeamMapForTournament(Tournament tournament) {

        Map<String, TournamentTeam> teamMap = new HashMap<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                TournamentPlayerTable.ALL_COLS_FOR_TOURNAMENT_PLAYER_TABLE,
                TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            TournamentPlayer tournamentPlayer = cursorToTournamentPlayer(cursor);

            String teamName = tournamentPlayer.getTeamName();

            if (teamName == null) {
                teamName = "";
            }

            TournamentTeam tournamentTeam = new TournamentTeam(teamName);

            if (!teamMap.containsKey(teamName)) {
                teamMap.put(teamName, tournamentTeam);
            }

            cursor.moveToNext();
        }

        cursor.close();

        return teamMap;
    }


    private TournamentPlayer cursorToTournamentPlayer(Cursor cursor) {

        TournamentPlayer tournamentPlayer = new TournamentPlayer();
        tournamentPlayer.set_id(cursor.getInt(0));
        tournamentPlayer.setTournamentId(cursor.getString(1));
        tournamentPlayer.setPlayerUUID(cursor.getString(2));

        tournamentPlayer.setFirstName(cursor.getString(3));
        tournamentPlayer.setNickName(cursor.getString(4));
        tournamentPlayer.setLastName(cursor.getString(5));
        tournamentPlayer.setTeamName(cursor.getString(6));
        tournamentPlayer.setFaction(cursor.getString(7));
        tournamentPlayer.setMeta(cursor.getString(8));
        tournamentPlayer.setDummy(cursor.getInt(9) != 0);
        tournamentPlayer.setDroppedInRound(cursor.getInt(10));
        tournamentPlayer.setLocal(cursor.getInt(11) != 0);

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
            String online_uuid = player.getPlayerUUID();

            DatabaseReference referenceForUpdateTournamentPlayer = FirebaseDatabase.getInstance()
                    .getReference(FirebaseReferences.TOURNAMENT_PLAYERS + "/" + tournament.getOnlineUUID() + "/"
                        + online_uuid);

            referenceForUpdateTournamentPlayer.setValue(player);
        }
    }


    @Override
    public void dropPlayerFromTournament(TournamentPlayer player, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentPlayerTable.COLUMN_DROPPED_IN_ROUND, tournament.getActualRound());

        db.update(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, contentValues,
            TournamentPlayerTable.COLUMN_ID + " = ?", new String[] { String.valueOf(player.get_id()) });
    }


    @Override
    public void editTournamentPlayer(TournamentPlayer tournamentPlayer, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TournamentPlayerTable.COLUMN_TEAMNAME, tournamentPlayer.getTeamName());
        contentValues.put(TournamentPlayerTable.COLUMN_FACTION, tournamentPlayer.getFaction());

        db.update(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, contentValues,
            TournamentPlayerTable.COLUMN_ID + " = ?", new String[] { String.valueOf(tournamentPlayer.get_id()) });
    }


    @Override
    public void deleteTournamentPlayersFromTournament(Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ? ",
            new String[] { String.valueOf(tournament.get_id()) });
    }


    @Override
    public boolean checkPlayerAlreadyInTournament(Tournament tournament, Player player) {

        boolean alreadyInTournament = false;

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor;

        cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                new String[] { TournamentPlayerTable.COLUMN_PLAYER_UUID },
                TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ? AND " + TournamentPlayerTable.COLUMN_PLAYER_UUID
                + " = ?", new String[] { Long.toString(tournament.get_id()), player.getUUID() }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            alreadyInTournament = true;

            cursor.moveToNext();
        }

        cursor.close();

        return alreadyInTournament;
    }
}
