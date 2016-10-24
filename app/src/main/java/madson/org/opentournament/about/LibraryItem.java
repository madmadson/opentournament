package madson.org.opentournament.about;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import madson.org.opentournament.R;


/**
 * Library item containing the information about a library or license used within the app.
 */
public class LibraryItem implements Comparable<LibraryItem> {

    private String id;
    private String name;
    private String author;
    private String description;
    private String version;
    private String url;

    /**
     * @param  id  the id of the library - e.g. the artifactId of a dependency, mandatory
     * @param  name  the human readable name of the library, mandatory
     * @param  description  the description of the library, mandatory
     * @param  author  the author of the library, may be null
     * @param  version  the version of the library, may be null
     * @param  url  the url to the library web page, may be null
     */
    public LibraryItem(@NonNull String id, @NonNull String name, @NonNull String description, @Nullable String author,
        @Nullable String version, @Nullable String url) {

        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.version = version;
        this.url = url;
    }

    public String getId() {

        return id;
    }


    public String getName() {

        return name;
    }


    public String getAuthor() {

        return author;
    }


    public String getDescription() {

        return description;
    }


    public String getVersion() {

        return version;
    }


    public String getUrl() {

        return url;
    }


    @Override
    public int compareTo(@NonNull LibraryItem another) {

        return this.name.compareTo(another.name);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        LibraryItem that = (LibraryItem) o;

        return id.equals(that.id);
    }


    @Override
    public int hashCode() {

        return id.hashCode();
    }

    /**
     * TournamentViewHolder for a {@link LibraryItem} holding the views a library item can have. It prevents the app
     * from automatically inflate and instantiate a new library card layout for a new item if the layout of the item's
     * position is already one used to display Library items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected CardView cardView;
        protected ViewGroup contentView;
        protected TextView name;
        protected TextView author;
        protected TextView version;
        protected TextView description;

        public ViewHolder(View libView) {

            super(libView);
            cardView = (CardView) libView;
            contentView = (ViewGroup) libView.findViewById(R.id.lib_card_content);
            name = (TextView) libView.findViewById(R.id.lib_name);
            author = (TextView) libView.findViewById(R.id.lib_author);
            version = (TextView) libView.findViewById(R.id.lib_version);
            description = (TextView) libView.findViewById(R.id.lib_description);
        }
    }
}
