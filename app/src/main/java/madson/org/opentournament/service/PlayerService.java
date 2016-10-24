package madson.org.opentournament.service;

import java.util.List;

import madson.org.opentournament.domain.Player;


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
    void createLocalPlayer(Player player);


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


    /**
     * Delete given player.
     *
     * @param  player
     */
    void deleteLocalPlayer(Player player);
}
