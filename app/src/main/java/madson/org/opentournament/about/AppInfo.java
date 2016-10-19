package madson.org.opentournament.about;

/**
 * Basic Information about the app to show to the user.
 */
public class AppInfo {

    private String name;
    private String versionName;
    private String description;
    private int aboutIconResourceId;

    private String dbVersionName;

    public AppInfo(String name, String versionName, String description, int aboutIconResourceId, String dbVersion) {

        this.name = name;
        this.versionName = versionName;
        this.description = description;
        this.aboutIconResourceId = aboutIconResourceId;
        this.dbVersionName = dbVersion;
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


    public String getDbVersionName() {

        return dbVersionName;
    }
}
