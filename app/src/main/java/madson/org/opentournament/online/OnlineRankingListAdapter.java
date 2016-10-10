package madson.org.opentournament.online;

import android.content.Context;

import android.graphics.Color;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.viewHolder.TournamentRankingViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineRankingListAdapter extends RecyclerView.Adapter<TournamentRankingViewHolder> {

    private List<TournamentRanking> rankingList;

    private Context context;

    public OnlineRankingListAdapter(Context context) {

        this.context = context;

        this.rankingList = new ArrayList<>();
    }

    @Override
    public TournamentRankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ranking, parent, false);

        return new TournamentRankingViewHolder(v);
    }


    @Override
    public void onBindViewHolder(TournamentRankingViewHolder holder, int position) {

        final TournamentRanking ranking = rankingList.get(position);

        holder.getRankingNumber().setText(String.valueOf(position + 1));
        holder.getScore().setText(String.valueOf(ranking.getScore()));
        holder.getSos().setText(String.valueOf(ranking.getSos()));
        holder.getCp().setText(String.valueOf(ranking.getControl_points()));
        holder.getVp().setText(String.valueOf(ranking.getVictory_points()));

        TournamentPlayer tournamentPlayer = ranking.getTournamentPlayer();

        holder.getPlayerNameInList()
            .setText(context.getResources()
                .getString(R.string.player_name_in_row, tournamentPlayer.getFirstName(), tournamentPlayer.getNickName(),
                    tournamentPlayer.getLastName()));

        holder.getPlayerTeamNameInList().setText(tournamentPlayer.getTeamName());
        holder.getPlayerFactionInList().setText(tournamentPlayer.getFaction());

        if (ranking.getTournamentPlayer().isLocal()) {
            if (holder.getOfflineIcon() != null) {
                holder.getOfflineIcon().setVisibility(View.VISIBLE);
            }
        }

        if (ranking.getTournamentPlayer().getDroppedInRound() != 0) {
            holder.getDroppedInRound()
                .setText(context.getResources()
                    .getString(R.string.dropped_in_round, ranking.getTournamentPlayer().getDroppedInRound()));
            holder.getDroppedInRound().setVisibility(View.VISIBLE);
        }

        if (position % 2 == 0) {
            holder.getRankingCard().setCardBackgroundColor(Color.LTGRAY);
        } else {
            holder.getRankingCard().setCardBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {

        return rankingList.size();
    }


    public void addRanking(TournamentRanking ranking) {

        rankingList.add(ranking);
        notifyDataSetChanged();
    }
}
