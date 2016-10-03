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
    private String online_uuid;

    private long tournament_id;
    private int tournament_round;

    private String playerId;
    private String playerOnlineUUID;

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
    public String getPlayerOnlineUUID() {

        return playerOnlineUUID;
    }


    @Exclude
    public String getRealPlayerId() {

        if (playerOnlineUUID != null) {
            return playerOnlineUUID;
        } else {
            return String.valueOf(playerId);
        }
    }


    public void setPlayerOnlineUUID(String playerOnlineUUID) {

        this.playerOnlineUUID = playerOnlineUUID;
    }


    public TournamentPlayer getTournamentPlayer() {

        return tournamentPlayer;
    }


    public void setTournamentPlayer(TournamentPlayer tournamentPlayer) {

        this.tournamentPlayer = tournamentPlayer;
    }


    @Exclude
    public String getOnline_uuid() {

        return online_uuid;
    }


    public void setOnline_uuid(String online_uuid) {

        this.online_uuid = online_uuid;
    }


    @Exclude
    public long get_id() {

        return _id;
    }


    public void set_id(long _id) {

        this._id = _id;
    }


    @Exclude
    public String getPlayerId() {

        return playerId;
    }


    public void setPlayerId(String playerId) {

        this.playerId = playerId;
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
            + ", playerId=" + playerId
            + ", playerOnlineUUID='" + playerOnlineUUID + '\''
            + ", score=" + score
            + ", sos=" + sos
            + ", control_points=" + control_points
            + ", victory_points=" + victory_points
            + ", listOfOpponentsPlayerIds=" + listOfOpponentsPlayerIds + '}';
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
}
