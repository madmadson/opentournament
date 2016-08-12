package madson.org.opentournament.tournament;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentRanking;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class WarmachineTournamentPlayerListAdapter extends RecyclerView.Adapter<WarmachineTournamentPlayerListAdapter.ViewHolder> {

    private TournamentPlayerListFragment.TournamentPlayerListItemListener mListener;
    private List<WarmachineTournamentRanking> tournamentPlayerList;

    public WarmachineTournamentPlayerListAdapter(List<WarmachineTournamentRanking> tournamentPlayerList,
        TournamentPlayerListFragment.TournamentPlayerListItemListener mListener) {

        this.mListener = mListener;
        this.tournamentPlayerList = tournamentPlayerList;
    }

    @Override
    public WarmachineTournamentPlayerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tournament_player_list_row, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(WarmachineTournamentPlayerListAdapter.ViewHolder holder, int position) {

        if (position == 0) {
        } else {
            final WarmachineTournamentRanking player = tournamentPlayerList.get(position);
            holder.setPlayer(player);
            holder.getPlayerNumber().setText(String.valueOf(position + 1));
            holder.getPlayerNameInList()
                .setText(player.getFirstname() + " \"" + player.getNickname() + "\" " + player.getLastname());
        }
    }


    @Override
    public int getItemCount() {

        return tournamentPlayerList.size() + 1;
    }


    public void add(WarmachineTournamentRanking item) {

        tournamentPlayerList.add(item);
        notifyDataSetChanged();
    }


    /**
     * add new player.
     *
     * @param  player
     */
    public void add(Player player) {

        WarmachineTournamentRanking warmachineTournamentRanking = new WarmachineTournamentRanking();
        warmachineTournamentRanking.setPlayer_id(player.get_id());
        warmachineTournamentRanking.setFirstname(player.getFirstname());
        warmachineTournamentRanking.setNickname(player.getNickname());
        warmachineTournamentRanking.setLastname(player.getLastname());

        add(warmachineTournamentRanking);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView playerNameInList;
        private TextView playerNumber;
        private WarmachineTournamentRanking player;

        public ViewHolder(View v) {

            super(v);
            v.setOnClickListener(this);

            playerNameInList = (TextView) v.findViewById(R.id.tournament_player_row_name);
            playerNumber = (TextView) v.findViewById(R.id.tournmanet_player_row_player_number);
        }

        public TextView getPlayerNameInList() {

            return playerNameInList;
        }


        public void setPlayer(WarmachineTournamentRanking player) {

            this.player = player;
        }


        public TextView getPlayerNumber() {

            return playerNumber;
        }


        @Override
        public void onClick(View v) {

            Log.i(v.getClass().getName(), "remove player from tournament player list: " + player);

            tournamentPlayerList.remove(player);
            notifyDataSetChanged();

            mListener.onTournamentPlayerListItemClicked(player.getPlayer_id());
        }
    }
}
