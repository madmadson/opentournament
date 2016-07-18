package madson.org.opentournament.service;

import android.database.Cursor;

import madson.org.opentournament.domain.Tournament;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface TournamentService {

    Tournament getTournamentForId(Long id);


    Cursor getCursorForTournaments();
}
