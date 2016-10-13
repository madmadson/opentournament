package madson.org.opentournament.organize;

import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.TournamentEventTag;


/**
 * Events for tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface TournamentEventListener {

    void startRound(int roundToStart, Tournament tournament);


    void pairRoundAgain(int round_for_pairing);


    void pairingChanged(Game game1, Game game2);


    void enterGameResultConfirmed(TournamentEventTag tag, Game game);


    void addTournamentPlayer(TournamentPlayer tournamentPlayer);


    void removeTournamentPlayer(TournamentPlayer tournamentPlayer);


    void addPlayerToTournament(Player player);


    void removeAvailablePlayer(Player player);


    void updateTournamentPlayer(TournamentPlayer updatedPLayer, String oldTeamName);


    void addRegistration(TournamentPlayer player);


    void handleTournamentEvent(TournamentEventTag eventTag);
}
