package madson.org.opentournament.config;

import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.PairingOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class MapOfPairingConfig {

    // for visualisation you need a string resource
    public static final String PLAYER_NOT_PLAY_TWICE_AGAINST_EACH_OVER =
        "pairing_option_player_not_play_twice_against_each_other";
    private Map<GameOrSportTyp, PairingOption> pairingOptions;

    public MapOfPairingConfig() {

        pairingOptions = new HashMap<>();
        pairingOptions.put(GameOrSportTyp.ALL, new PairingOption(PLAYER_NOT_PLAY_TWICE_AGAINST_EACH_OVER, true));
    }

    public Map<GameOrSportTyp, PairingOption> getPairingOptions() {

        return pairingOptions;
    }
}
