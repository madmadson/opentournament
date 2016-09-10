package madson.org.opentournament.service;

import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.domain.warmachine.Game;

import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface OngoingTournamentService {

    /**
     * @param  tournament  id of the tournament
     * @param  round  the given round
     *
     * @return  list of pairings for round
     */
    List<Game> getGamesForRound(Tournament tournament, int round);


    /**
     * Create games for given round.
     *
     * @param  tournament  tournament
     * @param  round  the round calculated for
     * @param  rankingForRound
     * @param  pairingOptions
     *
     * @return  new create list of pairings
     */
    List<Game> createGamesForRound(Tournament tournament, int round, Map<String, TournamentRanking> rankingForRound,
        List<PairingOption> pairingOptions);


    /**
     * Get specific pairing.
     *
     * @param  game_id
     *
     * @return  pairing
     */
    Game getGameForId(long game_id);


    /**
     * Save result.
     *
     * @param  game
     */
    void saveGameResult(Game game);


    /**
     * checks if previous round is finished before new round could be paired.
     *
     * @param  tournament
     * @param  round
     *
     * @return  if all games are finished or not
     */
    boolean checkAllGamesAreFinishedForRound(Tournament tournament, int round);
}
