package madson.org.opentournament.domain;

import madson.org.opentournament.db.PlayerTable;

import java.util.Comparator;


/**
 * For "sorting" playerList.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerComparator implements Comparator<Player> {

    @Override
    public int compare(Player player1, Player player2) {

        return player1.getNickName().compareTo(player2.getNickName());
    }
}
