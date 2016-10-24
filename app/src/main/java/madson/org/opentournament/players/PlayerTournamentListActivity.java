package madson.org.opentournament.players;

import android.content.Intent;

import android.os.Bundle;

import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;

import android.util.Log;

import android.view.Menu;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.online.OnlineGamesListFragment;
import madson.org.opentournament.online.OnlineRankingListFragment;
import madson.org.opentournament.online.OnlineTournamentPlayerListFragment;
import madson.org.opentournament.online.RegisterTournamentPlayerListFragment;
import madson.org.opentournament.tournaments.OnlineTournamentListFragment;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Activity for tournament organiser.
 */
public class PlayerTournamentListActivity extends BaseActivity {

    public static final String EXTRA_PLAYER = "player";

    private Player player;

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

        player = (Player) extras.get(EXTRA_PLAYER);

        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setTitle(getString(R.string.player_name_in_row, player.getFirstName(),
                    player.getNickName(), player.getLastName()));
        }

        Log.i(this.getClass().toString(), "online tournament opened with uuid " + player);

        Bundle bundle = new Bundle();
        bundle.putParcelable(PlayerTournamentListFragment.BUNDLE_PLAYER, player);

        PlayerTournamentListFragment playerTournamentListFragment = new PlayerTournamentListFragment();
        playerTournamentListFragment.setArguments(bundle);
        replaceFragment(playerTournamentListFragment);
    }
}
