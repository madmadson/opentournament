package madson.org.opentournament.domain;

import java.util.Comparator;


/**
 * For "sorting" playerList.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerComparator implements Comparator<TournamentPlayer> {

    @Override
    public int compare(TournamentPlayer player1, TournamentPlayer player2) {

        return player1.getNickName().compareTo(player2.getNickName());
    }
}
