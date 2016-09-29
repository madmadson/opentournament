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

        if (t1.getState().compareTo(t2.getState()) != 0) {
            return t2.getState().compareTo(t1.getState());
        } else if (t1.getDateOfTournament() != null && t2.getDateOfTournament() != null) {
            return t1.getDateOfTournament().compareTo(t2.getDateOfTournament());
        } else {
            return t1.getName().compareTo(t2.getName());
        }
    }
}
