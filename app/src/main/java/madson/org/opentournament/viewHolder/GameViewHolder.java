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
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.EnterResultForGameDialog;
import madson.org.opentournament.organize.GameListAdapter;
import madson.org.opentournament.organize.setup.TournamentPlayerListAdapter;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView tableNumber;
    private View pairingRow;
    private Game game;

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

    /**
     * used by online game list.
     *
     * @param  v
     */
    public GameViewHolder(View v) {

        super(v);

        setFields(v);
    }


    public GameViewHolder(GameListAdapter adapter, View v) {

        super(v);

        v.setOnClickListener(this);

        setFields(v);
    }

    private void setFields(View v) {

        tableNumber = (TextView) v.findViewById(R.id.table_number);

        pairingRow = v.findViewById(R.id.pairing_row);

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


    @Override
    public void onClick(View v) {

        Log.i(v.getClass().getName(), "game clicked: " + game);

        EnterResultForGameDialog dialog = new EnterResultForGameDialog();

        Bundle resultForPairingResult = new Bundle();
        resultForPairingResult.putParcelable(EnterResultForGameDialog.BUNDLE_GAME, game);
        dialog.setArguments(resultForPairingResult);

        FragmentManager supportFragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
        dialog.show(supportFragmentManager, "enterGameResultDialog");
    }


    public void setGame(Game game) {

        this.game = game;
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
}