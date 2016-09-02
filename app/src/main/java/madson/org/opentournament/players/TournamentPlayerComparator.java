package madson.org.opentournament.players;

import madson.org.opentournament.domain.TournamentPlayer;

import java.util.Comparator;


/**
 * For "sorting" playerList.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerComparator implements Comparator<TournamentPlayer> {

    @Override
    public int compare(TournamentPlayer player1, TournamentPlayer player2) {

        return player1.getNickname().compareTo(player2.getNickname());
    }
}
