package madson.org.opentournament.service.warmachine;

import madson.org.opentournament.domain.TournamentPlayer;

import java.util.Comparator;


/**
 * For "sorting" playerList.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachinePlayerComparator implements Comparator<TournamentPlayer> {

    @Override
    public int compare(TournamentPlayer player1, TournamentPlayer player2) {

        return player2.getScore() - player1.getScore();
    }
}
