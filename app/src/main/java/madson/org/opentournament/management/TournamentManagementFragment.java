package madson.org.opentournament.management;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.utility.DrawerLocker;


public class TournamentManagementFragment extends Fragment
    implements TournamentListFragment.OnListFragmentInteractionListener,
        TournamentDetailFragment.OnTournamentEditedListener {

    public static final String TAG = "tournament_management_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        TournamentListFragment tournamentListFragment = new TournamentListFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.left_fragment_container, tournamentListFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
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

                            TournamentDetailFragment tournamentDetailFragment = new TournamentDetailFragment();

                            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                            fragmentTransaction.replace(R.id.right_fragment_container, tournamentDetailFragment);
                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            fragmentTransaction.commit();
                        }
                    });
            }
        }
    }


    @Override
    public void onTournamentListItemClicked(long id) {

        final View view = getView();

        TournamentDetailFragment tournamentDetailFragment = new TournamentDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(TournamentDetailFragment.BUNDLE_TOURNAMENT_ID, id);
        tournamentDetailFragment.setArguments(bundle);

        Log.i("TournamentActivity", "clicked on tournament: " + id);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        if (view.findViewById(R.id.right_fragment_container) == null) {
            ((DrawerLocker) getActivity()).setDrawerEnabled(false);

            fab.hide();
            fragmentTransaction.replace(R.id.left_fragment_container, tournamentDetailFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(R.id.right_fragment_container, tournamentDetailFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onTournamentEditedClicked() {

        final View view = getView();

        TournamentListFragment tournamentListFragment = new TournamentListFragment();

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
