package madson.org.opentournament.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Mapping Class for tournament and mapping. Is own instance for whole tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayer {

    private long _id;

    private long tournament_id;
    private long player_id;
    private int round;

    private int score;
    private int sos;
    private int control_points;
    private int victory_points;

    private SportOrGameScoreData scoringData;

    // used for shortcut not always go for names
    private String firstname;
    private String nickname;
    private String lastname;

    private String player_onlineUUID;

    private List<Long> listOfOpponents;

    public TournamentPlayer() {

        listOfOpponents = new ArrayList<>();
    }


    public TournamentPlayer(long tournament_id, long player_id, int round) {

        this.tournament_id = tournament_id;
        this.player_id = player_id;
        this.round = round;

        listOfOpponents = new ArrayList<>();
    }


    /**
     * constructor to enable player to tournament mapping.
     *
     * @param  player
     * @param  tournament
     */
    public TournamentPlayer(Player player, Tournament tournament) {

        this.player_id = player.get_id();
        this.player_onlineUUID = player.getOnlineUUID();
        this.firstname = player.getFirstname();
        this.lastname = player.getLastname();
        this.nickname = player.getNickname();

        listOfOpponents = new ArrayList<>();
    }

    public long get_id() {

        return _id;
    }


    public void setPlayer_onlineUUID(String player_onlineUUID) {

        this.player_onlineUUID = player_onlineUUID;
    }


    public SportOrGameScoreData getScoringData() {

        return scoringData;
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


    public List<Long> getListOfOpponents() {

        return listOfOpponents;
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

        return "TournamentPlayer{"
            + "_id=" + _id
            + ", tournament_id=" + tournament_id
            + ", player_id=" + player_id
            + ", round=" + round
            + ", score=" + score
            + ", sos=" + sos
            + ", control_points=" + control_points
            + ", victory_points=" + victory_points
            + ", scoringData=" + scoringData
            + ", firstname='" + firstname + '\''
            + ", nickname='" + nickname + '\''
            + ", lastname='" + lastname + '\''
            + ", player_onlineUUID='" + player_onlineUUID + '\''
            + ", listOfOpponents=" + listOfOpponents + '}';
    }
}
