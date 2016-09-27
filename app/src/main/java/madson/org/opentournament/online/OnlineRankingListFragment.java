package madson.org.opentournament.online;

import android.graphics.Color;

import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Handler;

import android.service.notification.NotificationListenerService;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.db.GameTable;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentRanking;
import madson.org.opentournament.viewHolder.GameViewHolder;
import madson.org.opentournament.viewHolder.TournamentRankingViewHolder;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineRankingListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_UUID = "tournament_uuid";
    public static final String BUNDLE_ROUND = "round";

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<TournamentRanking, TournamentRankingViewHolder> mFirebaseAdapter;
    private String tournament_uuid;
    private int round;
    private ProgressBar mProgressBar;

    public static Fragment newInstance(int round, String tournament_uuid) {

        OnlineRankingListFragment fragment = new OnlineRankingListFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_TOURNAMENT_UUID, tournament_uuid);
        args.putInt(BUNDLE_ROUND, round);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getString(BUNDLE_TOURNAMENT_UUID) != null) {
            tournament_uuid = bundle.getString(BUNDLE_TOURNAMENT_UUID);
        }

        if (bundle != null && bundle.getInt(BUNDLE_ROUND) != 0) {
            round = bundle.getInt(BUNDLE_ROUND);
        }

        View view = inflater.inflate(R.layout.fragment_online_ranking_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.ranking_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_RANKINGS + "/"
                + tournament_uuid + "/" + round);

        Query orderedGames = child.orderByChild("rank");

        mFirebaseAdapter = new FirebaseRecyclerAdapter<TournamentRanking, TournamentRankingViewHolder>(
                TournamentRanking.class, R.layout.row_ranking, TournamentRankingViewHolder.class, orderedGames) {

            @Override
            protected void populateViewHolder(TournamentRankingViewHolder holder, TournamentRanking ranking,
                int position) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                holder.getRankingNumber().setText(String.valueOf(position + 1));
                holder.getScore().setText(String.valueOf(ranking.getScore()));
                holder.getSos().setText(String.valueOf(ranking.getSos()));
                holder.getCp().setText(String.valueOf(ranking.getControl_points()));
                holder.getVp().setText(String.valueOf(ranking.getVictory_points()));

                TournamentPlayer tournamentPlayer = ranking.getTournamentPlayer();

                holder.getPlayerNameInList()
                    .setText(getActivity().getResources()
                        .getString(R.string.tournament_player_name_in_row, tournamentPlayer.getFirstname(),
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
                        .setText(getActivity().getResources()
                            .getString(R.string.dropped_in_round, ranking.getTournamentPlayer().getDroppedInRound()));
                    holder.getDroppedInRound().setVisibility(View.VISIBLE);
                }

                if (position % 2 == 0) {
                    holder.getRankingCard().setCardBackgroundColor(Color.LTGRAY);
                } else {
                    holder.getRankingCard().setCardBackgroundColor(Color.WHITE);
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {

                    super.onItemRangeRemoved(positionStart, itemCount);

                    if (mFirebaseAdapter.getItemCount() == 0) {
                    }
                }


                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {

                    super.onItemRangeInserted(positionStart, itemCount);

                    if (mFirebaseAdapter.getItemCount() != 0) {
                    }
                }
            });

        recyclerView.setAdapter(mFirebaseAdapter);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (mFirebaseAdapter.getItemCount() == 0) {
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    }

                    mFirebaseAdapter.notifyDataSetChanged();
                }
            }, 5000);
        mFirebaseAdapter.notifyDataSetChanged();

        return view;
    }
}
