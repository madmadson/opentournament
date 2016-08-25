package madson.org.opentournament.tournaments;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.ongoing.RankingListFragment;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.utility.DrawerLocker;


public class TournamentManagementFragment extends Fragment
    implements TournamentListsFragment.TournamentListItemListener, TournamentDetailFragment.OnTournamentEditedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        TournamentListsFragment tournamentListFragment = new TournamentListsFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.left_fragment_container, tournamentListFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_tournament_management);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tournament_management, container, false);
    }


    @Override
    public void onStart() {

        super.onStart();

        final View view = getView();

        if (view != null) {
            final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

            if (view.findViewById(R.id.right_fragment_container) == null) {
                fab.setOnClickListener(new View.OnClickListener() {

                        // new empty tournament
                        @Override
                        public void onClick(View view) {

                            TournamentDetailFragment tournamentDetailFragment = new TournamentDetailFragment();

                            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                            ((DrawerLocker) getActivity()).setDrawerEnabled(false);

                            fab.hide();

                            fragmentTransaction.replace(R.id.left_fragment_container, tournamentDetailFragment);
                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            fragmentTransaction.commit();
                        }
                    });
            } else {
                fab.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Log.i(this.getClass().getName(), "click fab tournament management");

                            TournamentManagementDialog dialog = new TournamentManagementDialog();

                            FragmentManager supportFragmentManager = getChildFragmentManager();
                            dialog.show(supportFragmentManager, "tournament management new tournament");
                        }
                    });
            }
        }
    }


    @Override
    public void onTournamentListItemClicked(long id) {

        final View view = getView();

        // get actual tournament to get actual state
        TournamentService tournamentService = ((BaseApplication) getActivity().getApplication()).getTournamentService();
        Tournament tournament = tournamentService.getTournamentForId(id);

        RankingListFragment rankingListFragment = new RankingListFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(RankingListFragment.BUNDLE_TOURNAMENT_ID, id);

        bundle.putInt(RankingListFragment.BUNDLE_ROUND, tournament.getActualRound());
        rankingListFragment.setArguments(bundle);

        Log.i(this.getClass().getName(), "clicked on tournament: " + tournament);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        if (view.findViewById(R.id.right_fragment_container) == null) {
            ((DrawerLocker) getActivity()).setDrawerEnabled(false);

            fab.hide();
            fragmentTransaction.replace(R.id.left_fragment_container, rankingListFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(R.id.right_fragment_container, rankingListFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onOnlineTournamentListItemClicked(String onlineUUID) {

        Log.i(this.getClass().getName(), "clicked on tournament: " + onlineUUID);
    }


    @Override
    public void onTournamentEditedClicked() {

        final View view = getView();

        TournamentListsFragment tournamentListFragment = new TournamentListsFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        if (view.findViewById(R.id.right_fragment_container) == null) {
            ((DrawerLocker) getActivity()).setDrawerEnabled(false);

            fab.hide();
        }

        fragmentTransaction.replace(R.id.left_fragment_container, tournamentListFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }
}
