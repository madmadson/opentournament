package madson.org.opentournament.ongoing;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private Tournament tournament;
    private WarmachineTournamentPlayerListAdapter localTournamentPlayerListAdapter;

    private TextView heading;
    private DatabaseReference mFirebaseDatabaseReference;

    public static TournamentPlayerListFragment newInstance(Tournament tournament) {

        TournamentPlayerListFragment fragment = new TournamentPlayerListFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        if (tournament.getOnlineUUID() != null) {
            View view = inflater.inflate(R.layout.fragment_online_tournament_player_list, container, false);

            loadOnlineTournamentPlayers();

            return view;
        } else {
            View view = inflater.inflate(R.layout.fragment_local_tournament_player_list, container, false);
            // ********************* LOCAL ***************************

            TournamentPlayerService tournamentPlayerService = ((BaseApplication) getActivity().getApplication())
                .getTournamentPlayerService();

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_player_list_recycler_view);

            recyclerView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);

            List<TournamentPlayer> players = tournamentPlayerService.getAllPlayersForTournament(tournament);

            heading = (TextView) view.findViewById(R.id.heading_tournament_players);
            heading.setText(getString(R.string.heading_tournament_player, players.size()));

            localTournamentPlayerListAdapter = new WarmachineTournamentPlayerListAdapter(players, null);

            recyclerView.setAdapter(localTournamentPlayerListAdapter);

            return view;
        }
    }


    private void loadOnlineTournamentPlayers() {

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        final List<String> onlineTournamentPlayersUUIDs = new ArrayList<>();

        ValueEventListener tournamentPlayerListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    onlineTournamentPlayersUUIDs.add(playerSnapshot.getKey());
                    Log.i(this.getClass().getName(), "online player id of tournaments:" + playerSnapshot.getKey());

                    DatabaseReference child = mFirebaseDatabaseReference.child("tournament_players/"
                            + playerSnapshot.getKey());
                    child.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                TournamentPlayer player = dataSnapshot.getValue(TournamentPlayer.class);

                                Log.i(this.getClass().getName(), "online player of tournaments:" + player);
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(this.getClass().getName(), "failed to load online players");
            }
        };

        DatabaseReference child = mFirebaseDatabaseReference.child("tournaments/" + tournament.getOnlineUUID()
                + "/players");

        child.addValueEventListener(tournamentPlayerListener);
    }


    public void addPlayer(final Player player) {

        Log.i(this.getClass().getName(), "add player to tournament player list: " + player);

        // local tournament
        if (tournament.getOnlineUUID() == null) {
            if (localTournamentPlayerListAdapter != null) {
                localTournamentPlayerListAdapter.add(player);

                heading.setText(getString(R.string.heading_tournament_player,
                        localTournamentPlayerListAdapter.getItemCount()));

                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {

                        Log.i(this.getClass().getName(), "add player to tournament ");

                        BaseApplication application = (BaseApplication) getActivity().getApplication();
                        application.getTournamentPlayerService().addPlayerToTournament(player, tournament);
                    }
                };
                runnable.run();
            }
        } else {
            // online tournament
        }
    }


    public void removePlayer() {

        heading.setText(getString(R.string.heading_tournament_player, localTournamentPlayerListAdapter.getItemCount()));
    }

    public interface TournamentPlayerListItemListener {

        void onTournamentPlayerListItemClicked(long player_id);
    }
}
