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
public class GameViewHolder extends RecyclerView.ViewHolder {

    private ImageView swapPlayerTwo;
    private ImageView swapPlayerOne;
    private TextView tableNumber;
    private View pairingRow;

    private CardView playerOneCardView;

    private TextView playerOneNameInList;
    private TextView playerOneFaction;
    private TextView playerOneScore;
    private TextView playerOneControlPoints;
    private TextView playerOneVictoryPoints;

    private CardView playerTwoCardView;

    private TextView playerTwoNameInList;
    private TextView playerTwoFaction;
    private TextView playerTwoScore;
    private TextView playerTwoControlPoints;
    private TextView playerTwoVictoryPoints;

    public GameViewHolder(View v) {

        super(v);

        pairingRow = v.findViewById(R.id.pairing_row);
        tableNumber = (TextView) v.findViewById(R.id.table_number);

        swapPlayerOne = (ImageView) v.findViewById(R.id.swap_player_one);
        swapPlayerTwo = (ImageView) v.findViewById(R.id.swap_player_two);

        playerOneCardView = (CardView) v.findViewById(R.id.game_list_player_one_card_view);
        playerTwoCardView = (CardView) v.findViewById(R.id.game_list_player_two_card_view);

        playerOneNameInList = (TextView) v.findViewById(R.id.player_one_name);
        playerOneFaction = (TextView) v.findViewById(R.id.player_one_faction);
        playerOneScore = (TextView) v.findViewById(R.id.pairing_player_one_score);
        playerOneControlPoints = (TextView) v.findViewById(R.id.pairing_player_one_control_points);
        playerOneVictoryPoints = (TextView) v.findViewById(R.id.pairing_player_one_victory_points);

        playerTwoNameInList = (TextView) v.findViewById(R.id.player_two_name);
        playerTwoFaction = (TextView) v.findViewById(R.id.player_two_faction);
        playerTwoScore = (TextView) v.findViewById(R.id.pairing_player_two_score);
        playerTwoControlPoints = (TextView) v.findViewById(R.id.pairing_player_two_control_points);
        playerTwoVictoryPoints = (TextView) v.findViewById(R.id.pairing_player_two_victory_points);
    }

    public CardView getPlayerOneCardView() {

        return playerOneCardView;
    }


    public CardView getPlayerTwoCardView() {

        return playerTwoCardView;
    }


    public TextView getPlayerOneNameInList() {

        return playerOneNameInList;
    }


    public TextView getPlayerTwoNameInList() {

        return playerTwoNameInList;
    }


    public TextView getPlayerTwoVictoryPoints() {

        return playerTwoVictoryPoints;
    }


    public TextView getPlayerTwoControlPoints() {

        return playerTwoControlPoints;
    }


    public TextView getPlayerTwoScore() {

        return playerTwoScore;
    }


    public TextView getPlayerOneVictoryPoints() {

        return playerOneVictoryPoints;
    }


    public TextView getPlayerOneControlPoints() {

        return playerOneControlPoints;
    }


    public TextView getPlayerOneScore() {

        return playerOneScore;
    }


    public TextView getPlayerTwoFaction() {

        return playerTwoFaction;
    }


    public TextView getPlayerOneFaction() {

        return playerOneFaction;
    }


    public View getPairingRow() {

        return pairingRow;
    }


    public TextView getTableNumber() {

        return tableNumber;
    }


    public ImageView getSwapPlayerTwo() {

        return swapPlayerTwo;
    }


    public ImageView getSwapPlayerOne() {

        return swapPlayerOne;
    }
}
