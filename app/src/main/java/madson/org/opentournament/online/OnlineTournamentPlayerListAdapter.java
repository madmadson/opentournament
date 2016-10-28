package madson.org.opentournament.online;

import android.content.Context;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.viewHolder.TournamentPlayerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineTournamentPlayerListAdapter extends RecyclerView.Adapter<TournamentPlayerViewHolder> {

    private List<TournamentPlayer> playerList;

    private Context context;

    public OnlineTournamentPlayerListAdapter(Context context) {

        this.context = context;

        this.playerList = new ArrayList<>();
    }

    @Override
    public TournamentPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament_player, parent, false);

        return new TournamentPlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(TournamentPlayerViewHolder viewHolder, int position) {

        final TournamentPlayer player = playerList.get(position);

        viewHolder.getPlayerNumber().setText(String.valueOf(position + 1));
        viewHolder.getTeamName().setText(player.getTeamName());
        viewHolder.getFaction().setText(player.getFaction());

        viewHolder.getPlayerNameInList()
            .setText(context.getResources()
                .getString(R.string.player_name_in_row, player.getLastNameWithMaximumCharacters(10),
                    player.getNickNameWithMaximumCharacters(10), player.getLastNameWithMaximumCharacters(10)));

        if (player.getMeta() != null) {
            viewHolder.getAffiliation().setText(player.getMeta());
            viewHolder.getAffiliation().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getAffiliation().setVisibility(View.GONE);
        }

        if (player.getDroppedInRound() != 0) {
            viewHolder.getDroppedInRound()
                .setText(context.getResources().getString(R.string.dropped_in_round, player.getDroppedInRound()));
            viewHolder.getDroppedInRound().setVisibility(View.VISIBLE);
        }

        if (position % 2 == 0) {
            viewHolder.getTournamentPlayerCard()
                .setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGrey));
        } else {
            viewHolder.getTournamentPlayerCard()
                .setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAlmostWhite));
        }

        if (viewHolder.getEditIcon() != null) {
            viewHolder.getEditIcon().setVisibility(View.GONE);
        }

        if (viewHolder.getAddListIcon() != null) {
            viewHolder.getAddListIcon().setVisibility(View.GONE);
        }

        if (viewHolder.getLocalIcon() != null) {
            viewHolder.getEditIcon().setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {

        return playerList.size();
    }


    public void addPlayer(TournamentPlayer player) {

        Log.i(this.getClass().getName(), "player added to adapter ");

        playerList.add(player);
        notifyDataSetChanged();
    }
}
