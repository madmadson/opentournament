package madson.org.opentournament.domain;

/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Player {

    private int _id;
    private String firstname;
    private String lastname;
    private String nickname;

    public Player(int _id, String firstname, String lastname, String nickname) {

        this._id = _id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.nickname = nickname;
    }

    public int getId() {

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
}
