package madson.org.opentournament.domain;

import android.database.Cursor;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;


/**
 * Represent one game (for one round in tournament).
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Game implements Parcelable {

    public static final Creator<Game> CREATOR = new Creator<Game>() {

        @Override
        public Game createFromParcel(Parcel in) {

            return new Game(in);
        }


        @Override
        public Game[] newArray(int size) {

            return new Game[size];
        }
    };

    private long _id;
    private String online_uuid;
    private long tournament_id;
    private int tournament_round;

    private int playing_field;

    private String playerOneId;
    private String playerOneOnlineUUID;
    private TournamentPlayer player1;
    private int player_one_score;
    private int player_one_control_points;
    private int player_one_victory_points;

    private String playerTwoId;
    private String playerTwoOnlineUUID;
    private TournamentPlayer player2;
    private int player_two_score;
    private int player_two_control_points;
    private int player_two_victory_points;

    private boolean finished;
    private String scenario;

    protected Game(Parcel in) {

        _id = in.readLong();
        online_uuid = in.readString();
        tournament_id = in.readLong();
        tournament_round = in.readInt();
        playerOneId = in.readString();
        playerOneOnlineUUID = in.readString();
        player1 = in.readParcelable(TournamentPlayer.class.getClassLoader());
        player_one_score = in.readInt();
        player_one_control_points = in.readInt();
        player_one_victory_points = in.readInt();
        playerTwoId = in.readString();
        playerTwoOnlineUUID = in.readString();
        player2 = in.readParcelable(TournamentPlayer.class.getClassLoader());
        player_two_score = in.readInt();
        player_two_control_points = in.readInt();
        player_two_victory_points = in.readInt();
        finished = in.readByte() != 0;
        scenario = in.readString();
        playing_field = in.readInt();
    }


    public Game() {
    }

    public String getPlayerTwoId() {

        return playerTwoId;
    }


    @Exclude
    public long get_id() {

        return _id;
    }


    public String getOnline_uuid() {

        return online_uuid;
    }


    @Exclude
    public long getTournament_id() {

        return tournament_id;
    }


    public int getTournament_round() {

        return tournament_round;
    }


    @Exclude
    public String getPlayerOneId() {

        return playerOneId;
    }


    public String getPlayerOneOnlineUUID() {

        return playerOneOnlineUUID;
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


    public String getPlayerTwoOnlineUUID() {

        return playerTwoOnlineUUID;
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


    public void setTournament_round(int tournament_round) {

        this.tournament_round = tournament_round;
    }


    public void setPlayerOneId(String playerOneId) {

        this.playerOneId = playerOneId;
    }


    public void setPlayerOneOnlineUUID(String playerOneOnlineUUID) {

        this.playerOneOnlineUUID = playerOneOnlineUUID;
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


    public void setPlayerTwoId(String playerTwoId) {

        this.playerTwoId = playerTwoId;
    }


    public void setPlayerTwoOnlineUUID(String playerTwoOnlineUUID) {

        this.playerTwoOnlineUUID = playerTwoOnlineUUID;
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
        game.setTournament_round(cursor.getInt(3));

        game.setPlayerOneId(cursor.getString(4));
        game.setPlayerOneOnlineUUID(cursor.getString(5));

        game.setPlayer_one_score(cursor.getInt(6));
        game.setPlayer_one_control_points(cursor.getInt(7));
        game.setPlayer_one_victory_points(cursor.getInt(8));

        game.setPlayerTwoId(cursor.getString(9));
        game.setPlayerTwoOnlineUUID(cursor.getString(10));

        game.setPlayer_two_score(cursor.getInt(11));
        game.setPlayer_two_control_points(cursor.getInt(12));
        game.setPlayer_two_victory_points(cursor.getInt(13));

        game.setFinished(cursor.getInt(14) == 1);

        game.setScenario(cursor.getString(15));

        game.setPlaying_field(cursor.getInt(16));

        return game;
    }


    public TournamentPlayer getPlayer1() {

        return player1;
    }


    public void setPlayer1(TournamentPlayer player1) {

        this.player1 = player1;
    }


    public TournamentPlayer getPlayer2() {

        return player2;
    }


    public void setPlayer2(TournamentPlayer player2) {

        this.player2 = player2;
    }


    @Override
    public int describeContents() {

        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(_id);
        dest.writeString(online_uuid);
        dest.writeLong(tournament_id);
        dest.writeInt(tournament_round);
        dest.writeString(playerOneId);
        dest.writeString(playerOneOnlineUUID);
        dest.writeParcelable(player1, flags);
        dest.writeInt(player_one_score);
        dest.writeInt(player_one_control_points);
        dest.writeInt(player_one_victory_points);
        dest.writeString(playerTwoId);
        dest.writeString(playerTwoOnlineUUID);
        dest.writeParcelable(player2, flags);
        dest.writeInt(player_two_score);
        dest.writeInt(player_two_control_points);
        dest.writeInt(player_two_victory_points);
        dest.writeByte((byte) (finished ? 1 : 0));
        dest.writeString(scenario);
        dest.writeInt(playing_field);
    }


    @Override
    public String toString() {

        return "Game{"
            + "_id=" + _id
            + ", online_uuid='" + online_uuid + '\''
            + ", tournament_id=" + tournament_id
            + ", tournament_round=" + tournament_round
            + ", playing_field=" + playing_field
            + ", playerOneId=" + playerOneId
            + ", playerOneOnlineUUID='" + playerOneOnlineUUID + '\''
            + ", player1=" + player1
            + ", player_one_score=" + player_one_score
            + ", player_one_control_points=" + player_one_control_points
            + ", player_one_victory_points=" + player_one_victory_points
            + ", playerTwoId=" + playerTwoId
            + ", playerTwoOnlineUUID='" + playerTwoOnlineUUID + '\''
            + ", player2=" + player2
            + ", player_two_score=" + player_two_score
            + ", player_two_control_points=" + player_two_control_points
            + ", player_two_victory_points=" + player_two_victory_points
            + ", finished=" + finished
            + ", scenario='" + scenario + '\'' + '}';
    }


    @Exclude
    public String getRealPlayerOneId() {

        if (playerOneOnlineUUID != null) {
            return playerOneOnlineUUID;
        } else {
            return String.valueOf(playerOneId);
        }
    }


    @Exclude
    public String getRealPlayerTwoId() {

        if (playerTwoOnlineUUID != null) {
            return playerTwoOnlineUUID;
        } else {
            return String.valueOf(playerTwoId);
        }
    }


    public int getPlaying_field() {

        return playing_field;
    }


    public void setPlaying_field(int playing_field) {

        this.playing_field = playing_field;
    }
}
