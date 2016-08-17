package madson.org.opentournament.about;

/**
 * Basic Information about the app to show to the user.
 */
public class AppInfo {

    private String name;
    private String versionName;
    private String description;
    private int aboutIconResourceId;

    public AppInfo(String name, String versionName, String description, int aboutIconResourceId) {

        this.name = name;
        this.versionName = versionName;
        this.description = description;
        this.aboutIconResourceId = aboutIconResourceId;
    }

    public String getName() {

        return name;
    }


    public String getVersionName() {

        return versionName;
    }


    public String getDescription() {

        return description;
    }


    public int getAboutIconResourceId() {

        return aboutIconResourceId;
    }
}
