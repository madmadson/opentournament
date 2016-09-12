package madson.org.opentournament.organize;

import android.content.Context;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.utility.BaseApplication;

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
        heading.setText(getString(R.string.heading_ranking_for_round, round));

        RankingListHeaderFragment headerFragment = new RankingListHeaderFragment();
        getChildFragmentManager().beginTransaction().add(R.id.row_ranking_header_container, headerFragment).commit();

        rankingListAdapter = new RankingListAdapter(getActivity());
        recyclerView.setAdapter(rankingListAdapter);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                RankingService rankingService = ((BaseApplication) getActivity().getApplication()).getRankingService();

                List<TournamentRanking> rankingsForRound = rankingService.getTournamentRankingForRound(tournament,
                        round);

                rankingListAdapter.setRankings(rankingsForRound);
            }
        };

        runnable.run();

        return view;
    }

    private class RankingListAdapter extends RecyclerView.Adapter<RankingListAdapter.ViewHolder> {

        private List<TournamentRanking> rankingList;
        private Context context;

        public RankingListAdapter(Context context) {

            this.context = context;

            this.rankingList = new ArrayList<>();
        }

        @Override
        public RankingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ranking, parent, false);

            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }


        @Override
        public void onBindViewHolder(RankingListAdapter.ViewHolder holder, int position) {

            final TournamentRanking ranking = rankingList.get(position);

            holder.setRanking(ranking);
            holder.getRankingNumber().setText(String.valueOf(position + 1));
            holder.getScore().setText(String.valueOf(ranking.getScore()));
            holder.getSos().setText(String.valueOf(ranking.getSos()));
            holder.getCp().setText(String.valueOf(ranking.getControl_points()));
            holder.getVp().setText(String.valueOf(ranking.getVictory_points()));

            TournamentPlayer tournamentPlayer = ranking.getTournamentPlayer();

            holder.getPlayerNameInList()
                .setText(context.getResources()
                    .getString(R.string.tournament_player_name_in_row, tournamentPlayer.getFirstname(),
                        tournamentPlayer.getNickname(), tournamentPlayer.getLastname()));

            holder.getPlayerTeamNameInList().setText(tournamentPlayer.getTeamname());
            holder.getPlayerFactionInList().setText(tournamentPlayer.getFaction());

            if (ranking.getPlayer_online_uuid() != null) {
                if (holder.getOnlineIcon() != null) {
                    holder.getOnlineIcon().setVisibility(View.VISIBLE);
                }
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

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView onlineIcon;
            private TextView playerFactionInList;
            private TextView score;
            private TextView sos;
            private TextView cp;
            private TextView vp;
            private TextView playerTeamNameInList;
            private TournamentRanking ranking;
            private TextView playerNumber;
            private TextView playerNameInList;

            public ViewHolder(View itemView) {

                super(itemView);

                playerNameInList = (TextView) itemView.findViewById(R.id.ranking_row_name);
                playerTeamNameInList = (TextView) itemView.findViewById(R.id.ranking_player_teamname);
                playerFactionInList = (TextView) itemView.findViewById(R.id.ranking_player_faction);
                playerNumber = (TextView) itemView.findViewById(R.id.ranking_row_player_number);
                score = (TextView) itemView.findViewById(R.id.ranking_row_score);
                sos = (TextView) itemView.findViewById(R.id.ranking_row_sos);
                cp = (TextView) itemView.findViewById(R.id.ranking_row_control_points);
                vp = (TextView) itemView.findViewById(R.id.ranking_row_victory_points);
                onlineIcon = (ImageView) itemView.findViewById(R.id.ranking_row_online_icon);
            }

            public void setRanking(TournamentRanking ranking) {

                this.ranking = ranking;
            }


            public TextView getRankingNumber() {

                return playerNumber;
            }


            public TextView getPlayerNameInList() {

                return playerNameInList;
            }


            public TextView getScore() {

                return score;
            }


            public TextView getSos() {

                return sos;
            }


            public TextView getCp() {

                return cp;
            }


            public TextView getVp() {

                return vp;
            }


            public TextView getPlayerTeamNameInList() {

                return playerTeamNameInList;
            }


            public ImageView getOnlineIcon() {

                return onlineIcon;
            }


            public TextView getPlayerFactionInList() {

                return playerFactionInList;
            }
        }
    }
}
