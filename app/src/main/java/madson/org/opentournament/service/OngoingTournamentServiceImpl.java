package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.db.PlayerTable;
import madson.org.opentournament.db.TournamentPlayerTable;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OngoingTournamentServiceImpl implements OngoingTournamentService {

    private OpenTournamentDBHelper openTournamentDBHelper;

    public OngoingTournamentServiceImpl(Context context) {

        Log.w(OngoingTournamentServiceImpl.class.getName(), "OngoingTournamentServiceImpl Constructor");

        if (openTournamentDBHelper == null) {
            openTournamentDBHelper = new OpenTournamentDBHelper(context);
        }

        deleteAllTournamentPlayers();
        createMockTournamentPlayers();
    }

    private void createMockTournamentPlayers() {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("tournament_id", 1);
        contentValues.put("player_id", 1);
        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);
        contentValues.clear();

        contentValues = new ContentValues();
        contentValues.put("tournament_id", 1);
        contentValues.put("player_id", 2);
        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);
        contentValues.clear();

        contentValues = new ContentValues();
        contentValues.put("tournament_id", 1);
        contentValues.put("player_id", 3);
        db.insert(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, contentValues);
    }


    private void deleteAllTournamentPlayers() {

        SQLiteDatabase db = openTournamentDBHelper.getWritableDatabase();
        db.delete(TournamentPlayerTable.TABLE_TOURNAMENT_PLAYER, null, null);
    }
}
