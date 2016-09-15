package madson.org.opentournament.organize;

import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.TournamentPlayer;


/**
 * Events for tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface TournamentEventListener {

    void startRound(int roundToStart);


    void pairRoundAgain(int round_for_pairing);


    void pairingChanged(Game game1, Game game2);
}
