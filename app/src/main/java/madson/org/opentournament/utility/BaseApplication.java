package madson.org.opentournament.utility;

import android.app.Application;

import android.content.Context;

import android.net.ConnectivityManager;

import android.util.Log;

import com.facebook.FacebookSdk;

import madson.org.opentournament.R;
import madson.org.opentournament.about.AppInfo;
import madson.org.opentournament.about.LibraryItem;
import madson.org.opentournament.config.MapOfPairingConfig;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.TournamentEventListener;
import madson.org.opentournament.organize.setup.TournamentSetupEventListener;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.OngoingTournamentServiceImpl;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.service.PlayerServiceImpl;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.RankingServiceImpl;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentPlayerServiceImpl;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.service.TournamentServiceImpl;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Provides Basic functionality for Applications - All other Applications should extend this Application.
 */
public abstract class BaseApplication extends Application {

    private static OngoingTournamentService ongoingTournamentService;
    private static TournamentService tournamentService;
    private static RankingService rankingService;
    private static PlayerService playerService;
    private static TournamentPlayerService tournamentPlayerService;
    private TournamentEventListener tournamentEventListener;

    private Set<TournamentEventListener> tournamentEventListeners = new HashSet<>();

    @Override
    public void onCreate() {

        super.onCreate();

        // fail fast -> Environment.valueOf() in getEnvironment() will break if wrong value is configured
        Log.d(getClass().getName(), "Starting application for env: " + getEnvironment().name());

        if (getAppInfo() == null) {
            throw new IllegalStateException("AppInfo must be set!");
        }

        JodaTimeAndroid.init(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

        // NOTE: order of service wiring matters!

        if (playerService == null) {
            playerService = new PlayerServiceImpl(getApplicationContext());
        }

        if (tournamentService == null) {
            tournamentService = new TournamentServiceImpl(getApplicationContext());
        }

        if (tournamentPlayerService == null) {
            tournamentPlayerService = new TournamentPlayerServiceImpl(getApplicationContext());
        }

        // depend on tournamentPlayerService -> to do ranking -_-
        if (rankingService == null) {
            rankingService = new RankingServiceImpl(getApplicationContext());
        }

        // depend on tournament service, player service, ranking -> must pair games
        if (ongoingTournamentService == null) {
            ongoingTournamentService = new OngoingTournamentServiceImpl(getApplicationContext());
        }
    }


    /**
     * Check if the User is online (active network connection - needs permission ACCESS_NETWORK_STATE).
     *
     * @return  true if the user has an active network connection, false otherwise
     */
    public boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    /**
     * Returns the list of used third-party libraries. Override {@link #getAdditionalLibraries()} to addTournamentPlayer
     * libraries used by a specific app.
     *
     * @return  the list of {@link LibraryItem}s for this app.
     */
    public final List<LibraryItem> getAboutLibraries() {

        List<LibraryItem> libraries = getAdditionalLibraries();

        if (libraries == null) {
            libraries = new ArrayList<>();
        }

        LibraryItem materialDesignIcons = new LibraryItem(getString(R.string.material_design_icons_id),
                getString(R.string.material_design_icons_name), getString(R.string.material_design_icons_description),
                getString(R.string.material_design_icons_author), null, getString(R.string.material_design_icons_url));

        if (!libraries.contains(materialDesignIcons)) {
            libraries.add(materialDesignIcons);
        }

        Collections.sort(libraries);

        return libraries;
    }


    /**
     * @return  the environment this app runs in.
     */
    public abstract Environment getEnvironment();


    /**
     * @return  the list of used libraries in this app, excluding the ones already used in the app-lib. Duplicates will
     *          be removed from the list (matched by artifactId).
     */
    public abstract List<LibraryItem> getAdditionalLibraries();


    /**
     * @return  the {@link AppInfo} for this app.
     */
    public abstract AppInfo getAppInfo();


    public TournamentService getTournamentService() {

        return tournamentService;
    }


    public PlayerService getPlayerService() {

        return playerService;
    }


    public OngoingTournamentService getOngoingTournamentService() {

        return ongoingTournamentService;
    }


    public RankingService getRankingService() {

        return rankingService;
    }


    public TournamentPlayerService getTournamentPlayerService() {

        return tournamentPlayerService;
    }


    /**
     * Registers a listener .
     *
     * @param  listener
     */
    public void registerTournamentEventListener(TournamentEventListener listener) {

        if (!tournamentEventListeners.contains(listener)) {
            tournamentEventListeners.add(listener);
        }
    }


    /**
     * Registers a listener .
     *
     * @param  listener
     */
    public void unregisterTournamentEventListener(TournamentEventListener listener) {

        if (tournamentEventListeners.contains(listener)) {
            tournamentEventListeners.remove(listener);
        }
    }


    public void notifyNextRoundPaired(int roundToStart, Tournament updatedTournament) {

        // iterate over a copy of the listeners to enable the listeners to unregister themselves on notifications
        Set<TournamentEventListener> listeners = new HashSet<>(tournamentEventListeners);
        Iterator<TournamentEventListener> iterator = listeners.iterator();

        while (iterator.hasNext()) {
            iterator.next().startRound(roundToStart, updatedTournament);
        }
    }


    public void notifyPairAgain(int roundToStart) {

        // iterate over a copy of the listeners to enable the listeners to unregister themselves on notifications
        Set<TournamentEventListener> listeners = new HashSet<>(tournamentEventListeners);
        Iterator<TournamentEventListener> iterator = listeners.iterator();

        while (iterator.hasNext()) {
            iterator.next().pairRoundAgain(roundToStart);
        }
    }


    public Map<String, PairingOption> getPairingOptionsForTournament(Tournament tournament) {

        Map<String, PairingOption> returnedPairingOptions = new HashMap();
        MapOfPairingConfig pairingConfigs = new MapOfPairingConfig();

        Map<GameOrSportTyp, List<PairingOption>> pairingOptions = pairingConfigs.getPairingOptions();

        for (Map.Entry<GameOrSportTyp, List<PairingOption>> pairingEntry : pairingOptions.entrySet()) {
            if (pairingEntry.getKey().name().equals(GameOrSportTyp.ALL.name())
                    || pairingEntry.getKey().name().equals(tournament.getGameOrSportTyp())) {
                for (PairingOption option : pairingEntry.getValue()) {
                    returnedPairingOptions.put(option.getPairingOptionName(), option);
                }
            }
        }

        return returnedPairingOptions;
    }


    public void notifyPairingChanged(Game game1, Game game2) {

        Set<TournamentEventListener> listeners = new HashSet<>(tournamentEventListeners);
        Iterator<TournamentEventListener> iterator = listeners.iterator();

        while (iterator.hasNext()) {
            iterator.next().pairingChanged(game1, game2);
        }
    }
}
