package madson.org.opentournament.service;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentRanking;

import java.util.List;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface RankingService {

    /**
     * @param  tournament
     *
     * @return  list of all ranking for given round of tournament
     */
    List<TournamentRanking> getTournamentRankingForRound(Tournament tournament, int round);


    /**
     * Calculates ranking of players for all played rounds. Note: calculation base are all games players in given
     * tournament
     *
     * @param  tournament  the tournament
     * @param  round_number  round number till calculation
     */
    void createRankingForRound(Tournament tournament, int round_number);
}