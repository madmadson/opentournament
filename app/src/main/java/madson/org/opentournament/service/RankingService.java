package madson.org.opentournament.service;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentRanking;

import java.util.List;
import java.util.Map;


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
    Map<String, TournamentRanking> createRankingForRound(Tournament tournament, int round_number);


    /**
     * @param  tournament
     * @param  round_for_pairing
     */
    void deleteRankingForRound(Tournament tournament, int round_for_pairing);


    /**
     * upload rankings to firbase.
     *
     * @param  tournament
     */
    void uploadRankingsForTournament(Tournament tournament);
}
