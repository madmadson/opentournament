package madson.org.opentournament.organize.setup;

import android.content.Context;

import android.graphics.Color;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.viewHolder.TournamentPlayerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerListAdapter extends RecyclerView.Adapter<TournamentPlayerViewHolder> {

    private Context context;
    private TournamentSetupEventListener mListener;
    private List<TournamentPlayer> tournamentPlayerList = new ArrayList<>();

    public TournamentPlayerListAdapter(Context context, TournamentSetupEventListener mListener) {

        this.context = context;

        this.mListener = mListener;
    }

    @Override
    public TournamentPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament_player, parent, false);

        TournamentPlayerViewHolder vh = new TournamentPlayerViewHolder(this, v);

        return vh;
    }


    @Override
    public void onBindViewHolder(TournamentPlayerViewHolder holder, int position) {

        final TournamentPlayer player = tournamentPlayerList.get(position);
        holder.setPlayer(player);
        holder.getPlayerNumber().setText(String.valueOf(position + 1));
        holder.getTeamName().setText(player.getTeamname());
        holder.getFaction().setText(player.getFaction());

        String firstname = player.getFirstname();
        String nickname = player.getNickname();
        String lastname = player.getLastname();
        holder.getPlayerNameInList()
            .setText(context.getResources()
                .getString(R.string.tournament_player_name_in_row, firstname, nickname, lastname));

        // mark online player
        if (player.getPlayer_online_uuid() != null) {
            holder.getOnlineIcon().setVisibility(View.VISIBLE);
        } else {
            holder.getOnlineIcon().setVisibility(View.GONE);
        }

        if (player.getDroppedInRound() != 0) {
            holder.getDroppedInRound()
                .setText(context.getResources().getString(R.string.dropped_in_round, player.getDroppedInRound()));
            holder.getDroppedInRound().setVisibility(View.VISIBLE);
        }

        if (position % 2 == 0) {
            holder.getTournamentPlayerCard().setCardBackgroundColor(Color.LTGRAY);
        } else {
            holder.getTournamentPlayerCard().setCardBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {

        return tournamentPlayerList.size();
    }


    /**
     * Add new tournament player.
     *
     * @param  tournamentPlayer
     */
    public void addTournamentPlayer(TournamentPlayer tournamentPlayer) {

        if (!tournamentPlayerList.contains(tournamentPlayer)) {
            tournamentPlayerList.add(tournamentPlayer);
            notifyDataSetChanged();
        }
    }


    public void removeTournamentPlayer(TournamentPlayer tournamentPlayer) {

        int position = tournamentPlayerList.indexOf(tournamentPlayer);
        tournamentPlayerList.remove(position);
        notifyDataSetChanged();
    }


    public boolean contains(TournamentPlayer player) {

        if (player != null) {
            return tournamentPlayerList.contains(player);
        } else {
            return false;
        }
    }


    public void addTournamentPlayers(List<TournamentPlayer> localTournamentPlayers) {

        tournamentPlayerList = localTournamentPlayers;
        notifyDataSetChanged();
    }


    public TournamentSetupEventListener getmListener() {

        return mListener;
    }


    public void updateTournamentPlayer(TournamentPlayer tournamentPlayer) {

        if (tournamentPlayerList.contains(tournamentPlayer)) {
            tournamentPlayerList.remove(tournamentPlayer);
            tournamentPlayerList.add(tournamentPlayer);
            notifyDataSetChanged();
        }
    }
}
