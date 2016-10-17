package madson.org.opentournament.events;

import madson.org.opentournament.domain.TournamentPlayer;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public class UpdateTournamentPlayerEvent extends OpenTournamentEvent {

    private String oldTeamName;

    private TournamentPlayer tournamentPlayer;

    public UpdateTournamentPlayerEvent(String oldTeamName, TournamentPlayer tournamentPlayer) {

        this.oldTeamName = oldTeamName;
        this.tournamentPlayer = tournamentPlayer;
    }

    public TournamentPlayer getTournamentPlayer() {

        return tournamentPlayer;
    }


    public void setTournamentPlayer(TournamentPlayer tournamentPlayer) {

        this.tournamentPlayer = tournamentPlayer;
    }


    public String getOldTeamName() {

        return oldTeamName;
    }


    public void setOldTeamName(String oldTeamName) {

        this.oldTeamName = oldTeamName;
    }
}
