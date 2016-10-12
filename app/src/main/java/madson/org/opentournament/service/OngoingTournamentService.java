package madson.org.opentournament.service;

import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;

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
     * @return  if pairing was successfully
     */
    boolean createGamesForRound(Tournament tournament, int round, Map<String, TournamentRanking> rankingForRound,
        Map<String, PairingOption> pairingOptions);


    /**
     * Save result.
     *
     * @param  game
     */
    Game saveGameResult(Game game);


    /**
     * checks if previous round is finished before new round could be paired.
     *
     * @param  tournament
     * @param  round
     *
     * @return  if all games are finished or not
     */
    boolean checkAllGamesAreFinishedForRound(Tournament tournament, int round);


    /**
     * @param  tournament
     * @param  round_for_pairing
     */
    void deleteGamesForRound(Tournament tournament, int round_for_pairing);


    /**
     * @param  uploadedTournament
     */
    void uploadGames(Tournament uploadedTournament);


    /**
     * DElete all games for given tournament.
     *
     * @param  tournament
     */
    void deleteGamesForTournament(Tournament tournament);


    /**
     * @param  tournament
     * @param  parent_game
     *
     * @return
     */
    List<Game> getGamesForTeamMatch(Tournament tournament, Game parent_game);


    /**
     * @param  gameToSave
     * @param  tournament
     *
     * @return
     */
    Game updateTeamMatch(Game gameToSave, Tournament tournament);
}
