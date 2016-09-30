package madson.org.opentournament.organize;

import android.content.Context;

import android.graphics.Color;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.tasks.LoadRankingListTask;
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.viewHolder.TournamentRankingViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RankingListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND = "round";
    private Tournament tournament;
    private int round;
    private RankingListAdapter rankingListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        if (bundle != null && bundle.getInt(BUNDLE_ROUND) != 0) {
            round = bundle.getInt(BUNDLE_ROUND);
        }

        View view = inflater.inflate(R.layout.fragment_ranking_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.ranking_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        TextView heading = (TextView) view.findViewById(R.id.heading_ranking_for_round);

        if (tournament.getState().equals(Tournament.TournamentState.FINISHED.name())
                && round == tournament.getActualRound()) {
            heading.setText(getString(R.string.final_standings));
        } else {
            heading.setText(getString(R.string.heading_ranking_for_round, round));
        }

        RankingListHeaderFragment headerFragment = new RankingListHeaderFragment();
        getChildFragmentManager().beginTransaction().add(R.id.row_ranking_header_container, headerFragment).commit();

        rankingListAdapter = new RankingListAdapter(getActivity());
        recyclerView.setAdapter(rankingListAdapter);

        new LoadRankingListTask((BaseApplication) getActivity().getApplication(), tournament, round, rankingListAdapter)
            .execute();

        return view;
    }

    public class RankingListAdapter extends RecyclerView.Adapter<TournamentRankingViewHolder> {

        private List<TournamentRanking> rankingList;
        private Context context;

        public RankingListAdapter(Context context) {

            this.context = context;

            this.rankingList = new ArrayList<>();
        }

        @Override
        public TournamentRankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ranking, parent, false);

            // set the view's size, margins, paddings and layout parameters
            TournamentRankingViewHolder vh = new TournamentRankingViewHolder(v);

            return vh;
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
                    .getString(R.string.player_name_in_row, tournamentPlayer.getFirstname(),
                        tournamentPlayer.getNickname(), tournamentPlayer.getLastname()));

            holder.getPlayerTeamNameInList().setText(tournamentPlayer.getTeamname());
            holder.getPlayerFactionInList().setText(tournamentPlayer.getFaction());

            if (ranking.getPlayer_online_uuid() != null) {
                if (holder.getOnlineIcon() != null) {
                    holder.getOnlineIcon().setVisibility(View.VISIBLE);
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


        public void setRankings(List<TournamentRanking> rankingsForRound) {

            rankingList = rankingsForRound;
            notifyDataSetChanged();
        }
    }
}
