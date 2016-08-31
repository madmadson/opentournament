package madson.org.opentournament.domain;

/**
 * Metadata for game or sport.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public abstract class SportOrGameScoreData {

    /**
     */
    private long tournament_player_id;
    private String tournament_player_online_uuid;

    /**
     * Main score - amount of points in th.
     */
    private int score;

    /**
     */
    private int round;
}
