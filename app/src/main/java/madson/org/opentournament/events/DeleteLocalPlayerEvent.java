package madson.org.opentournament.events;

import madson.org.opentournament.domain.Player;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class DeleteLocalPlayerEvent extends OpenTournamentEvent {

    private Player player;

    public DeleteLocalPlayerEvent(Player player) {

        this.player = player;
    }

    public Player getPlayer() {

        return player;
    }
}
