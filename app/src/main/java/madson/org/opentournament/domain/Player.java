package madson.org.opentournament.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Player implements Parcelable {

    public static final Creator<Player> CREATOR = new Creator<Player>() {

        @Override
        public Player createFromParcel(Parcel in) {

            return new Player(in);
        }


        @Override
        public Player[] newArray(int size) {

            return new Player[size];
        }
    };

    private long _id;
    private String onlineUUID;

    private String firstname;
    private String nickname;
    private String lastname;

    // list of tournaments player played. Note: online online persistent
    private Map<String, Boolean> tournaments = new HashMap<>();

    private String auth_email;

    public Player(long id, String firstname, String nickname, String lastname) {

        this._id = id;
        this.firstname = firstname;
        this.nickname = nickname;
        this.lastname = lastname;
    }


    public Player(String firstname, String nickname, String lastname) {

        this.firstname = firstname;
        this.nickname = nickname;
        this.lastname = lastname;
    }


    public Player() {
    }


    protected Player(Parcel in) {

        _id = in.readLong();
        onlineUUID = in.readString();
        firstname = in.readString();
        nickname = in.readString();
        lastname = in.readString();
        auth_email = in.readString();
    }

    public Map<String, Boolean> getTournaments() {

        return tournaments;
    }


    public void setTournaments(Map<String, Boolean> tournaments) {

        this.tournaments = tournaments;
    }


    public long get_id() {

        return _id;
    }


    public String getFirstname() {

        return firstname;
    }


    public String getLastname() {

        return lastname;
    }


    public String getNickname() {

        return nickname;
    }


    public String getOnlineUUID() {

        return onlineUUID;
    }


    public void setOnlineUUID(String onlineUUID) {

        this.onlineUUID = onlineUUID;
    }


    public void setFirstname(String firstname) {

        this.firstname = firstname;
    }


    public void setNickname(String nickname) {

        this.nickname = nickname;
    }


    public void setLastname(String lastname) {

        this.lastname = lastname;
    }


    public void set_id(long _id) {

        this._id = _id;
    }


    public String getAuth_email() {

        return auth_email;
    }


    public void setAuth_email(String auth_email) {

        this.auth_email = auth_email;
    }


    @Override
    public String toString() {

        return "Player{"
            + "_id=" + _id
            + ", onlineUUID='" + onlineUUID + '\''
            + ", firstname='" + firstname + '\''
            + ", nickname='" + nickname + '\''
            + ", lastname='" + lastname + '\''
            + ", auth_email='" + auth_email + '\'' + '}';
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Player player = (Player) o;

        if (_id != player._id)
            return false;

        if (onlineUUID != null ? !onlineUUID.equals(player.onlineUUID) : player.onlineUUID != null)
            return false;

        if (firstname != null ? !firstname.equals(player.firstname) : player.firstname != null)
            return false;

        if (nickname != null ? !nickname.equals(player.nickname) : player.nickname != null)
            return false;

        if (lastname != null ? !lastname.equals(player.lastname) : player.lastname != null)
            return false;

        return auth_email != null ? auth_email.equals(player.auth_email) : player.auth_email == null;
    }


    @Override
    public int hashCode() {

        int result = (int) (_id ^ (_id >>> 32));
        result = 31 * result + (onlineUUID != null ? onlineUUID.hashCode() : 0);
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (auth_email != null ? auth_email.hashCode() : 0);

        return result;
    }


    @Override
    public int describeContents() {

        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(_id);
        parcel.writeString(onlineUUID);
        parcel.writeString(firstname);
        parcel.writeString(nickname);
        parcel.writeString(lastname);
        parcel.writeString(auth_email);
    }


    public static Player fromTournamentPlayer(TournamentPlayer tournamentPlayer) {

        Player player = new Player();

        player.setOnlineUUID(tournamentPlayer.getPlayer_online_uuid());
        player.setFirstname(tournamentPlayer.getFirstname());
        player.setNickname(tournamentPlayer.getNickname());
        player.setLastname(tournamentPlayer.getLastname());

        return player;
    }
}
