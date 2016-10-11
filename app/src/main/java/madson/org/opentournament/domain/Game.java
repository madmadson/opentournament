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
    private String parent_UUID;
    private String tournamentId;
    private int tournament_round;

    private int playing_field;

    private String participantOneUUID;
    private TournamentParticipant participantOne;
    private int participant_one_score;
    private int participant_one_control_points;
    private int participant_one_victory_points;

    private String participantTwoUUID;
    private TournamentParticipant participantTwo;
    private int participant_two_score;
    private int participant_two_control_points;
    private int participant_two_victory_points;

    private boolean finished;
    private String scenario;

    protected Game(Parcel in) {

        _id = in.readLong();
        UUID = in.readString();
        parent_UUID = in.readString();
        tournamentId = in.readString();
        tournament_round = in.readInt();

        participantOneUUID = in.readString();
        participant_one_score = in.readInt();
        participant_one_control_points = in.readInt();
        participant_one_victory_points = in.readInt();

        participantTwoUUID = in.readString();
        participant_two_score = in.readInt();
        participant_two_control_points = in.readInt();
        participant_two_victory_points = in.readInt();

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


    public String getUUID() {

        return UUID;
    }


    public int getTournament_round() {

        return tournament_round;
    }


    public int getParticipant_one_score() {

        return participant_one_score;
    }


    public int getParticipant_one_control_points() {

        return participant_one_control_points;
    }


    public int getParticipant_one_victory_points() {

        return participant_one_victory_points;
    }


    public int getParticipant_two_score() {

        return participant_two_score;
    }


    public int getParticipant_two_control_points() {

        return participant_two_control_points;
    }


    public int getParticipant_two_victory_points() {

        return participant_two_victory_points;
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


    public void setParticipant_one_score(int participant_one_score) {

        this.participant_one_score = participant_one_score;
    }


    public void setParticipant_one_control_points(int participant_one_control_points) {

        this.participant_one_control_points = participant_one_control_points;
    }


    public void setParticipant_one_victory_points(int participant_one_victory_points) {

        this.participant_one_victory_points = participant_one_victory_points;
    }


    public void setParticipant_two_score(int participant_two_score) {

        this.participant_two_score = participant_two_score;
    }


    public void setParticipant_two_control_points(int participant_two_control_points) {

        this.participant_two_control_points = participant_two_control_points;
    }


    public void setParticipant_two_victory_points(int participant_two_victory_points) {

        this.participant_two_victory_points = participant_two_victory_points;
    }


    public void setFinished(boolean finished) {

        this.finished = finished;
    }


    public void setScenario(String scenario) {

        this.scenario = scenario;
    }


    public String getParticipantOneUUID() {

        return participantOneUUID;
    }


    public void setParticipantOneUUID(String participantOneUUID) {

        this.participantOneUUID = participantOneUUID;
    }


    public String getParticipantTwoUUID() {

        return participantTwoUUID;
    }


    public void setParticipantTwoUUID(String participantTwoUUID) {

        this.participantTwoUUID = participantTwoUUID;
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
        game.setUUID(cursor.getString(1));
        game.setParent_UUID(cursor.getString(2));

        game.setTournamentId(cursor.getString(3));
        game.setTournament_round(cursor.getInt(4));

        game.setParticipantOneUUID(cursor.getString(5));

        game.setParticipant_one_score(cursor.getInt(6));
        game.setParticipant_one_control_points(cursor.getInt(7));
        game.setParticipant_one_victory_points(cursor.getInt(8));

        game.setParticipantTwoUUID(cursor.getString(9));

        game.setParticipant_two_score(cursor.getInt(10));
        game.setParticipant_two_control_points(cursor.getInt(11));
        game.setParticipant_two_victory_points(cursor.getInt(12));

        game.setFinished(cursor.getInt(13) == 1);

        game.setScenario(cursor.getString(14));

        game.setPlaying_field(cursor.getInt(13));

        return game;
    }


    public TournamentParticipant getParticipantOne() {

        return participantOne;
    }


    public void setParticipantOne(TournamentParticipant participantOne) {

        this.participantOne = participantOne;
    }


    public TournamentParticipant getParticipantTwo() {

        return participantTwo;
    }


    public void setParticipantTwo(TournamentParticipant participantTwo) {

        this.participantTwo = participantTwo;
    }


    @Override
    public int describeContents() {

        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(_id);
        dest.writeString(UUID);
        dest.writeString(parent_UUID);
        dest.writeString(tournamentId);
        dest.writeInt(tournament_round);

        dest.writeString(participantOneUUID);
        dest.writeInt(participant_one_score);
        dest.writeInt(participant_one_control_points);
        dest.writeInt(participant_one_victory_points);

        dest.writeString(participantTwoUUID);
        dest.writeInt(participant_two_score);
        dest.writeInt(participant_two_control_points);
        dest.writeInt(participant_two_victory_points);

        dest.writeByte((byte) (finished ? 1 : 0));
        dest.writeString(scenario);
        dest.writeInt(playing_field);
    }


    public String getParent_UUID() {

        return parent_UUID;
    }


    public void setParent_UUID(String parent_UUID) {

        this.parent_UUID = parent_UUID;
    }
}
