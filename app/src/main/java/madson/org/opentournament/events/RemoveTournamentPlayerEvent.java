package madson.org.opentournament.events;

import madson.org.opentournament.domain.TournamentPlayer;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RemoveTournamentPlayerEvent extends OpenTournamentEvent {

    private TournamentPlayer tournamentPlayer;

    public RemoveTournamentPlayerEvent(TournamentPlayer tournamentPlayer) {

        this.tournamentPlayer = tournamentPlayer;
    }

    public TournamentPlayer getTournamentPlayer() {

        return tournamentPlayer;
    }


    public void setTournamentPlayer(TournamentPlayer tournamentPlayer) {

        this.tournamentPlayer = tournamentPlayer;
    }
}
