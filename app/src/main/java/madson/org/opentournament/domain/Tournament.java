package madson.org.opentournament.domain;

import java.util.Date;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Tournament {

    public static final String[] ALL_COLS_FOR_TOURNAMENT = {
        "_id", "name", "location", "date", "maxNumberOfPlayers", "actualRound", "onlineUUID", "creator", "creatorEmail"
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


    public long getId() {

        return _id;
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
            + ", onlineUUID=" + onlineUUID
            + ", creatorName='" + creatorName + '\''
            + ", creatorEmail='" + creatorEmail + '\'' + '}';
    }
}
