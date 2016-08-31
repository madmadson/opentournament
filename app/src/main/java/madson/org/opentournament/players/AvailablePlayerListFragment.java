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

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


public class AvailablePlayerListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private AvailablePlayerListAdapter availablePlayerListAdapter;

    private AvailablePlayerListItemListener mListener;
    private Tournament tournament;

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

        EditText filterPlayer = (EditText) view.findViewById(R.id.input_filter_player);

        filterPlayer.addTextChangedListener(new PlayerFilterTextWatcher());

        filterPlayer.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus) {
                        Log.i(this.getClass().getName(), "lost focus");
                    } else {
                        Log.i(this.getClass().getName(), "has focus");
                    }
                }
            });

        PlayerService playerService = ((BaseApplication) getActivity().getApplication()).getPlayerService();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.player_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Player> players = playerService.getAllPlayers();

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);

            OngoingTournamentService ongoingTournamentService = ((BaseApplication) getActivity().getApplication())
                .getOngoingTournamentService();
            List<Player> alreadyPlayingPlayers = ongoingTournamentService.getAllPlayersForTournament(
                    tournament.get_id());
            players.removeAll(alreadyPlayingPlayers);
        }

        // listener may be null
        availablePlayerListAdapter = new AvailablePlayerListAdapter(players, mListener);
        recyclerView.setAdapter(availablePlayerListAdapter);

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

        if (availablePlayerListAdapter != null) {
            final BaseApplication application = (BaseApplication) getActivity().getApplication();
            final Player player = application.getPlayerService().getPlayerForId(player_id);
            availablePlayerListAdapter.add(player);

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
            availablePlayerListAdapter.getFilter().filter(s.toString());
        }


        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
