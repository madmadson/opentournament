package madson.org.opentournament.organize.setup;

import android.content.Context;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.TournamentPlayer;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerListAdapter extends RecyclerView.Adapter<TournamentPlayerListAdapter.ViewHolder> {

    private Context context;
    private TournamentSetupEventListener mListener;
    private List<TournamentPlayer> tournamentPlayerList = new ArrayList<>();

    public TournamentPlayerListAdapter(Context context, TournamentSetupEventListener mListener) {

        this.context = context;

        this.mListener = mListener;
    }

    @Override
    public TournamentPlayerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament_player, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(TournamentPlayerListAdapter.ViewHolder holder, int position) {

        final TournamentPlayer player = tournamentPlayerList.get(position);
        holder.setPlayer(player);
        holder.getPlayerNumber().setText(String.valueOf(position + 1));
        holder.getTeamName().setText(player.getTeamname());
        holder.getFaction().setText(player.getFaction());

        String firstname = player.getFirstname();
        String nickname = player.getNickname();
        String lastname = player.getLastname();
        holder.getPlayerNameInList()
            .setText(context.getResources()
                .getString(R.string.tournament_player_name_in_row, firstname, nickname, lastname));

        // mark online player
        if (player.getPlayer_online_uuid() != null) {
            holder.getOnlineIcon().setVisibility(View.VISIBLE);
        } else {
            holder.getOnlineIcon().setVisibility(View.GONE);
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
    public void addTournamentPlayer(TournamentPlayer tournamentPlayer) {

        if (!tournamentPlayerList.contains(tournamentPlayer)) {
            tournamentPlayerList.add(tournamentPlayer);
            notifyDataSetChanged();
        }
    }


    public void removeTournamentPlayer(TournamentPlayer tournamentPlayer) {

        int position = tournamentPlayerList.indexOf(tournamentPlayer);
        tournamentPlayerList.remove(position);
        notifyDataSetChanged();
    }


    public boolean contains(TournamentPlayer player) {

        if (player != null) {
            return tournamentPlayerList.contains(player);
        } else {
            return false;
        }
    }


    public void addTournamentPlayers(List<TournamentPlayer> localTournamentPlayers) {

        tournamentPlayerList = localTournamentPlayers;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView faction;

        private TextView playerNameInList;
        private TextView playerNumber;
        private TextView teamName;

        private ImageView onlineIcon;
        private TournamentPlayer player;

        public ViewHolder(View v) {

            super(v);
            v.setOnClickListener(this);

            playerNumber = (TextView) v.findViewById(R.id.tournament_player_row_player_number);
            playerNameInList = (TextView) v.findViewById(R.id.tournament_player_fullname);
            teamName = (TextView) v.findViewById(R.id.tournament_player_teamname);
            faction = (TextView) v.findViewById(R.id.tournament_player_row_faction);
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


        public TextView getTeamName() {

            return teamName;
        }


        public TextView getFaction() {

            return faction;
        }


        @Override
        public void onClick(View v) {

            Log.i(v.getClass().getName(), "removePlayer player from tournament player list: " + player);

            if (mListener != null) {
                mListener.clickTournamentPlayerListItem(player);
            }
        }
    }
}
