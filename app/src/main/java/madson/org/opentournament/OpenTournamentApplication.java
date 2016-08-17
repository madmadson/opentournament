package madson.org.opentournament;

import madson.org.opentournament.about.AppInfo;
import madson.org.opentournament.about.LibraryItem;
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.utility.Environment;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OpenTournamentApplication extends BaseApplication {

    private AppInfo appInfo;

    private List<LibraryItem> libraryItems;

    @Override
    public Environment getEnvironment() {

        return Environment.DEV;
    }


    @Override
    public List<LibraryItem> getAdditionalLibraries() {

        return libraryItems;
    }


    @Override
    public AppInfo getAppInfo() {

        if (appInfo != null) {
            return appInfo;
        }

        return new AppInfo("Test", "1", "App to test the about stuff.", R.drawable.avatar_anonymous);
    }
}
