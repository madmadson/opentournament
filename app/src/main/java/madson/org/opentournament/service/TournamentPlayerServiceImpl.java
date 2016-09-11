package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
public class TournamentPlayerServiceImpl implements TournamentPlayerService {

    private OpenTournamentDBHelper openTournamentDBHelper;

    public TournamentPlayerServiceImpl(Context context) {

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        deleteAllTournamentPlayers();
        createMockTournamentPlayers();
    }

    @Override
    public void removePlayerFromTournament(Player player, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, "tournament_id  = ?  AND  player_id = ? ",
            new String[] { String.valueOf(tournament.get_id()), String.valueOf(player.get_id()) });

        db.close();
    }


    @Override
    public List<String> getAllTeamNamesForTournament(Tournament tournament) {

        List<String> listOfTeamnames = new ArrayList<>();

        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursorWithAllTeamnames = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                new String[] { TournamentPlayerTable.COLUMN_TEAMNAME }, "tournament_id  = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursorWithAllTeamnames.moveToFirst();

        while (!cursorWithAllTeamnames.isAfterLast()) {
            String teamname = cursorWithAllTeamnames.getString(0);

            if (teamname != null && !teamname.isEmpty() && !listOfTeamnames.contains(teamname)) {
                listOfTeamnames.add(teamname);
            }

            cursorWithAllTeamnames.moveToNext();
        }

        Collections.sort(listOfTeamnames);

        cursorWithAllTeamnames.close();
        readableDatabase.close();

        return listOfTeamnames;
    }


    @Override
    public void setTournamentPlayerToFirebase(TournamentPlayer tournamentPlayer, Tournament tournament) {

        Log.i(this.getClass().getName(), "pushes tournament player online: " + tournament);

        UUID uuid = UUID.randomUUID();

        tournamentPlayer.setOnline_uuid(uuid.toString());

        // save TournamentPlayer
        DatabaseReference referenceForNewTournamentPlayer = FirebaseDatabase.getInstance()
                .getReference("tournament_players/" + tournament.getOnlineUUID() + "/" + uuid.toString());

        referenceForNewTournamentPlayer.setValue(tournamentPlayer);

        if (tournamentPlayer.getPlayer_online_uuid() != null) {
            // save reference to player
            DatabaseReference referenceToNewPlayerTournamentEntry = FirebaseDatabase.getInstance()
                    .getReference("players/" + tournamentPlayer.getPlayer_online_uuid() + "/tournaments/"
                        + tournament.getOnlineUUID());

            referenceToNewPlayerTournamentEntry.setValue(true);
        }

        // save teamname
        if (!tournamentPlayer.getTeamname().isEmpty()) {
            DatabaseReference referenceToNewPlayerTournamentEntry = FirebaseDatabase.getInstance()
                    .getReference("tournaments/" + tournament.getOnlineUUID() + "/teamnames/"
                        + tournamentPlayer.getTeamname());

            referenceToNewPlayerTournamentEntry.setValue(true);
        }

        // save reference to tournament
        DatabaseReference referenceInTournament = FirebaseDatabase.getInstance()
                .getReference("tournaments/" + tournament.getOnlineUUID() + "/tournament_players/" + uuid.toString());

        referenceInTournament.setValue(true);

        // update actual players on tournament
        final DatabaseReference referenceActualRoundInTournament = FirebaseDatabase.getInstance()
                .getReference("tournaments/" + tournament.getOnlineUUID() + "/actualPlayers");

        final ValueEventListener actualPlayerListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Integer actualPlayers = dataSnapshot.getValue(Integer.class);
                referenceActualRoundInTournament.setValue(actualPlayers + 1);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        DatabaseReference child = FirebaseDatabase.getInstance()
                .getReference()
                .child("tournaments/" + tournament.getOnlineUUID()
                    + "/actualPlayers");

        child.addListenerForSingleValueEvent(actualPlayerListener);
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
    public void removeTournamentPlayerFromFirebase(TournamentPlayer tournamentPlayer, final Tournament tournament) {

        Log.i(this.getClass().getName(), "delete online tournament tournamentPlayer in firebase: " + tournamentPlayer);

        DatabaseReference referencePlayerInTournamentToDelete = FirebaseDatabase.getInstance()
                .getReference("tournaments/" + tournament.getOnlineUUID() + "/tournament_players/"
                    + tournamentPlayer.getOnline_uuid());

        referencePlayerInTournamentToDelete.removeValue();

        DatabaseReference referenceInPlayerToTournamentToDelete = FirebaseDatabase.getInstance()
                .getReference("players/" + tournamentPlayer.getPlayer_online_uuid() + "/tournaments/"
                    + tournament.getOnlineUUID());

        referenceInPlayerToTournamentToDelete.removeValue();

        DatabaseReference referenceTournamentPlayerToDelete = FirebaseDatabase.getInstance()
                .getReference("tournament_players/" + tournament.getOnlineUUID() + "/"
                    + tournamentPlayer.getOnline_uuid());

        referenceTournamentPlayerToDelete.removeValue();

        // update actual players on tournament
        final DatabaseReference referenceActualRoundInTournament = FirebaseDatabase.getInstance()
                .getReference("tournaments/" + tournament.getOnlineUUID() + "/actualPlayers");

        final ValueEventListener actualPlayerListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Integer actualPlayers = dataSnapshot.getValue(Integer.class);
                referenceActualRoundInTournament.setValue(actualPlayers - 1);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        DatabaseReference child = FirebaseDatabase.getInstance()
                .getReference()
                .child("tournaments/" + tournament.getOnlineUUID()
                    + "/actualPlayers");

        child.addListenerForSingleValueEvent(actualPlayerListener);
    }


    @Override
    public void removeOnlinePlayerFromTournament(TournamentPlayer tournamentPlayer, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
            "tournament_id  = ?  AND  " + TournamentPlayerTable.COLUMN_PLAYER_ONLINE_UUID + " = ? ",
            new String[] {
                String.valueOf(tournament.get_id()), String.valueOf(tournamentPlayer.getPlayer_online_uuid())
            });

        db.close();
    }


    @Override
    public void addTournamentPlayerToTournament(TournamentPlayer player, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentPlayerTable.COLUMN_TOURNAMENT_ID, tournament.get_id());
        contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ID, player.getPlayer_id());
        contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ONLINE_UUID, player.getPlayer_online_uuid());
        contentValues.put(TournamentPlayerTable.COLUMN_FIRSTNAME, player.getFirstname());
        contentValues.put(TournamentPlayerTable.COLUMN_NICKNAME, player.getNickname());
        contentValues.put(TournamentPlayerTable.COLUMN_LASTNAME, player.getLastname());
        contentValues.put(TournamentPlayerTable.COLUMN_TEAMNAME, player.getTeamname());
        contentValues.put(TournamentPlayerTable.COLUMN_FACTION, player.getFaction());
        contentValues.put(TournamentPlayerTable.COLUMN_DUMMY, player.getDummy());

        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);

        db.close();
    }


    @Override
    public List<TournamentPlayer> getAllPlayersForTournament(Tournament tournament) {

        List<TournamentPlayer> players = new ArrayList<>();
        SQLiteDatabase readableDatabase = openTournamentDBHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER,
                TournamentPlayerTable.ALL_COLS_FOR_TOURNAMENT_PLAYER_TABLE, "tournament_id  = ?",
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
                TournamentPlayerTable.ALL_COLS_FOR_TOURNAMENT_PLAYER_TABLE, "tournament_id  = ?",
                new String[] { Long.toString(tournament.get_id()) }, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            TournamentPlayer tournamentPlayer = cursorToTournamentPlayer(cursor);

            if (tournamentPlayer.getPlayer_online_uuid() != null) {
                players.put(tournamentPlayer.getPlayer_online_uuid(), tournamentPlayer);
            } else {
                players.put(String.valueOf(tournamentPlayer.getPlayer_id()), tournamentPlayer);
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
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(TournamentPlayerTable.COLUMN_TOURNAMENT_ID, tournament_id);
        contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ID, player.get_id());
        contentValues.put(TournamentPlayerTable.COLUMN_FIRSTNAME, player.getFirstname());
        contentValues.put(TournamentPlayerTable.COLUMN_NICKNAME, player.getNickname());
        contentValues.put(TournamentPlayerTable.COLUMN_LASTNAME, player.getLastname());
        contentValues.put(TournamentPlayerTable.COLUMN_TEAMNAME, teamname);
        contentValues.put(TournamentPlayerTable.COLUMN_FACTION, faction);
        contentValues.put(TournamentPlayerTable.COLUMN_META, meta);
        contentValues.put(TournamentPlayerTable.COLUMN_DUMMY, false);

        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);
    }


    private void deleteAllTournamentPlayers() {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, null);
    }


    private TournamentPlayer cursorToTournamentPlayer(Cursor cursor) {

        TournamentPlayer tournamentPlayer = new TournamentPlayer();
        tournamentPlayer.set_id(cursor.getInt(0));
        tournamentPlayer.setTournament_id(cursor.getInt(1));
        tournamentPlayer.setPlayer_id(cursor.getInt(2));
        tournamentPlayer.setPlayer_online_uuid(cursor.getString(3));

        tournamentPlayer.setFirstname(cursor.getString(4));
        tournamentPlayer.setNickname(cursor.getString(5));
        tournamentPlayer.setLastname(cursor.getString(6));
        tournamentPlayer.setTeamname(cursor.getString(7));
        tournamentPlayer.setFaction(cursor.getString(8));
        tournamentPlayer.setMeta(cursor.getString(9));
        tournamentPlayer.setDummy(cursor.getInt(10) != 0);

        return tournamentPlayer;
    }
}
