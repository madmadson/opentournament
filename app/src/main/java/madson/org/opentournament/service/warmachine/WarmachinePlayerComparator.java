package madson.org.opentournament.service.warmachine;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentPlayer;

import java.util.Comparator;


/**
 * For "sorting" playerList.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachinePlayerComparator implements Comparator<WarmachineTournamentPlayer> {

    @Override
    public int compare(WarmachineTournamentPlayer player1, WarmachineTournamentPlayer player2) {

        return player1.getScore() - player2.getScore();
    }
}
