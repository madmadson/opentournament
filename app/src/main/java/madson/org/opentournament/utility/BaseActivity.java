package madson.org.opentournament.utility;

import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.facebook.login.LoginManager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

import madson.org.opentournament.AccountFragment;
import madson.org.opentournament.HomeFragment;
import madson.org.opentournament.R;
import madson.org.opentournament.SignInActivity;
import madson.org.opentournament.about.AboutActivity;
import madson.org.opentournament.players.PlayerListFragment;
import madson.org.opentournament.tournaments.OnlineTournamentListFragment;
import madson.org.opentournament.tournaments.OrganizedTournamentList;


/**
 * Base Activity for all activities.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    GoogleApiClient.OnConnectionFailedListener {

    private static BaseApplication mApplication;

    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;

    private DrawerLayout drawer;
    private NavigationView navView;
    private CoordinatorLayout coordinatorLayout;

    private ImageView userAvatar;
    private TextView userMail;

    // Auth
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (useTabLayout()) {
            setContentView(R.layout.activity_main_tablayout);
        } else {
            if (useNavigationDrawer()) {
                setContentView(R.layout.activity_main_drawer);
            } else {
                setContentView(R.layout.activity_main);
            }
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_main);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeAsUp());

        if (isConnected()) {
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (mFirebaseUser == null) {
                // Not signed in, launch the Sign In activity
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            } else {
                mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API).build();

                getBaseApplication().updateAuthenticatedUser();
            }
        } else {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.offline_text, Snackbar.LENGTH_LONG);

            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorWarning));

            snackbar.show();
        }

        if (useNavigationDrawer()) {
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            navView = (NavigationView) findViewById(R.id.nav_view);
            navView.setNavigationItemSelectedListener(this);

            View headerView = navView.getHeaderView(0);

            userAvatar = (CircleImageView) headerView.findViewById(R.id.drawer_avatar);

            userMail = (TextView) headerView.findViewById(R.id.drawer_mail);

            if (mFirebaseUser != null) {
                if (mFirebaseUser.getPhotoUrl() != null) {
                    Glide.with(this).load(mFirebaseUser.getPhotoUrl()).into(userAvatar);
                }

                userMail.setText(mFirebaseUser.getEmail());
            }
        }
    }


    @Override
    public void onBackPressed() {

        if (useNavigationDrawer()) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }


    /**
     * Drawer menu.
     *
     * @param  item
     *
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if (useNavigationDrawer()) {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Log.i("Nav", "Open home");
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_tournaments) {
                Log.i("Nav", "Open online tournaments");
                replaceFragment(new OnlineTournamentListFragment());
            } else if (id == R.id.nav_players) {
                Log.i("Nav", "Open players");
                replaceFragment(new PlayerListFragment());
            } else if (id == R.id.nav_account) {
                Log.i("Nav", "Open Account Management");
                replaceFragment(new AccountFragment());
            } else if (id == R.id.nav_organized_tournaments) {
                Log.i("Nav", "Open organized tournaments");
                replaceFragment(new OrganizedTournamentList());
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            return true;
        } else {
            return false;
        }
    }


    /**
     * Toolbar menu.
     *
     * @param  item
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();

        if (id == android.R.id.home) {
            this.onBackPressed();

            return true;
        } else if (id == R.id.toolbar_menu_sign_out) {
            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

            if (isConnected() && mFirebaseAuth != null) {
                mFirebaseAuth.signOut();
                LoginManager.getInstance().logOut();

                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                startActivity(new Intent(this, SignInActivity.class));
            } else if (isConnected() && mFirebaseAuth == null) {
                startActivity(new Intent(this, SignInActivity.class));
            }

            if (!isConnected()) {
                Log.i(this.getClass().getName(), "sign out when no internet");
                Toast.makeText(BaseActivity.this, R.string.toast_sign_out_no_connection, Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.toolbar_menu_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void replaceFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.replace(R.id.content_main, fragment);

        transaction.commit();
    }


    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }


    /**
     * @return  the default {@link CoordinatorLayout}.
     */
    public CoordinatorLayout getCoordinatorLayout() {

        return coordinatorLayout;
    }


    /**
     * Gets the FloatingActionButton of the toolbar_menu layout. To use it, set a click listener, an image, and set it
     * to visible, as it is invisible by default.
     *
     * @return  the FloatingActionButton of the toolbar_menu layout.
     */
    public FloatingActionButton getFloatingActionButton() {

        return floatingActionButton;
    }


    /**
     * @return  the BaseApplication for the current App.
     */
    public BaseApplication getBaseApplication() {

        if (mApplication == null) {
            mApplication = (BaseApplication) getApplication();
        }

        return mApplication;
    }


    /**
     * States if the Activity should use the default navigation drawer. Main Activities should do this in almost every
     * case.
     *
     * @return  if the Activity should use the default navigation drawer. Defaults to {@code true}.
     */
    public boolean useNavigationDrawer() {

        return true;
    }


    /**
     * States if the Activity should use tab layout with view pager.
     *
     * @return  if the Activity should use tab layout. Defaults to {@code true}.
     */
    public boolean useTabLayout() {

        return false;
    }


    /**
     * Defines whether the 'home' button on the actionbar should act as a 'up' (or 'back') button click and so close the
     * current Activity to get back to the previous one.
     *
     * @return  true if it should do this, false otherwise. Defaults to {@code false}.
     */
    public boolean isDisplayHomeAsUp() {

        return false;
    }


    /**
     * Closes the soft keyboard, if open.
     */
    public void closeKeyboard() {

        if (getWindow() != null && getWindow().getDecorView() != null) {
            // close the soft input, if open
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(this.getClass().getName(), "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    /**
     * Gets the main Toolbar of the Activity. Only call this <i>after</i> super.onPostCreate() has been called!
     *
     * @return  the toolbar
     */
    public Toolbar getToolbar() {

        return this.toolbar;
    }


    public FirebaseUser getCurrentFireBaseUser() {

        return FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }
}
