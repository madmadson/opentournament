package madson.org.opentournament.players;

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

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.service.PlayerService;

import java.util.List;


public class PlayerListFragment extends Fragment {

    public static final String TAG = "player_list_fragment";

    private PlayerListAdapter playerListAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public PlayerListFragment() {
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

        PlayerService playerService = ((OpenTournamentApplication) getActivity().getApplication()).getPlayerService();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.player_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Player> players = playerService.getAllPlayers();

        playerListAdapter = new PlayerListAdapter(players);

        recyclerView.setAdapter(playerListAdapter);

        return view;
    }

    private class PlayerFilterTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.i(this.getClass().getName(), "filtered by: " + s.toString());
            playerListAdapter.getFilter().filter(s.toString());
        }


        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
