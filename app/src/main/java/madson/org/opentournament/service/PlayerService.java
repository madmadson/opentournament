package madson.org.opentournament.service;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;

import java.util.List;


/**
 * Service for player management.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface PlayerService {

    /**
     * Create local player.
     *
     * @param  player
     */
    Player createLocalPlayer(Player player);


    /**
     * get player for id.
     *
     * @param  playerId
     *
     * @return  the player
     */
    Player getPlayerForId(String playerId);


    /**
     * Get all players.
     *
     * @return  list with all players
     */
    List<Player> getAllLocalPlayers();
}
