package madson.org.opentournament.tournaments;

import madson.org.opentournament.domain.Tournament;

import java.util.Comparator;


/**
 * Compares two tournaments. First date. Second name
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentComparator implements Comparator<Tournament> {

    @Override
    public int compare(Tournament t1, Tournament t2) {

        if (t1.getDateOfTournament() != null && t2.getDateOfTournament() != null) {
            return t1.getDateOfTournament().getTime() > t2.getDateOfTournament().getTime() ? 1 : 0;
        } else {
            return t1.getName().compareTo(t2.toString());
        }
    }
}
