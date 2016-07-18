package madson.org.opentournament.service;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import madson.org.opentournament.domain.Tournament;

import java.util.Date;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentServiceImpl implements TournamentService {

    private SQLiteOpenHelper dbHelper;

    public TournamentServiceImpl(SQLiteOpenHelper dbHelper) {

        this.dbHelper = dbHelper;
    }

    @Override
    public Tournament getTournamentForId(Long id) {

        Tournament tournament = new Tournament();

        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.query("tournament", Tournament.ALL_COLS_FOR_TOURNAMENT, "_id  = ?",
                new String[] { Long.toString(id) }, null, null, null);

        try {
            if (cursor.moveToFirst()) {
                tournament = new Tournament(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), new Date(cursor.getLong(4)));
            }
        } finally {
            cursor.close();
            dbHelper.close();
        }

        return tournament;
    }
}
