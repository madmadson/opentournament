package madson.org.opentournament.service;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.domain.warmachine.Game;

import java.util.List;


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
     *
     * @return  new create list of pairings
     */
    List<Game> createGamesForRound(Tournament tournament, int round);


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
     * @param  tournament_id
     * @param  round
     *
     * @return  if all games are finished or not
     */
    boolean checkAllGamesAreFinishedForRound(Tournament tournament, int round);
}
