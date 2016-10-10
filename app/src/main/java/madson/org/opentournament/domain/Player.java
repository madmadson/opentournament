package madson.org.opentournament.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
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
    private String UUID;

    private String firstName;
    private String nickName;
    private String lastName;

    private boolean local;

    // list of tournaments player played. Note: online persistent
    private Map<String, Boolean> tournaments = new HashMap<>();

    private String authenticatedEmail;

    private String meta;

    public Player() {
    }


    public Player(String uuid) {

        this.UUID = uuid;
    }


    protected Player(Parcel in) {

        _id = in.readLong();
        UUID = in.readString();
        firstName = in.readString();
        nickName = in.readString();
        lastName = in.readString();
        authenticatedEmail = in.readString();
        meta = in.readString();
        local = in.readInt() != 0;
    }

    public Map<String, Boolean> getTournaments() {

        return tournaments;
    }


    public void setTournaments(Map<String, Boolean> tournaments) {

        this.tournaments = tournaments;
    }


    @Exclude
    public long get_id() {

        return _id;
    }


    public String getFirstName() {

        return firstName;
    }


    public String getLastName() {

        return lastName;
    }


    public String getNickName() {

        return nickName;
    }


    public String getUUID() {

        return UUID;
    }


    public void setUUID(String UUID) {

        this.UUID = UUID;
    }


    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }


    public void setNickName(String nickName) {

        this.nickName = nickName;
    }


    public void setLastName(String lastName) {

        this.lastName = lastName;
    }


    public void set_id(long _id) {

        this._id = _id;
    }


    public String getAuthenticatedEmail() {

        return authenticatedEmail;
    }


    public void setAuthenticatedEmail(String authenticatedEmail) {

        this.authenticatedEmail = authenticatedEmail;
    }


    public String getMeta() {

        return meta;
    }


    public void setMeta(String meta) {

        this.meta = meta;
    }


    @Exclude
    public boolean isLocal() {

        return local;
    }


    public void setLocal(boolean local) {

        this.local = local;
    }


    @Override
    public String toString() {

        return "Player{"
            + "_id=" + _id
            + ", UUID='" + UUID + '\''
            + ", firstName='" + firstName + '\''
            + ", nickName='" + nickName + '\''
            + ", lastName='" + lastName + '\''
            + ", local=" + local
            + ", tournaments=" + tournaments
            + ", authenticatedEmail='" + authenticatedEmail + '\''
            + ", meta='" + meta + '\'' + '}';
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Player player = (Player) o;

        return UUID != null ? UUID.equals(player.UUID) : player.UUID == null;
    }


    @Override
    public int hashCode() {

        return UUID != null ? UUID.hashCode() : 0;
    }


    @Override
    public int describeContents() {

        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(_id);
        parcel.writeString(UUID);
        parcel.writeString(firstName);
        parcel.writeString(nickName);
        parcel.writeString(lastName);
        parcel.writeString(authenticatedEmail);
        parcel.writeString(meta);
        parcel.writeInt(local ? 1 : 0);
    }
}
