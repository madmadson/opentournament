package madson.org.opentournament.organize;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Color;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.tasks.LoadRankingListTask;
import madson.org.opentournament.tasks.TournamentUploadTask;
import madson.org.opentournament.tasks.UndoEndTournamentTask;
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
    private Button uploadGamesButton;
    private Button undoEndTournamentButton;

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

        undoEndTournamentButton = (Button) view.findViewById(R.id.button_undo_end_tournament);
        undoEndTournamentButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.confirm_undo_round)
                    .setView(R.layout.dialog_undo_round)
                    .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    new UndoEndTournamentTask(baseActivity, tournament, round).execute();
                                }
                            })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
                }
            });

        uploadGamesButton = (Button) view.findViewById(R.id.button_upload_games);
        uploadGamesButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (baseActivity.isConnected()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.confirm_upload_tournament)
                        .setView(R.layout.dialog_upload_games)
                        .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Toolbar toolbar = baseActivity.getToolbar();
                                        ProgressBar progressBar = (ProgressBar) toolbar.findViewById(
                                                R.id.toolbar_progress_bar);

                                        new TournamentUploadTask(baseActivity, tournament, progressBar).execute();
                                    }
                                })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.offline_text).setPositiveButton(R.string.dialog_confirm, null).show();
                    }
                }
            });

        if (tournament.getState().equals(Tournament.TournamentState.FINISHED.name())
                && round == tournament.getActualRound()) {
            heading.setText(getString(R.string.final_standings));
            undoEndTournamentButton.setVisibility(View.VISIBLE);
            uploadGamesButton.setVisibility(View.VISIBLE);
        } else {
            heading.setText(R.string.heading_ranking_for_round);
            undoEndTournamentButton.setVisibility(View.GONE);
            uploadGamesButton.setVisibility(View.GONE);
        }

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
