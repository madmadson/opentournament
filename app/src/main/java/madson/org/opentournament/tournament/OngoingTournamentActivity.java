package madson.org.opentournament.tournament;

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

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.management.TournamentDetailFragment;
import madson.org.opentournament.players.NewPlayerForTournamentDialog;
import madson.org.opentournament.service.TournamentService;


public class OngoingTournamentActivity extends AppCompatActivity {

    public static final String EXTRA_TOURNAMENT_ID = "tournament_id";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private OngoingTournamentManagementFragment ongoingTournamentManagementFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_tournament);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        final long tournamentId = (long) extras.get(EXTRA_TOURNAMENT_ID);

        if (tournamentId != 0) {
            Log.i(this.getClass().toString(), "tournament started with id " + tournamentId);

            TournamentService tournamentService = ((OpenTournamentApplication) getApplication()).getTournamentService();
            Tournament tournament = tournamentService.getTournamentForId(tournamentId);

            ActionBar supportActionBar = getSupportActionBar();

            if (supportActionBar != null) {
                supportActionBar.setTitle(tournament.getName());
            }

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tournament);

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            if (fab != null) {
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
        } else {
            throw new RuntimeException();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public OngoingTournamentManagementFragment getOngoingTournamentManagementFragment() {

        return ongoingTournamentManagementFragment;
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Tournament tournament;

        public SectionsPagerAdapter(FragmentManager fm, Tournament tournament) {

            super(fm);
            this.tournament = tournament;
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                ongoingTournamentManagementFragment = new OngoingTournamentManagementFragment();

                Bundle bundle = new Bundle();
                bundle.putLong(TournamentDetailFragment.BUNDLE_TOURNAMENT_ID, tournament.getId());
                ongoingTournamentManagementFragment.setArguments(bundle);

                return ongoingTournamentManagementFragment;
            } else {
                return new OngoingTournamentManagementFragment();
            }
        }


        @Override
        public int getCount() {

            return 1;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getApplication().getResources().getString(R.string.nav_tournament_setup);
            }

            return null;
        }
    }
}
