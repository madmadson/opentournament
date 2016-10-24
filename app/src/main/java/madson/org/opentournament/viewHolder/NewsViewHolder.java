package madson.org.opentournament.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import madson.org.opentournament.R;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class NewsViewHolder extends RecyclerView.ViewHolder {

    private TextView newsHeaderItem;
    private TextView newsItem;

    public NewsViewHolder(View v) {

        super(v);

        newsItem = (TextView) v.findViewById(R.id.news_item);
        newsHeaderItem = (TextView) v.findViewById(R.id.news_header);
    }

    public TextView getNewsItem() {

        return newsItem;
    }


    public TextView getNewsHeaderItem() {

        return newsHeaderItem;
    }
}
