package madson.org.opentournament.viewHolder;

import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.TextView;

import madson.org.opentournament.R;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerTournamentGameViewHolder extends RecyclerView.ViewHolder {

    private final TextView playerOne;
    private final TextView playerTwo;
    private final TextView playerOneStat;
    private final TextView playerTwoStat;
    private final TextView playerOneArmyList;
    private final TextView playerTwoArmyList;
    private final TextView playerOneEloTrend;
    private final TextView playerTwoEloTrend;
    private View rowGame;

    public PlayerTournamentGameViewHolder(View v) {

        super(v);

        rowGame = v.findViewById(R.id.row_player_tournament_game);

        playerOne = (TextView) v.findViewById(R.id.player_one);
        playerOneStat = (TextView) v.findViewById(R.id.player_one_stat);
        playerTwo = (TextView) v.findViewById(R.id.player_two);
        playerTwoStat = (TextView) v.findViewById(R.id.player_two_stat);
        playerOneArmyList = (TextView) v.findViewById(R.id.player_one_army_list);
        playerTwoArmyList = (TextView) v.findViewById(R.id.player_two_army_list);

        playerOneEloTrend = (TextView) v.findViewById(R.id.player_one_elo_trend);
        playerTwoEloTrend = (TextView) v.findViewById(R.id.player_two_elo_trend);
    }

    public View getRowGame() {

        return rowGame;
    }


    public TextView getPlayerOne() {

        return playerOne;
    }


    public TextView getPlayerTwo() {

        return playerTwo;
    }


    public TextView getPlayerOneStat() {

        return playerOneStat;
    }


    public TextView getPlayerTwoStat() {

        return playerTwoStat;
    }


    public TextView getPlayerOneArmyList() {

        return playerOneArmyList;
    }


    public TextView getPlayerTwoArmyList() {

        return playerTwoArmyList;
    }


    public TextView getPlayerOneEloTrend() {

        return playerOneEloTrend;
    }


    public TextView getPlayerTwoEloTrend() {

        return playerTwoEloTrend;
    }
}
