package madson.org.opentournament.organize.setup;

import android.content.DialogInterface;

import android.graphics.Color;

import android.support.v7.app.AlertDialog;
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
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.tasks.CheckPlayerAlreadyInTournamentTask;
import madson.org.opentournament.tasks.DeleteLocalPlayerTask;
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
    private Tournament tournament;
    private List<Player> originalPlayerList;
    private List<Player> filteredPlayerList;
    private ItemFilter mFilter = new ItemFilter();

    public LocalPlayerListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;

        this.originalPlayerList = new ArrayList<>();
        this.filteredPlayerList = new ArrayList<>();
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player, parent, false);

        return new PlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {

        final Player player = filteredPlayerList.get(position);

        holder.getPlayerNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player.getFirstName(), player.getNickName(),
                    player.getLastName()));

        if (position % 2 == 0) {
            holder.getPlayerCardLayout().setBackgroundColor(Color.LTGRAY);
        } else {
            holder.getPlayerCardLayout().setBackgroundColor(Color.WHITE);
        }

        holder.getPlayerCardLayout().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    new CheckPlayerAlreadyInTournamentTask(baseActivity, tournament, player).execute();
                }
            });

        holder.getLocalIcon().setVisibility(View.VISIBLE);
        holder.getDeleteIcon().setVisibility(View.VISIBLE);
        holder.getDeleteIcon().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                    builder.setTitle(R.string.really_delete_local_player)
                    .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    new DeleteLocalPlayerTask(baseActivity, player).execute();
                                }
                            })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
                }
            });
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


    public void removeTournamentPlayer(TournamentPlayer tournamentPlayer) {

        Player player = new Player();
        player.setUUID(tournamentPlayer.getPlayerUUID());

        if (originalPlayerList.contains(player)) {
            int position = originalPlayerList.indexOf(player);

            originalPlayerList.remove(position);
            notifyDataSetChanged();
        }
    }


    @Override
    public Filter getFilter() {

        return mFilter;
    }


    public void removePlayer(Player player) {

        if (originalPlayerList.contains(player)) {
            int position = originalPlayerList.indexOf(player);

            originalPlayerList.remove(position);
            notifyDataSetChanged();
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
