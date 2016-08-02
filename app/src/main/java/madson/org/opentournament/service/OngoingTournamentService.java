package madson.org.opentournament.service;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentPairing;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentPlayer;

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
    List<WarmachineTournamentPlayer> getPlayersForWarmachineTournament(Long tournamentId);


    /**
     * @param  player
     * @param  tournamentId
     */
    void addPlayerToTournament(Player player, Long tournamentId);


    /**
     * @param  player
     * @param  tournamentId
     */
    void removePlayerFromTournament(Player player, Long tournamentId);


    /**
     * @param  tournamentId  id of the tournament
     * @param  round  the given round
     *
     * @return  list of pairings for round
     */
    List<WarmachineTournamentPairing> getPairingsForTournament(Long tournamentId, int round);


    /**
     * @param  tournamentId
     * @param  round
     *
     * @return  new create list of pairings
     */
    List<WarmachineTournamentPairing> createPairingForRound(Long tournamentId, int round);


    /**
     * @param  tournamentId
     *
     * @return
     */
    List<Player> getPlayersForTournament(long tournamentId);
}
