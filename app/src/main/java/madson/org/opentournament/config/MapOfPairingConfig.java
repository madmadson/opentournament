package madson.org.opentournament.config;

import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.PairingOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class MapOfPairingConfig {

    // ATTENTION! YOU NEED A STRING RESOURCE FOR VISUALIZATION
    public static final String PLAYER_NOT_PLAY_TWICE_AGAINST_EACH_OVER =
        "pairing_option_player_not_play_twice_against_each_other";
    public static final String PLAYER_WITH_SAME_TEAM_NOT_PLAY_AGAINST_EACH_OTHER =
        "pairing_option_player_with_same_team_not_play_against_each_other";
    public static final String PARTICIPANT_WITH_SAME_AFFILIATION_NOT_PLAY_AGAINST_EACH_OTHER =
        "pairing_option_affiliation_with_same_affiliation_not_play_against_each_other";
    private Map<GameOrSportTyp, List<PairingOption>> pairingOptions;

    public MapOfPairingConfig() {

        pairingOptions = new HashMap<>();

        List<PairingOption> optionsForAllTournaments = new ArrayList<>();
        optionsForAllTournaments.add(new PairingOption(PLAYER_NOT_PLAY_TWICE_AGAINST_EACH_OVER, true));
        optionsForAllTournaments.add(new PairingOption(PARTICIPANT_WITH_SAME_AFFILIATION_NOT_PLAY_AGAINST_EACH_OTHER,
                false));

        pairingOptions.put(GameOrSportTyp.ALL, optionsForAllTournaments);

        List<PairingOption> optionsForSoloWarmachine = new ArrayList<>();
        optionsForSoloWarmachine.add(new PairingOption(PLAYER_WITH_SAME_TEAM_NOT_PLAY_AGAINST_EACH_OTHER, false));

        pairingOptions.put(GameOrSportTyp.WARMACHINE_SOLO, optionsForSoloWarmachine);
    }

    public Map<GameOrSportTyp, List<PairingOption>> getPairingOptions() {

        return pairingOptions;
    }
}
