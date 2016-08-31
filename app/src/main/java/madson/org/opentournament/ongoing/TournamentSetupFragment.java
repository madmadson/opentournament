package madson.org.opentournament.ongoing;

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
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.players.AddLocalPlayerToTournamentDialog;
import madson.org.opentournament.players.AvailablePlayerListFragment;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Fragment combining list of all players and tournament players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentSetupFragment extends Fragment
    implements AddLocalPlayerToTournamentDialog.TournamentPlayerAddListener,
        AvailablePlayerListFragment.AvailablePlayerListItemListener {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tournament_setup, container, false);

        tournament = getArguments().getParcelable(BUNDLE_TOURNAMENT);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        createAvailablePlayerListFragment();
        fragmentTransaction.replace(R.id.left_fragment_container, availablePlayerListFragment);

        createTournamentPlayerListFragment();
        fragmentTransaction.replace(R.id.right_fragment_container, tournamentPlayerListFragment);

        Log.i(this.getClass().getName(), "show setup for  tournament " + tournament);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "click floatingActionButton player add to tournament");

                    AddLocalPlayerToTournamentDialog dialog = new AddLocalPlayerToTournamentDialog();
                    dialog.setTargetFragment(TournamentSetupFragment.this, 1);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AddLocalPlayerToTournamentDialog.BUNDLE_TOURNAMENT, tournament);
                    dialog.setArguments(bundle);

                    FragmentManager supportFragmentManager = getChildFragmentManager();
                    dialog.show(supportFragmentManager, "tournament setup new player");
                }
            });

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

        Log.i(this.getClass().getName(), "manually player added to tournament");
    }


    @Override
    public void onAvailablePlayerListItemClicked(Player player) {

        Log.i(this.getClass().getName(), "clicked on available player to add: " + player);

        if (tournamentPlayerListFragment != null) {
            tournamentPlayerListFragment.addPlayer(player);
        }
    }
}
