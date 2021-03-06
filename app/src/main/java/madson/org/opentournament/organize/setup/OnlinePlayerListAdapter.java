package madson.org.opentournament.organize.setup;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.tasks.CheckPlayerAlreadyInTournamentTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.PlayerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlinePlayerListAdapter extends RecyclerView.Adapter<PlayerViewHolder> {

    private List<Player> originalPlayerList;
    private List<Player> filteredPlayerList;

    private ItemFilter filter;
    private BaseActivity baseActivity;
    private Tournament tournament;

    public OnlinePlayerListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;

        this.originalPlayerList = new ArrayList<>();
        this.filteredPlayerList = new ArrayList<>();
        this.filter = new ItemFilter();
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player, parent, false);

        return new PlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {

        final Player player = filteredPlayerList.get(position);

        holder.getPlayerNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player.getFirstName(), player.getNickName(),
                    player.getLastName()));

        if (player.getMeta() != null) {
            if (!player.getMeta().isEmpty()) {
                holder.getPlayerAffiliation().setText(player.getMeta());
                holder.getPlayerAffiliation().setVisibility(View.VISIBLE);
            } else {
                holder.getPlayerAffiliation().setVisibility(View.GONE);
            }
        } else {
            holder.getPlayerAffiliation().setVisibility(View.GONE);
        }

        if (player.getGamesCounter() >= 5) {
            holder.getPlayerElo().setText(String.valueOf(player.getElo()));
            holder.getPlayerElo().setVisibility(View.VISIBLE);
            holder.getEloIcon().setVisibility(View.VISIBLE);
        } else {
            holder.getPlayerElo().setVisibility(View.GONE);
            holder.getEloIcon().setVisibility(View.GONE);
        }

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

                    new CheckPlayerAlreadyInTournamentTask(baseActivity, tournament, player).execute();
                }
            });
    }


    @Override
    public int getItemCount() {

        return filteredPlayerList.size();
    }


    public void addPlayer(Player player) {

        this.originalPlayerList.add(player);
        notifyDataSetChanged();
    }


    public void removeTournamentPlayer(TournamentPlayer tournamentPlayer) {

        Player player = new Player();
        player.setUUID(tournamentPlayer.getPlayerUUID());

        if (originalPlayerList.contains(player)) {
            int position = originalPlayerList.indexOf(player);

            originalPlayerList.remove(position);
            notifyDataSetChanged();
        }
    }


    public Filter getFilter() {

        return filter;
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

                if (filterablePlayer.getFirstName().toLowerCase().contains(filterString)
                        || filterablePlayer.getNickName().toLowerCase().contains(filterString)
                        || filterablePlayer.getLastName().toLowerCase().contains(filterString)) {
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
