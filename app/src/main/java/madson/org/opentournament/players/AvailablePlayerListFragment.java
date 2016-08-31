package madson.org.opentournament.players;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


public class AvailablePlayerListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String PLAYERS_CHILD = "players";

    private LocalPlayerListAdapter localPlayerListAdapter;

    private AvailablePlayerListItemListener mListener;
    private Tournament tournament;
    private RecyclerView mOnlinePlayerRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private OnlinePlayerListAdapter onlinePlayerListAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public AvailablePlayerListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player_list, container, false);

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);

            EditText filterPlayer = (EditText) view.findViewById(R.id.input_filter_player);

            filterPlayer.addTextChangedListener(new PlayerFilterTextWatcher());

            if (((BaseActivity) getActivity()).isConnected()) {
                mOnlinePlayerRecyclerView = (RecyclerView) view.findViewById(R.id.online_player_list_recycler_view);
                mOnlinePlayerRecyclerView.setHasFixedSize(true);
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                mOnlinePlayerRecyclerView.setLayoutManager(linearLayoutManager);
                onlinePlayerListAdapter = new OnlinePlayerListAdapter();

                ValueEventListener playerListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.i(this.getClass().getName(), "player loaded from firebase");

                        for (DataSnapshot playerSnapShot : dataSnapshot.getChildren()) {
                            Player player = playerSnapShot.getValue(Player.class);
                            onlinePlayerListAdapter.addPlayer(player);
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };

                DatabaseReference child = mFirebaseDatabaseReference.child("players");

                child.addValueEventListener(playerListener);

                mOnlinePlayerRecyclerView.setAdapter(onlinePlayerListAdapter);
            }

            PlayerService playerService = ((BaseApplication) getActivity().getApplication()).getPlayerService();
            List<Player> localPlayers = playerService.getAllLocalPlayers();

            RecyclerView localPlayerRecyclerView = (RecyclerView) view.findViewById(
                    R.id.local_player_list_recycler_view);

            localPlayerRecyclerView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            localPlayerRecyclerView.setLayoutManager(linearLayoutManager);

            OngoingTournamentService ongoingTournamentService = ((BaseApplication) getActivity().getApplication())
                .getOngoingTournamentService();
            List<Player> alreadyPlayingPlayers = ongoingTournamentService.getAllPlayersForTournament(
                    tournament.get_id());
            localPlayers.removeAll(alreadyPlayingPlayers);

            // listener may be null
            localPlayerListAdapter = new LocalPlayerListAdapter(localPlayers, mListener);
            localPlayerRecyclerView.setAdapter(localPlayerListAdapter);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        // mlistener to call when player is clicked
        if (getParentFragment() instanceof AvailablePlayerListItemListener) {
            mListener = (AvailablePlayerListItemListener) getParentFragment();
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();

        if (mListener != null) {
            mListener = null;
        }
    }


    public void addPlayer(final long player_id) {

        Log.i(this.getClass().getName(), "add player to tournament player list: " + player_id);

        if (localPlayerListAdapter != null) {
            final BaseApplication application = (BaseApplication) getActivity().getApplication();
            final Player player = application.getPlayerService().getPlayerForId(player_id);
            localPlayerListAdapter.add(player);

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    Log.i(this.getClass().getName(), "remove player from tournament ");

                    application.getOngoingTournamentService().removePlayerFromTournament(player, tournament.get_id());
                }
            };
            runnable.run();
        }
    }

    public interface AvailablePlayerListItemListener {

        void onAvailablePlayerListItemClicked(Player player);
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
