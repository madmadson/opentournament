package madson.org.opentournament.viewHolder;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.organize.EnterResultForGameDialog;
import madson.org.opentournament.organize.GameListAdapter;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentRankingViewHolder extends RecyclerView.ViewHolder {

    private CardView rankingCard;
    private TextView droppedInRound;
    private ImageView onlineIcon;
    private TextView playerFactionInList;
    private TextView score;
    private TextView sos;
    private TextView cp;
    private TextView vp;
    private TextView playerTeamNameInList;
    private TextView playerNumber;
    private TextView playerNameInList;

    public TournamentRankingViewHolder(View itemView) {

        super(itemView);

        playerNameInList = (TextView) itemView.findViewById(R.id.ranking_row_name);
        playerTeamNameInList = (TextView) itemView.findViewById(R.id.ranking_player_teamname);
        playerFactionInList = (TextView) itemView.findViewById(R.id.ranking_player_faction);
        playerNumber = (TextView) itemView.findViewById(R.id.ranking_row_player_number);
        score = (TextView) itemView.findViewById(R.id.ranking_row_score);
        sos = (TextView) itemView.findViewById(R.id.ranking_row_sos);
        cp = (TextView) itemView.findViewById(R.id.ranking_row_control_points);
        vp = (TextView) itemView.findViewById(R.id.ranking_row_victory_points);
        onlineIcon = (ImageView) itemView.findViewById(R.id.ranking_row_online_icon);
        droppedInRound = (TextView) itemView.findViewById(R.id.ranking_dropped_in_round);
        rankingCard = (CardView) itemView.findViewById(R.id.ranking_row_card_view);
    }

    public TextView getRankingNumber() {

        return playerNumber;
    }


    public TextView getPlayerNameInList() {

        return playerNameInList;
    }


    public TextView getScore() {

        return score;
    }


    public TextView getSos() {

        return sos;
    }


    public TextView getCp() {

        return cp;
    }


    public TextView getVp() {

        return vp;
    }


    public TextView getPlayerTeamNameInList() {

        return playerTeamNameInList;
    }


    public ImageView getOnlineIcon() {

        return onlineIcon;
    }


    public TextView getPlayerFactionInList() {

        return playerFactionInList;
    }


    public TextView getDroppedInRound() {

        return droppedInRound;
    }


    public CardView getRankingCard() {

        return rankingCard;
    }
}
