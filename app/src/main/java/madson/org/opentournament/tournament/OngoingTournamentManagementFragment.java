package madson.org.opentournament.tournament;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.management.TournamentDetailFragment;
import madson.org.opentournament.management.TournamentListFragment;
import madson.org.opentournament.players.PlayerListFragment;
import madson.org.opentournament.utility.DrawerLocker;


public class OngoingTournamentManagementFragment extends Fragment {

    public static final String TAG = "ongoing_tournament_management_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_ongoing_tournament_management, container, false);
    }


    @Override
    public void onStart() {

        super.onStart();

        final View view = getView();

        if (view != null) {
            PlayerListFragment playerListFragment = new PlayerListFragment();

            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.left_fragment_container, playerListFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();

//            if (view.findViewById(R.id.right_fragment_container) != null) {
//                PlayerListFragment playerListFragment2 = new PlayerListFragment();
//
//                fragmentTransaction.replace(R.id.right_fragment_container, playerListFragment2);
//                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                fragmentTransaction.commit();
//            }
        }
    }
}
