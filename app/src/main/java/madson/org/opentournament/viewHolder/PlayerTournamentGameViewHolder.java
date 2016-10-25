package madson.org.opentournament.viewHolder;

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
public class PlayerTournamentGameViewHolder extends RecyclerView.ViewHolder {

    private final TextView playerOne;
    private final TextView playerTwo;
    private View rowGame;

    public PlayerTournamentGameViewHolder(View v) {

        super(v);

        rowGame = v.findViewById(R.id.row_player_tournament_game);

        playerOne = (TextView) v.findViewById(R.id.player_one);
        playerTwo = (TextView) v.findViewById(R.id.player_two);
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
}
