package madson.org.opentournament.viewHolder;

import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerTournamentViewHolder extends RecyclerView.ViewHolder {

    private final TextView ranking;
    private ImageView teamIcon;

    private View rowTournament;
    private TextView tournamentNameInList;

    public PlayerTournamentViewHolder(View v) {

        super(v);

        rowTournament = v.findViewById(R.id.row_player_tournament);

        ranking = (TextView) v.findViewById(R.id.ranking);
        tournamentNameInList = (TextView) v.findViewById(R.id.tournament_name);

        teamIcon = (ImageView) v.findViewById(R.id.team_tournament_icon);
    }

    public TextView getTournamentNameInList() {

        return tournamentNameInList;
    }


    public TextView getRanking() {

        return ranking;
    }


    public View getRowTournament() {

        return rowTournament;
    }


    public ImageView getTeamIcon() {

        return teamIcon;
    }
}
