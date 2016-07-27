package madson.org.opentournament.service;

import android.content.ContentValues;

import android.database.Cursor;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;

import java.util.List;


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
     * @param  contentValues  the content
     */
    void createTournament(ContentValues contentValues);


    /**
     * @param  tournamentId  id of the tournament
     * @param  contentValues  the content
     */
    void editTournament(Long tournamentId, ContentValues contentValues);


    /**
     * @return  all tournaments
     */
    List<Tournament> getTournaments();
}
