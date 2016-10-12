package madson.org.opentournament.organize.setup;

import android.content.Context;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import madson.org.opentournament.organize.TournamentEventListener;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.TournamentEventTag;


/**
 * Fragment combining list of all players and tournament players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentSetupFragment extends Fragment implements TournamentEventListener {

    private static final String BUNDLE_TOURNAMENT = "tournament";
    private Tournament tournament;

    // both lists of players for adding to tournament
    private AvailablePlayerListFragment availablePlayerListFragment;
    private TournamentPlayerListFragment tournamentPlayerListFragment;

    /**
     * create instance of fragment.
     *
     * @param  tournament  specific tournament
     *
     * @return  new instance
     */
    public static TournamentSetupFragment newInstance(Tournament tournament) {

        TournamentSetupFragment fragment = new TournamentSetupFragment();
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

        View view = inflater.inflate(R.layout.fragment_tournament_setup, container, false);

        tournament = getArguments().getParcelable(BUNDLE_TOURNAMENT);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if (tournament.getState().equals(Tournament.TournamentState.PLANED.name())) {
            createAvailablePlayerListFragment();
            fragmentTransaction.replace(R.id.available_player_fragment_container, availablePlayerListFragment);
        }

        createTournamentPlayerListFragment();
        fragmentTransaction.replace(R.id.tournament_player_fragment_container, tournamentPlayerListFragment);

        Log.i(this.getClass().getName(), "show setup for  tournament " + tournament);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        if (tournament.getState().equals(Tournament.TournamentState.PLANED.name())) {
            FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp);

            floatingActionButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(this.getClass().getName(),
                            "click floatingActionButton player addTournamentPlayer to tournament");

                        AddTournamentPlayerDialog dialog = new AddTournamentPlayerDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = getChildFragmentManager();
                        dialog.show(supportFragmentManager, "tournament setup new player");
                    }
                });
        } else {
            FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();
            floatingActionButton.setVisibility(View.GONE);
        }

        return view;
    }


    private void createAvailablePlayerListFragment() {

        Bundle bundleForAllPlayers = new Bundle();
        bundleForAllPlayers.putParcelable(AvailablePlayerListFragment.BUNDLE_TOURNAMENT, tournament);

        availablePlayerListFragment = new AvailablePlayerListFragment();
        availablePlayerListFragment.setArguments(bundleForAllPlayers);
    }


    private void createTournamentPlayerListFragment() {

        tournamentPlayerListFragment = new TournamentPlayerListFragment();

        Bundle bundleForTournamentPlayers = new Bundle();
        bundleForTournamentPlayers.putParcelable(TournamentPlayerListFragment.BUNDLE_TOURNAMENT, tournament);
        tournamentPlayerListFragment.setArguments(bundleForTournamentPlayers);
    }


    @Override
    public void addTournamentPlayer(TournamentPlayer tournamentPlayer) {
    }


    @Override
    public void removeTournamentPlayer(final TournamentPlayer tournamentPlayer) {
    }


    @Override
    public void addPlayerToTournament(Player player) {
    }


    @Override
    public void removeAvailablePlayer(Player player) {
    }


    @Override
    public void updateTournamentPlayer(TournamentPlayer updatedPLayer, String oldTeamName) {
    }


    @Override
    public void addRegistration(TournamentPlayer player) {
    }


    @Override
    public void startRound(int roundToStart, Tournament tournament) {

        if (roundToStart == 2) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            fragmentTransaction.remove(availablePlayerListFragment);
            fragmentTransaction.commit();
        }

        tournamentPlayerListFragment.startButtonInvisible();
    }


    @Override
    public void pairRoundAgain(int round_for_pairing) {
    }


    @Override
    public void pairingChanged(Game game1, Game game2) {
    }


    @Override
    public void enterGameResultConfirmed(TournamentEventTag tag, Game game) {
    }
}
