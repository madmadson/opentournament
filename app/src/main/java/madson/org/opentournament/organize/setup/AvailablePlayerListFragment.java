package madson.org.opentournament.organize.setup;

import android.content.Context;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseFragment;

import java.util.List;


public class AvailablePlayerListFragment extends BaseFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private Tournament tournament;

    private TournamentSetupEventListener mListener;
    private RecyclerView mOnlinePlayerRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;

    // list adapters for both online and offline lists of players
    private OnlinePlayerListAdapter onlinePlayerListAdapter;
    private LocalPlayerListAdapter localPlayerListAdapter;

    private ProgressBar progressBar;
    private TextView noLocalTournamentPlayersTextView;
    private TextView noOnlineTournamentPlayersTextView;
    private RecyclerView localPlayerRecyclerView;
    private EditText filterPlayerTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available_player_list, container, false);

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);

            filterPlayerTextView = (EditText) view.findViewById(R.id.input_filter_player);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            noLocalTournamentPlayersTextView = (TextView) view.findViewById(R.id.no_local_available_players);
            noOnlineTournamentPlayersTextView = (TextView) view.findViewById(R.id.no_online_available_players);

            final TournamentPlayerService tournamentPlayerService = getBaseApplication().getTournamentPlayerService();
            final PlayerService playerService = getBaseApplication().getPlayerService();

            filterPlayerTextView.addTextChangedListener(new PlayerFilterTextWatcher());

            if (((BaseActivity) getActivity()).isConnected()) {
                mOnlinePlayerRecyclerView = (RecyclerView) view.findViewById(R.id.online_player_list_recycler_view);
                mOnlinePlayerRecyclerView.setHasFixedSize(true);
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                mOnlinePlayerRecyclerView.setLayoutManager(linearLayoutManager);
                onlinePlayerListAdapter = new OnlinePlayerListAdapter(getActivity(), mListener);

                // need player ids for filtering online players
                final List<String> alreadyPlayingPlayersUUIDs =
                    tournamentPlayerService.getAllPlayersOnlineUUIDForTournament(tournament);

                ValueEventListener playerListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.i(this.getClass().getName(), "players loaded from firebase");

                        for (DataSnapshot playerSnapShot : dataSnapshot.getChildren()) {
                            String player_online_uuid = playerSnapShot.getKey();

                            Player player = playerSnapShot.getValue(Player.class);

                            if (player != null && !player.getTournaments().containsKey(tournament.getOnlineUUID())) {
                                player.setOnlineUUID(player_online_uuid);

                                if (!alreadyPlayingPlayersUUIDs.contains(player_online_uuid)) {
                                    onlinePlayerListAdapter.addPlayer(player);
                                }
                            }
                        }

                        progressBar.setVisibility(View.GONE);

                        if (onlinePlayerListAdapter.getItemCount() == 0) {
                            noOnlineTournamentPlayersTextView.setVisibility(View.VISIBLE);
                        } else {
                            noOnlineTournamentPlayersTextView.setVisibility(View.GONE);
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };

                DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYERS);

                child.addValueEventListener(playerListener);

                mOnlinePlayerRecyclerView.setAdapter(onlinePlayerListAdapter);
            }

            localPlayerRecyclerView = (RecyclerView) view.findViewById(R.id.local_player_list_recycler_view);

            localPlayerRecyclerView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            localPlayerRecyclerView.setLayoutManager(linearLayoutManager);

            // listener may be null
            localPlayerListAdapter = new LocalPlayerListAdapter(getActivity(), mListener);
            localPlayerRecyclerView.setAdapter(localPlayerListAdapter);

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    List<TournamentPlayer> allPlayersForTournament = tournamentPlayerService.getAllPlayersForTournament(
                            tournament);
                    List<Player> allLocalPlayers = playerService.getAllLocalPlayersNotInTournament(
                            allPlayersForTournament);

                    if (allLocalPlayers.size() > 0) {
                        noLocalTournamentPlayersTextView.setVisibility(View.GONE);
                        localPlayerListAdapter.addPlayerList(allLocalPlayers);
                    }
                }
            };
            runnable.run();
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        // mlistener to call when player is clicked
        if (getParentFragment() instanceof TournamentSetupFragment) {
            mListener = (TournamentSetupEventListener) getParentFragment();
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();

        if (mListener != null) {
            mListener = null;
        }
    }


    public void addPlayer(final TournamentPlayer tournamentPlayer) {

        Log.i(this.getClass().getName(), " player removed from tournament player list: " + tournamentPlayer);

        if (tournamentPlayer.getPlayer_online_uuid() == null) {
            final Player player = getBaseApplication().getPlayerService()
                    .getPlayerForId(tournamentPlayer.getPlayer_id());

            localPlayerListAdapter.add(player);
            localPlayerListAdapter.getFilter().filter(filterPlayerTextView.getText().toString());

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    Log.i(this.getClass().getName(), "removePlayer player from tournament ");

                    getBaseApplication().getTournamentPlayerService().removePlayerFromTournament(player, tournament);

                    getBaseApplication().getTournamentService().decreaseActualPlayerForTournament(tournament);
                }
            };
            runnable.run();
        } else {
            Player player = Player.fromTournamentPlayer(tournamentPlayer);
            player.setOnlineUUID(tournamentPlayer.getPlayer_online_uuid());
            onlinePlayerListAdapter.addPlayer(player);
            onlinePlayerListAdapter.getFilter().filter(filterPlayerTextView.getText().toString());

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    Log.i(this.getClass().getName(), "removePlayer online player from offline tournament ");

                    getBaseApplication().getTournamentPlayerService()
                        .removeOnlinePlayerFromTournament(tournamentPlayer, tournament);
                    getBaseApplication().getTournamentService().decreaseActualPlayerForTournament(tournament);
                }
            };
            runnable.run();
        }

        checkIfNoPlayerTextView();
    }


    private void checkIfNoPlayerTextView() {

        if (localPlayerListAdapter.getItemCount() == 0) {
            noLocalTournamentPlayersTextView.setVisibility(View.VISIBLE);
        } else {
            noLocalTournamentPlayersTextView.setVisibility(View.GONE);
        }

        if (onlinePlayerListAdapter.getItemCount() == 0) {
            noOnlineTournamentPlayersTextView.setVisibility(View.VISIBLE);
        } else {
            noOnlineTournamentPlayersTextView.setVisibility(View.GONE);
        }
    }


    public void removePlayer(Player player) {

        Log.i(this.getClass().getName(), "removePlayer player from tournament: " + player);

        if (player.getOnlineUUID() == null) {
            int indexOfPlayerAdapter = localPlayerListAdapter.removePlayer(player);
            localPlayerRecyclerView.removeViewAt(indexOfPlayerAdapter);
        } else {
            onlinePlayerListAdapter.removePlayer(player);
        }

        checkIfNoPlayerTextView();
    }

    private class PlayerFilterTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.i(this.getClass().getName(), "filtered by: " + s.toString());
            localPlayerListAdapter.getFilter().filter(s.toString());
            onlinePlayerListAdapter.getFilter().filter(s.toString());
        }


        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}