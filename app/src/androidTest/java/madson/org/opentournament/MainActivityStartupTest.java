package madson.org.opentournament;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import android.support.v7.widget.Toolbar;

import android.view.Gravity;

import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;


@RunWith(AndroidJUnit4.class)
public class MainActivityStartupTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void ensureTextChangesWork() {

        // Type text and then press the button.
        onView(withId(R.id.inputField)).perform(typeText("HELLO"), closeSoftKeyboard());
        onView(withId(R.id.changeText)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.inputField)).check(matches(withText("Lalala")));
    }


    @Test
    public void ensureNavigationToTournamentsManagement() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open(Gravity.START));

        onView(withText("Manage Tournaments")).perform(click());

        // Check that the text was changed.
        String toolbarTitle = getInstrumentation().getTargetContext().getString(R.string.title_tournament_management);

        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(
                withText(toolbarTitle)));
    }
}
