package madson.org.opentournament.organize.team;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.ActionBar;

import android.util.Log;

import android.view.Menu;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public class TeamTournamentManagementActivity extends BaseActivity {

    public static final String EXTRA_TOURNAMENT = "tournament";
    public static final String EXTRA_GAME = "game";

    @Override
    public boolean useTabLayout() {

        return false;
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

        Tournament tournament = (Tournament) extras.get(EXTRA_TOURNAMENT);

        Game game = (Game) extras.get(EXTRA_GAME);

        if (tournament != null && game != null) {
            Log.i(this.getClass().toString(), "tournament opened with id " + tournament);

            ActionBar supportActionBar = getSupportActionBar();

            if (supportActionBar != null) {
                supportActionBar.setTitle(game.getParticipantOneUUID() + " VS " + game.getParticipantTwoUUID());
            }

            TeamGameListFragment teamGameListFragment = TeamGameListFragment.newInstance(game, tournament);

            replaceFragment(teamGameListFragment);
        }
    }
}
