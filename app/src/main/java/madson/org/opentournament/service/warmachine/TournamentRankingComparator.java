package madson.org.opentournament.service.warmachine;

import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;

import java.util.Comparator;


/**
 * Compare rankings to get ranking list or final standing.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentRankingComparator implements Comparator<TournamentRanking> {

    @Override
    public int compare(TournamentRanking ranking1, TournamentRanking ranking2) {

        if (ranking2.getScore() - ranking1.getScore() != 0) {
            return ranking2.getScore() - ranking1.getScore();
        } else if (ranking2.getSos() - ranking1.getSos() != 0) {
            return ranking2.getSos() - ranking1.getSos();
        } else if (ranking2.getControl_points() - ranking1.getControl_points() != 0) {
            return ranking2.getControl_points() - ranking1.getControl_points();
        } else if (ranking2.getVictory_points() - ranking1.getVictory_points() != 0) {
            return ranking2.getVictory_points() - ranking1.getVictory_points();
        } else {
            return ranking2.getLastname().compareTo(ranking1.getLastname());
        }
    }
}
