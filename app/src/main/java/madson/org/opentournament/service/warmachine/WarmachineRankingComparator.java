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

        if (player1.getScore() - player2.getScore() != 0) {
            return player1.getScore() - player2.getScore();
        } else if (player1.getSos() - player2.getSos() != 0) {
            return player1.getSos() - player2.getSos();
        } else if (player1.getControl_points() - player2.getControl_points() != 0) {
            return player1.getControl_points() - player2.getControl_points();
        } else if (player1.getVictory_points() - player2.getVictory_points() != 0) {
            return player1.getVictory_points() - player2.getVictory_points();
        } else {
            return player1.getLastname().compareTo(player2.getLastname());
        }
    }
}
