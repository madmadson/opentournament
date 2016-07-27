package madson.org.opentournament;

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
import android.view.View;

import madson.org.opentournament.management.TournamentManagementFragment;
import madson.org.opentournament.utility.DrawerLocker;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    DrawerLocker {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Thread.setDefaultUncaughtExceptionHandler(new GeneralExceptionHandler(this));

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i("Nav", "Toolbar navigation tournaments");

                    setDrawerEnabled(true);
                    toggle.syncState();

                    FragmentManager manager = getSupportFragmentManager();

                    TournamentManagementFragment fragment = new TournamentManagementFragment();
                    manager.beginTransaction()
                    .replace(R.id.main_fragment_container, fragment, TournamentManagementFragment.TAG)
                    .commit();
                }
            });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(TournamentManagementFragment.TAG);

        if (fragment == null) {
            fragment = new TournamentManagementFragment();
            manager.beginTransaction()
                .replace(R.id.main_fragment_container, fragment, TournamentManagementFragment.TAG)
                .commit();
        }
    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            Log.i("Nav", "Open home");

            Fragment fragment = manager.findFragmentByTag(HomeFragment.TAG);

            if (fragment == null) {
                fragment = new HomeFragment();
                manager.beginTransaction().replace(R.id.main_fragment_container, fragment, HomeFragment.TAG).commit();
            }
        } else if (id == R.id.nav_tournaments) {
            Log.i("Nav", "Open tournaments");

            Fragment fragment = manager.findFragmentByTag(TournamentManagementFragment.TAG);

            if (fragment == null) {
                fragment = new TournamentManagementFragment();
                manager.beginTransaction()
                    .replace(R.id.main_fragment_container, fragment, TournamentManagementFragment.TAG)
                    .commit();
            }
        } else if (id == R.id.nav_players) {
            Log.i("Nav", "Open players");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void setDrawerEnabled(boolean enabled) {

        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);

        toggle.setDrawerIndicatorEnabled(enabled);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(!enabled);
    }
}
