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

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.tournaments.OnlineTournamentListAdapter;
import madson.org.opentournament.tournaments.OrganizedTournamentList;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.Environment;


public class PlayerTournamentListFragment extends Fragment {

    public static final String BUNDLE_PLAYER = "player";

    private BaseActivity baseActivity;

    private Player player;
    private DatabaseReference mFirebaseDatabaseReference;
    private ProgressBar progressBar;
    private TextView noTournamentsTextView;
    private RecyclerView mOnlineTournamentRecyclerView;
    private OnlineTournamentListAdapter onlineTournamentListAdapter;
    private TextView offlineText;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FloatingActionButton floatingActionButton = baseActivity.getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_PLAYER) != null) {
            player = bundle.getParcelable(BUNDLE_PLAYER);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_player_tournament_list, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        offlineText = (TextView) view.findViewById(R.id.offline_text);

        noTournamentsTextView = (TextView) view.findViewById(R.id.no_online_tournaments);

        mOnlineTournamentRecyclerView = (RecyclerView) view.findViewById(R.id.online_tournament_list_recycler_view);
        mOnlineTournamentRecyclerView.setHasFixedSize(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mOnlineTournamentRecyclerView.setLayoutManager(linearLayoutManager);

        onlineTournamentListAdapter = new OnlineTournamentListAdapter(baseActivity);

        loadPlayerTournaments();

        return view;
    }


    private void loadPlayerTournaments() {

        if (baseActivity.getBaseApplication().isOnline()) {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            // do this configurable for other sport games
            DatabaseReference childSolo = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYER_GAMES + "/"
                    + GameOrSportTyp.WARMACHINE_SOLO + "/" + player.getUUID());

            // do this configurable for other sport games
            DatabaseReference childTeam = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYER_GAMES + "/"
                    + GameOrSportTyp.WARMACHINE_TEAM + "/" + player.getUUID());

            ChildEventListener tournamentEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    progressBar.setVisibility(View.GONE);
                    noTournamentsTextView.setVisibility(View.GONE);

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    tournament.setUuid(dataSnapshot.getKey());
                    onlineTournamentListAdapter.addTournament(tournament);
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    tournament.setUuid(dataSnapshot.getKey());
                    onlineTournamentListAdapter.replaceTournament(tournament);
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    tournament.setUuid(dataSnapshot.getKey());
                    onlineTournamentListAdapter.removeTournament(tournament);
                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            childSolo.addChildEventListener(tournamentEventListener);
            childTeam.addChildEventListener(tournamentEventListener);

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
            handler.postDelayed(runnable, 10000);
        } else {
            offlineText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
