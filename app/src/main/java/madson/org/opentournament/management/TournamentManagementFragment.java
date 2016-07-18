package madson.org.opentournament.management;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;

import madson.org.opentournament.R;


public class TournamentManagementFragment extends Fragment
    implements TournamentListFragment.OnListFragmentInteractionListener,
        TournamentDetailFragment.OnTournamentEditedListener {

    public static final String TAG = "tournament_management_fragment";
    private CursorAdapter cursorAdapter;
    private Cursor cursor;
    private SQLiteDatabase readableDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        TournamentListFragment tournamentListFragment = new TournamentListFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.list_fragment_container, tournamentListFragment);
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
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {

                    // new empty tournament
                    @Override
                    public void onClick(View view) {

                        TournamentDetailFragment tournamentDetailFragment = new TournamentDetailFragment();

                        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

                        fragmentTransaction.replace(R.id.detail_fragment_container, tournamentDetailFragment);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.commit();
                    }
                });
        }
    }


    @Override
    public void onTournamentListItemClicked(long id) {

        final View view = getView();

        if (view.findViewById(R.id.detail_fragment_container) != null) {
            TournamentDetailFragment tournamentDetailFragment = new TournamentDetailFragment();

            Log.i("TounamentActivity", "clicked on tournament: " + id);

            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            Bundle bundle = new Bundle();
            bundle.putLong("tournamentId", id);
            tournamentDetailFragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.detail_fragment_container, tournamentDetailFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onTournamentEditedClicked() {

        TournamentListFragment tournamentListFragment = new TournamentListFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.list_fragment_container, tournamentListFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }
}
