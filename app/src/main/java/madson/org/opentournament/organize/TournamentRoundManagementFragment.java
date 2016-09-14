package madson.org.opentournament.organize;

import android.content.Context;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.utility.BaseActivity;


public class TournamentRoundManagementFragment extends Fragment implements GameListFragment.GameResultEnteredListener,
    TournamentEventListener {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND = "round";

    private Tournament tournament;
    private int round;

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
    public void onAttach(Context context) {

        super.onAttach(context);
        ((BaseActivity) getActivity()).getBaseApplication().registerTournamentEventListener(this);
    }


    @Override
    public void onDetach() {

        super.onDetach();

        ((BaseActivity) getActivity()).getBaseApplication().unregisterTournamentEventListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ongoing_tournament_management, container, false);

        round = getArguments().getInt(BUNDLE_ROUND);
        tournament = getArguments().getParcelable(BUNDLE_TOURNAMENT);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        Log.i(this.getClass().getName(), "show round " + round + " of tournament " + tournament);

        showRound(round, fragmentTransaction);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        return view;
    }


    private GameListFragment createPairingListFragment(int round) {

        gameListFragment = new GameListFragment();

        Bundle bundleForPairing = new Bundle();
        bundleForPairing.putParcelable(GameListFragment.BUNDLE_TOURNAMENT, tournament);
        bundleForPairing.putInt(GameListFragment.BUNDLE_ROUND, round);
        gameListFragment.setArguments(bundleForPairing);

        return gameListFragment;
    }


    private RankingListFragment createRankingListFragment(int round) {

        rankingForRoundListFragment = new RankingListFragment();

        Bundle bundleForRanking = new Bundle();
        bundleForRanking.putParcelable(RankingListFragment.BUNDLE_TOURNAMENT, tournament);
        bundleForRanking.putInt(RankingListFragment.BUNDLE_ROUND, round);
        rankingForRoundListFragment.setArguments(bundleForRanking);

        return rankingForRoundListFragment;
    }


    private void showRound(int round, FragmentTransaction fragmentTransaction) {

        GameListFragment gameListFragment = createPairingListFragment(round);
        fragmentTransaction.replace(R.id.left_fragment_container, gameListFragment);

        RankingListFragment rankingListFragment = createRankingListFragment(round);
        fragmentTransaction.replace(R.id.right_fragment_container, rankingListFragment);
    }


    @Override
    public void onResultConfirmed(Game game) {

        Log.i(this.getClass().getName(), "game result entered: " + game);

        if (gameListFragment != null) {
            gameListFragment.updateGameInList(game);
        }
    }


    @Override
    public void startRound(int roundToStart) {

        // nothing
    }


    @Override
    public void pairRoundAgain(int roundPairedAgainFor) {

        if (roundPairedAgainFor == round) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            showRound(roundPairedAgainFor, fragmentTransaction);

            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }
}
