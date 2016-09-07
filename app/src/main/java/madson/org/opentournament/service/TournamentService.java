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
     * Pushes a tournament to online services. Clones given tournament and addTournamentPlayer meta data for online
     * instance. given tournament will not be manipulated
     *
     * @param  tournament
     */
    void setTournamentToFirebase(Tournament tournament);


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
     * @param  tournamentId
     * @param  round
     */
    void updateActualRound(Long tournamentId, int round);


    /**
     * @param  tournament
     */
    void updateTournamentInFirebase(Tournament tournament);
}
