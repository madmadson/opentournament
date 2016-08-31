package madson.org.opentournament.service;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentGame;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface OngoingTournamentService {

    /**
     * @param  tournamentId
     *
     * @return  list of all players for given tournament
     */
    List<TournamentPlayer> getRankingForRound(Long tournamentId, int round);


    /**
     * Add Player to tournament and return new tournament player instance.
     *
     * @param  player  player instance
     * @param  tournament  tournament
     */
    TournamentPlayer addPlayerToTournament(Player player, Tournament tournament);


    /**
     * @param  player
     * @param  tournamentId
     */
    void removePlayerFromTournament(Player player, Long tournamentId);


    /**
     * @param  tournamentId  id of the tournament
     * @param  round  the given round
     *
     * @return  list of pairings for round
     */
    List<WarmachineTournamentGame> getAllGamesForTournamentRound(Long tournamentId, int round);


    /**
     * @param  tournamentId
     * @param  round
     *
     * @return  new create list of pairings
     */
    List<WarmachineTournamentGame> createPairingForRound(Long tournamentId, int round);


    /**
     * @param  tournamentId
     *
     * @return
     */
    List<Player> getAllPlayersForTournament(long tournamentId);


    /**
     * Get specific pairing.
     *
     * @param  game_id
     *
     * @return  pairing
     */
    WarmachineTournamentGame getGameForId(long game_id);


    /**
     * Save result.
     *
     * @param  game
     */
    void saveGameResult(WarmachineTournamentGame game);


    /**
     * Calculates ranking of players for all played rounds. Note: calculation base are all games playes in given
     * tournament
     *
     * @param  tournament_id  the tournament_id
     * @param  round_number  round number till calculation
     */
    void createRankingForRound(long tournament_id, int round_number);


    /**
     * checks if previous round is finished before new round could be paired.
     *
     * @param  tournament_id
     * @param  round
     *
     * @return  if all games are finished or not
     */
    boolean checkAllGamesAreFinishedForRound(long tournament_id, int round);


    /**
     * Get all players for offline tournament.
     *
     * @param  tournament  tournament
     *
     * @return
     */
    List<TournamentPlayer> getLocalTournamentPlayers(Tournament tournament);
}
