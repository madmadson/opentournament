package madson.org.opentournament;

import java.util.List;

import madson.org.opentournament.about.AppInfo;
import madson.org.opentournament.about.LibraryItem;
import madson.org.opentournament.db.OpenTournamentDBHelper;
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.utility.Environment;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OpenTournamentApplication extends BaseApplication {

    @Override
    public Environment getEnvironment() {

        return Environment.valueOf(BuildConfig.ENVIRONMENT);
    }


    @Override
    public List<LibraryItem> getAdditionalLibraries() {

        return null;
    }


    @Override
    public AppInfo getAppInfo() {

        return new AppInfo(getString(R.string.app_name), BuildConfig.VERSION_NAME, getString(R.string.app_description),
                R.drawable.open_tournament, String.valueOf(OpenTournamentDBHelper.DB_VERSION));
    }
}
