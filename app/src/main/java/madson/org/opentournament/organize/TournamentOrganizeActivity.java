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
import madson.org.opentournament.organize.setup.AvailablePlayerListFragment;
import madson.org.opentournament.organize.setup.TournamentPlayerListFragment;
import madson.org.opentournament.organize.setup.TournamentSetupFragment;
import madson.org.opentournament.tasks.LoadTournamentTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.TournamentEventTag;


/**
 * Activity for tournament organiser.
 */
public class TournamentOrganizeActivity extends BaseActivity implements TournamentEventListener {

    public static final String EXTRA_TOURNAMENT = "tournament";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

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
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        mSectionsPagerAdapter = null;
        mViewPager = null;
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

        Tournament tournament = (Tournament) extras.get(EXTRA_TOURNAMENT);

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

        float widthOfScreen = getWidthOfScreen();

        if (widthOfScreen < 720) {
            int round = (roundToStart * 2) - 1;
            mViewPager.setCurrentItem(round);
        } else {
            mViewPager.setCurrentItem(roundToStart);
        }
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
    public void enterGameResultConfirmed(TournamentEventTag tag, Game game) {

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

            if (tournamentToOrganize.getState().equals(Tournament.TournamentState.PLANED.name())) {
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

                if (tournamentToOrganize.getState().equals(Tournament.TournamentState.FINISHED.name())
                        && tournamentToOrganize.getActualRound() == round) {
                    return RankingListFragment.newInstance(tournamentToOrganize.getActualRound(), tournamentToOrganize);
                }

                if (position % 2 == 1) {
                    return GameListFragment.newInstance(round, tournamentToOrganize);
                } else {
                    return RankingListFragment.newInstance(round, tournamentToOrganize);
                }
            } else {
                if (tournamentToOrganize.getState().equals(Tournament.TournamentState.FINISHED.name())
                        && position == tournamentToOrganize.getActualRound()) {
                    return RankingListFragment.newInstance(tournamentToOrganize.getActualRound(), tournamentToOrganize);
                } else {
                    return TournamentRoundManagementFragment.newInstance(position, tournamentToOrganize);
                }
            }
        }


        @Override
        public int getCount() {

            float widthOfScreen = getWidthOfScreen();

            if (tournamentToOrganize != null) {
                if (tournamentToOrganize.getState().equals(Tournament.TournamentState.PLANED.name())) {
                    if (widthOfScreen < 720) {
                        return 2;
                    } else {
                        return 1;
                    }
                } else {
                    if (tournamentToOrganize.getState().equals(Tournament.TournamentState.FINISHED.name())) {
                        if (widthOfScreen < 720) {
                            return (tournamentToOrganize.getActualRound() * 2);
                        } else {
                            return tournamentToOrganize.getActualRound() + 1;
                        }
                    } else {
                        if (widthOfScreen < 720) {
                            return (tournamentToOrganize.getActualRound() * 2) + 1;
                        } else {
                            return tournamentToOrganize.getActualRound() + 1;
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

            if (tournamentToOrganize.getState().equals(Tournament.TournamentState.PLANED.name())) {
                if (position == 1 && widthOfScreen < 720) {
                    return getApplication().getResources().getString(R.string.nav_available_players);
                }
            }

            if (widthOfScreen < 720) {
                int round = (position + 1) / 2;

                if (tournamentToOrganize.getState().equals(Tournament.TournamentState.FINISHED.name())
                        && position == ((tournamentToOrganize.getActualRound() * 2) - 1)) {
                    return getApplication().getResources().getString(R.string.nav_final_standing_tab);
                } else {
                    if (position % 2 == 1) {
                        return getApplication().getResources().getString(R.string.nav_games_tab, round);
                    } else {
                        return getApplication().getResources().getString(R.string.nav_ranking_tab, round);
                    }
                }
            } else {
                if (tournamentToOrganize.getState().equals(Tournament.TournamentState.FINISHED.name())
                        && position == tournamentToOrganize.getActualRound()) {
                    return getApplication().getResources().getString(R.string.nav_final_standing_tab);
                } else {
                    return getApplication().getResources().getString(R.string.nav_round_tab, position);
                }
            }
        }


        public void setTournamentToOrganize(Tournament tournamentToOrganize) {

            this.tournamentToOrganize = tournamentToOrganize;

            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }
}
