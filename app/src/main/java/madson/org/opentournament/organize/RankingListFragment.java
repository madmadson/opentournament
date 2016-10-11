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
import madson.org.opentournament.domain.TournamentParticipant;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.domain.TournamentTeam;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.tasks.LoadRankingListTask;
import madson.org.opentournament.utility.BaseActivity;
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
    private BaseActivity baseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    public static RankingListFragment newInstance(int roundNumber, Tournament tournament) {

        RankingListFragment fragment = new RankingListFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ROUND, roundNumber);
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
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
            heading.setText(R.string.heading_ranking_for_round);
        }

        RankingListHeaderFragment headerFragment = new RankingListHeaderFragment();
        getChildFragmentManager().beginTransaction().add(R.id.row_ranking_header_container, headerFragment).commit();

        rankingListAdapter = new RankingListAdapter(baseActivity.getBaseApplication());
        recyclerView.setAdapter(rankingListAdapter);

        new LoadRankingListTask(baseActivity.getBaseApplication(), tournament, round, rankingListAdapter).execute();

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

            if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                rankingForSoloTournament(holder, ranking);
            } else {
                rankingForTeamTournament(holder, ranking);
            }

            if (position % 2 == 0) {
                holder.getRankingCard().setCardBackgroundColor(Color.LTGRAY);
            } else {
                holder.getRankingCard().setCardBackgroundColor(Color.WHITE);
            }
        }


        private void rankingForTeamTournament(TournamentRankingViewHolder holder, TournamentRanking ranking) {

            holder.getPlayerNameInList().setText(ranking.getParticipantUUID());
            holder.getPlayerTeamNameInList().setVisibility(View.GONE);
            holder.getPlayerFactionInList().setVisibility(View.GONE);
        }


        private void rankingForSoloTournament(TournamentRankingViewHolder holder, TournamentRanking ranking) {

            TournamentPlayer tournamentPlayer = (TournamentPlayer) ranking.getTournamentParticipant();

            holder.getPlayerNameInList()
                .setText(context.getResources()
                    .getString(R.string.player_name_in_row, tournamentPlayer.getFirstName(),
                        tournamentPlayer.getNickName(), tournamentPlayer.getLastName()));

            holder.getPlayerTeamNameInList().setText(tournamentPlayer.getTeamName());
            holder.getPlayerFactionInList().setText(tournamentPlayer.getFaction());

            if (tournamentPlayer.isLocal()) {
                if (holder.getOfflineIcon() != null) {
                    holder.getOfflineIcon().setVisibility(View.VISIBLE);
                }
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


        public void setRankings(List<TournamentRanking> rankingsForRound) {

            rankingList = rankingsForRound;
            notifyDataSetChanged();
        }
    }
}
