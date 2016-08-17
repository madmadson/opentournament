package madson.org.opentournament.utility;

import android.content.Context;

import android.support.design.widget.CoordinatorLayout;

import android.support.v4.app.Fragment;

import android.view.inputmethod.InputMethodManager;


/**
 * Provides Basic functionality for Fragments - All other Fragments should extend this Fragment.
 */
public class BaseFragment extends Fragment {

    private static BaseApplication mApplication;

    /**
     * @return  the BaseApplication of this app - can be null if the Fragment is not attached to an Activity.
     */
    public BaseApplication getBaseApplication() {

        // if the application was not set before, set it.
        if (mApplication == null && getActivity() != null) {
            mApplication = (BaseApplication) getActivity().getApplication();
        }

        return mApplication;
    }


    /**
     * Prompt the user to sign in. The method to achieve this can change.
     *
     * @param  requestCode  to send via the signedIn event to destinguish between actions to take after sign in
     */
    public void promptToSignIn(int requestCode) {

        BaseApplication baseApplication = getBaseApplication();
        CoordinatorLayout coordinatorLayout = getBaseActivity().getCoordinatorLayout();
    }


    /**
     * Closes the soft keyboard, if open.
     */
    public void closeKeyboard() {

        if (getView() != null && getView().getWindowToken() != null) {
            // close the soft input, if open
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }


    /**
     * Set the base application for all fragments - intended for tests.
     *
     * @param  application
     */
    public static void setBaseApplication(BaseApplication application) {

        mApplication = application;
    }


    /**
     * @return  if the fragment is attached to an activity and if that activity is still running.
     */
    public boolean isActivityRunning() {

        return !(getActivity() == null || getActivity().isFinishing());
    }


    /**
     * @return  the Activity of this Fragment (cast to BaseActivity), can be null.
     */
    public BaseActivity getBaseActivity() {

        return (BaseActivity) getActivity();
    }
}
