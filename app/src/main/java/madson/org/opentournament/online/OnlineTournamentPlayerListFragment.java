package madson.org.opentournament.online;

import android.os.Bundle;
import android.os.Handler;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.setup.AddTournamentPlayerDialog;
import madson.org.opentournament.organize.setup.TournamentPlayerListAdapter;
import madson.org.opentournament.players.TournamentPlayerViewHolder;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineTournamentPlayerListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_UUID = "tournament_uuid";

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<TournamentPlayer, TournamentPlayerViewHolder> mFirebaseAdapter;
    private String tournament_uuid;
    private ProgressBar mProgressBar;
    private TextView noTournamentPlayersTextView;

    public static Fragment newInstance(String tournament_uuid) {

        OnlineTournamentPlayerListFragment fragment = new OnlineTournamentPlayerListFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_TOURNAMENT_UUID, tournament_uuid);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getString(BUNDLE_TOURNAMENT_UUID) != null) {
            tournament_uuid = bundle.getString(BUNDLE_TOURNAMENT_UUID);
        }

        View view = inflater.inflate(R.layout.fragment_online_tournament_player_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noTournamentPlayersTextView = (TextView) view.findViewById(R.id.no_tournament_players);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_player_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_PLAYERS + "/"
                + tournament_uuid);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<TournamentPlayer, TournamentPlayerViewHolder>(
                TournamentPlayer.class, R.layout.row_tournament_player, TournamentPlayerViewHolder.class, child) {

            @Override
            protected void populateViewHolder(TournamentPlayerViewHolder viewHolder, TournamentPlayer player,
                int position) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.setPlayer(player);
                viewHolder.getPlayerNumber().setText(String.valueOf(position + 1));
                viewHolder.getTeamName().setText(player.getTeamname());
                viewHolder.getFaction().setText(player.getFaction());

                String firstname = player.getFirstname();
                String nickname = player.getNickname();
                String lastname = player.getLastname();
                viewHolder.getPlayerNameInList()
                    .setText(getActivity().getResources()
                        .getString(R.string.tournament_player_name_in_row, firstname, nickname, lastname));

                // mark online player
                if (player.getPlayer_online_uuid() != null) {
                    viewHolder.getOnlineIcon().setVisibility(View.VISIBLE);
                } else {
                    viewHolder.getOnlineIcon().setVisibility(View.GONE);
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {

                    super.onItemRangeRemoved(positionStart, itemCount);

                    if (mFirebaseAdapter.getItemCount() == 0) {
                        noTournamentPlayersTextView.setVisibility(View.VISIBLE);
                    }
                }


                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {

                    super.onItemRangeInserted(positionStart, itemCount);

                    if (mFirebaseAdapter.getItemCount() != 0) {
                        noTournamentPlayersTextView.setVisibility(View.GONE);
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
                        noTournamentPlayersTextView.setVisibility(View.VISIBLE);
                    }

                    mFirebaseAdapter.notifyDataSetChanged();
                }
            }, 5000);
        mFirebaseAdapter.notifyDataSetChanged();

        return view;
    }
}
