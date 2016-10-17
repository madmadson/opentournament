package madson.org.opentournament.events;

import madson.org.opentournament.domain.Tournament;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class DeleteTournamentEvent extends OpenTournamentEvent {

    private Tournament tournament;

    public DeleteTournamentEvent(Tournament tournament) {

        this.tournament = tournament;
    }

    public Tournament getTournament() {

        return tournament;
    }


    public void setTournament(Tournament tournament) {

        this.tournament = tournament;
    }
}
