package madson.org.opentournament.domain.warmachine;

/**
 * Represent one pairing for one round in tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachineTournamentPairing {

    public static final String[] ALL_COLS_FOR_TOURNAMENT_PAIRING = {
        "_id", "tournament_id", "round", "player_one_id", "player_one_score", "player_one_control_points",
        "player_one_victory_points", "player_two_id", "player_two_score", "player_two_control_points",
        "player_two_victory_points"
    };

    private long _id;
    private int tournament_id;
    private int round;

    private int player_one_id;
    private int player_one_score;
    private int player_one_control_points;
    private int player_one_victory_points;

    private int player_two_id;
    private int player_two_score;
    private int player_two_control_points;
    private int player_two_victory_points;

    public WarmachineTournamentPairing(long _id, int tournament_id, int round, int player_one_id, int player_one_score,
        int player_one_control_points, int player_one_victory_points, int player_two_id, int player_two_score,
        int player_two_control_points, int player_two_victory_points) {

        this._id = _id;
        this.tournament_id = tournament_id;
        this.round = round;
        this.player_one_id = player_one_id;
        this.player_one_score = player_one_score;
        this.player_one_control_points = player_one_control_points;
        this.player_one_victory_points = player_one_victory_points;
        this.player_two_id = player_two_id;
        this.player_two_score = player_two_score;
        this.player_two_control_points = player_two_control_points;
        this.player_two_victory_points = player_two_victory_points;
    }


    public WarmachineTournamentPairing() {
    }

    public void setTournament_id(int tournament_id) {

        this.tournament_id = tournament_id;
    }


    public void setRound(int round) {

        this.round = round;
    }


    public void setPlayer_one_id(int player_one_id) {

        this.player_one_id = player_one_id;
    }


    public void setPlayer_one_score(int player_one_score) {

        this.player_one_score = player_one_score;
    }


    public void setPlayer_one_control_points(int player_one_control_points) {

        this.player_one_control_points = player_one_control_points;
    }


    public void setPlayer_one_victory_points(int player_one_victory_points) {

        this.player_one_victory_points = player_one_victory_points;
    }


    public void setPlayer_two_id(int player_two_id) {

        this.player_two_id = player_two_id;
    }


    public void setPlayer_two_score(int player_two_score) {

        this.player_two_score = player_two_score;
    }


    public void setPlayer_two_control_points(int player_two_control_points) {

        this.player_two_control_points = player_two_control_points;
    }


    public void setPlayer_two_victory_points(int player_two_victory_points) {

        this.player_two_victory_points = player_two_victory_points;
    }


    public long get_id() {

        return _id;
    }


    public int getTournament_id() {

        return tournament_id;
    }


    public int getPlayer_one_id() {

        return player_one_id;
    }


    public int getPlayer_one_score() {

        return player_one_score;
    }


    public int getPlayer_one_control_points() {

        return player_one_control_points;
    }


    public int getPlayer_one_victory_points() {

        return player_one_victory_points;
    }


    public int getPlayer_two_id() {

        return player_two_id;
    }


    public int getPlayer_two_score() {

        return player_two_score;
    }


    public int getPlayer_two_control_points() {

        return player_two_control_points;
    }


    public int getPlayer_two_victory_points() {

        return player_two_victory_points;
    }


    public int getRound() {

        return round;
    }


    @Override
    public String toString() {

        return "WarmachineTournamentPairing{"
            + "tournament_id=" + tournament_id + ""
            + ", round: " + round
            + ", Player1 (" + player_one_id + ") "
            + "vs Player2 (" + player_two_id + "')}'";
    }
}
