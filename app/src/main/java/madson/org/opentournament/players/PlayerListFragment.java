package madson.org.opentournament.players;

import android.content.Context;

import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.db.GameTable;
import madson.org.opentournament.db.PlayerTable;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.setup.OnlinePlayerListAdapter;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.tournaments.OnlineTournamentListAdapter;
import madson.org.opentournament.tournaments.TournamentComparator;
import madson.org.opentournament.tournaments.TournamentManagementDialog;
import madson.org.opentournament.tournaments.TournamentManagementEventListener;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.viewHolder.GameViewHolder;
import madson.org.opentournament.viewHolder.PlayerViewHolder;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class PlayerListFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView noPlayersTextView;
    private RecyclerView mOnlinePlayerRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private OnlinePlayerListAdapter onlinePlayerListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "click floatingActionButton tournament management");

                    TournamentManagementDialog dialog = new TournamentManagementDialog();

                    FragmentManager supportFragmentManager = getChildFragmentManager();
                    dialog.show(supportFragmentManager, "tournament management new tournament");
                }
            });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ((BaseActivity) getActivity()).getToolbar().setTitle(R.string.title_player_management);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_player_list, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noPlayersTextView = (TextView) view.findViewById(R.id.no_online_players);

        mOnlinePlayerRecyclerView = (RecyclerView) view.findViewById(R.id.player_list_recycler_view);
        mOnlinePlayerRecyclerView.setHasFixedSize(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mOnlinePlayerRecyclerView.setLayoutManager(linearLayoutManager);

        final PlayerListAdapter playerListAdapter = new PlayerListAdapter(getActivity());

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYERS);

        Query orderedPlayer = child.orderByChild(PlayerTable.COLUMN_NICKNAME);

        orderedPlayer.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progressBar.setVisibility(View.GONE);

                    for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                        Player player = playerSnapshot.getValue(Player.class);
                        player.setOnlineUUID(dataSnapshot.getKey());
                        playerListAdapter.addPlayer(player);
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        mOnlinePlayerRecyclerView.setAdapter(playerListAdapter);

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if (playerListAdapter.getItemCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    noPlayersTextView.setVisibility(View.VISIBLE);
                }
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 5000);

        return view;
    }
}
