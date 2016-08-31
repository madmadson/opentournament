package madson.org.opentournament.ongoing;

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

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.utility.BaseActivity;


public class OngoingTournamentActivity extends BaseActivity {

    public static final String EXTRA_TOURNAMENT = "tournament";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private TournamentRoundManagementFragment tournamentRoundManagementFragment;
    private Tournament tournament;

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

        if (tournament != null) {
            Log.i(this.getClass().toString(), "tournament opened with id " + tournament);

            ActionBar supportActionBar = getSupportActionBar();

            if (supportActionBar != null) {
                supportActionBar.setTitle(tournament.getName());
            }

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }
    }


    public TournamentRoundManagementFragment getTournamentRoundManagementFragment() {

        return tournamentRoundManagementFragment;
    }


    public void addRoundAfterNewPairing() {

        Log.i(this.getClass().getName(), "Add tab to view pager");

        mSectionsPagerAdapter.addTabToPager();
        mSectionsPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mSectionsPagerAdapter.getCount());
    }


    public void setRoundTabToRoundNumber(int roundNumber) {

        mViewPager.setCurrentItem(roundNumber);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int amountOfTabs;

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);

            amountOfTabs = tournament.getActualRound() + 1;
        }

        @Override
        public Fragment getItem(int position) {

            Log.i(this.getClass().getName(), "create tournament fragment: " + tournament + " on position: " + position);

            if (position == 0 && checkIfOnlineTournamentAndUserNotCreator()) {
                // show online tournament player list
                return TournamentPlayerListFragment.newInstance(tournament);
            } else if (position == 0) {
                // setup to add players to tournament
                return TournamentSetupFragment.newInstance(tournament);
            } else {
                tournamentRoundManagementFragment = TournamentRoundManagementFragment.newInstance(position, tournament);

                return tournamentRoundManagementFragment;
            }
        }


        private boolean checkIfOnlineTournamentAndUserNotCreator() {

            return tournament.getOnlineUUID() != null
                && !tournament.getCreatorEmail().equals(getCurrentFireBaseUser().getEmail());
        }


        @Override
        public int getCount() {

            return amountOfTabs;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            if (position == 0) {
                return getApplication().getResources().getString(R.string.nav_player_list);
            } else {
                return getApplication().getResources().getString(R.string.nav_round_tab, position);
            }
        }


        public void addTabToPager() {

            amountOfTabs = amountOfTabs + 1;
        }
    }
}
