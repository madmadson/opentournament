package madson.org.opentournament.domain.warmachine;

import android.os.Parcelable;


/**
 * Represent one pairing for one round in tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachineTournamentGame {

    private long _id;
    private long tournament_id;
    private int round;

    private long player_one_id;
    private String player_one_full_name;
    private int player_one_score;
    private int player_one_control_points;
    private int player_one_victory_points;

    private long player_two_id;
    private String player_two_full_name;
    private int player_two_score;
    private int player_two_control_points;
    private int player_two_victory_points;

    private boolean finished;

    public WarmachineTournamentGame(long _id) {

        this._id = _id;
    }


    /**
     * for creation.
     */
    public WarmachineTournamentGame() {
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


    public String getPlayer_one_full_name() {

        return player_one_full_name;
    }


    public void setPlayer_one_full_name(String player_one_full_name) {

        this.player_one_full_name = player_one_full_name;
    }


    public String getPlayer_two_full_name() {

        return player_two_full_name;
    }


    public void setPlayer_two_full_name(String player_two_full_name) {

        this.player_two_full_name = player_two_full_name;
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


    public long getTournament_id() {

        return tournament_id;
    }


    public long getPlayer_one_id() {

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


    public long getPlayer_two_id() {

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


    public boolean isFinished() {

        return finished;
    }


    public void setFinished(boolean finished) {

        this.finished = finished;
    }


    @Override
    public String toString() {

        return "WarmachineTournamentGame{"
            + "_id=" + _id
            + ", tournament_id=" + tournament_id
            + ", round=" + round
            + ", player_one_id=" + player_one_id
            + ", player_one_full_name='" + player_one_full_name + '\''
            + ", player_one_score=" + player_one_score
            + ", player_one_control_points=" + player_one_control_points
            + ", player_one_victory_points=" + player_one_victory_points
            + ", player_two_id=" + player_two_id
            + ", player_two_full_name='" + player_two_full_name + '\''
            + ", player_two_score=" + player_two_score
            + ", player_two_control_points=" + player_two_control_points
            + ", player_two_victory_points=" + player_two_victory_points
            + ", finished=" + finished + '}';
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        WarmachineTournamentGame that = (WarmachineTournamentGame) o;

        if (_id != that._id)
            return false;

        if (tournament_id != that.tournament_id)
            return false;

        return round == that.round;
    }


    @Override
    public int hashCode() {

        int result = (int) (_id ^ (_id >>> 32));
        result = 31 * result + (int) (tournament_id ^ (tournament_id >>> 32));
        result = 31 * result + round;

        return result;
    }
}
