package madson.org.opentournament.about;

import android.content.Context;
import android.content.Intent;

import android.graphics.drawable.Drawable;

import android.net.Uri;

import android.support.v7.widget.RecyclerView;

import android.text.Html;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.utility.BaseActivity;

import java.util.List;


/**
 * Adapter for a list of app information and the used libraries.
 */
public class AboutAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_LIBRARY = 2;

    private Context context;
    private AppInfo appInfo;
    private List<LibraryItem> items;
    private Drawable aboutDrawable;

    public AboutAdapter(BaseActivity context) {

        this.context = context;
        this.appInfo = context.getBaseApplication().getAppInfo();
        this.aboutDrawable = context.getResources().getDrawable(appInfo.getAboutIconResourceId());
        this.items = context.getBaseApplication().getAboutLibraries();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_HEADER:

                View header = inflater.inflate(R.layout.about_header, parent, false);
                viewHolder = new AboutHeaderViewHolder(header);
                break;

            case TYPE_LIBRARY:

                View card = inflater.inflate(R.layout.cardview_library, parent, false);
                viewHolder = new LibraryItem.ViewHolder(card);
                break;
        }

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case TYPE_HEADER:

                AboutHeaderViewHolder headerVH = (AboutHeaderViewHolder) viewHolder;

                setHeaderData(headerVH);

                break;

            case TYPE_LIBRARY:

                LibraryItem.ViewHolder libraryVH = (LibraryItem.ViewHolder) viewHolder;
                LibraryItem library = items.get(position - 1);
                setLibraryData(libraryVH, library);

                break;
        }
    }


    /**
     * Set the data of the given TournamentViewHolder, using the given {@link LibraryItem}.
     *
     * @param  viewHolder  to set the data to
     * @param  library  to get the data from
     */
    private void setLibraryData(LibraryItem.ViewHolder viewHolder, LibraryItem library) {

        viewHolder.name.setText(library.getName());
        viewHolder.author.setText(library.getAuthor());
        viewHolder.version.setText(library.getVersion());
        viewHolder.description.setText(library.getDescription());

        final String url = library.getUrl();

        if (url != null && !url.isEmpty()) {
            viewHolder.contentView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    }
                });
        } else {
            viewHolder.contentView.setOnClickListener(null);
        }
    }


    /**
     * Set the data to the given header TournamentViewHolder.
     *
     * @param  viewHolder  to set the data to
     */
    private void setHeaderData(AboutHeaderViewHolder viewHolder) {

        String aboutPageTitle = appInfo.getName();
        viewHolder.name.setText(aboutPageTitle);

        String version = appInfo.getVersionName();
        viewHolder.version.setText(version);

        if (version == null || version.isEmpty()) {
            viewHolder.version.setVisibility(View.GONE);
        } else {
            viewHolder.version.setVisibility(View.VISIBLE);
        }

        String dbVersion = appInfo.getDbVersionName();
        viewHolder.dbVersion.setText(context.getString(R.string.db_version, dbVersion));

        if (dbVersion == null || dbVersion.isEmpty()) {
            viewHolder.dbVersion.setVisibility(View.GONE);
        } else {
            viewHolder.dbVersion.setVisibility(View.VISIBLE);
        }

        String description = appInfo.getDescription();
        viewHolder.description.setText(Html.fromHtml(description));

        viewHolder.icon.setImageDrawable(aboutDrawable);
    }


    /**
     * Returns the views type according to its position. The first item (and therefore position 0) is always the header,
     * all others are {@link LibraryItem}(s).
     *
     * @param  position  the position of the item
     *
     * @return  the view type for the given position
     */
    @Override
    public int getItemViewType(int position) {

        return position == 0 ? TYPE_HEADER : TYPE_LIBRARY;
    }


    @Override
    public int getItemCount() {

        // +1 for the header
        return items.size() + 1;
    }
}
