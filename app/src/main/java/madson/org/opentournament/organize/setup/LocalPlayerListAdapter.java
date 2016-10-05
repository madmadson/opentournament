package madson.org.opentournament.organize.setup;

import android.graphics.Color;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.PlayerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * adapter for local player list.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LocalPlayerListAdapter extends RecyclerView.Adapter<PlayerViewHolder> implements Filterable {

    private BaseActivity baseActivity;
    private List<Player> originalPlayerList;
    private List<Player> filteredPlayerList;
    private ItemFilter mFilter = new ItemFilter();

    public LocalPlayerListAdapter(BaseActivity baseActivity) {

        this.baseActivity = baseActivity;

        this.originalPlayerList = new ArrayList<>();
        this.filteredPlayerList = new ArrayList<>();
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return new PlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {

        final Player player = filteredPlayerList.get(position);

        holder.getPlayerNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player.getFirstname(), player.getNickname(),
                    player.getLastname()));

        if (position % 2 == 0) {
            holder.getPlayerCardLayout().setBackgroundColor(Color.LTGRAY);
        } else {
            holder.getPlayerCardLayout().setBackgroundColor(Color.WHITE);
        }

        holder.getPlayerCardLayout().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    baseActivity.getBaseApplication().notifyPlayerAddToTournament(player);
                }
            });

        holder.getLocalIcon().setVisibility(View.VISIBLE);
    }


    @Override
    public int getItemCount() {

        return filteredPlayerList.size();
    }


    public void add(Player item) {

        originalPlayerList.add(item);
    }


    public void addPlayerList(List<Player> allPlayers) {

        originalPlayerList = allPlayers;
        notifyDataSetChanged();
    }


    public int removePlayer(Player player) {

        int position = originalPlayerList.indexOf(player);
        originalPlayerList.remove(position);

        return position;
    }


    @Override
    public Filter getFilter() {

        return mFilter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            if (filterString.isEmpty()) {
                results.values = originalPlayerList;
                results.count = originalPlayerList.size();

                return results;
            }

            final List<Player> list = originalPlayerList;

            int count = list.size();
            final List<Player> newListOfPlayers = new ArrayList<>(count);

            Player filterablePlayer;

            for (int i = 0; i < count; i++) {
                filterablePlayer = list.get(i);

                if (filterablePlayer.getFirstname().toLowerCase().contains(filterString)
                        || filterablePlayer.getNickname().toLowerCase().contains(filterString)
                        || filterablePlayer.getLastname().toLowerCase().contains(filterString)) {
                    Log.i(this.getClass().getName(), "addTournamentPlayer to players: " + filterablePlayer.toString());
                    newListOfPlayers.add(filterablePlayer);
                }
            }

            results.values = newListOfPlayers;
            results.count = newListOfPlayers.size();

            return results;
        }


        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredPlayerList = (ArrayList<Player>) results.values;
            notifyDataSetChanged();
        }
    }
}
