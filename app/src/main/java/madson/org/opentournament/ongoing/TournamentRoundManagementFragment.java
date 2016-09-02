package madson.org.opentournament.ongoing;

import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.warmachine.Game;
import madson.org.opentournament.players.AvailablePlayerListFragment;
import madson.org.opentournament.players.TournamentPlayerListFragment;


public class TournamentRoundManagementFragment extends Fragment implements GameListFragment.GameResultEnteredListener {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND = "round";

    private Tournament tournament;
    private int round;

    private TournamentPlayerListFragment tournamentPlayerListFragment;
    private AvailablePlayerListFragment availablePlayerListFragment;
    private RoundChangeButtonFragment nextRoundButtonFragment;
    private RoundChangeButtonFragment previousRoundFragment;
    private GameListFragment gameListFragment;
    private RankingListFragment rankingForRoundListFragment;

    public static TournamentRoundManagementFragment newInstance(int roundNumber, Tournament tournament) {

        TournamentRoundManagementFragment fragment = new TournamentRoundManagementFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ROUND, roundNumber);
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ongoing_tournament_management, container, false);

        round = getArguments().getInt(BUNDLE_ROUND);
        tournament = getArguments().getParcelable(BUNDLE_TOURNAMENT);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        Log.i(this.getClass().getName(), "show round " + round + " of tournament " + tournament);

        if (round == 0) {
            showPlayerList(fragmentTransaction);
        } else {
            showRound(round, fragmentTransaction);
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        return view;
    }


    private void showPlayerList(FragmentTransaction fragmentTransaction) {

        createAvailablePlayerListFragment();
        fragmentTransaction.replace(R.id.left_fragment_container, availablePlayerListFragment);

        createTournamentPlayerListFragment();
        fragmentTransaction.replace(R.id.right_fragment_container, tournamentPlayerListFragment);

        nextRoundButtonFragment = createNextRoundFragment(0);
        fragmentTransaction.replace(R.id.next_round_container, nextRoundButtonFragment);

        if (previousRoundFragment != null) {
            fragmentTransaction.detach(previousRoundFragment);
        }
    }


    private void createTournamentPlayerListFragment() {

        tournamentPlayerListFragment = new TournamentPlayerListFragment();

        Bundle bundleForTournamentPlayers = new Bundle();
        bundleForTournamentPlayers.putParcelable(TournamentPlayerListFragment.BUNDLE_TOURNAMENT, tournament);
        tournamentPlayerListFragment.setArguments(bundleForTournamentPlayers);
    }


    private void createAvailablePlayerListFragment() {

        Bundle bundleForAllPlayers = new Bundle();
        bundleForAllPlayers.putParcelable(AvailablePlayerListFragment.BUNDLE_TOURNAMENT, tournament);

        availablePlayerListFragment = new AvailablePlayerListFragment();
        availablePlayerListFragment.setArguments(bundleForAllPlayers);
    }


    @NonNull
    private RoundChangeButtonFragment createPreviousRoundFragment(int round) {

        previousRoundFragment = new RoundChangeButtonFragment();

        Bundle bundleForPreviousRound = new Bundle();
        bundleForPreviousRound.putParcelable(RoundChangeButtonFragment.BUNDLE_TOURNAMENT, tournament);
        bundleForPreviousRound.putInt(RoundChangeButtonFragment.BUNDLE_ROUND_TO_DISPLAY, (round - 1));
        bundleForPreviousRound.putString(RoundChangeButtonFragment.BUNDLE_NEXT_OR_PREVIOUS,
            RoundChangeButtonFragment.NextOrPrevious.PREVIOUS.name());
        previousRoundFragment.setArguments(bundleForPreviousRound);

        return previousRoundFragment;
    }


    @NonNull
    private RoundChangeButtonFragment createNextRoundFragment(int round) {

        nextRoundButtonFragment = new RoundChangeButtonFragment();

        Bundle nextRoundButtonBundle = new Bundle();
        nextRoundButtonBundle.putParcelable(RoundChangeButtonFragment.BUNDLE_TOURNAMENT, tournament);
        nextRoundButtonBundle.putInt(RoundChangeButtonFragment.BUNDLE_ROUND_TO_DISPLAY, (round + 1));
        nextRoundButtonBundle.putString(RoundChangeButtonFragment.BUNDLE_NEXT_OR_PREVIOUS,
            RoundChangeButtonFragment.NextOrPrevious.NEXT.name());
        nextRoundButtonFragment.setArguments(nextRoundButtonBundle);

        return nextRoundButtonFragment;
    }


    private GameListFragment createPairingListFragment(int round) {

        gameListFragment = new GameListFragment();

        Bundle bundleForPairing = new Bundle();
        bundleForPairing.putLong(GameListFragment.BUNDLE_TOURNAMENT, tournament.get_id());
        bundleForPairing.putInt(GameListFragment.BUNDLE_ROUND, round);
        gameListFragment.setArguments(bundleForPairing);

        return gameListFragment;
    }


    private RankingListFragment createRankingListFragment(int round) {

        rankingForRoundListFragment = new RankingListFragment();

        Bundle bundleForRanking = new Bundle();
        bundleForRanking.putLong(RankingListFragment.BUNDLE_TOURNAMENT, tournament.get_id());
        bundleForRanking.putInt(RankingListFragment.BUNDLE_ROUND, round);
        rankingForRoundListFragment.setArguments(bundleForRanking);

        return rankingForRoundListFragment;
    }


    private void showRound(int round, FragmentTransaction fragmentTransaction) {

        RoundChangeButtonFragment nextRoundFragment = createNextRoundFragment(round);
        fragmentTransaction.replace(R.id.next_round_container, nextRoundFragment);

        GameListFragment gameListFragment = createPairingListFragment(round);
        fragmentTransaction.replace(R.id.left_fragment_container, gameListFragment);

        RankingListFragment rankingListFragment = createRankingListFragment(round);
        fragmentTransaction.replace(R.id.right_fragment_container, rankingListFragment);

        RoundChangeButtonFragment previousRoundFragment = createPreviousRoundFragment(round);
        fragmentTransaction.replace(R.id.previous_round_container, previousRoundFragment);

        View fab = getActivity().findViewById(R.id.fab);

        if (fab != null) {
            if (round == 0) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.INVISIBLE);
            }
        }
    }


    @Override
    public void onResultConfirmed(Game game) {

        Log.i(this.getClass().getName(), "game result entered: " + game);

        if (gameListFragment != null) {
            gameListFragment.updateGameInList(game);
        }
    }
}
