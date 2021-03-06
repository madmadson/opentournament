package madson.org.opentournament.tournaments;

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

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.Environment;


public class OnlineTournamentListFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView noTournamentsTextView;
    private RecyclerView mOnlineTournamentRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;

    private Button linkToOwnTournamentButton;
    private BaseActivity baseActivity;
    private Spinner stateFilter;
    private Query soloQuery;
    private Query teamQuery;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (baseActivity.getBaseApplication().getEnvironment() != Environment.PROD) {
            baseActivity.getToolbar().setTitle(R.string.toolbar_title_online_tournaments_DEMO);
        } else {
            baseActivity.getToolbar().setTitle(R.string.toolbar_title_online_tournaments);
        }
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

        linkToOwnTournamentButton = (Button) view.findViewById(R.id.create_own_tournament_button);
        linkToOwnTournamentButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    baseActivity.replaceFragment(new OrganizedTournamentList());
                }
            });

        if (baseActivity.getBaseApplication().isOnline()) {
            noTournamentsTextView = (TextView) view.findViewById(R.id.no_online_tournaments);

            mOnlineTournamentRecyclerView = (RecyclerView) view.findViewById(R.id.online_tournament_list_recycler_view);
            mOnlineTournamentRecyclerView.setHasFixedSize(true);
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mOnlineTournamentRecyclerView.setLayoutManager(linearLayoutManager);

            final OnlineTournamentListAdapter onlineTournamentListAdapter = new OnlineTournamentListAdapter(
                    baseActivity);

            stateFilter = (Spinner) view.findViewById(R.id.state_filter);

            final ArrayAdapter<CharSequence> tournament_states_adapter = ArrayAdapter.createFromResource(baseActivity,
                    R.array.tournament_states, android.R.layout.simple_spinner_item);
            tournament_states_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stateFilter.setAdapter(tournament_states_adapter);

            // do this configurable for other sport games
            final DatabaseReference childSolo = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENTS + "/"
                    + GameOrSportTyp.WARMACHINE_SOLO);

            soloQuery = childSolo.orderByChild("state")
                .equalTo(String.valueOf(tournament_states_adapter.getItem(0)).toUpperCase());

            // do this configurable for other sport games
            final DatabaseReference childTeam = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENTS + "/"
                    + GameOrSportTyp.WARMACHINE_TEAM);

            teamQuery = childTeam.orderByChild("state")
                .equalTo(String.valueOf(tournament_states_adapter.getItem(0)).toUpperCase());

            final ChildEventListener tournamentEventListener = new ChildEventListener() {

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
            soloQuery.addChildEventListener(tournamentEventListener);
            teamQuery.addChildEventListener(tournamentEventListener);

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

            stateFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        String filter = String.valueOf(tournament_states_adapter.getItem(i)).toUpperCase();

                        onlineTournamentListAdapter.deleteAllTournaments();
                        soloQuery.removeEventListener(tournamentEventListener);
                        teamQuery.removeEventListener(tournamentEventListener);

                        if (filter.equals("ALL")) {
                            soloQuery = childSolo.orderByChild("state");
                            teamQuery = childTeam.orderByChild("state");
                        } else {
                            soloQuery = childSolo.orderByChild("state").equalTo(filter);
                            teamQuery = childTeam.orderByChild("state").equalTo(filter);
                        }

                        soloQuery.addChildEventListener(tournamentEventListener);
                        teamQuery.addChildEventListener(tournamentEventListener);
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
        } else {
            TextView offlineText = (TextView) view.findViewById(R.id.offline_text);
            offlineText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        return view;
    }
}
