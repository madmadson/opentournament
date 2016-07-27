package madson.org.opentournament.tournament;

import android.content.Intent;

import android.os.Bundle;

import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import android.view.MenuItem;

import madson.org.opentournament.HomeFragment;
import madson.org.opentournament.MainActivity;
import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.exception.GeneralExceptionHandler;
import madson.org.opentournament.management.TournamentManagementFragment;
import madson.org.opentournament.players.PlayerListFragment;
import madson.org.opentournament.service.TournamentService;


public class OngoingTournamentActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA_TOURNAMENT_ID = "tournament_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_tournament);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        long tournamentId = (long) extras.get(EXTRA_TOURNAMENT_ID);

        if (tournamentId != 0) {
            Log.i(this.getClass().toString(), "tournament started with id " + tournamentId);

            TournamentService tournamentService = ((OpenTournamentApplication) getApplication()).getTournamentService();
            Tournament tournament = tournamentService.getTournamentForId(tournamentId);

            ActionBar supportActionBar = getSupportActionBar();

            if (supportActionBar != null) {
                supportActionBar.setTitle(tournament.getName());
            }

            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentByTag(TournamentManagementFragment.TAG);

            if (fragment == null) {
                fragment = new PlayerListFragment();
                manager.beginTransaction()
                    .replace(R.id.main_fragment_container, fragment, PlayerListFragment.TAG)
                    .commit();
            }
        } else {
            throw new RuntimeException();
        }
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            Log.i("Nav", "Main Activity");

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_tournaments_setup) {
            Log.i("Nav", "Open Tournament Setup");

            Fragment fragment = manager.findFragmentByTag(madson.org.opentournament.players.PlayerListFragment.TAG);

            if (fragment == null) {
                fragment = new HomeFragment();
                manager.beginTransaction().replace(R.id.main_fragment_container, fragment, HomeFragment.TAG).commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
