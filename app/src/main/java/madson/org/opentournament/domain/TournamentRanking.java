package madson.org.opentournament.domain;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;


/**
 * Represent ranking after specific tournament_round of tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentRanking {

    private long _id;

    private String tournamentId;
    private int tournament_round;

    private String playerUUID;

    private TournamentPlayer tournamentPlayer;

    private int score;
    private int sos;
    private int control_points;
    private int victory_points;

    private List<String> listOfOpponentsPlayerIds = new ArrayList<>();

    private int rank;

    public TournamentRanking() {
    }

    @Exclude
    public String getPlayerUUID() {

        return playerUUID;
    }


    public void setPlayerUUID(String playerUUID) {

        this.playerUUID = playerUUID;
    }


    public TournamentPlayer getTournamentPlayer() {

        return tournamentPlayer;
    }


    public void setTournamentPlayer(TournamentPlayer tournamentPlayer) {

        this.tournamentPlayer = tournamentPlayer;
    }


    @Exclude
    public long get_id() {

        return _id;
    }


    public void set_id(long _id) {

        this._id = _id;
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


    @Exclude
    public int getTournament_round() {

        return tournament_round;
    }


    public void setTournament_round(int tournament_round) {

        this.tournament_round = tournament_round;
    }


    public String getTournament_id() {

        return tournamentId;
    }


    public void setTournament_id(String tournamentId) {

        this.tournamentId = tournamentId;
    }


    public List<String> getListOfOpponentsPlayerIds() {

        return listOfOpponentsPlayerIds;
    }


    public int getRank() {

        return rank;
    }


    public void setRank(int rank) {

        this.rank = rank;
    }


    @Override
    public String toString() {

        return "TournamentRanking{"
            + "_id=" + _id
            + ", tournamentId='" + tournamentId + '\''
            + ", tournament_round=" + tournament_round
            + ", playerUUID='" + playerUUID + '\''
            + ", tournamentPlayer=" + tournamentPlayer
            + ", score=" + score
            + ", sos=" + sos
            + ", control_points=" + control_points
            + ", victory_points=" + victory_points
            + ", listOfOpponentsPlayerIds=" + listOfOpponentsPlayerIds
            + ", rank=" + rank + '}';
    }
}
