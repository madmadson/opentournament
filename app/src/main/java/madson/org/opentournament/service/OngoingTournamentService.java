package madson.org.opentournament.service;

import madson.org.opentournament.domain.Player;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface OngoingTournamentService {

    /**
     * @param  tournamentId
     *
     * @return  list of all players for given tournament
     */
    List<Player> getPlayersForTournament(Long tournamentId);
}
