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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.TournamentRoundManagementFragment;
import madson.org.opentournament.organize.setup.TournamentPlayerListFragment;
import madson.org.opentournament.organize.setup.TournamentSetupFragment;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Activity for tournament organiser.
 */
public class OnlineTournamentActivity extends BaseActivity {

    public static final String EXTRA_TOURNAMENT_UUID = "tournament_uuid";
    public static final String EXTRA_TOURNAMENT_GAME_OR_SPORT_TYP = "tournament_game_or_sport_typ";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private DatabaseReference mFirebaseDatabaseReference;

    private TournamentRoundManagementFragment tournamentRoundManagementFragment;
    private TournamentSetupFragment tournamentSetupFragment;
    private String tournament_game_or_sport_typ;
    private String tournament_uuid;
    private ActionBar supportActionBar;

    @Override
    public boolean useTabLayout() {

        return true;
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
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
        tournament_game_or_sport_typ = (String) extras.get(EXTRA_TOURNAMENT_GAME_OR_SPORT_TYP);

        Log.i(this.getClass().toString(), "online tournament opened with uuid " + tournament_uuid);

        supportActionBar = getSupportActionBar();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        loadOnlineTournament();
    }


    private void loadOnlineTournament() {

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        final ValueEventListener tournamentListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Tournament tournament = dataSnapshot.getValue(Tournament.class);

                if (supportActionBar != null) {
                    if (tournament != null) {
                        supportActionBar.setTitle(tournament.getName());
                    }
                }

                mSectionsPagerAdapter.setTournament(tournament);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(this.getClass().getName(), "failed to load online players");
            }
        };

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENTS + "/"
                + tournament_game_or_sport_typ + "/" + tournament_uuid);

        child.addValueEventListener(tournamentListener);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Tournament tournament;

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            int round = (position + 1) / 2;

            // list of all players
            if (position == 0) {
                return OnlineTournamentPlayerListFragment.newInstance(tournament_uuid);
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

            if (tournament != null) {
                if (tournament.getState().equals(Tournament.TournamentState.FINISHED.name())) {
                    return (tournament.getActualRound() * 2);
                } else {
                    return (tournament.getActualRound() * 2) + 1;
                }
            } else {
                return 1;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {

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
