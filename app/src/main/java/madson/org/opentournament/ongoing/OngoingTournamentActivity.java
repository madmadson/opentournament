package madson.org.opentournament.ongoing;

import android.content.Intent;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.players.NewPlayerForTournamentDialog;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;


public class OngoingTournamentActivity extends BaseActivity {

    public static final String EXTRA_TOURNAMENT_ID = "tournament_id";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private OngoingTournamentManagementFragment ongoingTournamentManagementFragment;
    private long tournamentId;
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

        tournamentId = (long) extras.get(EXTRA_TOURNAMENT_ID);

        if (tournamentId != 0) {
            Log.i(this.getClass().toString(), "tournament started with id " + tournamentId);

            TournamentService tournamentService = ((BaseApplication) getApplication()).getTournamentService();
            tournament = tournamentService.getTournamentForId(tournamentId);

            ActionBar supportActionBar = getSupportActionBar();

            if (supportActionBar != null) {
                supportActionBar.setTitle(tournament.getName());
            }

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Log.i(this.getClass().getName(), "click fab ongoing tournament");

                        NewPlayerForTournamentDialog dialog = new NewPlayerForTournamentDialog();

                        Bundle bundleForTournamentPlayers = new Bundle();
                        bundleForTournamentPlayers.putLong(NewPlayerForTournamentDialog.BUNDLE_TOURNAMENT_ID,
                            tournamentId);
                        dialog.setArguments(bundleForTournamentPlayers);

                        FragmentManager supportFragmentManager = getSupportFragmentManager();
                        dialog.show(supportFragmentManager, "NewPlayerForTournamentDialog");
                    }
                });
        }
    }


    public OngoingTournamentManagementFragment getOngoingTournamentManagementFragment() {

        return ongoingTournamentManagementFragment;
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
            ongoingTournamentManagementFragment = OngoingTournamentManagementFragment.newInstance(position,
                    tournament.get_id());

            return ongoingTournamentManagementFragment;
        }


        @Override
        public int getCount() {

            return amountOfTabs;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            if (position == 0) {
                return getApplication().getResources().getString(R.string.nav_tournament_setup);
            } else {
                return getApplication().getResources().getString(R.string.nav_round_tab, position);
            }
        }


        public void addTabToPager() {

            amountOfTabs = amountOfTabs + 1;
        }
    }
}
