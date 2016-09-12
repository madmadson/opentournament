package madson.org.opentournament.organize.setup;

import android.content.Context;

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

import java.util.ArrayList;
import java.util.List;


/**
 * adapter for local player list.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LocalPlayerListAdapter extends RecyclerView.Adapter<LocalPlayerListAdapter.ViewHolder>
    implements Filterable {

    private Context context;
    private TournamentSetupEventListener mListener;
    private List<Player> originalPlayerList;
    private List<Player> filteredPlayerList;
    private ItemFilter mFilter = new ItemFilter();

    /**
     * @param  mListener  maybe null when no listener is needed
     */
    public LocalPlayerListAdapter(Context context, TournamentSetupEventListener mListener) {

        this.context = context;

        this.mListener = mListener;
        this.originalPlayerList = new ArrayList<>();
        this.filteredPlayerList = new ArrayList<>();
    }

    @Override
    public LocalPlayerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_available_player, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(LocalPlayerListAdapter.ViewHolder holder, int position) {

        final Player player = filteredPlayerList.get(position);
        holder.setPlayer(player);
        holder.getPlayerNameInList()
            .setText(context.getResources()
                .getString(R.string.tournament_player_name_in_row, player.getFirstname(), player.getNickname(),
                    player.getLastname()));
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

            Log.i(v.getClass().getName(), "click available offline player : " + player);

            // call listener if set
            if (mListener != null) {
                mListener.clickAvailablePlayerListItem(player);
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
