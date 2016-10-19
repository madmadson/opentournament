package madson.org.opentournament.organize;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.events.OpenTournamentEvent;
import madson.org.opentournament.events.OpenTournamentEventListener;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.PairRoundAgainEvent;
import madson.org.opentournament.utility.BaseActivity;


public class TournamentRoundManagementFragment extends Fragment implements OpenTournamentEventListener {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND = "round";

    private Tournament tournament;
    private int round;

    private GameListFragment gameListFragment;
    private RankingListFragment rankingForRoundListFragment;
    private BaseActivity baseActivity;

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
        baseActivity = ((BaseActivity) getActivity());
        baseActivity.getBaseApplication().registerTournamentEventListener(this);
    }


    @Override
    public void onDetach() {

        super.onDetach();

        baseActivity.getBaseApplication().unregisterTournamentEventListener(this);
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

        getActivity().findViewById(R.id.fab).setVisibility(View.GONE);

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
    public void handleEvent(OpenTournamentEventTag eventTag, OpenTournamentEvent parameter) {

        if (OpenTournamentEventTag.PAIR_ROUND_AGAIN.equals(eventTag)) {
            PairRoundAgainEvent pairRoundAgainEvent = (PairRoundAgainEvent) parameter;

            if (pairRoundAgainEvent.getRound() == round) {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                showRound(pairRoundAgainEvent.getRound(), fragmentTransaction);

                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        }
    }
}
