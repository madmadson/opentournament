package madson.org.opentournament.tournaments;

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
import madson.org.opentournament.db.TournamentTable;
import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.setup.OnlinePlayerListAdapter;
import madson.org.opentournament.players.PlayerListAdapter;
import madson.org.opentournament.utility.BaseActivity;


public class OnlineTournamentListFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView noTournamentsTextView;
    private RecyclerView mOnlineTournamentRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private OnlinePlayerListAdapter onlinePlayerListAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ((BaseActivity) getActivity()).getToolbar().setTitle(R.string.title_online_tournaments);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_online_tournament_list, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noTournamentsTextView = (TextView) view.findViewById(R.id.no_online_tournaments);

        mOnlineTournamentRecyclerView = (RecyclerView) view.findViewById(R.id.online_tournament_list_recycler_view);
        mOnlineTournamentRecyclerView.setHasFixedSize(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mOnlineTournamentRecyclerView.setLayoutManager(linearLayoutManager);

        final OnlineTournamentListAdapter onlineTournamentListAdapter = new OnlineTournamentListAdapter((BaseActivity)
                getActivity());

        // do this configurable for other sport games
        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENTS + "/"
                + GameOrSportTyp.WARMACHINE.name());

        child.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    progressBar.setVisibility(View.GONE);

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    tournament.setOnlineUUID(dataSnapshot.getKey());
                    onlineTournamentListAdapter.addTournament(tournament);
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    tournament.setOnlineUUID(dataSnapshot.getKey());
                    onlineTournamentListAdapter.replaceTournament(tournament);
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    tournament.setOnlineUUID(dataSnapshot.getKey());
                    onlineTournamentListAdapter.removeTournament(tournament);
                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        mOnlineTournamentRecyclerView.setAdapter(onlineTournamentListAdapter);

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if (onlineTournamentListAdapter.getItemCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    noTournamentsTextView.setVisibility(View.VISIBLE);
                }
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 5000);

        return view;
    }
}
