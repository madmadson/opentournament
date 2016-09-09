package madson.org.opentournament.organize;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.TournamentPlayer;


/**
 * Events for tournament setup.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface TournamentEventListener {

    void startRound(int roundToStart);
}
