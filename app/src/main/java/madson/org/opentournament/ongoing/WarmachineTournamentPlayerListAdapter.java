package madson.org.opentournament.ongoing;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.TournamentPlayer;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachineTournamentPlayerListAdapter extends RecyclerView.Adapter<WarmachineTournamentPlayerListAdapter.ViewHolder> {

    private TournamentPlayerListFragment.TournamentPlayerListItemListener mListener;
    private List<TournamentPlayer> tournamentPlayerList;

    public WarmachineTournamentPlayerListAdapter(List<TournamentPlayer> tournamentPlayerList,
        TournamentPlayerListFragment.TournamentPlayerListItemListener mListener) {

        this.mListener = mListener;
        this.tournamentPlayerList = tournamentPlayerList;
    }

    @Override
    public WarmachineTournamentPlayerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament_player, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(WarmachineTournamentPlayerListAdapter.ViewHolder holder, int position) {

        final TournamentPlayer player = tournamentPlayerList.get(position);
        holder.setPlayer(player);
        holder.getPlayerNumber().setText(String.valueOf(position + 1));
        holder.getPlayerNameInList()
            .setText(player.getFirstname() + " \"" + player.getNickname() + "\" " + player.getLastname());
    }


    @Override
    public int getItemCount() {

        return tournamentPlayerList.size();
    }


    public void add(TournamentPlayer item) {

        tournamentPlayerList.add(item);
        notifyDataSetChanged();
    }


    /**
     * add new player.
     *
     * @param  player
     */
    public void add(Player player) {

        TournamentPlayer tournamentPlayer = new TournamentPlayer();
        tournamentPlayer.setPlayer_id(player.get_id());
        tournamentPlayer.setFirstname(player.getFirstname());
        tournamentPlayer.setNickname(player.getNickname());
        tournamentPlayer.setLastname(player.getLastname());

        add(tournamentPlayer);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView playerNameInList;
        private TextView playerNumber;
        private TournamentPlayer player;

        public ViewHolder(View v) {

            super(v);
            v.setOnClickListener(this);

            playerNameInList = (TextView) v.findViewById(R.id.tournament_player_row_name);
            playerNumber = (TextView) v.findViewById(R.id.tournmanet_player_row_player_number);
        }

        public TextView getPlayerNameInList() {

            return playerNameInList;
        }


        public void setPlayer(TournamentPlayer player) {

            this.player = player;
        }


        public TextView getPlayerNumber() {

            return playerNumber;
        }


        @Override
        public void onClick(View v) {

            Log.i(v.getClass().getName(), "remove player from tournament player list: " + player);
        }
    }
}
