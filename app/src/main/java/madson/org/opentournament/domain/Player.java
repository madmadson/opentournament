package madson.org.opentournament.domain;

/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Player {

    private long _id;
    private String firstname;
    private String nickname;
    private String lastname;

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


    @Override
    public String toString() {

        return "Player{"
            + "_id=" + _id
            + ", firstname='" + firstname + '\''
            + ", lastname='" + lastname + '\''
            + ", nickname='" + nickname + '\'' + '}';
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

        if (firstname != null ? !firstname.equals(player.firstname) : player.firstname != null)
            return false;

        if (nickname != null ? !nickname.equals(player.nickname) : player.nickname != null)
            return false;

        return lastname != null ? lastname.equals(player.lastname) : player.lastname == null;
    }


    @Override
    public int hashCode() {

        int result = (int) (_id ^ (_id >>> 32));
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);

        return result;
    }
}
