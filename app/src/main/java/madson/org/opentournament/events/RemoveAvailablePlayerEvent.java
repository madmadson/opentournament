package madson.org.opentournament.events;

import madson.org.opentournament.domain.Player;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RemoveAvailablePlayerEvent extends OpenTournamentEvent {

    private Player player;

    public RemoveAvailablePlayerEvent(Player player) {

        this.player = player;
    }

    public Player getPlayer() {

        return player;
    }


    public void setPlayer(Player player) {

        this.player = player;
    }
}
