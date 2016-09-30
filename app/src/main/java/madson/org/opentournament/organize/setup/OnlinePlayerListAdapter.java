package madson.org.opentournament.organize.setup;

import android.content.Context;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlinePlayerListAdapter extends RecyclerView.Adapter<OnlinePlayerListAdapter.PlayerViewHolder> {

    private List<Player> originalPlayerList;
    private List<Player> filteredPlayerList;

    private ItemFilter filter;
    private BaseActivity baseActivity;

    public OnlinePlayerListAdapter(BaseActivity baseActivity) {

        this.baseActivity = baseActivity;

        this.originalPlayerList = new ArrayList<>();
        this.filteredPlayerList = new ArrayList<>();
        this.filter = new ItemFilter();
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_online_player, parent, false);

        return new PlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {

        final Player player = filteredPlayerList.get(position);
        holder.setPlayer(player);
        holder.getPlayerFullNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.tournament_player_name_in_row, player.getFirstname(), player.getNickname(),
                    player.getLastname()));
    }


    @Override
    public int getItemCount() {

        return filteredPlayerList.size();
    }


    public void addPlayer(Player player) {

        Log.i(this.getClass().getName(), "player added to adapter ");

        this.originalPlayerList.add(player);
        notifyDataSetChanged();
    }


    public void removePlayer(Player player) {

        int position = originalPlayerList.indexOf(player);

        originalPlayerList.remove(position);
        notifyDataSetChanged();
    }


    public Filter getFilter() {

        return filter;
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView playerFullNameInList;
        private Player player;

        public PlayerViewHolder(View v) {

            super(v);
            v.setOnClickListener(this);

            playerFullNameInList = (TextView) v.findViewById(R.id.online_player_full_name);
        }

        @Override
        public void onClick(View v) {

            Log.i(v.getClass().getName(), "click available online player : " + player);

            baseActivity.getBaseApplication().notifyPlayerAddToTournament(player);
        }


        public TextView getPlayerFullNameInList() {

            return playerFullNameInList;
        }


        public void setPlayer(Player player) {

            this.player = player;
        }
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
            final ArrayList<Player> newListOfPlayers = new ArrayList<>(count);

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
