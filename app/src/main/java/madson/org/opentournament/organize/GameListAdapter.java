package madson.org.opentournament.organize;

import android.content.Context;

import android.os.Bundle;

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
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.warmachine.Game;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {

    private List<Game> gamesForRound;
    private Context context;

    public GameListAdapter(Context context) {

        this.gamesForRound = new ArrayList<>();
        this.context = context;
    }

    public void updateGame(Game game) {

        if (gamesForRound.contains(game)) {
            int indexOfGame = gamesForRound.indexOf(game);

            gamesForRound.remove(game);
            gamesForRound.add(indexOfGame, game);
            notifyDataSetChanged();
        }
    }


    public void setGames(List<Game> games) {

        this.gamesForRound = games;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_game, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Game game = gamesForRound.get(position);
        holder.setGame(game);

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

        TournamentPlayer player1 = game.getPlayer1();

        if (player1 != null) {
            holder.getPlayerOneNameInList()
                .setText(context.getResources()
                    .getString(R.string.tournament_player_name_in_row, player1.getFirstname(), player1.getNickname(),
                        player1.getLastname()));
            holder.getPlayerOneFaction().setText(player1.getFaction());
        } else {
            holder.getPlayerOneNameInList()
                .setText(context.getResources()
                    .getString(R.string.tournament_player_name_in_row, "Dummy", "THE KING", "Player"));
        }

        holder.getPlayerOneScore()
            .setText(context.getResources().getString(R.string.game_win, game.getPlayer_one_score()));
        holder.getPlayerOneControlPoints()
            .setText(context.getResources().getString(R.string.game_cp, game.getPlayer_one_control_points()));
        holder.getPlayerOneVictoryPoints()
            .setText(context.getResources().getString(R.string.game_vp, game.getPlayer_one_victory_points()));

        TournamentPlayer player2 = game.getPlayer2();

        if (player2 != null) {
            holder.getPlayerTwoNameInList()
                .setText(context.getResources()
                    .getString(R.string.tournament_player_name_in_row, player2.getFirstname(), player2.getNickname(),
                        player2.getLastname()));
            holder.getPlayerTwoFaction().setText(player2.getFaction());
        } else {
            holder.getPlayerTwoNameInList()
                .setText(context.getResources()
                    .getString(R.string.tournament_player_name_in_row, "Dummy", "THE HAMMER", "Player"));
        }

        holder.getPlayerTwoScore()
            .setText(context.getResources().getString(R.string.game_win, game.getPlayer_two_score()));
        holder.getPlayerTwoControlPoints()
            .setText(context.getResources().getString(R.string.game_cp, game.getPlayer_two_control_points()));
        holder.getPlayerTwoVictoryPoints()
            .setText(context.getResources().getString(R.string.game_vp, game.getPlayer_two_victory_points()));
    }


    @Override
    public int getItemCount() {

        return gamesForRound.size();
    }


    public boolean allGamesAreFinished() {

        for (Game game : gamesForRound) {
            if (!game.isFinished()) {
                return false;
            }
        }

        return true;
    }


    public boolean atLeastOneGameStarted() {

        for (Game game : gamesForRound) {
            if (game.isFinished()) {
                return true;
            }
        }

        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        public ViewHolder(View v) {

            super(v);

            v.setOnClickListener(this);

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
    }
}
