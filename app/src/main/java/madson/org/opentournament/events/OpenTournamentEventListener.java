package madson.org.opentournament.events;

/**
 * Events for tournament.
 *
 * @author  Tobi as Matt - tmatt@contargo.net
 */
public interface OpenTournamentEventListener {

    void handleEvent(OpenTournamentEventTag eventTag, OpenTournamentEvent parameterObject);
}
