package madson.org.opentournament.organize;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.warmachine.Game;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {

    private List<Game> pairingsForTournament;
    private FragmentActivity context;

    public GameListAdapter(List<Game> pairingsForTournament, FragmentActivity activity) {

        this.pairingsForTournament = pairingsForTournament;
        context = activity;
    }

    public void updateGame(Game game) {

        if (pairingsForTournament.contains(game)) {
            int indexOfGame = pairingsForTournament.indexOf(game);

            pairingsForTournament.remove(game);
            pairingsForTournament.add(indexOfGame, game);
            notifyDataSetChanged();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pairing, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Game game = pairingsForTournament.get(position);
        holder.setPairing(game);

        if (game.isFinished()) {
            ;
            holder.getPlayerOneCardView()
                .setCardBackgroundColor(game.getPlayer_one_score() == 1
                        ? context.getResources().getColor(R.color.colorWin)
                        : context.getResources().getColor(R.color.colorLoose));
            holder.getPlayerTwoCardView()
                .setCardBackgroundColor(game.getPlayer_two_score() == 1
                        ? context.getResources().getColor(R.color.colorWin)
                        : context.getResources().getColor(R.color.colorLoose));
        }

        holder.getPlayerOneNameInList().setText(String.valueOf(game.getPlayer_one_full_name()));
        holder.getPlayerOneScore().setText("WIN: " + String.valueOf(game.getPlayer_one_score()));
        holder.getPlayerOneControlPoints().setText("CP: " + String.valueOf(game.getPlayer_one_control_points()));
        holder.getPlayerOneVictoryPoints().setText("VP: " + String.valueOf(game.getPlayer_one_victory_points()));

        holder.getPlayerTwoNameInList().setText(String.valueOf(game.getPlayer_two_full_name()));
        holder.getPlayerTwoScore().setText("WIN: " + String.valueOf(game.getPlayer_two_score()));
        holder.getPlayerTwoControlPoints().setText("CP: " + String.valueOf(game.getPlayer_two_control_points()));
        holder.getPlayerTwoVictoryPoints().setText("VP: " + String.valueOf(game.getPlayer_two_victory_points()));
    }


    @Override
    public int getItemCount() {

        return pairingsForTournament.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Game pairing;

        private CardView playerOneCardView;

        private TextView playerOneNameInList;
        private TextView playerOneScore;
        private TextView playerOneControlPoints;
        private TextView playerOneVictoryPoints;

        private CardView playerTwoCardView;

        private TextView playerTwoNameInList;
        private TextView playerTwoScore;
        private TextView playerTwoControlPoints;
        private TextView playerTwoVictoryPoints;

        public ViewHolder(View v) {

            super(v);

            v.setOnClickListener(this);

            playerOneCardView = (CardView) v.findViewById(R.id.game_list_player_one_card_view);
            playerTwoCardView = (CardView) v.findViewById(R.id.game_list_player_two_card_view);

            playerOneNameInList = (TextView) v.findViewById(R.id.pairing_player_one_name);
            playerOneScore = (TextView) v.findViewById(R.id.pairing_player_one_score);
            playerOneControlPoints = (TextView) v.findViewById(R.id.pairing_player_one_control_points);
            playerOneVictoryPoints = (TextView) v.findViewById(R.id.pairing_player_one_victory_points);

            playerTwoNameInList = (TextView) v.findViewById(R.id.pairing_player_two_name);
            playerTwoScore = (TextView) v.findViewById(R.id.pairing_player_two_score);
            playerTwoControlPoints = (TextView) v.findViewById(R.id.pairing_player_two_control_points);
            playerTwoVictoryPoints = (TextView) v.findViewById(R.id.pairing_player_two_victory_points);
        }

        @Override
        public void onClick(View v) {

            Log.i(v.getClass().getName(), "pairing clicked: " + pairing);

            EnterResultForGameDialog dialog = new EnterResultForGameDialog();

            Bundle resultForPairingResult = new Bundle();
            resultForPairingResult.putLong(EnterResultForGameDialog.BUNDLE_GAME_ID, pairing.get_id());
            dialog.setArguments(resultForPairingResult);

            FragmentManager supportFragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
            dialog.show(supportFragmentManager, "enterGameResultDialog");
        }


        public void setPairing(Game pairing) {

            this.pairing = pairing;
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
    }
}
