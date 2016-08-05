package madson.org.opentournament.domain.warmachine;

import madson.org.opentournament.db.PlayerTable;
import madson.org.opentournament.domain.Player;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachineTournamentPlayer extends Player {

    private int tournament_id;

    private int score;
    private int sos;
    private int control_points;
    private int victory_points;

    public WarmachineTournamentPlayer(Player player) {

        super(player.getId(), player.getFirstname(), player.getNickname(), player.getLastname());
    }

    public int getScore() {

        return score;
    }


    public int getControl_points() {

        return control_points;
    }


    public int getVictory_points() {

        return victory_points;
    }


    public void setScore(int score) {

        this.score = score;
    }


    public void setControl_points(int control_points) {

        this.control_points = control_points;
    }


    public void setVictory_points(int victory_points) {

        this.victory_points = victory_points;
    }


    public int getSos() {

        return sos;
    }


    public void setSos(int sos) {

        this.sos = sos;
    }


    @Override
    public String toString() {

        return "WarmachineTournamentPlayer{"
            + "tournament_id=" + tournament_id
            + ", score=" + score
            + ", sos=" + sos
            + ", control_points=" + control_points
            + ", victory_points=" + victory_points + '}';
    }
}
