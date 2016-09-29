package madson.org.opentournament.online;

import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.TournamentPlayer;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineTournamentTeamListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_UUID = "tournament_uuid";

    private DatabaseReference mFirebaseDatabaseReference;
    private String tournament_uuid;
    private ProgressBar mProgressBar;
    private TextView noTournamentPlayersTextView;

    public static Fragment newInstance(String tournament_uuid) {

        OnlineTournamentTeamListFragment fragment = new OnlineTournamentTeamListFragment();
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

        View view = inflater.inflate(R.layout.fragment_online_tournament_team_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noTournamentPlayersTextView = (TextView) view.findViewById(R.id.no_tournament_players);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_player_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        final OnlineTournamentPlayerListAdapter playerListAdapter = new OnlineTournamentPlayerListAdapter(
                getActivity());

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_TEAMS + "/"
                + tournament_uuid);

        child.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                        TournamentPlayer player = playerSnapshot.getValue(TournamentPlayer.class);

                        if (player != null) {
                            mProgressBar.setVisibility(View.GONE);
                            playerListAdapter.addPlayer(player);
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        recyclerView.setAdapter(playerListAdapter);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (playerListAdapter.getItemCount() == 0) {
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        noTournamentPlayersTextView.setVisibility(View.VISIBLE);
                    }
                }
            }, 5000);

        return view;
    }
}
