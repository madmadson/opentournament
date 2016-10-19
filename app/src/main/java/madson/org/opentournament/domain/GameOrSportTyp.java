package madson.org.opentournament.domain;

import java.io.Serializable;


/**
 * Represent type of game or sport (warmachine, soccer, chess...).
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public enum GameOrSportTyp implements Serializable {

    ALL,
    WARMACHINE_SOLO,
    WARMACHINE_TEAM
}
