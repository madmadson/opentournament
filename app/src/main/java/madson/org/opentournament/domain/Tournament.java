package madson.org.opentournament.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

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
    private int maxNumberOfPlayers;
    private Date dateOfTournament;
    private int actualRound;
    private String onlineUUID;
    private String creatorName;
    private String creatorEmail;
    private TournamentTyp tournamentTyp;

    // empty constructor
    public Tournament() {

        tournamentTyp = TournamentTyp.WARMACHINE;
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
        this.dateOfTournament = tournament.getDateOfTournament();
        this.actualRound = tournament.getActualRound();
        this.onlineUUID = tournament.getOnlineUUID();
        this.creatorName = tournament.getCreatorName();
        this.creatorEmail = tournament.getCreatorEmail();
        this.tournamentTyp = TournamentTyp.valueOf(tournament.getTournamentTyp());
    }


    public Tournament(Parcel in) {

        _id = in.readLong();
        name = in.readString();
        location = in.readString();
        maxNumberOfPlayers = in.readInt();
        actualRound = in.readInt();
        onlineUUID = in.readString();
        creatorName = in.readString();
        creatorEmail = in.readString();
        dateOfTournament = new Date(in.readLong());
    }

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


    @Override
    public String toString() {

        return "Tournament{"
            + "_id=" + _id
            + ", name='" + name + '\''
            + ", location='" + location + '\''
            + ", maxNumberOfPlayers=" + maxNumberOfPlayers
            + ", dateOfTournament=" + dateOfTournament
            + ", actualRound=" + actualRound
            + ", onlineUUID='" + onlineUUID + '\''
            + ", creatorName='" + creatorName + '\''
            + ", creatorEmail='" + creatorEmail + '\''
            + ", tournamentTyp=" + tournamentTyp + '}';
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
        parcel.writeInt(maxNumberOfPlayers);
        parcel.writeInt(actualRound);
        parcel.writeString(onlineUUID);
        parcel.writeString(creatorName);
        parcel.writeString(creatorEmail);

        if (dateOfTournament != null) {
            parcel.writeLong(dateOfTournament.getTime());
        } else {
            // get today
            parcel.writeLong(DateTime.now().getMillis());
        }
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

        return _id == that._id;
    }


    @Override
    public int hashCode() {

        return (int) (_id ^ (_id >>> 32));
    }
}
