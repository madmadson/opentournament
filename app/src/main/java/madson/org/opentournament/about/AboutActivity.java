package madson.org.opentournament.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

import madson.org.opentournament.R;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Activity to display information about the app.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(AboutFragment.TAG);

        if (fragment == null) {
            fragment = new AboutFragment();
            manager.beginTransaction().replace(R.id.content_main, fragment, AboutFragment.TAG).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }


    @Override
    public boolean useNavigationDrawer() {

        return false;
    }


    @Override
    public boolean isDisplayHomeAsUp() {

        return true;
    }
}
