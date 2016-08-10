package madson.org.opentournament.service.warmachine;

import madson.org.opentournament.domain.warmachine.WarmachineTournamentRanking;

import java.util.Comparator;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachineRankingComparator implements Comparator<WarmachineTournamentRanking> {

    @Override
    public int compare(WarmachineTournamentRanking player1, WarmachineTournamentRanking player2) {

        if (player2.getScore() - player1.getScore() != 0) {
            return player2.getScore() - player1.getScore();
        } else if (player2.getSos() - player1.getSos() != 0) {
            return player2.getSos() - player1.getSos();
        } else if (player2.getControl_points() - player1.getControl_points() != 0) {
            return player2.getControl_points() - player1.getControl_points();
        } else if (player2.getVictory_points() - player1.getVictory_points() != 0) {
            return player2.getVictory_points() - player1.getVictory_points();
        } else {
            return player2.getLastname().compareTo(player1.getLastname());
        }
    }
}
