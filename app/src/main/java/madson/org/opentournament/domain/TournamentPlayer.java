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
    private String playerOnlineUUID;
    private String playerId;

    private long tournamentId;

    // used for shortcut not always go for names
    private String firstname;
    private String nickname;
    private String lastname;

    private String teamname;
    private String faction;
    private String meta;

    private int droppedInRound;

    private boolean dummy;

    private List<String> opponentsPlayerIds = new ArrayList<>();

    public TournamentPlayer() {
    }


    protected TournamentPlayer(Parcel in) {

        _id = in.readLong();
        playerId = in.readString();
        playerOnlineUUID = in.readString();
        firstname = in.readString();
        nickname = in.readString();
        lastname = in.readString();
        teamname = in.readString();
        faction = in.readString();
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


    public String getFirstname() {

        return firstname;
    }


    public long getTournamentId() {

        return tournamentId;
    }


    public void setTournamentId(long tournamentId) {

        this.tournamentId = tournamentId;
    }


    public void setPlayerId(String playerId) {

        this.playerId = playerId;
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


    public String getTeamname() {

        return teamname;
    }


    public void setTeamname(String teamname) {

        this.teamname = teamname;
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


    public String getPlayerOnlineUUID() {

        return playerOnlineUUID;
    }


    public void setPlayerOnlineUUID(String playerOnlineUUID) {

        this.playerOnlineUUID = playerOnlineUUID;
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


    public String getPlayerId() {

        return playerId;
    }


    @Exclude
    public String getRealPlayerId() {

        if (playerOnlineUUID != null) {
            return playerOnlineUUID;
        } else {
            return String.valueOf(playerId);
        }
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        TournamentPlayer that = (TournamentPlayer) o;

        return playerOnlineUUID != null ? playerOnlineUUID.equals(that.playerOnlineUUID)
                                        : that.playerOnlineUUID == null;
    }


    @Override
    public int hashCode() {

        return playerOnlineUUID != null ? playerOnlineUUID.hashCode() : 0;
    }


    @Override
    public String toString() {

        return "TournamentPlayer{"
            + "_id=" + _id
            + ", playerOnlineUUID='" + playerOnlineUUID + '\''
            + ", firstname='" + firstname + '\''
            + ", nickname='" + nickname + '\''
            + ", lastname='" + lastname + '\''
            + ", teamname='" + teamname + '\''
            + ", faction='" + faction + '\''
            + ", meta='" + meta + '\''
            + ", droppedInRound=" + droppedInRound
            + ", dummy=" + dummy
            + ", opponentsPlayerIds=" + opponentsPlayerIds + '}';
    }


    @Override
    public int describeContents() {

        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(_id);
        parcel.writeString(playerOnlineUUID);
        parcel.writeString(playerId);
        parcel.writeString(firstname);
        parcel.writeString(nickname);
        parcel.writeString(lastname);
        parcel.writeString(teamname);
        parcel.writeString(faction);
        parcel.writeString(meta);
        parcel.writeInt(dummy ? 0 : 1);
        parcel.writeInt(droppedInRound);
    }


    public List<String> getListOfOpponentsIds() {

        return opponentsPlayerIds;
    }
}
