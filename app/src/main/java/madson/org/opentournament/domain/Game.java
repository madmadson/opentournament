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
    private String tournamentUUID;
    private int tournament_round;

    private int playing_field;

    private String participantOneUUID;
    private TournamentParticipant participantOne;
    private int participant_one_score;
    private int participant_one_control_points;
    private int participant_one_victory_points;
    private String participant_one_army_list;

    private String participantTwoUUID;
    private TournamentParticipant participantTwo;
    private int participant_two_score;
    private int participant_two_control_points;
    private int participant_two_victory_points;
    private String participant_two_army_list;

    private boolean finished;
    private String scenario;

    private int participant_one_intermediate_points;
    private int participant_two_intermediate_points;

    // for online (abstract doesnt work
    private TournamentPlayer tournamentPlayerOne;
    private TournamentPlayer tournamentPlayerTwo;

    // only for swap player
    private boolean swappable;
    private boolean startSwappingPlayerOne;
    private boolean startSwappingPlayerTwo;

    public Game(Parcel in) {

        _id = in.readLong();
        UUID = in.readString();
        parent_UUID = in.readString();
        tournamentUUID = in.readString();
        tournament_round = in.readInt();

        participantOneUUID = in.readString();
        participant_one_score = in.readInt();
        participant_one_control_points = in.readInt();
        participant_one_victory_points = in.readInt();
        participant_one_army_list = in.readString();

        participantTwoUUID = in.readString();
        participant_two_score = in.readInt();
        participant_two_control_points = in.readInt();
        participant_two_victory_points = in.readInt();
        participant_two_army_list = in.readString();

        finished = in.readByte() != 0;
        scenario = in.readString();
        playing_field = in.readInt();
        participant_one_intermediate_points = in.readInt();
        participant_two_intermediate_points = in.readInt();
    }


    public Game() {
    }

    @Exclude
    public boolean isSwappable() {

        return swappable;
    }


    public void setSwappable(boolean swappable) {

        this.swappable = swappable;
    }


    @Exclude
    public boolean isStartSwappingPlayerOne() {

        return startSwappingPlayerOne;
    }


    public void setStartSwappingPlayerOne(boolean startSwappingPlayerOne) {

        this.startSwappingPlayerOne = startSwappingPlayerOne;
    }


    @Exclude
    public boolean isStartSwappingPlayerTwo() {

        return startSwappingPlayerTwo;
    }


    public void setStartSwappingPlayerTwo(boolean startSwappingPlayerTwo) {

        this.startSwappingPlayerTwo = startSwappingPlayerTwo;
    }


    @Exclude
    public long get_id() {

        return _id;
    }


    @Exclude
    public String getTournamentUUID() {

        return tournamentUUID;
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


    public void setTournamentUUID(String tournamentUUID) {

        this.tournamentUUID = tournamentUUID;
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

        game.setTournamentUUID(cursor.getString(3));
        game.setTournament_round(cursor.getInt(4));

        game.setParticipantOneUUID(cursor.getString(5));

        game.setParticipant_one_score(cursor.getInt(6));
        game.setParticipant_one_control_points(cursor.getInt(7));
        game.setParticipant_one_victory_points(cursor.getInt(8));
        game.setParticipant_one_army_list(cursor.getString(9));

        game.setParticipantTwoUUID(cursor.getString(10));

        game.setParticipant_two_score(cursor.getInt(11));
        game.setParticipant_two_control_points(cursor.getInt(12));
        game.setParticipant_two_victory_points(cursor.getInt(13));
        game.setParticipant_two_army_list(cursor.getString(14));

        game.setFinished(cursor.getInt(15) == 1);

        game.setScenario(cursor.getString(16));

        game.setPlaying_field(cursor.getInt(17));

        game.setParticipant_one_intermediate_points(cursor.getInt(18));
        game.setParticipant_two_intermediate_points(cursor.getInt(19));

        return game;
    }


    /**
     * Used by firebase mapper to get player information.
     *
     * @return
     */
    public TournamentPlayer getTournamentPlayerOne() {

        return tournamentPlayerOne;
    }


    public void setTournamentPlayerOne(TournamentPlayer tournamentPlayerOne) {

        this.tournamentPlayerOne = tournamentPlayerOne;
    }


    public TournamentPlayer getTournamentPlayerTwo() {

        return tournamentPlayerTwo;
    }


    public void setTournamentPlayerTwo(TournamentPlayer tournamentPlayerTwo) {

        this.tournamentPlayerTwo = tournamentPlayerTwo;
    }


    @Exclude
    public TournamentParticipant getParticipantOne() {

        return participantOne;
    }


    public void setParticipantOne(TournamentParticipant participantOne) {

        this.participantOne = participantOne;
    }


    @Exclude
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
        dest.writeString(tournamentUUID);
        dest.writeInt(tournament_round);

        dest.writeString(participantOneUUID);
        dest.writeInt(participant_one_score);
        dest.writeInt(participant_one_control_points);
        dest.writeInt(participant_one_victory_points);
        dest.writeString(participant_one_army_list);

        dest.writeString(participantTwoUUID);
        dest.writeInt(participant_two_score);
        dest.writeInt(participant_two_control_points);
        dest.writeInt(participant_two_victory_points);
        dest.writeString(participant_two_army_list);

        dest.writeByte((byte) (finished ? 1 : 0));
        dest.writeString(scenario);
        dest.writeInt(playing_field);
        dest.writeInt(participant_one_intermediate_points);
        dest.writeInt(participant_two_intermediate_points);
    }


    public String getParent_UUID() {

        return parent_UUID;
    }


    public void setParent_UUID(String parent_UUID) {

        this.parent_UUID = parent_UUID;
    }


    public int getParticipant_one_intermediate_points() {

        return participant_one_intermediate_points;
    }


    public void setParticipant_one_intermediate_points(int participant_one_intermediate_points) {

        this.participant_one_intermediate_points = participant_one_intermediate_points;
    }


    public int getParticipant_two_intermediate_points() {

        return participant_two_intermediate_points;
    }


    public void setParticipant_two_intermediate_points(int participant_two_intermediate_points) {

        this.participant_two_intermediate_points = participant_two_intermediate_points;
    }


    public String getParticipant_two_army_list() {

        return participant_two_army_list;
    }


    @Exclude
    public String getParticipantTwoArmyListWithMaximumCharacters(int maxChars) {

        if (participant_two_army_list != null && participant_two_army_list.length() > maxChars) {
            return participant_two_army_list.substring(0, maxChars).concat("...");
        } else {
            return participant_two_army_list;
        }
    }


    public void setParticipant_two_army_list(String participant_two_army_list) {

        this.participant_two_army_list = participant_two_army_list;
    }


    public String getParticipant_one_army_list() {

        return participant_one_army_list;
    }


    @Exclude
    public String getParticipantOneArmyListWithMaximumCharacters(int maxChars) {

        if (participant_one_army_list != null && participant_one_army_list.length() > maxChars) {
            return participant_one_army_list.substring(0, maxChars).concat("...");
        } else {
            return participant_one_army_list;
        }
    }


    public void setParticipant_one_army_list(String participant_one_army_list) {

        this.participant_one_army_list = participant_one_army_list;
    }
}
