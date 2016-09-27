package madson.org.opentournament.online;

import android.graphics.Color;

import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Handler;

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
import madson.org.opentournament.organize.GameListAdapter;
import madson.org.opentournament.viewHolder.GameViewHolder;
import madson.org.opentournament.viewHolder.TournamentPlayerViewHolder;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineGamesListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_UUID = "tournament_uuid";
    public static final String BUNDLE_ROUND = "round";

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Game, GameViewHolder> mFirebaseAdapter;
    private String tournament_uuid;
    private int round;
    private ProgressBar mProgressBar;
    private Drawable winnerShape;
    private Drawable looserShape;

    public static Fragment newInstance(int round, String tournament_uuid) {

        OnlineGamesListFragment fragment = new OnlineGamesListFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_TOURNAMENT_UUID, tournament_uuid);
        args.putInt(BUNDLE_ROUND, round);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        winnerShape = getActivity().getResources().getDrawable(R.drawable.shape_winner);
        looserShape = getActivity().getResources().getDrawable(R.drawable.shape_looser);

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getString(BUNDLE_TOURNAMENT_UUID) != null) {
            tournament_uuid = bundle.getString(BUNDLE_TOURNAMENT_UUID);
        }

        if (bundle != null && bundle.getInt(BUNDLE_ROUND) != 0) {
            round = bundle.getInt(BUNDLE_ROUND);
        }

        View view = inflater.inflate(R.layout.fragment_online_games_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.games_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_GAMES + "/"
                + tournament_uuid + "/" + round);

        Query orderedGames = child.orderByChild(GameTable.COLUMN_PLAYING_FIELD);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Game, GameViewHolder>(Game.class, R.layout.row_game,
                GameViewHolder.class, orderedGames) {

            @Override
            protected void populateViewHolder(GameViewHolder holder, Game game, int position) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                holder.setGame(game);

                holder.getTableNumber()
                    .setText(getActivity().getResources().getString(R.string.table_number, game.getPlaying_field()));

                if (game.isFinished()) {
                    holder.getPlayerOneCardView()
                        .setBackgroundDrawable(game.getPlayer_one_score() == 1 ? winnerShape : looserShape);
                    holder.getPlayerTwoCardView()
                        .setBackgroundDrawable(game.getPlayer_two_score() == 1 ? winnerShape : looserShape);
                }

                TournamentPlayer player1 = game.getPlayer1();

                holder.getPlayerOneNameInList()
                    .setText(getActivity().getResources()
                        .getString(R.string.tournament_player_name_in_row, player1.getFirstname(),
                            player1.getNickname(), player1.getLastname()));
                holder.getPlayerOneFaction().setText(player1.getFaction());

                holder.getPlayerOneScore()
                    .setText(getActivity().getResources().getString(R.string.game_win, game.getPlayer_one_score()));
                holder.getPlayerOneControlPoints()
                    .setText(getActivity().getResources()
                        .getString(R.string.game_cp, game.getPlayer_one_control_points()));
                holder.getPlayerOneVictoryPoints()
                    .setText(getActivity().getResources()
                        .getString(R.string.game_vp, game.getPlayer_one_victory_points()));

                TournamentPlayer player2 = game.getPlayer2();

                holder.getPlayerTwoNameInList()
                    .setText(getActivity().getResources()
                        .getString(R.string.tournament_player_name_in_row, player2.getFirstname(),
                            player2.getNickname(), player2.getLastname()));
                holder.getPlayerTwoFaction().setText(player2.getFaction());

                holder.getPlayerTwoScore()
                    .setText(getActivity().getResources().getString(R.string.game_win, game.getPlayer_two_score()));
                holder.getPlayerTwoControlPoints()
                    .setText(getActivity().getResources()
                        .getString(R.string.game_cp, game.getPlayer_two_control_points()));
                holder.getPlayerTwoVictoryPoints()
                    .setText(getActivity().getResources()
                        .getString(R.string.game_vp, game.getPlayer_two_victory_points()));

                if (position % 2 == 0) {
                    holder.getPairingRow().setBackgroundColor(Color.LTGRAY);
                } else {
                    holder.getPairingRow().setBackgroundColor(Color.WHITE);
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
