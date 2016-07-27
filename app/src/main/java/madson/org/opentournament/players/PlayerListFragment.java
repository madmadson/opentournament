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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.PlayerService;

import java.util.ArrayList;
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

    public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> implements Filterable {

        private List<Player> mDataset;
        private List<Player> mfilteredDataset;
        private ItemFilter mFilter = new ItemFilter();

        public PlayerListAdapter(List<Player> myDataset) {

            mDataset = myDataset;
            mfilteredDataset = myDataset;
        }

        @Override
        public PlayerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_list_row, parent, false);

            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }


        @Override
        public void onBindViewHolder(PlayerListAdapter.ViewHolder holder, int position) {

            final Player player = mfilteredDataset.get(position);
            holder.setPlayer(player);
            holder.getPlayerNameInList()
                .setText(player.getFirstname() + " \"" + player.getNickname() + "\" " + player.getLastname());
            holder.getAddPlayerButton().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(v.getClass().getName(), "player added:" + player);

                        // TODO: add player to player

                    }
                });
        }


        @Override
        public int getItemCount() {

            return mfilteredDataset.size();
        }


        public void add(int position, Player item) {

            mDataset.add(position, item);
            notifyItemInserted(position);
        }


        public void remove(Tournament item) {

            int position = mDataset.indexOf(item);
            mDataset.remove(position);
            notifyItemRemoved(position);
        }


        @Override
        public Filter getFilter() {

            return mFilter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView playerNameInList;
            private ImageButton addPlayerButton;
            private Player player;

            public ViewHolder(View v) {

                super(v);

                playerNameInList = (TextView) v.findViewById(R.id.playerNameInList);
                addPlayerButton = (ImageButton) v.findViewById(R.id.addPlayerButton);
            }

            public TextView getPlayerNameInList() {

                return playerNameInList;
            }


            public ImageButton getAddPlayerButton() {

                return addPlayerButton;
            }


            public void setPlayer(Player player) {

                this.player = player;
            }
        }

        private class ItemFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                if (filterString.isEmpty()) {
                    results.values = mDataset;
                    results.count = mDataset.size();

                    return results;
                }

                final List<Player> list = mDataset;

                int count = list.size();
                final ArrayList<Player> newListOfPlayers = new ArrayList<>(count);

                Player filterablePlayer;

                for (int i = 0; i < count; i++) {
                    filterablePlayer = list.get(i);

                    if (filterablePlayer.getFirstname().toLowerCase().contains(filterString)
                            || filterablePlayer.getNickname().toLowerCase().contains(filterString)
                            || filterablePlayer.getLastname().toLowerCase().contains(filterString)) {
                        Log.i(this.getClass().getName(), "add to players: " + filterablePlayer.toString());
                        newListOfPlayers.add(filterablePlayer);
                    }
                }

                results.values = newListOfPlayers;
                results.count = newListOfPlayers.size();

                return results;
            }


            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mfilteredDataset = (ArrayList<Player>) results.values;
                notifyDataSetChanged();
            }
        }
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
