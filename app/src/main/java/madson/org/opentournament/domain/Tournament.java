package madson.org.opentournament.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.util.Date;
import java.util.UUID;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class Tournament {

    public static final String[] ALL_COLS_FOR_TOURNAMENT = { "_id", "name", "description", "numberOfPlayers", "date" };

    private int _id;
    private String name;
    private String description;
    private int numberOfPlayers;
    private Date dateOfTournament;

    public Tournament(int id, String name, String description, int numberOfPlayers, Date dateOfTournament) {

        this._id = id;
        this.name = name;
        this.description = description;
        this.numberOfPlayers = numberOfPlayers;
        this.dateOfTournament = dateOfTournament;
    }

    public String getName() {

        return name;
    }


    public int getId() {

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

        return name;
    }
}
