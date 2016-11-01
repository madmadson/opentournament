package madson.org.opentournament.viewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentRankingViewHolder extends RecyclerView.ViewHolder {

    private final TextView playerAffiliation;
    private final TextView playerElo;
    private final ImageView playerEloIcon;
    private CardView rankingCard;
    private TextView droppedInRound;
    private ImageView offlineIcon;
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
        playerAffiliation = (TextView) itemView.findViewById(R.id.ranking_row_affiliation);
        playerElo = (TextView) itemView.findViewById(R.id.ranking_row_elo);
        playerEloIcon = (ImageView) itemView.findViewById(R.id.ranking_row_elo_icon);

        playerFactionInList = (TextView) itemView.findViewById(R.id.ranking_player_faction);
        playerNumber = (TextView) itemView.findViewById(R.id.ranking_row_player_number);
        score = (TextView) itemView.findViewById(R.id.ranking_row_score);
        sos = (TextView) itemView.findViewById(R.id.ranking_row_sos);
        cp = (TextView) itemView.findViewById(R.id.ranking_row_control_points);
        vp = (TextView) itemView.findViewById(R.id.ranking_row_victory_points);
        offlineIcon = (ImageView) itemView.findViewById(R.id.ranking_row_offline_icon);
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


    public ImageView getOfflineIcon() {

        return offlineIcon;
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


    public TextView getPlayerAffiliation() {

        return playerAffiliation;
    }


    public TextView getPlayerElo() {

        return playerElo;
    }


    public TextView getPlayerNumber() {

        return playerNumber;
    }


    public ImageView getPlayerEloIcon() {

        return playerEloIcon;
    }
}
