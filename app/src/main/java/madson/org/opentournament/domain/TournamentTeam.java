package madson.org.opentournament.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;


/**
 * Mapping Class for tournament and mapping. Is own instance for whole tournament.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentTeam extends TournamentParticipant implements Parcelable {

    public static final Creator<TournamentTeam> CREATOR = new Creator<TournamentTeam>() {

        @Override
        public TournamentTeam createFromParcel(Parcel in) {

            return new TournamentTeam(in);
        }


        @Override
        public TournamentTeam[] newArray(int size) {

            return new TournamentTeam[size];
        }
    };

    private long _id;

    private long tournamentId;
    private String teamName;
    private String teamUUID;

    private String meta;

    private int droppedInRound;

    private boolean dummy;

    private List<String> opponentsPlayerIds = new ArrayList<>();

    public TournamentTeam(String teamName) {

        this.teamName = teamName;
    }


    protected TournamentTeam(Parcel in) {

        _id = in.readLong();

        teamName = in.readString();
        meta = in.readString();
        droppedInRound = in.readInt();
    }

    @Exclude
    public long get_id() {

        return _id;
    }


    public void set_id(long _id) {

        this._id = _id;
    }


    public long getTournamentId() {

        return tournamentId;
    }


    public void setTournamentId(long tournamentId) {

        this.tournamentId = tournamentId;
    }


    public String getTeamName() {

        return teamName;
    }


    public void setTeamName(String teamName) {

        this.teamName = teamName;
    }


    public String getMeta() {

        return meta;
    }


    public void setMeta(String meta) {

        this.meta = meta;
    }


    public boolean isDummy() {

        return dummy;
    }


    public void setDummy(boolean dummy) {

        this.dummy = dummy;
    }


    public int getDroppedInRound() {

        return droppedInRound;
    }


    public void setDroppedInRound(int droppedInRound) {

        this.droppedInRound = droppedInRound;
    }


    public List<String> getListOfOpponentsIds() {

        return opponentsPlayerIds;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(_id);

        parcel.writeString(teamName);
        parcel.writeString(meta);
        parcel.writeInt(dummy ? 0 : 1);
        parcel.writeInt(droppedInRound);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        TournamentTeam that = (TournamentTeam) o;

        return teamName != null ? teamName.equals(that.teamName) : that.teamName == null;
    }


    @Override
    public int hashCode() {

        return teamName != null ? teamName.hashCode() : 0;
    }


    @Override
    public int describeContents() {

        return 0;
    }


    @Override
    public String getUuid() {

        return teamName;
    }
}
