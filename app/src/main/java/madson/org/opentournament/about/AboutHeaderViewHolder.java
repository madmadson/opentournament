package madson.org.opentournament.about;

import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;


public class AboutHeaderViewHolder extends RecyclerView.ViewHolder {

    protected TextView name;
    protected TextView version;
    protected ImageView icon;
    protected TextView description;

    public AboutHeaderViewHolder(View headerView) {

        super(headerView);

        name = (TextView) headerView.findViewById(R.id.about_name);
        version = (TextView) headerView.findViewById(R.id.about_version);
        icon = (ImageView) headerView.findViewById(R.id.about_icon);
        description = (TextView) headerView.findViewById(R.id.about_description);
    }
}
