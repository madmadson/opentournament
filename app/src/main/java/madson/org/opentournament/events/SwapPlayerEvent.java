package madson.org.opentournament.events;

import madson.org.opentournament.domain.Game;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SwapPlayerEvent extends OpenTournamentEvent {

    private Game swappedGame;

    public SwapPlayerEvent(Game swappedGame) {

        this.swappedGame = swappedGame;
    }

    public Game getSwappedGame() {

        return swappedGame;
    }


    public void setSwappedGame(Game swappedGame) {

        this.swappedGame = swappedGame;
    }
}
