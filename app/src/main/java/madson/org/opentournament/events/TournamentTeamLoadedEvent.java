package madson.org.opentournament.events;

import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTeam;

import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public class TournamentTeamLoadedEvent extends OpenTournamentEvent {

    private Map<TournamentTeam, List<TournamentPlayer>> teamMap;

    public TournamentTeamLoadedEvent(Map<TournamentTeam, List<TournamentPlayer>> teamMap) {

        this.teamMap = teamMap;
    }

    public Map<TournamentTeam, List<TournamentPlayer>> getTeamMap() {

        return teamMap;
    }
}
