package madson.org.opentournament.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * Represent ranking after specific tournament_round of tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentRanking {

    private long _id;
    private String online_uuid;

    private long tournament_id;
    private int tournament_round;

    private long player_id;
    private String player_online_uuid;

    private TournamentPlayer tournamentPlayer;

    private int score;
    private int sos;
    private int control_points;
    private int victory_points;

    // online needed for sos calculation
    private List<Long> listOfOpponentsPlayerIds = new ArrayList<>();

    public TournamentRanking(long tournament_id, long player_id, int round) {

        this.tournament_id = tournament_id;
        this.player_id = player_id;
        this.tournament_round = round;
    }


    public TournamentRanking() {
    }

    public String getPlayer_online_uuid() {

        return player_online_uuid;
    }


    public void setPlayer_online_uuid(String player_online_uuid) {

        this.player_online_uuid = player_online_uuid;
    }


    public TournamentPlayer getTournamentPlayer() {

        return tournamentPlayer;
    }


    public void setTournamentPlayer(TournamentPlayer tournamentPlayer) {

        this.tournamentPlayer = tournamentPlayer;
    }


    public String getOnline_uuid() {

        return online_uuid;
    }


    public void setOnline_uuid(String online_uuid) {

        this.online_uuid = online_uuid;
    }


    public long get_id() {

        return _id;
    }


    public void set_id(long _id) {

        this._id = _id;
    }


    public long getPlayer_id() {

        return player_id;
    }


    public void setPlayer_id(long player_id) {

        this.player_id = player_id;
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


    public int getTournament_round() {

        return tournament_round;
    }


    public void setTournament_round(int tournament_round) {

        this.tournament_round = tournament_round;
    }


    public long getTournament_id() {

        return tournament_id;
    }


    public void setTournament_id(long tournament_id) {

        this.tournament_id = tournament_id;
    }


    @Override
    public String toString() {

        return "TournamentRanking{"
            + "_id=" + _id
            + ", online_uuid='" + online_uuid + '\''
            + ", tournament_id=" + tournament_id
            + ", tournament_round=" + tournament_round
            + ", player_id=" + player_id
            + ", player_online_uuid='" + player_online_uuid + '\''
            + ", score=" + score
            + ", sos=" + sos
            + ", control_points=" + control_points
            + ", victory_points=" + victory_points
            + ", listOfOpponentsPlayerIds=" + listOfOpponentsPlayerIds + '}';
    }


    public List<Long> getListOfOpponentsPlayerIds() {

        return listOfOpponentsPlayerIds;
    }
}
