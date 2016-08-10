package madson.org.opentournament.service.warmachine;

import madson.org.opentournament.domain.warmachine.WarmachineTournamentRanking;

import java.util.Comparator;


/**
 * For "sorting" playerList.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachinePlayerComparator implements Comparator<WarmachineTournamentRanking> {

    @Override
    public int compare(WarmachineTournamentRanking player1, WarmachineTournamentRanking player2) {

        return player1.getScore() - player2.getScore();
    }
}
