package madson.org.opentournament.players;

import android.content.Context;
import android.content.Intent;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.PlayerComparator;
import madson.org.opentournament.online.OnlineTournamentActivity;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.PlayerViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerViewHolder> {

    private List<Player> playerList;

    private BaseActivity baseActivity;

    public PlayerListAdapter(BaseActivity baseActivity) {

        this.baseActivity = baseActivity;

        this.playerList = new ArrayList<>();
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player, parent, false);

        return new PlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {

        final Player player = playerList.get(position);

        holder.getPlayerNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player.getFirstName(), player.getNickName(),
                    player.getLastName()));

        if (position % 2 == 0) {
            holder.getPlayerCardLayout()
                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightGrey));
        } else {
            holder.getPlayerCardLayout()
                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostWhite));
        }

        holder.getPlayerCardLayout().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(baseActivity, PlayerTournamentListActivity.class);
                    intent.putExtra(PlayerTournamentListActivity.EXTRA_PLAYER, player);
                    baseActivity.startActivity(intent);
                }
            });
    }


    @Override
    public int getItemCount() {

        return playerList.size();
    }


    public void addPlayer(Player player) {

        Log.i(this.getClass().getName(), "player added to adapter ");

        playerList.add(player);
        Collections.sort(playerList, new PlayerComparator());
        notifyDataSetChanged();
    }


    public void removePlayer(Player player) {

        Log.i(this.getClass().getName(), "player added to adapter ");

        playerList.remove(player);
        notifyDataSetChanged();
    }


    public void updatePlayer(Player player) {

        int index = playerList.indexOf(player);

        playerList.set(index, player);
        Collections.sort(playerList, new PlayerComparator());
        notifyDataSetChanged();
    }
}
