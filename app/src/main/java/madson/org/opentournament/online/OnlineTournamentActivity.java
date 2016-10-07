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
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.organize.TournamentRoundManagementFragment;
import madson.org.opentournament.organize.setup.TournamentSetupFragment;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Activity for tournament organiser.
 */
public class OnlineTournamentActivity extends BaseActivity {

    public static final String EXTRA_TOURNAMENT_UUID = "tournament_uuid";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private String tournament_uuid;
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

        tournament_uuid = (String) extras.get(EXTRA_TOURNAMENT_UUID);

        Log.i(this.getClass().toString(), "online tournament opened with uuid " + tournament_uuid);

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
                tournament.setOnlineUUID(tournament_uuid);

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
                + getBaseApplication().getSelectedGameOrSportTyp() + "/" + tournament_uuid);

        loadReferenceTournament.addValueEventListener(onlineTournamentListener);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Tournament tournament;

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (tournament.getState().equals(Tournament.TournamentState.PLANED.name())) {
                return RegisterTournamentPlayerListFragment.newInstance(tournament);
            }

            int round = (position + 1) / 2;

            // list of all players
            if (position == 0) {
                if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                    return OnlineTournamentPlayerListFragment.newInstance(tournament_uuid);
                } else {
                    return OnlineTournamentTeamListFragment.newInstance(tournament_uuid);
                }
            } else if (tournament.getState().equals(Tournament.TournamentState.FINISHED.name())
                    && tournament.getActualRound() == round) {
                return OnlineRankingListFragment.newInstance(round, tournament_uuid);
            }

            if (position % 2 == 1) {
                return OnlineGamesListFragment.newInstance(round, tournament_uuid);
            } else {
                return OnlineRankingListFragment.newInstance(round, tournament_uuid);
            }
        }


        @Override
        public int getCount() {

            if (tournament.getState().equals(Tournament.TournamentState.PLANED.name())) {
                return 1;
            }

            if (tournament.getState().equals(Tournament.TournamentState.FINISHED.name())) {
                return (tournament.getActualRound() * 2);
            } else {
                return (tournament.getActualRound() * 2) + 1;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {

            if (tournament.getState().equals(Tournament.TournamentState.PLANED.name())) {
                return getApplication().getResources().getString(R.string.nav_registration_tab);
            }

            int round = (position + 1) / 2;

            if (position == 0) {
                return getApplication().getResources().getString(R.string.nav_setup_tab);
            } else if (tournament.getState().equals(Tournament.TournamentState.FINISHED.name())
                    && tournament.getActualRound() == round) {
                return getApplication().getResources().getString(R.string.nav_final_standing_tab);
            } else {
                if (position % 2 == 1) {
                    return getApplication().getResources().getString(R.string.nav_games_tab, round);
                } else {
                    return getApplication().getResources().getString(R.string.nav_ranking_tab, round);
                }
            }
        }


        public Tournament getTournament() {

            return tournament;
        }


        public void setTournament(Tournament tournament) {

            this.tournament = tournament;
            notifyDataSetChanged();
        }
    }
}
