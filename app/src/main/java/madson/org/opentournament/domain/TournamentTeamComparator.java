package madson.org.opentournament.domain;

import java.util.Comparator;


/**
 * For "sorting" playerList.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentTeamComparator implements Comparator<TournamentTeam> {

    @Override
    public int compare(TournamentTeam player1, TournamentTeam player2) {

        return player1.getTeamName().compareTo(player2.getTeamName());
    }
}
