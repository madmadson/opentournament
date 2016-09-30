package madson.org.opentournament.players;

import android.content.Context;

import android.graphics.Color;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.viewHolder.PlayerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerViewHolder> {

    private List<Player> playerList;

    private Context context;

    public PlayerListAdapter(Context context) {

        this.context = context;

        this.playerList = new ArrayList<>();
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_available_player, parent, false);

        return new PlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {

        final Player player = playerList.get(position);

        holder.getPlayerNameInList()
            .setText(context.getResources()
                .getString(R.string.tournament_player_name_in_row, player.getFirstname(), player.getNickname(),
                    player.getLastname()));

        if (position % 2 == 0) {
            holder.getPlayerCardLayout().setBackgroundColor(Color.LTGRAY);
        } else {
            holder.getPlayerCardLayout().setBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {

        return playerList.size();
    }


    public void addPlayer(Player player) {

        Log.i(this.getClass().getName(), "player added to adapter ");

        playerList.add(player);
        notifyDataSetChanged();
    }
}
