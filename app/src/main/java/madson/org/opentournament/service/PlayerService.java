package madson.org.opentournament.service;

import android.database.Cursor;

import madson.org.opentournament.domain.Player;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface PlayerService {

    void createPlayer(Player player);


    Player getPlayerForId(Long playerId);
}
