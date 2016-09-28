package madson.org.opentournament.viewHolder;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.View;

import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.organize.EnterResultForGameDialog;
import madson.org.opentournament.organize.GameListAdapter;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerViewHolder extends RecyclerView.ViewHolder {

    private TextView playerNameInList;

    /**
     * used by online game list.
     *
     * @param  v
     */
    public PlayerViewHolder(View v) {

        super(v);

        playerNameInList = (TextView) v.findViewById(R.id.available_player_name);
    }

    public TextView getPlayerNameInList() {

        return playerNameInList;
    }
}
