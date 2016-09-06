package madson.org.opentournament.players;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.TournamentPlayer;


/**
 * Events for tournament setup.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface TournamentSetupEventListener {

    void addTournamentPlayer(TournamentPlayer tournamentPlayer);


    void clickTournamentPlayerListItem(TournamentPlayer tournamentPlayer);


    void clickAvailablePlayerListItem(Player player);


    void removeAvailablePlayer(Player player);
}
