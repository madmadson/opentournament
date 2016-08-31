package madson.org.opentournament.players;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;

import java.util.ArrayList;
import java.util.List;


/**
 * adapter for player list.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class AvailablePlayerListAdapter extends RecyclerView.Adapter<AvailablePlayerListAdapter.ViewHolder>
    implements Filterable {

    private AvailablePlayerListFragment.AvailablePlayerListItemListener mListener;
    private List<Player> originalPlayerList;
    private List<Player> filteredPlayerList;
    private ItemFilter mFilter = new ItemFilter();

    /**
     * @param  playerList  list of players to show
     * @param  mListener  maybe null when no listener is needed
     */
    public AvailablePlayerListAdapter(List<Player> playerList,
        AvailablePlayerListFragment.AvailablePlayerListItemListener mListener) {

        this.mListener = mListener;
        this.originalPlayerList = playerList;
        this.filteredPlayerList = playerList;
    }

    @Override
    public AvailablePlayerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_available_player, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(AvailablePlayerListAdapter.ViewHolder holder, int position) {

        final Player player = filteredPlayerList.get(position);
        holder.setPlayer(player);
        holder.getPlayerNameInList()
            .setText(player.getFirstname() + " \"" + player.getNickname() + "\" " + player.getLastname());
    }


    @Override
    public int getItemCount() {

        return filteredPlayerList.size();
    }


    public void add(Player item) {

        originalPlayerList.add(item);
        notifyDataSetChanged();
    }


    public void remove(Tournament item) {

        int position = filteredPlayerList.indexOf(item);
        filteredPlayerList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public Filter getFilter() {

        return mFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView playerNameInList;
        private Player player;

        public ViewHolder(CardView v) {

            super(v);
            v.setOnClickListener(this);

            playerNameInList = (TextView) v.findViewById(R.id.available_player_name);
        }

        public TextView getPlayerNameInList() {

            return playerNameInList;
        }


        public void setPlayer(Player player) {

            this.player = player;
        }


        @Override
        public void onClick(View v) {

            Log.i(v.getClass().getName(), "remove player from player list: " + player);

            filteredPlayerList.remove(player);
            notifyDataSetChanged();

            // call listener if set
            if (mListener != null) {
                mListener.onAvailablePlayerListItemClicked(player);
            }
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
                    Log.i(this.getClass().getName(), "add to players: " + filterablePlayer.toString());
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
