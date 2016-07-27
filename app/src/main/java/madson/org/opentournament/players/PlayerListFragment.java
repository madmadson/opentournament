package madson.org.opentournament.players;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.PlayerService;

import java.util.List;


public class PlayerListFragment extends Fragment {

    public static final String TAG = "player_list_fragment";

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

        PlayerService playerService = ((OpenTournamentApplication) getActivity().getApplication()).getPlayerService();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.player_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Player> players = playerService.getAllPlayers();

        PlayerListAdapter playerListAdapter = new PlayerListAdapter(players);

        recyclerView.setAdapter(playerListAdapter);

        return view;
    }

    public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {

        private List<Player> mDataset;

        public PlayerListAdapter(List<Player> myDataset) {

            mDataset = myDataset;
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

            final Player player = mDataset.get(position);
            holder.setPlayer(player);
            holder.getPlayerNameInList().setText(player.getFirstname() + " " + player.getLastname());
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

            return mDataset.size();
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
    }
}
