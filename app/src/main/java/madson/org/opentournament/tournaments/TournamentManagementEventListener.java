package madson.org.opentournament.tournaments;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;


/**
 * Events for tournament setup.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface TournamentManagementEventListener {

    void onTournamentListItemClicked(Tournament tournament);


    void onTournamentChangedEvent(Tournament tournament);


    void onTournamentAddedEvent(Tournament tournament);


    void onTournamentDeletedEvent(Tournament tournament);


    void onTournamentEditClicked(Tournament tournament);


    void onTournamentUploadClicked(Tournament tournament);
}
