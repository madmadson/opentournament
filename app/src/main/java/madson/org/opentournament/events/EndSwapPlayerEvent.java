package madson.org.opentournament.events;

import madson.org.opentournament.domain.Game;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class EndSwapPlayerEvent extends OpenTournamentEvent {

    private Game game;

    public EndSwapPlayerEvent(Game game) {

        this.game = game;
    }

    public Game getGame() {

        return game;
    }
}
