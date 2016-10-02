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
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.BaseActivity;


public class TournamentFinalStandingFragment extends Fragment implements TournamentEventListener {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private Tournament tournament;

    private RankingListFragment rankingForRoundListFragment;

    public static TournamentFinalStandingFragment newInstance(Tournament tournament) {

        TournamentFinalStandingFragment fragment = new TournamentFinalStandingFragment();
        Bundle args = new Bundle();
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

        View view = inflater.inflate(R.layout.fragment_final_standings, container, false);

        tournament = getArguments().getParcelable(BUNDLE_TOURNAMENT);

        rankingForRoundListFragment = new RankingListFragment();

        Bundle bundleForRanking = new Bundle();
        bundleForRanking.putParcelable(RankingListFragment.BUNDLE_TOURNAMENT, tournament);
        bundleForRanking.putInt(RankingListFragment.BUNDLE_ROUND, tournament.getActualRound());
        rankingForRoundListFragment.setArguments(bundleForRanking);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.final_standings_container, rankingForRoundListFragment);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        return view;
    }


    @Override
    public void enterGameResultConfirmed(Game game) {

        // update when correcting games
    }


    @Override
    public void addTournamentPlayer(TournamentPlayer tournamentPlayer) {
    }


    @Override
    public void removeTournamentPlayer(TournamentPlayer tournamentPlayer) {
    }


    @Override
    public void addPlayerToTournament(Player player) {
    }


    @Override
    public void removeAvailablePlayer(Player player) {
    }


    @Override
    public void updateTournamentPlayer(TournamentPlayer updatedPLayer) {
    }


    @Override
    public void addRegistration(TournamentPlayer player) {
    }


    @Override
    public void startRound(int roundToStart, Tournament tournament) {

        // nothing
    }


    @Override
    public void pairRoundAgain(int roundPairedAgainFor) {

        // nothing
    }


    @Override
    public void pairingChanged(Game game1, Game game2) {

        // nothing
    }
}
