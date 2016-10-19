package madson.org.opentournament.events;

/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public class UndoRoundEvent extends OpenTournamentEvent {

    private int round;

    public UndoRoundEvent(int round) {

        this.round = round;
    }

    public int getRound() {

        return round;
    }
}
