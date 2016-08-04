package madson.org.opentournament.tournament;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.management.TournamentDetailFragment;
import madson.org.opentournament.players.AvailablePlayerListFragment;
import madson.org.opentournament.service.TournamentService;


public class OngoingTournamentManagementFragment extends Fragment
    implements AvailablePlayerListFragment.AvailablePlayerListItemListener,
        TournamentPlayerListFragment.TournamentPlayerListItemListener, NextRoundFragment.NextRoundEventListener {

    public static final String TAG = "ongoing_tournament_management_fragment";
    private static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    private Tournament tournament;

    private TournamentPlayerListFragment tournamentPlayerListFragment;
    private AvailablePlayerListFragment availablePlayerListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            long aLong = bundle.getLong(BUNDLE_TOURNAMENT_ID);
            TournamentService tournamentService = ((OpenTournamentApplication) getActivity().getApplication())
                .getTournamentService();
            tournament = tournamentService.getTournamentForId(aLong);
        }

        return inflater.inflate(R.layout.fragment_ongoing_tournament_management, container, false);
    }


    @Override
    public void onStart() {

        super.onStart();

        final View view = getView();

        if (view != null) {
            Bundle bundleForAllPlayers = new Bundle();
            bundleForAllPlayers.putLong(AvailablePlayerListFragment.BUNDLE_TOURNAMENT_ID, tournament.getId());

            availablePlayerListFragment = new AvailablePlayerListFragment();
            availablePlayerListFragment.setArguments(bundleForAllPlayers);

            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.left_fragment_container, availablePlayerListFragment);

            if (view.findViewById(R.id.right_fragment_container) != null) {
                tournamentPlayerListFragment = new TournamentPlayerListFragment();

                Bundle bundleForTournamentPlayers = new Bundle();
                bundleForTournamentPlayers.putLong(TournamentDetailFragment.BUNDLE_TOURNAMENT_ID, tournament.getId());
                tournamentPlayerListFragment.setArguments(bundleForTournamentPlayers);

                fragmentTransaction.replace(R.id.right_fragment_container, tournamentPlayerListFragment);
            }

            NextRoundFragment nextRoundFragment = new NextRoundFragment();
            Bundle bundleForNextRound = new Bundle();
            bundleForNextRound.putLong(NextRoundFragment.BUNDLE_TOURNAMENT_ID, tournament.getId());
            nextRoundFragment.setArguments(bundleForNextRound);

            fragmentTransaction.replace(R.id.next_round_container, nextRoundFragment);

            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onAvailablePlayerListItemClicked(Player player) {

        Log.i(this.getClass().getName(), "clicked on available player to add: " + player);

        if (tournamentPlayerListFragment != null) {
            tournamentPlayerListFragment.addPlayer(player);
        }
    }


    @Override
    public void onTournamentPlayerListItemClicked(Player player) {

        Log.i(this.getClass().getName(), "clicked on tournament player to remove: " + player);

        if (availablePlayerListFragment != null) {
            availablePlayerListFragment.addPlayer(player);
        }

        // for the heading
        if (tournamentPlayerListFragment != null) {
            tournamentPlayerListFragment.removePlayer();
        }
    }


    @Override
    public void onNextRound(int round) {

        Log.i(this.getClass().getName(), "clicked next round: " + round);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        PairingListFragment pairingListFragment = new PairingListFragment();

        Bundle bundleForPairing = new Bundle();
        bundleForPairing.putLong(PairingListFragment.BUNDLE_TOURNAMENT_ID, tournament.getId());
        bundleForPairing.putInt(PairingListFragment.BUNDLE_ROUND, round);
        pairingListFragment.setArguments(bundleForPairing);

        fragmentTransaction.replace(R.id.left_fragment_container, pairingListFragment);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        fragmentTransaction.detach(tournamentPlayerListFragment);

        if (round != 0) {
            View fab = getActivity().findViewById(R.id.fab);

            if (fab != null) {
                fab.setVisibility(View.INVISIBLE);
            }
        }
    }
}