package madson.org.opentournament.events;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public class UpdateTournamentEvent extends OpenTournamentEvent {

    private Tournament tournament;

    public UpdateTournamentEvent(Tournament tournament) {

        this.tournament = tournament;
    }

    public Tournament getTournament() {

        return tournament;
    }


    public void setTournament(Tournament tournament) {

        this.tournament = tournament;
    }
}
