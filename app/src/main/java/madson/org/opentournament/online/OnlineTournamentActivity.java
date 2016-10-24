package madson.org.opentournament.online;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Activity for tournament organiser.
 */
public class OnlineTournamentActivity extends BaseActivity {

    public static final String EXTRA_TOURNAMENT = "tournament";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private Tournament tournament;
    private ActionBar supportActionBar;
    private DatabaseReference loadReferenceTournament;
    private ValueEventListener onlineTournamentListener;

    @Override
    public boolean useTabLayout() {

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // additionally to the navigation drawer, inflate the base menu into the options menu
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }


    @Override
    public boolean isDisplayHomeAsUp() {

        return true;
    }


    @Override
    public boolean useNavigationDrawer() {

        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        tournament = (Tournament) extras.get(EXTRA_TOURNAMENT);

        Log.i(this.getClass().toString(), "online tournament opened with uuid " + tournament);

        supportActionBar = getSupportActionBar();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        loadOnlineTournament();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        loadReferenceTournament.removeEventListener(onlineTournamentListener);
    }


    private void loadOnlineTournament() {

        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        onlineTournamentListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Tournament tournament = dataSnapshot.getValue(Tournament.class);
                tournament.setUuid(dataSnapshot.getKey());

                if (supportActionBar != null) {
                    supportActionBar.setTitle(tournament.getName());
                }

                mSectionsPagerAdapter.setTournament(tournament);

                if (mViewPager != null) {
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(this.getClass().getName(), "failed to load online players");
            }
        };

        loadReferenceTournament = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENTS + "/"
                + tournament.getGameOrSportTyp() + "/" + tournament.getUUID());

        loadReferenceTournament.addValueEventListener(onlineTournamentListener);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Tournament tournament;
        private String state;
        private int actualRound;

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (state.equals(Tournament.TournamentState.PLANED.name())) {
                return RegisterTournamentPlayerListFragment.newInstance(tournament);
            }

            int round = (position + 1) / 2;

            // list of all players
            if (position == 0) {
                return OnlineTournamentPlayerListFragment.newInstance(tournament);
            }

            if (state.equals(Tournament.TournamentState.FINISHED.name())) {
                if ((actualRound * 2) - 1 == position) {
                    // final standings
                    return OnlineRankingListFragment.newInstance(round, tournament);
                }
            }

            if (position % 2 == 1) {
                return OnlineGamesListFragment.newInstance(round, tournament);
            } else {
                return OnlineRankingListFragment.newInstance(round, tournament);
            }
        }


        @Override
        public int getCount() {

            if (state.equals(Tournament.TournamentState.PLANED.name())) {
                return 1;
            }

            return actualRound * 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            if (state.equals(Tournament.TournamentState.PLANED.name())) {
                return getApplication().getResources().getString(R.string.nav_registration_tab);
            }

            int round = (position + 1) / 2;

            if (position == 0) {
                return getApplication().getResources().getString(R.string.nav_tournament_players);
            } else if (state.equals(Tournament.TournamentState.FINISHED.name())
                    && (((actualRound * 2) - 1) == position)) {
                return getApplication().getResources().getString(R.string.nav_final_standing_tab);
            } else {
                if (position % 2 == 1) {
                    return getApplication().getResources().getString(R.string.nav_games_tab, round);
                } else {
                    return getApplication().getResources().getString(R.string.nav_ranking_tab, (round - 1));
                }
            }
        }


        public Tournament getTournament() {

            return tournament;
        }


        public void setTournament(Tournament tournament) {

            state = tournament.getState();
            actualRound = tournament.getActualRound();

            this.tournament = tournament;
            notifyDataSetChanged();
        }
    }
}
