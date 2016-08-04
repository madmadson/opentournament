package madson.org.opentournament.domain;

import java.util.Date;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Tournament {

    public static final String[] ALL_COLS_FOR_TOURNAMENT = {
        "_id", "name", "description", "numberOfPlayers", "date", "actualRound"
    };

    private long _id;
    private String name;
    private String description;
    private int numberOfPlayers;
    private Date dateOfTournament;
    private int actualRound;

    public Tournament(long id, String name, String description, int numberOfPlayers, Date dateOfTournament,
        int actualRound) {

        this._id = id;
        this.name = name;
        this.description = description;
        this.numberOfPlayers = numberOfPlayers;
        this.dateOfTournament = dateOfTournament;
        this.actualRound = actualRound;
    }


    public Tournament() {
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


    public String getDescription() {

        return description;
    }


    public int getNumberOfPlayers() {

        return numberOfPlayers;
    }


    public Date getDateOfTournament() {

        return dateOfTournament;
    }


    @Override
    public String toString() {

        return "Tournament{"
            + "_id=" + _id
            + ", name='" + name + '\''
            + ", description='" + description + '\''
            + ", numberOfPlayers=" + numberOfPlayers
            + ", dateOfTournament=" + dateOfTournament
            + ", actualRound=" + actualRound + '}';
    }
}
