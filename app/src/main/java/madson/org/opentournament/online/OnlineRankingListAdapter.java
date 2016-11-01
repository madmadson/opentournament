package madson.org.opentournament.online;

import android.content.Context;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.domain.TournamentTyp;
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
    private Tournament tournament;

    public OnlineRankingListAdapter(Context context, Tournament tournament) {

        this.context = context;
        this.tournament = tournament;

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

        if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
            rankingForSoloTournament(holder, ranking);
        } else {
            rankingForTeamTournament(holder, ranking);
        }

        if (position % 2 == 0) {
            holder.getRankingCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorLightGrey));
        } else {
            holder.getRankingCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAlmostWhite));
        }
    }


    private void rankingForTeamTournament(TournamentRankingViewHolder holder, TournamentRanking ranking) {

        holder.getPlayerNameInList().setText(ranking.getParticipantUUID());
        holder.getPlayerTeamNameInList().setVisibility(View.GONE);
        holder.getPlayerElo().setVisibility(View.GONE);
        holder.getPlayerEloIcon().setVisibility(View.GONE);
    }


    private void rankingForSoloTournament(TournamentRankingViewHolder holder, TournamentRanking ranking) {

        TournamentPlayer tournamentPlayer = ranking.getTournamentPlayer();

        holder.getPlayerNameInList()
            .setText(context.getResources()
                .getString(R.string.player_name_in_row, tournamentPlayer.getLastNameWithMaximumCharacters(10),
                    tournamentPlayer.getNickNameWithMaximumCharacters(10),
                    tournamentPlayer.getLastNameWithMaximumCharacters(10)));

        holder.getPlayerFactionInList().setText(tournamentPlayer.getFaction());

        if (tournamentPlayer.isLocal()) {
            if (holder.getOfflineIcon() != null) {
                holder.getOfflineIcon().setVisibility(View.VISIBLE);
            }
        }

        if (tournamentPlayer.getTeamName() != null) {
            if (!tournamentPlayer.getTeamName().isEmpty()) {
                holder.getPlayerTeamNameInList().setText(tournamentPlayer.getTeamName());
                holder.getPlayerTeamNameInList().setVisibility(View.VISIBLE);
            } else {
                holder.getPlayerTeamNameInList().setVisibility(View.GONE);
            }
        } else {
            holder.getPlayerTeamNameInList().setVisibility(View.GONE);
        }

        if (tournamentPlayer.getMeta() != null) {
            if (!tournamentPlayer.getMeta().isEmpty()) {
                holder.getPlayerAffiliation().setText(tournamentPlayer.getMeta());
                holder.getPlayerAffiliation().setVisibility(View.VISIBLE);
            } else {
                holder.getPlayerAffiliation().setVisibility(View.GONE);
            }
        } else {
            holder.getPlayerAffiliation().setVisibility(View.GONE);
        }

        if (tournamentPlayer.getGamesCounter() >= 5) {
            holder.getPlayerElo().setText(String.valueOf(tournamentPlayer.getElo()));
            holder.getPlayerElo().setVisibility(View.VISIBLE);
            holder.getPlayerEloIcon().setVisibility(View.VISIBLE);
        } else {
            holder.getPlayerElo().setVisibility(View.GONE);
            holder.getPlayerEloIcon().setVisibility(View.GONE);
        }

        if (holder.getDroppedInRound() != null && tournamentPlayer.getDroppedInRound() != 0) {
            holder.getDroppedInRound()
                .setText(context.getResources()
                    .getString(R.string.dropped_in_round, tournamentPlayer.getDroppedInRound()));
            holder.getDroppedInRound().setVisibility(View.VISIBLE);
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
