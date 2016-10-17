package madson.org.opentournament.events;

import madson.org.opentournament.domain.Game;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class EnterGameResultConfirmed extends OpenTournamentEvent {

    private Game enteredGame;

    public EnterGameResultConfirmed(Game enteredGame) {

        this.enteredGame = enteredGame;
    }

    public Game getEnteredGame() {

        return enteredGame;
    }


    public void setEnteredGame(Game enteredGame) {

        this.enteredGame = enteredGame;
    }
}
