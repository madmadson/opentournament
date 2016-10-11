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
public class TournamentPlayerServiceMockImpl implements TournamentPlayerService {

    private OpenTournamentDBHelper openTournamentDBHelper;

    public TournamentPlayerServiceMockImpl(Context context) {

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        deleteAllTournamentPlayers();
        createMockTournamentPlayers();
    }

    private void createMockTournamentPlayers() {

        addPlayerToTournament(1, "Tobias", "Madson", "Matt", "48\" AD", "Cygnar", "KA", "1");
        addPlayerToTournament(1, "Christoph", "Zaziboy", "Scholl", "48\" AD", "Circle", "KA", "2");
        addPlayerToTournament(1, "David", "Wildjack", "Voigt", "48\" AD", "Cryx", "KA", "3");
        addPlayerToTournament(1, "Andreas", "Ragegear", "Neugebauer", "48\" AD", "Legion", "KA", "4");
        addPlayerToTournament(1, "Andreas", "Raskild", "Tonndorf", "Team Karlsruhe", "Cryx", "KA2", "5");
        addPlayerToTournament(1, "Martina", "Bazinga", "Haug", "Team Karlsruhe", "Menoth", "KA2", "6");
        addPlayerToTournament(1, "Tobias", "Zeus", "Rohrauer", "Team Karlsruhe", "Cygnar", "KA2", "7");
        addPlayerToTournament(1, "Yann", "Arcane", "Krehl", "Team Karlsruhe", "Trollbloods", "KA2", "8");

        addPlayerToTournament(2, "Tobias", "Madson", "Matt", "48\" AD", "Cygnar", "KA", "1");
        addPlayerToTournament(2, "Christoph", "Zaziboy", "Scholl", "48\" AD", "Circle", "KA", "2");
        addPlayerToTournament(2, "David", "Wildjack", "Voigt", "49\" AD", "Cryx", "KA", "3");
        addPlayerToTournament(2, "Andreas", "Ragegear", "Neugebauer", "49\" AD", "Legion", "KA", "4");
        addPlayerToTournament(2, "Andreas", "Raskild", "Tonndorf", "Team Karlsruhe", "Cryx", "KA2", "5");
        addPlayerToTournament(2, "Martina", "Bazinga", "Haug", "Team Karlsruhe", "Menoth", "KA2", "6");
        addPlayerToTournament(2, "Tobias", "Zeus", "Rohrauer", "Team Karlsruhe2", "Cygnar", "KA2", "7");
        addPlayerToTournament(2, "Yann", "Arcane", "Krehl", "Team Karlsruhe2", "Trollbloods", "KA2", "8");
    }


    private void addPlayerToTournament(int tournament_id, String firstName, String nickName, String lastName,
        String teamName, String faction, String meta, String uuid) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentPlayerTable.COLUMN_TOURNAMENT_ID, tournament_id);
        contentValues.put(TournamentPlayerTable.COLUMN_FIRSTNAME, firstName);
        contentValues.put(TournamentPlayerTable.COLUMN_NICKNAME, nickName);
        contentValues.put(TournamentPlayerTable.COLUMN_LASTNAME, lastName);
        contentValues.put(TournamentPlayerTable.COLUMN_TEAMNAME, teamName);
        contentValues.put(TournamentPlayerTable.COLUMN_FACTION, faction);
        contentValues.put(TournamentPlayerTable.COLUMN_META, meta);
        contentValues.put(TournamentPlayerTable.COLUMN_DUMMY, false);

        contentValues.put(TournamentPlayerTable.COLUMN_LOCAL, true);
        contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_UUID, uuid);

        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);

        db.close();
    }


    private void deleteAllTournamentPlayers() {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, null);

        db.close();
    }


    @Override
    public void removePlayerFromTournament(TournamentPlayer player) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
            TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ?  AND  " + TournamentPlayerTable.COLUMN_PLAYER_UUID
            + " = ? ", new String[] { player.getTournamentId(), player.getPlayerUUID() });

        db.close();
    }


    @Override
    public Map<TournamentTeam, List<TournamentPlayer>> getAllTeamsForTournament(Tournament tournament) {

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
        readableDatabase.close();

        return teamMap;
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
        readableDatabase.close();

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

            players.put(tournamentPlayer.getPlayerUUID(), tournamentPlayer);

            cursor.moveToNext();
        }

        cursor.close();
        readableDatabase.close();

        return players;
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

        db.close();
    }


    @Override
    public void editTournamentPlayer(TournamentPlayer tournamentPlayer, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TournamentPlayerTable.COLUMN_TEAMNAME, tournamentPlayer.getTeamName());
        contentValues.put(TournamentPlayerTable.COLUMN_FACTION, tournamentPlayer.getFaction());

        db.update(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, contentValues,
            TournamentPlayerTable.COLUMN_ID + " = ?", new String[] { String.valueOf(tournamentPlayer.get_id()) });

        db.close();
    }


    @Override
    public void deleteTournamentPlayersFromTournament(Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, TournamentPlayerTable.COLUMN_TOURNAMENT_ID + " = ? ",
            new String[] { String.valueOf(tournament.get_id()) });

        db.close();
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
        readableDatabase.close();

        return alreadyInTournament;
    }
}
