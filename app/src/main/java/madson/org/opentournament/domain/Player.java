package madson.org.opentournament.domain;

/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Player {

    private int _id;
    private String firstname;
    private String nickname;
    private String lastname;

    public Player(int id, String firstname, String nickname, String lastname) {

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
