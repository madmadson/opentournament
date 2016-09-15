package madson.org.opentournament.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.Date;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Tournament implements Parcelable {

    public static final Creator<Tournament> CREATOR = new Creator<Tournament>() {

        @Override
        public Tournament createFromParcel(Parcel in) {

            return new Tournament(in);
        }


        @Override
        public Tournament[] newArray(int size) {

            return new Tournament[size];
        }
    };

    private long _id;

    private String name;
    private String location;
    private int actualPlayers;
    private int maxNumberOfPlayers;
    private Date dateOfTournament;
    private int actualRound;
    private String onlineUUID;
    private String creatorName;
    private String creatorEmail;
    private TournamentTyp tournamentTyp;
    private GameOrSportTyp gameOrSportTyp;
    private String state;

    // empty constructor
    public Tournament() {

        gameOrSportTyp = GameOrSportTyp.WARMACHINE;
        tournamentTyp = TournamentTyp.SOLO;
    }


    /**
     * Clones given Tournament.
     *
     * @param  tournament
     */
    public Tournament(Tournament tournament) {

        this._id = tournament.get_id();
        this.name = tournament.getName();
        this.location = tournament.getLocation();
        this.maxNumberOfPlayers = tournament.getMaxNumberOfPlayers();
        this.actualPlayers = tournament.getActualPlayers();
        this.dateOfTournament = tournament.getDateOfTournament();
        this.actualRound = tournament.getActualRound();
        this.onlineUUID = tournament.getOnlineUUID();
        this.creatorName = tournament.getCreatorName();
        this.creatorEmail = tournament.getCreatorEmail();
        this.tournamentTyp = TournamentTyp.valueOf(tournament.getTournamentTyp());
        this.gameOrSportTyp = GameOrSportTyp.valueOf(tournament.getGameOrSportTyp());
        this.state = tournament.getState();
    }


    public Tournament(Parcel in) {

        _id = in.readLong();
        name = in.readString();
        location = in.readString();
        actualPlayers = in.readInt();
        maxNumberOfPlayers = in.readInt();

        long longFromDate = in.readLong();

        if (longFromDate != 0) {
            dateOfTournament = new Date(longFromDate);
        }

        actualRound = in.readInt();
        onlineUUID = in.readString();
        creatorName = in.readString();
        creatorEmail = in.readString();
        setTournamentTyp(in.readString());
        setGameOrSportTyp(in.readString());
        state = in.readString();
    }

    @Override
    public int describeContents() {

        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(_id);
        parcel.writeString(name);
        parcel.writeString(location);
        parcel.writeInt(actualPlayers);
        parcel.writeInt(maxNumberOfPlayers);

        if (dateOfTournament != null) {
            parcel.writeLong(dateOfTournament.getTime());
        } else {
            parcel.writeLong(0L);
        }

        parcel.writeInt(actualRound);
        parcel.writeString(onlineUUID);
        parcel.writeString(creatorName);
        parcel.writeString(creatorEmail);
        parcel.writeString(getTournamentTyp());
        parcel.writeString(getGameOrSportTyp());
        parcel.writeString(state);
    }


    public int getActualPlayers() {

        return actualPlayers;
    }


    public void setActualPlayers(int actualPlayers) {

        this.actualPlayers = actualPlayers;
    }


    /**
     * Excluded in josn sending to server.
     *
     * @return
     */
    @Exclude
    public long get_id() {

        return _id;
    }


    public void set_id(long _id) {

        this._id = _id;
    }


    public int getActualRound() {

        return actualRound;
    }


    public String getName() {

        return name;
    }


    public String getState() {

        return state;
    }


    public String getLocation() {

        return location;
    }


    public int getMaxNumberOfPlayers() {

        return maxNumberOfPlayers;
    }


    public Date getDateOfTournament() {

        return dateOfTournament;
    }


    public void setName(String name) {

        this.name = name;
    }


    public void setLocation(String location) {

        this.location = location;
    }


    public void setMaxNumberOfPlayers(int maxNumberOfPlayers) {

        this.maxNumberOfPlayers = maxNumberOfPlayers;
    }


    public void setDateOfTournament(Date dateOfTournament) {

        this.dateOfTournament = dateOfTournament;
    }


    public void setActualRound(int actualRound) {

        this.actualRound = actualRound;
    }


    @Exclude
    public String getOnlineUUID() {

        return onlineUUID;
    }


    public void setOnlineUUID(String onlineUUID) {

        this.onlineUUID = onlineUUID;
    }


    public String getCreatorName() {

        return creatorName;
    }


    public void setCreatorName(String creatorName) {

        this.creatorName = creatorName;
    }


    public String getCreatorEmail() {

        return creatorEmail;
    }


    public void setCreatorEmail(String creatorEmail) {

        this.creatorEmail = creatorEmail;
    }


    public String getGameOrSportTyp() {

        return gameOrSportTyp.name();
    }


    public void setGameOrSportTyp(String gameOrSportTyp) {

        this.gameOrSportTyp = GameOrSportTyp.valueOf(gameOrSportTyp);
    }


    @Override
    public String toString() {

        return "Tournament{"
            + "_id=" + _id
            + ", name='" + name + '\''
            + ", location='" + location + '\''
            + ", actualPlayers=" + actualPlayers
            + ", maxNumberOfPlayers=" + maxNumberOfPlayers
            + ", dateOfTournament=" + dateOfTournament
            + ", actualRound=" + actualRound
            + ", onlineUUID='" + onlineUUID + '\''
            + ", creatorName='" + creatorName + '\''
            + ", creatorEmail='" + creatorEmail + '\''
            + ", tournamentTyp=" + tournamentTyp
            + ", gameOrSportTyp=" + gameOrSportTyp
            + ", state='" + state + '\'' + '}';
    }


    public String getTournamentTyp() {

        return tournamentTyp.name();
    }


    public void setTournamentTyp(String tournamentTyp) {

        this.tournamentTyp = TournamentTyp.valueOf(tournamentTyp);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Tournament that = (Tournament) o;

        if (_id != that._id)
            return false;

        return onlineUUID != null ? onlineUUID.equals(that.onlineUUID) : that.onlineUUID == null;
    }


    @Override
    public int hashCode() {

        int result = (int) (_id ^ (_id >>> 32));
        result = 31 * result + (onlineUUID != null ? onlineUUID.hashCode() : 0);

        return result;
    }


    public void setState(String state) {

        this.state = state;
    }
}
