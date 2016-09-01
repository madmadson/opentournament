package madson.org.opentournament.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;


/**
 * Represent ranking after specific round of tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentRanking {

    private long _id;
    private String online_uuid;

    private long tournament_id;
    private String tournament_online_uuid;
    private long player_id;
    private String player_online_uuid;
    private int round;

    private int score;
    private int sos;
    private int control_points;
    private int victory_points;

    // used for shortcut not always go for names
    private String firstname;
    private String nickname;
    private String lastname;

    private String player_onlineUUID;

    // online needed for sos calculation
    private List<Long> listOfOpponentsPlayerIds = new ArrayList<>();

    public TournamentRanking(long tournament_id, long player_id, int round) {

        this.tournament_id = tournament_id;
        this.player_id = player_id;
        this.round = round;
    }


    /**
     * constructor to enable player to tournament mapping.
     *
     * @param  player
     * @param  tournament
     */
    public TournamentRanking(Player player, Tournament tournament) {

        this.player_id = player.get_id();
        this.player_onlineUUID = player.getOnlineUUID();
        this.firstname = player.getFirstname();
        this.lastname = player.getLastname();
        this.nickname = player.getNickname();
    }


    public TournamentRanking() {
    }

    public String getOnline_uuid() {

        return online_uuid;
    }


    public void setOnline_uuid(String online_uuid) {

        this.online_uuid = online_uuid;
    }


    public String getTournament_online_uuid() {

        return tournament_online_uuid;
    }


    public void setTournament_online_uuid(String tournament_online_uuid) {

        this.tournament_online_uuid = tournament_online_uuid;
    }


    public String getPlayer_online_uuid() {

        return player_online_uuid;
    }


    public void setPlayer_online_uuid(String player_online_uuid) {

        this.player_online_uuid = player_online_uuid;
    }


    public long get_id() {

        return _id;
    }


    public void set_id(long _id) {

        this._id = _id;
    }


    public void setPlayer_onlineUUID(String player_onlineUUID) {

        this.player_onlineUUID = player_onlineUUID;
    }


    public String getPlayer_onlineUUID() {

        return player_onlineUUID;
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


    public int getRound() {

        return round;
    }


    public void setRound(int round) {

        this.round = round;
    }


    public long getTournament_id() {

        return tournament_id;
    }


    public void setTournament_id(long tournament_id) {

        this.tournament_id = tournament_id;
    }


    public String getFirstname() {

        return firstname;
    }


    public void setFirstname(String firstname) {

        this.firstname = firstname;
    }


    public String getNickname() {

        return nickname;
    }


    public void setNickname(String nickname) {

        this.nickname = nickname;
    }


    public String getLastname() {

        return lastname;
    }


    public void setLastname(String lastname) {

        this.lastname = lastname;
    }


    @Override
    public String toString() {

        return "TournamentRanking{"
            + "_id=" + _id
            + ", online_uuid='" + online_uuid + '\''
            + ", tournament_id=" + tournament_id
            + ", tournament_online_uuid='" + tournament_online_uuid + '\''
            + ", player_id=" + player_id
            + ", player_online_uuid='" + player_online_uuid + '\''
            + ", round=" + round
            + ", score=" + score
            + ", sos=" + sos
            + ", control_points=" + control_points
            + ", victory_points=" + victory_points
            + ", firstname='" + firstname + '\''
            + ", nickname='" + nickname + '\''
            + ", lastname='" + lastname + '\''
            + ", player_onlineUUID='" + player_onlineUUID + '\'' + '}';
    }


    public List<Long> getListOfOpponentsPlayerIds() {

        return listOfOpponentsPlayerIds;
    }
}
