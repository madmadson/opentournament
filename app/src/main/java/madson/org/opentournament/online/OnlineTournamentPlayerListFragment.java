package madson.org.opentournament.online;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineTournamentPlayerListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private DatabaseReference mFirebaseDatabaseReference;
    private Tournament tournament;
    private ProgressBar mProgressBar;
    private TextView noTournamentPlayersTextView;
    private ImageView soloViewForTournamentPlayers;
    private ImageView teamViewForTournamentPlayers;
    private BaseActivity baseActivity;
    private OnlineTournamentPlayerTeamExpandableListAdapter tournamentPlayerTeamExpandableListAdapter;

    public static Fragment newInstance(Tournament tournament) {

        OnlineTournamentPlayerListFragment fragment = new OnlineTournamentPlayerListFragment();
        Bundle args = new Bundle();
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

        View view = inflater.inflate(R.layout.fragment_online_tournament_player_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noTournamentPlayersTextView = (TextView) view.findViewById(R.id.no_tournament_players);

        Button registerButton = (Button) view.findViewById(R.id.register_for_tournament_button);

        if (registerButton != null) {
            registerButton.setVisibility(View.GONE);
        }

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_player_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        final ExpandableListView teamExpandableList = (ExpandableListView) view.findViewById(
                R.id.tournament_teams_expandableList);

        tournamentPlayerTeamExpandableListAdapter = new OnlineTournamentPlayerTeamExpandableListAdapter(baseActivity,
                tournament);
        teamExpandableList.setAdapter(tournamentPlayerTeamExpandableListAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        final OnlineTournamentPlayerListAdapter playerListAdapter = new OnlineTournamentPlayerListAdapter(
                getActivity());

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_PLAYERS + "/"
                + tournament.getGameOrSportTyp() + "/" + tournament.getUUID());

        child.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                        TournamentPlayer player = playerSnapshot.getValue(TournamentPlayer.class);

                        if (player != null) {
                            mProgressBar.setVisibility(View.GONE);
                            playerListAdapter.addPlayer(player);
                            tournamentPlayerTeamExpandableListAdapter.addTournamentPlayer(player);
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
            }, 10000);

        if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
            recyclerView.setVisibility(View.VISIBLE);
            teamExpandableList.setVisibility(View.GONE);
        } else {
            teamExpandableList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        soloViewForTournamentPlayers = (ImageView) view.findViewById(R.id.solo_tournament_icon);
        soloViewForTournamentPlayers.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    recyclerView.setVisibility(View.VISIBLE);
                    teamExpandableList.setVisibility(View.GONE);
                }
            });

        teamViewForTournamentPlayers = (ImageView) view.findViewById(R.id.team_tournament_icon);
        teamViewForTournamentPlayers.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    teamExpandableList.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            });

        return view;
    }
}
