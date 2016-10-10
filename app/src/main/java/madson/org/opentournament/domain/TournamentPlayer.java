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
public class TournamentPlayer implements Parcelable {

    public static final Creator<TournamentPlayer> CREATOR = new Creator<TournamentPlayer>() {

        @Override
        public TournamentPlayer createFromParcel(Parcel in) {

            return new TournamentPlayer(in);
        }


        @Override
        public TournamentPlayer[] newArray(int size) {

            return new TournamentPlayer[size];
        }
    };

    private long _id;
    private String playerUUID;

    private String tournamentId;

    // used for shortcut not always go for names
    private String firstName;
    private String nickName;
    private String lastName;

    private String teamName;
    private String faction;
    private String meta;

    private int droppedInRound;

    private boolean dummy;
    private boolean local;

    private List<String> opponentsPlayerIds = new ArrayList<>();

    public TournamentPlayer() {
    }


    protected TournamentPlayer(Parcel in) {

        _id = in.readLong();
        playerUUID = in.readString();
        firstName = in.readString();
        nickName = in.readString();
        lastName = in.readString();
        teamName = in.readString();
        faction = in.readString();
        meta = in.readString();
        local = in.readInt() != 0;
        dummy = in.readInt() != 0;
        droppedInRound = in.readInt();
    }

    @Exclude
    public long get_id() {

        return _id;
    }


    public void set_id(long _id) {

        this._id = _id;
    }


    public String getFirstName() {

        return firstName;
    }


    public String getTournamentId() {

        return tournamentId;
    }


    public void setTournamentId(String tournamentId) {

        this.tournamentId = tournamentId;
    }


    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }


    public String getNickName() {

        return nickName;
    }


    public void setNickName(String nickName) {

        this.nickName = nickName;
    }


    public String getLastName() {

        return lastName;
    }


    public void setLastName(String lastName) {

        this.lastName = lastName;
    }


    public String getTeamName() {

        return teamName;
    }


    public void setTeamName(String teamName) {

        this.teamName = teamName;
    }


    public String getFaction() {

        return faction;
    }


    public void setFaction(String faction) {

        this.faction = faction;
    }


    public String getMeta() {

        return meta;
    }


    public void setMeta(String meta) {

        this.meta = meta;
    }


    public String getPlayerUUID() {

        return playerUUID;
    }


    public void setPlayerUUID(String playerUUID) {

        this.playerUUID = playerUUID;
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


    public boolean isLocal() {

        return local;
    }


    public void setLocal(boolean local) {

        this.local = local;
    }


    public List<String> getListOfOpponentsIds() {

        return opponentsPlayerIds;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        TournamentPlayer that = (TournamentPlayer) o;

        return playerUUID != null ? playerUUID.equals(that.playerUUID) : that.playerUUID == null;
    }


    @Override
    public int hashCode() {

        return playerUUID != null ? playerUUID.hashCode() : 0;
    }


    @Override
    public String toString() {

        return "TournamentPlayer{"
            + "_id=" + _id
            + ", playerUUID='" + playerUUID + '\''
            + ", tournamentId=" + tournamentId
            + ", firstName='" + firstName + '\''
            + ", nickName='" + nickName + '\''
            + ", lastName='" + lastName + '\''
            + ", teamName='" + teamName + '\''
            + ", faction='" + faction + '\''
            + ", meta='" + meta + '\''
            + ", droppedInRound=" + droppedInRound
            + ", dummy=" + dummy
            + ", local=" + local
            + ", opponentsPlayerIds=" + opponentsPlayerIds + '}';
    }


    @Override
    public int describeContents() {

        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(_id);
        parcel.writeString(playerUUID);

        parcel.writeString(firstName);
        parcel.writeString(nickName);
        parcel.writeString(lastName);
        parcel.writeString(teamName);
        parcel.writeString(faction);
        parcel.writeString(meta);
        parcel.writeInt(local ? 0 : 1);
        parcel.writeInt(dummy ? 0 : 1);
        parcel.writeInt(droppedInRound);
    }
}
