package madson.org.opentournament.organize;

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

import android.widget.ProgressBar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.setup.TournamentSetupFragment;
import madson.org.opentournament.tasks.LoadTournamentTask;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Activity for tournament organiser.
 */
public class TournamentOrganizeActivity extends BaseActivity implements TournamentEventListener {

    public static final String EXTRA_TOURNAMENT = "tournament";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private TournamentRoundManagementFragment tournamentRoundManagementFragment;
    private Tournament tournament;
    private TournamentSetupFragment tournamentSetupFragment;

    @Override
    public boolean useTabLayout() {

        return true;
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        getBaseApplication().unregisterTournamentEventListener(this);
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

        getBaseApplication().registerTournamentEventListener(this);

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

            ProgressBar progressBar = (ProgressBar) getToolbar().findViewById(R.id.toolbar_progress_bar);
            new LoadTournamentTask(getBaseApplication(), tournament, mSectionsPagerAdapter, mViewPager, progressBar)
                .execute();
        }
    }


    @Override
    public void startRound(int roundToStart, Tournament tournament) {

        Log.i(this.getClass().getName(), "set actual tournament");

        mSectionsPagerAdapter.setTournamentToOrganize(tournament);
        mViewPager.setCurrentItem(tournament.getActualRound());
    }


    @Override
    public void pairRoundAgain(int round_for_pairing) {

        // nothing
    }


    @Override
    public void pairingChanged(Game game1, Game game2) {

        // nothing
    }


    @Override
    public void enterGameResultConfirmed(Game game) {

        // nothing
    }


    @Override
    public void addTournamentPlayer(TournamentPlayer tournamentPlayer) {
    }


    @Override
    public void removeTournamentPlayer(TournamentPlayer tournamentPlayer) {
    }


    @Override
    public void addPlayerToTournament(Player player) {
    }


    @Override
    public void removeAvailablePlayer(Player player) {
    }


    @Override
    public void updateTournamentPlayer(TournamentPlayer updatedPLayer, String teamName) {
    }


    @Override
    public void addRegistration(TournamentPlayer player) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Tournament tournamentToOrganize;

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Log.i(this.getClass().getName(),
                "create tournament fragment: " + tournamentToOrganize + " on position: " + position);

            float widthOfScreen = getWidthOfScreen();

            if (position == 0) {
                return tournamentSetupFragment = TournamentSetupFragment.newInstance(tournamentToOrganize);
            } else if (position == 1 && widthOfScreen < 720) {
                // other thing
                return tournamentSetupFragment = TournamentSetupFragment.newInstance(tournamentToOrganize);
            }

            if (tournamentToOrganize.getState().equals(Tournament.TournamentState.FINISHED.name())
                    && position == tournamentToOrganize.getActualRound() + 1) {
                return TournamentFinalStandingFragment.newInstance(tournamentToOrganize);
            } else {
                tournamentRoundManagementFragment = TournamentRoundManagementFragment.newInstance(position,
                        tournamentToOrganize);

                return tournamentRoundManagementFragment;
            }
        }


        @Override
        public int getCount() {

            float widthOfScreen = getWidthOfScreen();

            if (tournamentToOrganize != null) {
                if (tournamentToOrganize.getState().equals(Tournament.TournamentState.FINISHED.name())) {
                    if (widthOfScreen < 720) {
                        return tournamentToOrganize.getActualRound() + 3;
                    } else {
                        return tournamentToOrganize.getActualRound() + 2;
                    }
                } else {
                    if (widthOfScreen < 720) {
                        return tournamentToOrganize.getActualRound() + 2;
                    } else {
                        return tournamentToOrganize.getActualRound() + 1;
                    }
                }
            } else {
                return 0;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {

            float widthOfScreen = getWidthOfScreen();

            if (position == 0) {
                return getApplication().getResources().getString(R.string.nav_tournament_players);
            } else if (position == 1 && widthOfScreen < 1024) {
                return getApplication().getResources().getString(R.string.nav_available_players);
            } else if (tournamentToOrganize.getState().equals(Tournament.TournamentState.FINISHED.name())
                    && position == tournamentToOrganize.getActualRound() + 1) {
                return getApplication().getResources().getString(R.string.nav_final_standing_tab);
            } else {
                return getApplication().getResources().getString(R.string.nav_round_tab, position);
            }
        }


        public void setTournamentToOrganize(Tournament tournamentToOrganize) {

            this.tournamentToOrganize = tournamentToOrganize;

            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }
}
