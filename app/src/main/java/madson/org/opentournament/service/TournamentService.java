package madson.org.opentournament.service;

import android.content.ContentValues;

import android.database.Cursor;

import madson.org.opentournament.domain.Tournament;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface TournamentService {

    /**
     * @param  id  id of the tournament
     *
     * @return
     */
    Tournament getTournamentForId(Long id);


    /**
     * @return  cursor for all tournaments
     */
    Cursor getCursorForAllTournaments();


    /**
     * @param  tournamentId
     *
     * @return
     */
    Cursor getCursorForPlayersOfTournament(Long tournamentId);


    /**
     * @param  contentValues  the conetent
     */
    void insertNewTournament(ContentValues contentValues);


    /**
     * @param  tournamentId  id of the tournament
     * @param  contentValues  the content
     */
    void editTournament(Long tournamentId, ContentValues contentValues);
}
