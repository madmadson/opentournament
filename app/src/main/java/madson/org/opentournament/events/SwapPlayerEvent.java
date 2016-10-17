package madson.org.opentournament.events;

import madson.org.opentournament.domain.Game;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SwapPlayerEvent extends OpenTournamentEvent {

    private Game swappedGame;
    private int player;

    public SwapPlayerEvent(Game swappedGame, int player) {

        this.swappedGame = swappedGame;
        this.player = player;
    }

    public Game getSwappedGame() {

        return swappedGame;
    }


    public void setSwappedGame(Game swappedGame) {

        this.swappedGame = swappedGame;
    }


    public int getPlayer() {

        return player;
    }


    public void setPlayer(int player) {

        this.player = player;
    }
}
