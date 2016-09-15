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
     * All tournament in local db.
     *
     * @return  all tournaments
     */
    List<Tournament> getTournaments();


    /**
     * Tournament to save.
     *
     * @param  tournament
     */
    Tournament createTournament(Tournament tournament);


    /**
     * Delete tournament for given id.
     *
     * @param  id
     */
    void deleteTournament(long id);


    /**
     * Remove value from firebase.
     *
     * @param  tournament
     */
    void removeTournamentInFirebase(Tournament tournament);


    /**
     * Edited Tournament.
     *
     * @param  tournament
     */
    void updateTournament(Tournament tournament);


    /**
     * @param  tournament
     * @param  round
     */
    Tournament updateActualRound(Tournament tournament, int round);


    /**
     * Update tournament online.
     *
     * @param  tournament  to upload
     */
    void updateTournamentInFirebase(Tournament tournament);


    /**
     * Pushes a tournament to online services. Add tournament UUID to tournament.
     *
     * @param  tournament  to upload
     *
     * @return  uploaded tournament with newly inserted onlineUUID.
     */
    Tournament uploadTournament(Tournament tournament);


    void increaseActualPlayerForTournament(Tournament tournament);


    void decreaseActualPlayerForTournament(Tournament tournament);


    /**
     * @param  tournament
     */
    void endTournament(Tournament tournament);
}
