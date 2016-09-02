package madson.org.opentournament.players;

import android.content.Context;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
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
public class TournamentPlayerListAdapter extends RecyclerView.Adapter<TournamentPlayerListAdapter.ViewHolder> {

    private TournamentSetupEventListener mListener;
    private List<TournamentPlayer> tournamentPlayerList;

    public TournamentPlayerListAdapter(List<TournamentPlayer> tournamentPlayerList,
        TournamentSetupEventListener mListener) {

        this.mListener = mListener;
        this.tournamentPlayerList = tournamentPlayerList;
    }

    @Override
    public TournamentPlayerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament_player, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(TournamentPlayerListAdapter.ViewHolder holder, int position) {

        final TournamentPlayer player = tournamentPlayerList.get(position);
        holder.setPlayer(player);
        holder.getPlayerNumber().setText(String.valueOf(position + 1));
        holder.getTeamname().setText(player.getTeamname());
        holder.getPlayerNameInList()
            .setText(player.getFirstname() + " \"" + player.getNickname() + "\" " + player.getLastname());

        // mark online player
        if (player.getPlayer_online_uuid() != null) {
            holder.getOnlineIcon().setVisibility(View.VISIBLE);
        }

        if (mListener != null) {
            mListener.tournamentPlayerListHeading();
        }
    }


    @Override
    public int getItemCount() {

        return tournamentPlayerList.size();
    }


    /**
     * Add new tournament player.
     *
     * @param  tournamentPlayer
     */
    public void add(TournamentPlayer tournamentPlayer) {

        if (!tournamentPlayerList.contains(tournamentPlayer)) {
            tournamentPlayerList.add(tournamentPlayer);
            notifyDataSetChanged();
        }
    }


    public boolean contains(TournamentPlayer player) {

        if (player != null) {
            return tournamentPlayerList.contains(player);
        } else {
            return false;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView playerNameInList;
        private TextView playerNumber;
        private TextView teamname;

        private ImageView onlineIcon;
        private TournamentPlayer player;

        public ViewHolder(View v) {

            super(v);
            v.setOnClickListener(this);

            playerNumber = (TextView) v.findViewById(R.id.tournament_player_row_player_number);
            playerNameInList = (TextView) v.findViewById(R.id.tournament_player_fullname);
            teamname = (TextView) v.findViewById(R.id.tournament_player_teamname);
            onlineIcon = (ImageView) v.findViewById(R.id.tournament_player_row_online_icon);
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


        public ImageView getOnlineIcon() {

            return onlineIcon;
        }


        public TextView getTeamname() {

            return teamname;
        }


        @Override
        public void onClick(View v) {

            Log.i(v.getClass().getName(), "remove player from tournament player list: " + player);

            if (mListener != null) {
                mListener.clickTournamentPlayerListItem(player);
            }
        }
    }
}
