package madson.org.opentournament.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public abstract class TournamentParticipant implements Parcelable {

    public abstract String getUuid();


    public abstract String getName();


    public abstract List<String> getListOfOpponentsUUIDs();
}
