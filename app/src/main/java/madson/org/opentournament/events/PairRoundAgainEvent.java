package madson.org.opentournament.events;

/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PairRoundAgainEvent extends OpenTournamentEvent {

    private int round;

    public PairRoundAgainEvent(int round) {

        this.round = round;
    }

    public int getRound() {

        return round;
    }


    public void setRound(int round) {

        this.round = round;
    }
}
