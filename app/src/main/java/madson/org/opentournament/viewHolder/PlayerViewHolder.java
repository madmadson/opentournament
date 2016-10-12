package madson.org.opentournament.viewHolder;

import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerViewHolder extends RecyclerView.ViewHolder {

    private ImageView localIcon;
    private View playerCardLayout;
    private TextView playerNameInList;

    /**
     * used by online game list.
     *
     * @param  v
     */
    public PlayerViewHolder(View v) {

        super(v);

        playerNameInList = (TextView) v.findViewById(R.id.full_player_name);

        playerCardLayout = v.findViewById(R.id.player_card_layout);

        localIcon = (ImageView) v.findViewById(R.id.local_icon);
    }

    public TextView getPlayerNameInList() {

        return playerNameInList;
    }


    public View getPlayerCardLayout() {

        return playerCardLayout;
    }


    public ImageView getLocalIcon() {

        return localIcon;
    }
}
