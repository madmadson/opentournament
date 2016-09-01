package madson.org.opentournament.domain.warmachine;

import android.database.Cursor;


/**
 * Represent one game (for one round in tournament).
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Game {

    private long _id;
    private String online_uuid;
    private long tournament_id;
    private String tournament_online_uuid;
    private int tournament_round;

    private long player_one_id;
    private String player_one_online_uuid;
    private String player_one_full_name;
    private int player_one_score;
    private int player_one_control_points;
    private int player_one_victory_points;

    private long player_two_id;
    private String player_two_online_uuid;
    private String player_two_full_name;
    private int player_two_score;
    private int player_two_control_points;
    private int player_two_victory_points;

    private boolean finished;
    private String scenario;

    public long get_id() {

        return _id;
    }


    public String getOnline_uuid() {

        return online_uuid;
    }


    public long getTournament_id() {

        return tournament_id;
    }


    public String getTournament_online_uuid() {

        return tournament_online_uuid;
    }


    public int getTournament_round() {

        return tournament_round;
    }


    public long getPlayer_one_id() {

        return player_one_id;
    }


    public String getPlayer_one_online_uuid() {

        return player_one_online_uuid;
    }


    public String getPlayer_one_full_name() {

        return player_one_full_name;
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


    public String getPlayer_two_online_uuid() {

        return player_two_online_uuid;
    }


    public String getPlayer_two_full_name() {

        return player_two_full_name;
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


    public boolean isFinished() {

        return finished;
    }


    public String getScenario() {

        return scenario;
    }


    public void set_id(long _id) {

        this._id = _id;
    }


    public void setOnline_uuid(String online_uuid) {

        this.online_uuid = online_uuid;
    }


    public void setTournament_id(long tournament_id) {

        this.tournament_id = tournament_id;
    }


    public void setTournament_online_uuid(String tournament_online_uuid) {

        this.tournament_online_uuid = tournament_online_uuid;
    }


    public void setTournament_round(int tournament_round) {

        this.tournament_round = tournament_round;
    }


    public void setPlayer_one_id(long player_one_id) {

        this.player_one_id = player_one_id;
    }


    public void setPlayer_one_online_uuid(String player_one_online_uuid) {

        this.player_one_online_uuid = player_one_online_uuid;
    }


    public void setPlayer_one_full_name(String player_one_full_name) {

        this.player_one_full_name = player_one_full_name;
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


    public void setPlayer_two_id(long player_two_id) {

        this.player_two_id = player_two_id;
    }


    public void setPlayer_two_online_uuid(String player_two_online_uuid) {

        this.player_two_online_uuid = player_two_online_uuid;
    }


    public void setPlayer_two_full_name(String player_two_full_name) {

        this.player_two_full_name = player_two_full_name;
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


    public void setFinished(boolean finished) {

        this.finished = finished;
    }


    public void setScenario(String scenario) {

        this.scenario = scenario;
    }


    @Override
    public String toString() {

        return "Game{"
            + "_id=" + _id
            + ", online_uuid='" + online_uuid + '\''
            + ", tournament_id=" + tournament_id
            + ", tournament_online_uuid='" + tournament_online_uuid + '\''
            + ", tournament_round=" + tournament_round
            + ", player_one_id=" + player_one_id
            + ", player_one_online_uuid='" + player_one_online_uuid + '\''
            + ", player_one_full_name='" + player_one_full_name + '\''
            + ", player_one_score=" + player_one_score
            + ", player_one_control_points=" + player_one_control_points
            + ", player_one_victory_points=" + player_one_victory_points
            + ", player_two_id=" + player_two_id
            + ", player_two_online_uuid='" + player_two_online_uuid + '\''
            + ", player_two_full_name='" + player_two_full_name + '\''
            + ", player_two_score=" + player_two_score
            + ", player_two_control_points=" + player_two_control_points
            + ", player_two_victory_points=" + player_two_victory_points
            + ", finished=" + finished
            + ", scenario='" + scenario + '\'' + '}';
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Game game = (Game) o;

        if (_id != game._id)
            return false;

        return online_uuid != null ? online_uuid.equals(game.online_uuid) : game.online_uuid == null;
    }


    @Override
    public int hashCode() {

        int result = (int) (_id ^ (_id >>> 32));
        result = 31 * result + (online_uuid != null ? online_uuid.hashCode() : 0);

        return result;
    }


    public static Game cursorToGame(Cursor cursor) {

        Game game = new Game();

        game.set_id(cursor.getInt(0));
        game.setOnline_uuid(cursor.getString(1));
        game.setTournament_id(cursor.getInt(2));
        game.setTournament_online_uuid(cursor.getString(3));
        game.setTournament_round(cursor.getInt(4));

        game.setPlayer_one_id(cursor.getInt(5));
        game.setPlayer_one_online_uuid(cursor.getString(6));
        game.setPlayer_one_full_name(cursor.getString(7));
        game.setPlayer_one_score(cursor.getInt(8));
        game.setPlayer_one_control_points(cursor.getInt(9));
        game.setPlayer_one_victory_points(cursor.getInt(10));

        game.setPlayer_two_id(cursor.getInt(11));
        game.setPlayer_two_online_uuid(cursor.getString(12));
        game.setPlayer_two_full_name(cursor.getString(13));
        game.setPlayer_two_score(cursor.getInt(14));
        game.setPlayer_two_control_points(cursor.getInt(15));
        game.setPlayer_two_victory_points(cursor.getInt(16));

        game.setFinished(cursor.getInt(15) == 1);

        game.setScenario(cursor.getString(16));

        return game;
    }
}