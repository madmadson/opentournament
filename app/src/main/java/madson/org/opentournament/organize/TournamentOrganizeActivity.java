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
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.events.OpenTournamentEvent;
import madson.org.opentournament.events.OpenTournamentEventListener;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.organize.setup.AvailablePlayerListFragment;
import madson.org.opentournament.organize.setup.TournamentPlayerListFragment;
import madson.org.opentournament.organize.setup.TournamentSetupFragment;
import madson.org.opentournament.tasks.LoadTournamentTask;
import madson.org.opentournament.utility.BaseActivity;

import java.util.HashMap;


/**
 * Activity for initialTournament organiser.
 */
public class TournamentOrganizeActivity extends BaseActivity implements OpenTournamentEventListener {

    public static final String EXTRA_TOURNAMENT = "initialTournament";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ProgressBar progressBar;
    private Tournament initialTournament;

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
    protected void onStart() {

        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getBaseApplication().registerTournamentEventListener(this);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        initialTournament = (Tournament) extras.get(EXTRA_TOURNAMENT);

        if (initialTournament != null) {
            Log.i(this.getClass().toString(), "initialTournament opened with id " + initialTournament);

            ActionBar supportActionBar = getSupportActionBar();

            if (supportActionBar != null) {
                supportActionBar.setTitle(initialTournament.getName());
            }

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            progressBar = (ProgressBar) getToolbar().findViewById(R.id.toolbar_progress_bar);
            new LoadTournamentTask(this, initialTournament, mSectionsPagerAdapter, mViewPager, progressBar).execute();
        }
    }


    @Override
    public void handleEvent(OpenTournamentEventTag eventTag, OpenTournamentEvent parameterObject) {

        if (eventTag.equals(OpenTournamentEventTag.TOURNAMENT_STARTED)) {
            finish();

            Intent intent = new Intent(this, TournamentOrganizeActivity.class);
            intent.putExtra(TournamentOrganizeActivity.EXTRA_TOURNAMENT, initialTournament);
            startActivity(intent);
        } else if (eventTag.equals(OpenTournamentEventTag.NEXT_ROUND_PAIRED)) {
            new LoadTournamentTask(this, initialTournament, mSectionsPagerAdapter, mViewPager, progressBar).execute();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Tournament tournamentToOrganize;
        private String state;
        private int actualRound;

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Log.i(this.getClass().getName(),
                "create initialTournament fragment: " + tournamentToOrganize + " on position: " + position);

            float widthOfScreen = getWidthOfScreen();

            if (state.equals(Tournament.TournamentState.PLANED.name())) {
                if (widthOfScreen < 720) {
                    if (position == 0) {
                        return TournamentPlayerListFragment.newInstance(tournamentToOrganize);
                    } else {
                        return AvailablePlayerListFragment.newInstance(tournamentToOrganize);
                    }
                } else {
                    return TournamentSetupFragment.newInstance(tournamentToOrganize);
                }
            } else {
                if (position == 0) {
                    return TournamentPlayerListFragment.newInstance(tournamentToOrganize);
                }
            }

            if (widthOfScreen < 720) {
                int round = (position + 1) / 2;

                if (state.equals(Tournament.TournamentState.FINISHED.name()) && actualRound == round) {
                    return RankingListFragment.newInstance(actualRound, tournamentToOrganize);
                }

                if (position % 2 == 1) {
                    return GameListFragment.newInstance(round, tournamentToOrganize);
                } else {
                    return RankingListFragment.newInstance(round, tournamentToOrganize);
                }
            } else {
                if (state.equals(Tournament.TournamentState.FINISHED.name()) && position == actualRound) {
                    return RankingListFragment.newInstance(actualRound, tournamentToOrganize);
                } else {
                    return TournamentRoundManagementFragment.newInstance(position, tournamentToOrganize);
                }
            }
        }


        @Override
        public int getCount() {

            float widthOfScreen = getWidthOfScreen();

            if (state != null) {
                if (state.equals(Tournament.TournamentState.PLANED.name())) {
                    if (widthOfScreen < 720) {
                        return 2;
                    } else {
                        return 1;
                    }
                } else {
                    if (state.equals(Tournament.TournamentState.FINISHED.name())) {
                        if (widthOfScreen < 720) {
                            return actualRound * 2;
                        } else {
                            return actualRound + 1;
                        }
                    } else {
                        if (widthOfScreen < 720) {
                            return (actualRound * 2) + 1;
                        } else {
                            return actualRound + 1;
                        }
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
            }

            if (state.equals(Tournament.TournamentState.PLANED.name())) {
                if (position == 1 && widthOfScreen < 720) {
                    return getApplication().getResources().getString(R.string.nav_available_players);
                }
            }

            if (widthOfScreen < 720) {
                int round = (position + 1) / 2;

                if (state.equals(Tournament.TournamentState.FINISHED.name()) && position == (actualRound * 2)) {
                    return getApplication().getResources().getString(R.string.nav_final_standing_tab);
                } else {
                    if (position % 2 == 1) {
                        return getApplication().getResources().getString(R.string.nav_games_tab, round);
                    } else {
                        return getApplication().getResources().getString(R.string.nav_ranking_tab, round);
                    }
                }
            } else {
                if (state.equals(Tournament.TournamentState.FINISHED.name()) && position == actualRound) {
                    return getApplication().getResources().getString(R.string.nav_final_standing_tab);
                } else {
                    return getApplication().getResources().getString(R.string.nav_round_tab, position);
                }
            }
        }


        public void setTournamentToOrganize(Tournament tournamentToOrganize) {

            this.tournamentToOrganize = new Tournament(tournamentToOrganize);

            state = tournamentToOrganize.getState();
            actualRound = tournamentToOrganize.getActualRound();

            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }
}
