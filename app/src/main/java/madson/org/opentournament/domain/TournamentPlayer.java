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
    private String online_uuid;

    private long tournament_id;
    private String tournament_online_uid;
    private long player_id;
    private String player_online_uuid;

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


    /**
     * constructor to enable player to tournament mapping.
     *
     * @param  player
     * @param  tournament
     */
    public TournamentPlayer(Player player, Tournament tournament) {

        this.player_id = player.get_id();
        this.player_online_uuid = player.getOnlineUUID();
        this.firstname = player.getFirstname();
        this.lastname = player.getLastname();
        this.nickname = player.getNickname();
    }


    protected TournamentPlayer(Parcel in) {

        _id = in.readLong();
        online_uuid = in.readString();
        tournament_id = in.readLong();
        tournament_online_uid = in.readString();
        player_id = in.readLong();
        player_online_uuid = in.readString();
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


    @Exclude
    public long getTournament_id() {

        return tournament_id;
    }


    public void setTournament_id(long tournament_id) {

        this.tournament_id = tournament_id;
    }


    @Exclude
    public long getPlayer_id() {

        return player_id;
    }


    public void setPlayer_id(long player_id) {

        this.player_id = player_id;
    }


    public String getFirstname() {

        return firstname;
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


    public String getOnline_uuid() {

        return online_uuid;
    }


    public void setOnline_uuid(String online_uuid) {

        this.online_uuid = online_uuid;
    }


    @Exclude
    public String getTournament_online_uid() {

        return tournament_online_uid;
    }


    public void setTournament_online_uid(String tournament_online_uid) {

        this.tournament_online_uid = tournament_online_uid;
    }


    public String getPlayer_online_uuid() {

        return player_online_uuid;
    }


    public void setPlayer_online_uuid(String player_online_uuid) {

        this.player_online_uuid = player_online_uuid;
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


    @Exclude
    public String getRealPlayerId() {

        if (player_online_uuid != null) {
            return player_online_uuid;
        } else {
            return String.valueOf(player_id);
        }
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        TournamentPlayer that = (TournamentPlayer) o;

        if (_id != that._id)
            return false;

        if (player_id != that.player_id)
            return false;

        if (online_uuid != null ? !online_uuid.equals(that.online_uuid) : that.online_uuid != null)
            return false;

        return player_online_uuid != null ? player_online_uuid.equals(that.player_online_uuid)
                                          : that.player_online_uuid == null;
    }


    @Override
    public int hashCode() {

        int result = (int) (_id ^ (_id >>> 32));
        result = 31 * result + (online_uuid != null ? online_uuid.hashCode() : 0);
        result = 31 * result + (int) (player_id ^ (player_id >>> 32));
        result = 31 * result + (player_online_uuid != null ? player_online_uuid.hashCode() : 0);

        return result;
    }


    @Override
    public String toString() {

        return "TournamentPlayer{"
            + "_id=" + _id
            + ", online_uuid='" + online_uuid + '\''
            + ", tournament_id=" + tournament_id
            + ", tournament_online_uid='" + tournament_online_uid + '\''
            + ", player_id=" + player_id
            + ", player_online_uuid='" + player_online_uuid + '\''
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
        parcel.writeString(online_uuid);
        parcel.writeLong(tournament_id);
        parcel.writeString(tournament_online_uid);
        parcel.writeLong(player_id);
        parcel.writeString(player_online_uuid);
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
