package madson.org.opentournament.players;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.setup.TournamentPlayerListAdapter;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TournamentPlayerListAdapter tournamentPlayerListAdapter;
    private TextView faction;

    private TextView playerNameInList;
    private TextView playerNumber;
    private TextView teamName;

    private ImageView onlineIcon;
    private TournamentPlayer player;

    /**
     * used by online tournament list player.
     *
     * @param  v
     */
    public TournamentPlayerViewHolder(View v) {

        super(v);

        playerNumber = (TextView) v.findViewById(R.id.tournament_player_row_player_number);
        playerNameInList = (TextView) v.findViewById(R.id.tournament_player_fullname);
        teamName = (TextView) v.findViewById(R.id.tournament_player_teamname);
        faction = (TextView) v.findViewById(R.id.tournament_player_row_faction);
        onlineIcon = (ImageView) v.findViewById(R.id.tournament_player_row_online_icon);
    }


    public TournamentPlayerViewHolder(TournamentPlayerListAdapter tournamentPlayerListAdapter, View v) {

        super(v);
        this.tournamentPlayerListAdapter = tournamentPlayerListAdapter;
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

        if (tournamentPlayerListAdapter.getmListener() != null) {
            tournamentPlayerListAdapter.getmListener().clickTournamentPlayerListItem(player);
        }
    }
}
