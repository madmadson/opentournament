package madson.org.opentournament.utility;

import android.app.Application;

import android.content.Context;

import android.net.ConnectivityManager;

import android.util.Log;

import com.facebook.FacebookSdk;

import com.facebook.appevents.AppEventsLogger;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.about.AppInfo;
import madson.org.opentournament.about.LibraryItem;
import madson.org.opentournament.config.MapOfPairingConfig;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.events.OpenTournamentEvent;
import madson.org.opentournament.events.OpenTournamentEventListener;
import madson.org.opentournament.events.OpenTournamentEventTag;
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

    private Set<OpenTournamentEventListener> tournamentEventListeners = new HashSet<>();
    private Player authenticatedPlayer;

    private GameOrSportTyp selectedGameOrSportTyp;

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
        AppEventsLogger.activateApp(this);

        if (isOnline()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            DatabaseReference tournaments = FirebaseDatabase.getInstance().getReference(FirebaseReferences.TOURNAMENTS);
            tournaments.keepSynced(true);

            DatabaseReference players = FirebaseDatabase.getInstance().getReference(FirebaseReferences.PLAYERS);
            players.keepSynced(true);

            DatabaseReference registrations = FirebaseDatabase.getInstance()
                    .getReference(FirebaseReferences.TOURNAMENT_REGISTRATIONS);
            registrations.keepSynced(true);

            DatabaseReference armyLists = FirebaseDatabase.getInstance()
                    .getReference(FirebaseReferences.TOURNAMENT_ARMY_LISTS);
            armyLists.keepSynced(true);

            DatabaseReference playerTournaments = FirebaseDatabase.getInstance()
                    .getReference(FirebaseReferences.PLAYER_TOURNAMENTS);
            playerTournaments.keepSynced(true);
        }

        // NOTE: order of service wiring matters!

        if (playerService == null) {
            if (getEnvironment() == Environment.DEV) {
                playerService = new PlayerServiceImpl(getApplicationContext());
            } else {
                playerService = new PlayerServiceImpl(getApplicationContext());
            }
        }

        if (tournamentService == null) {
            if (getEnvironment() == Environment.DEV) {
                tournamentService = new TournamentServiceImpl(getApplicationContext());
            } else {
                tournamentService = new TournamentServiceImpl(getApplicationContext());
            }
        }

        if (tournamentPlayerService == null) {
            if (getEnvironment() == Environment.DEV) {
                tournamentPlayerService = new TournamentPlayerServiceImpl(getApplicationContext());
            } else {
                tournamentPlayerService = new TournamentPlayerServiceImpl(getApplicationContext());
            }
        }

        // depend on tournamentPlayerService -> to do ranking -_-
        if (rankingService == null) {
            if (getEnvironment() == Environment.DEV) {
                rankingService = new RankingServiceImpl(getApplicationContext());
            } else {
                rankingService = new RankingServiceImpl(getApplicationContext());
            }
        }

        // depend on tournament service, player service, ranking -> must pair games
        if (ongoingTournamentService == null) {
            if (getEnvironment() == Environment.DEV) {
                ongoingTournamentService = new OngoingTournamentServiceImpl(getApplicationContext());
            } else {
                ongoingTournamentService = new OngoingTournamentServiceImpl(getApplicationContext());
            }
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadCurrentUserInfo(user);
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


    public Player getAuthenticatedPlayer() {

        return authenticatedPlayer;
    }


    public void updateAuthenticatedUser() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadCurrentUserInfo(user);
        }
    }


    private void loadCurrentUserInfo(FirebaseUser user) {

        final DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYERS + "/" + user.getUid());

        child.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    authenticatedPlayer = dataSnapshot.getValue(Player.class);
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
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
    public void registerTournamentEventListener(OpenTournamentEventListener listener) {

        if (!tournamentEventListeners.contains(listener)) {
            tournamentEventListeners.add(listener);
        }
    }


    /**
     * Registers a listener .
     *
     * @param  listener
     */
    public void unregisterTournamentEventListener(OpenTournamentEventListener listener) {

        if (tournamentEventListeners.contains(listener)) {
            tournamentEventListeners.remove(listener);
        }
    }


    public void notifyTournamentEvent(OpenTournamentEventTag eventTag, OpenTournamentEvent parameter) {

        Set<OpenTournamentEventListener> listeners = new HashSet<>(tournamentEventListeners);

        for (OpenTournamentEventListener listener : listeners) {
            listener.handleEvent(eventTag, parameter);
        }
    }


    public Map<String, PairingOption> getPairingOptionsForTournament(Tournament tournament) {

        Map<String, PairingOption> returnedPairingOptions = new HashMap<>();
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


    public String getSelectedGameOrSportTyp() {

        if (selectedGameOrSportTyp == null) {
            return GameOrSportTyp.WARMACHINE_SOLO.name();
        }

        return selectedGameOrSportTyp.name();
    }


    public void setSelectedGameOrSportTyp(GameOrSportTyp selectedGameOrSportTyp) {

        this.selectedGameOrSportTyp = selectedGameOrSportTyp;
    }
}
