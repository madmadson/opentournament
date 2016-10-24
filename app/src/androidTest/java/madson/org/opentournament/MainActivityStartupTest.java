package madson.org.opentournament;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;


@RunWith(AndroidJUnit4.class)
public class MainActivityStartupTest {

    @Rule
    public MainActivityTestTestRule mActivityRule = new MainActivityTestTestRule();

    @Test
    public void ensureNavigationToTournamentsManagement() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(Gravity.START));

        onView(withText(R.string.drawer_nav_online_tournaments)).perform(click());

        // Check that the text was changed.
        String toolbarTitle = getInstrumentation().getTargetContext().getString(R.string.title_tournament_management);

        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(
                withText(toolbarTitle)));
    }


    @Test
    public void ensureNavigationToPlayersManagement() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(Gravity.START));

        onView(withText(R.string.drawer_nav_players)).perform(click());

        // Check that the text was changed.
        String toolbarTitle = getInstrumentation().getTargetContext().getString(R.string.title_player_management);

        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(
                withText(toolbarTitle)));
    }


    @Test
    public void ensureNavigationToHome() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(Gravity.START));

        onView(withText(R.string.drawer_nav_home)).perform(click());

        // Check that the text was changed.
        String toolbarTitle = getInstrumentation().getTargetContext().getString(R.string.title_home);

        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(
                withText(toolbarTitle)));
    }


    @Test
    public void ensureNavigationToAccountManagement() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(Gravity.START));

        onView(withText(R.string.drawer_nav_account)).perform(click());

        // Check that the text was changed.
        String toolbarTitle = getInstrumentation().getTargetContext().getString(R.string.title_account_management);

        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(
                withText(toolbarTitle)));
    }

    private class MainActivityTestTestRule extends IntentsTestRule<MainActivity> {

        public MainActivityTestTestRule() {

            super(MainActivity.class, true, true);
        }

        @Override
        protected void beforeActivityLaunched() {

            super.beforeActivityLaunched();

            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseAuth.signInAnonymously();
        }


        @Override
        protected void afterActivityFinished() {

            super.afterActivityFinished();

            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseAuth.signOut();
        }
    }
}
