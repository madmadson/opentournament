package madson.org.opentournament;

import android.os.Bundle;

import android.view.Menu;

import madson.org.opentournament.utility.BaseActivity;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();

            getSupportFragmentManager().beginTransaction().add(R.id.content_main, homeFragment).commit();
        }
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
}
