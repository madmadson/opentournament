package madson.org.opentournament.service;

import android.database.Cursor;

import madson.org.opentournament.domain.Player;

import java.util.List;


/**
 * Service for player management.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface PlayerService {

    void createPlayer(Player player);


    /**
     * get player for id.
     *
     * @param  playerId
     *
     * @return  the player
     */
    Player getPlayerForId(Long playerId);


    /**
     * Get all players.
     *
     * @return  list with all players
     */
    List<Player> getAllPlayers();
}
