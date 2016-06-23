package madson.org.opentournament;

import android.app.Activity;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.View;

import android.widget.CursorAdapter;


public class TournamentManagementActivity extends AppCompatActivity
    implements TournamentListFragment.OnListFragmentInteractionListener,
        TournamentDetailFragment.OnTournamentEditedListener {

    private CursorAdapter cursorAdapter;
    private Cursor cursor;
    private SQLiteDatabase readableDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournaments);

        TournamentListFragment tournamentListFragment = new TournamentListFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.list_fragment_container, tournamentListFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

                // new empty tournament
                @Override
                public void onClick(View view) {

                    if (findViewById(R.id.detail_fragment_container) != null) {
                        TournamentDetailFragment tournamentDetailFragment = new TournamentDetailFragment();

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                        fragmentTransaction.replace(R.id.detail_fragment_container, tournamentDetailFragment);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.commit();
                    }
                }
            });
    }


    @Override
    public void onTournamentListItemClicked(long id) {

        if (findViewById(R.id.detail_fragment_container) != null) {
            TournamentDetailFragment tournamentDetailFragment = new TournamentDetailFragment();

            Log.i("TounamentActivity", "clicked on tournament: " + id);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

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

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.list_fragment_container, tournamentListFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }
}
