package madson.org.opentournament.events;

import madson.org.opentournament.domain.Game;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PairingChangedEvent extends OpenTournamentEvent {

    private Game gameOne;
    private Game gameTwo;

    public PairingChangedEvent(Game gameOne, Game gameTwo) {

        this.gameOne = gameOne;
        this.gameTwo = gameTwo;
    }

    public Game getGameOne() {

        return gameOne;
    }


    public void setGameOne(Game gameOne) {

        this.gameOne = gameOne;
    }


    public Game getGameTwo() {

        return gameTwo;
    }


    public void setGameTwo(Game gameTwo) {

        this.gameTwo = gameTwo;
    }
}
