package madson.org.opentournament.organize.setup;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;

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
import android.widget.Filter;
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
import madson.org.opentournament.events.AddTournamentPlayerEvent;
import madson.org.opentournament.events.DeleteLocalPlayerEvent;
import madson.org.opentournament.events.OpenTournamentEvent;
import madson.org.opentournament.events.OpenTournamentEventListener;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.RemoveTournamentPlayerEvent;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.tasks.LoadAllLocalPlayerTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseFragment;

import java.util.List;


public class AvailablePlayerListFragment extends BaseFragment implements OpenTournamentEventListener {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private Tournament tournament;

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
    private BaseActivity baseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
        baseActivity.getBaseApplication().registerTournamentEventListener(this);
    }


    @Override
    public void onDetach() {

        super.onDetach();
        baseActivity.getBaseApplication().unregisterTournamentEventListener(this);
    }


    public static AvailablePlayerListFragment newInstance(Tournament tournamentToOrganize) {

        AvailablePlayerListFragment fragment = new AvailablePlayerListFragment();
        Bundle args = new Bundle();

        args.putParcelable(BUNDLE_TOURNAMENT, tournamentToOrganize);
        fragment.setArguments(args);

        return fragment;
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

            filterPlayerTextView.addTextChangedListener(new PlayerFilterTextWatcher());

            if (baseActivity.isConnected()) {
                view.findViewById(R.id.offline_player_text).setVisibility(View.GONE);
                mOnlinePlayerRecyclerView = (RecyclerView) view.findViewById(R.id.online_player_list_recycler_view);
                mOnlinePlayerRecyclerView.setHasFixedSize(true);
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(baseActivity.getApplication());
                mOnlinePlayerRecyclerView.setLayoutManager(linearLayoutManager);
                onlinePlayerListAdapter = new OnlinePlayerListAdapter(baseActivity, tournament);

                // need player ids for filtering online players
                final List<String> alreadyPlayingPlayersUUIDs = tournamentPlayerService.getAllPlayersUUIDsForTournament(
                        tournament);

                ValueEventListener playerListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.i(this.getClass().getName(), "players loaded from firebase");

                        for (DataSnapshot playerSnapShot : dataSnapshot.getChildren()) {
                            progressBar.setVisibility(View.GONE);

                            String player_online_uuid = playerSnapShot.getKey();

                            Player player = playerSnapShot.getValue(Player.class);

                            if (player != null && !player.getTournaments().containsKey(tournament.getUuid())) {
                                player.setUUID(player_online_uuid);

                                if (!alreadyPlayingPlayersUUIDs.contains(player_online_uuid)) {
                                    onlinePlayerListAdapter.addPlayer(player);
                                    onlinePlayerListAdapter.getFilter()
                                        .filter(filterPlayerTextView.getText().toString());
                                }
                            }
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };

                DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYERS);

                child.addValueEventListener(playerListener);

                mOnlinePlayerRecyclerView.setAdapter(onlinePlayerListAdapter);

                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {

                        if (onlinePlayerListAdapter.getItemCount() == 0) {
                            progressBar.setVisibility(View.GONE);
                            noOnlineTournamentPlayersTextView.setVisibility(View.VISIBLE);
                        }
                    }
                };

                Handler handler = new Handler();
                handler.postDelayed(runnable, 5000);
            } else {
                view.findViewById(R.id.offline_player_text).setVisibility(View.VISIBLE);
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            }

            localPlayerRecyclerView = (RecyclerView) view.findViewById(R.id.local_player_list_recycler_view);

            localPlayerRecyclerView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            localPlayerRecyclerView.setLayoutManager(linearLayoutManager);

            // listener may be null
            localPlayerListAdapter = new LocalPlayerListAdapter(baseActivity, tournament);
            localPlayerRecyclerView.setAdapter(localPlayerListAdapter);

            new LoadAllLocalPlayerTask(baseActivity.getBaseApplication(), localPlayerListAdapter,
                filterPlayerTextView.getText().toString(), noLocalTournamentPlayersTextView, tournament).execute();
        }

        return view;
    }


    @Override
    public void handleEvent(OpenTournamentEventTag eventTag, OpenTournamentEvent parameter) {

        if (OpenTournamentEventTag.ADD_TOURNAMENT_PLAYER.equals(eventTag)) {
            AddTournamentPlayerEvent addTournamentPlayerEvent = (AddTournamentPlayerEvent) parameter;
            TournamentPlayer player = addTournamentPlayerEvent.getTournamentPlayer();

            if (player.isLocal()) {
                localPlayerListAdapter.removeTournamentPlayer(player);
                doFilter(filterPlayerTextView.getText());
            } else {
                onlinePlayerListAdapter.removeTournamentPlayer(player);
                doFilter(filterPlayerTextView.getText());
            }
        } else if (OpenTournamentEventTag.REMOVE_TOURNAMENT_PLAYER.equals(eventTag)) {
            RemoveTournamentPlayerEvent removeTournamentPlayerEvent = (RemoveTournamentPlayerEvent) parameter;
            TournamentPlayer tournamentPlayer = removeTournamentPlayerEvent.getTournamentPlayer();

            if (!tournamentPlayer.isDummy()) {
                Player player = new Player();

                player.setFirstName(tournamentPlayer.getFirstName());
                player.setNickName(tournamentPlayer.getNickName());
                player.setLastName(tournamentPlayer.getLastName());
                player.setUUID(tournamentPlayer.getPlayerUUID());
                player.setLocal(tournamentPlayer.isLocal());
                player.setElo(tournamentPlayer.getElo());
                player.setGamesCounter(tournamentPlayer.getGamesCounter());
                player.setMeta(tournamentPlayer.getMeta());

                if (tournamentPlayer.isLocal()) {
                    localPlayerListAdapter.add(player);
                    doFilter(filterPlayerTextView.getText());
                } else {
                    onlinePlayerListAdapter.addPlayer(player);
                    doFilter(filterPlayerTextView.getText());
                }
            }
        } else if (OpenTournamentEventTag.DELETE_LOCAL_PLAYER.equals(eventTag)) {
            DeleteLocalPlayerEvent deleteLocalPlayerEvent = (DeleteLocalPlayerEvent) parameter;
            Player player = deleteLocalPlayerEvent.getPlayer();

            localPlayerListAdapter.removePlayer(player);
            doFilter(filterPlayerTextView.getText());
        }
    }


    private void doFilter(CharSequence s) {

        localPlayerListAdapter.getFilter().filter(s.toString(), new Filter.FilterListener() {

                @Override
                public void onFilterComplete(int count) {

                    if (count == 0) {
                        noLocalTournamentPlayersTextView.setVisibility(View.VISIBLE);
                    } else {
                        noLocalTournamentPlayersTextView.setVisibility(View.GONE);
                    }
                }
            });

        onlinePlayerListAdapter.getFilter().filter(s.toString(), new Filter.FilterListener() {

                @Override
                public void onFilterComplete(int count) {

                    if (count == 0) {
                        noOnlineTournamentPlayersTextView.setVisibility(View.VISIBLE);
                    } else {
                        noOnlineTournamentPlayersTextView.setVisibility(View.GONE);
                    }
                }
            });
    }

    private class PlayerFilterTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.i(this.getClass().getName(), "filtered by: " + s.toString());
            doFilter(s);
        }


        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
