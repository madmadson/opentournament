package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.TournamentPlayerTable;
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.db.warmachine.GameTable;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.players.TournamentPlayerComparator;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    public TournamentPlayer addPlayerToTournament(Player player, Tournament tournament) {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TournamentPlayerTable.COLUMN_TOURNAMENT_ID, tournament.get_id());
        contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ID, player.get_id());
        contentValues.put(TournamentPlayerTable.COLUMN_PLAYER_ONLINE_UUID, player.getOnlineUUID());
        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);
        db.close();

        return new TournamentPlayer(player, tournament);
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

        return tournamentPlayer;
    }
}
