package madson.org.opentournament.players;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.db.PlayerTable;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.Environment;


public class PlayerListFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView noPlayersTextView;

    private BaseActivity baseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        FloatingActionButton floatingActionButton = baseActivity.getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        BaseActivity activity = (BaseActivity) getActivity();

        if (activity.getBaseApplication().getEnvironment() != Environment.PROD) {
            activity.getToolbar().setTitle(R.string.toolbar_title_players_DEMO);
        } else {
            activity.getToolbar().setTitle(R.string.toolbar_title_players);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_player_list, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noPlayersTextView = (TextView) view.findViewById(R.id.no_online_players);

        RecyclerView mOnlinePlayerRecyclerView = (RecyclerView) view.findViewById(R.id.player_list_recycler_view);
        mOnlinePlayerRecyclerView.setHasFixedSize(true);

        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mOnlinePlayerRecyclerView.setLayoutManager(linearLayoutManager);

        final PlayerListAdapter playerListAdapter = new PlayerListAdapter(getActivity().getApplicationContext());

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYERS);

        Query orderedPlayer = child.orderByChild(PlayerTable.COLUMN_NICKNAME);

        orderedPlayer.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    progressBar.setVisibility(View.GONE);

                    Player player = dataSnapshot.getValue(Player.class);
                    player.setUUID(dataSnapshot.getKey());
                    playerListAdapter.addPlayer(player);
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Player player = dataSnapshot.getValue(Player.class);
                    player.setUUID(dataSnapshot.getKey());
                    playerListAdapter.updatePlayer(player);
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    Player player = dataSnapshot.getValue(Player.class);
                    player.setUUID(dataSnapshot.getKey());
                    playerListAdapter.removePlayer(player);
                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
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
