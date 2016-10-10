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
    private String UUID;
    private String tournamentId;
    private int tournament_round;

    private int playing_field;

    private String playerOneUUID;
    private TournamentPlayer player1;
    private int player_one_score;
    private int player_one_control_points;
    private int player_one_victory_points;

    private String playerTwoUUID;
    private TournamentPlayer player2;
    private int player_two_score;
    private int player_two_control_points;
    private int player_two_victory_points;

    private boolean finished;
    private String scenario;

    protected Game(Parcel in) {

        _id = in.readLong();
        UUID = in.readString();
        tournamentId = in.readString();
        tournament_round = in.readInt();

        player1 = in.readParcelable(TournamentPlayer.class.getClassLoader());
        player_one_score = in.readInt();
        player_one_control_points = in.readInt();
        player_one_victory_points = in.readInt();
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

    @Exclude
    public long get_id() {

        return _id;
    }


    @Exclude
    public String getTournamentId() {

        return tournamentId;
    }


    public int getTournament_round() {

        return tournament_round;
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


    public void setUUID(String UUID) {

        this.UUID = UUID;
    }


    public void setTournamentId(String tournamentId) {

        this.tournamentId = tournamentId;
    }


    public void setTournament_round(int tournament_round) {

        this.tournament_round = tournament_round;
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


    public String getPlayerOneUUID() {

        return playerOneUUID;
    }


    public void setPlayerOneUUID(String playerOneUUID) {

        this.playerOneUUID = playerOneUUID;
    }


    public String getPlayerTwoUUID() {

        return playerTwoUUID;
    }


    public void setPlayerTwoUUID(String playerTwoUUID) {

        this.playerTwoUUID = playerTwoUUID;
    }


    public int getPlaying_field() {

        return playing_field;
    }


    public void setPlaying_field(int playing_field) {

        this.playing_field = playing_field;
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

        return UUID != null ? UUID.equals(game.UUID) : game.UUID == null;
    }


    @Override
    public int hashCode() {

        int result = (int) (_id ^ (_id >>> 32));
        result = 31 * result + (UUID != null ? UUID.hashCode() : 0);

        return result;
    }


    public static Game cursorToGame(Cursor cursor) {

        Game game = new Game();

        game.set_id(cursor.getInt(0));
        game.setTournamentId(cursor.getString(1));
        game.setTournament_round(cursor.getInt(2));

        game.setPlayerOneUUID(cursor.getString(3));

        game.setPlayer_one_score(cursor.getInt(4));
        game.setPlayer_one_control_points(cursor.getInt(5));
        game.setPlayer_one_victory_points(cursor.getInt(6));

        game.setPlayerTwoUUID(cursor.getString(7));

        game.setPlayer_two_score(cursor.getInt(8));
        game.setPlayer_two_control_points(cursor.getInt(9));
        game.setPlayer_two_victory_points(cursor.getInt(10));

        game.setFinished(cursor.getInt(11) == 1);

        game.setScenario(cursor.getString(12));

        game.setPlaying_field(cursor.getInt(13));

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
        dest.writeString(UUID);
        dest.writeString(tournamentId);
        dest.writeInt(tournament_round);
        dest.writeString(playerOneUUID);
        dest.writeParcelable(player1, flags);
        dest.writeInt(player_one_score);
        dest.writeInt(player_one_control_points);
        dest.writeInt(player_one_victory_points);
        dest.writeString(playerTwoUUID);
        dest.writeParcelable(player2, flags);
        dest.writeInt(player_two_score);
        dest.writeInt(player_two_control_points);
        dest.writeInt(player_two_victory_points);
        dest.writeByte((byte) (finished ? 1 : 0));
        dest.writeString(scenario);
        dest.writeInt(playing_field);
    }
}
