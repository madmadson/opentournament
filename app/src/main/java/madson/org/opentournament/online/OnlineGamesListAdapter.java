package madson.org.opentournament.online;

import android.content.Context;

import android.graphics.Color;

import android.graphics.drawable.Drawable;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.viewHolder.GameViewHolder;
import madson.org.opentournament.viewHolder.TournamentPlayerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineGamesListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    private List<Game> gameList;

    private Context context;
    private Drawable winnerShape;
    private Drawable looserShape;

    public OnlineGamesListAdapter(Context context) {

        this.context = context;

        this.gameList = new ArrayList<>();

        winnerShape = context.getResources().getDrawable(R.drawable.shape_winner);
        looserShape = context.getResources().getDrawable(R.drawable.shape_looser);
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_game, parent, false);

        return new GameViewHolder(v);
    }


    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {

        final Game game = gameList.get(position);

        holder.setGame(game);

        holder.getTableNumber()
            .setText(context.getResources().getString(R.string.table_number, game.getPlaying_field()));

        if (game.isFinished()) {
            holder.getPlayerOneCardView()
                .setBackgroundDrawable(game.getPlayer_one_score() == 1 ? winnerShape : looserShape);
            holder.getPlayerTwoCardView()
                .setBackgroundDrawable(game.getPlayer_two_score() == 1 ? winnerShape : looserShape);
        }

        TournamentPlayer player1 = game.getPlayer1();

        holder.getPlayerOneNameInList()
            .setText(context.getResources()
                .getString(R.string.tournament_player_name_in_row, player1.getFirstname(), player1.getNickname(),
                    player1.getLastname()));
        holder.getPlayerOneFaction().setText(player1.getFaction());

        holder.getPlayerOneScore()
            .setText(context.getResources().getString(R.string.game_win, game.getPlayer_one_score()));
        holder.getPlayerOneControlPoints()
            .setText(context.getResources().getString(R.string.game_cp, game.getPlayer_one_control_points()));
        holder.getPlayerOneVictoryPoints()
            .setText(context.getResources().getString(R.string.game_vp, game.getPlayer_one_victory_points()));

        TournamentPlayer player2 = game.getPlayer2();

        holder.getPlayerTwoNameInList()
            .setText(context.getResources()
                .getString(R.string.tournament_player_name_in_row, player2.getFirstname(), player2.getNickname(),
                    player2.getLastname()));
        holder.getPlayerTwoFaction().setText(player2.getFaction());

        holder.getPlayerTwoScore()
            .setText(context.getResources().getString(R.string.game_win, game.getPlayer_two_score()));
        holder.getPlayerTwoControlPoints()
            .setText(context.getResources().getString(R.string.game_cp, game.getPlayer_two_control_points()));
        holder.getPlayerTwoVictoryPoints()
            .setText(context.getResources().getString(R.string.game_vp, game.getPlayer_two_victory_points()));

        if (position % 2 == 0) {
            holder.getPairingRow().setBackgroundColor(Color.LTGRAY);
        } else {
            holder.getPairingRow().setBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {

        return gameList.size();
    }


    public void addGame(Game game) {

        Log.i(this.getClass().getName(), "player added to adapter ");

        gameList.add(game);
        notifyDataSetChanged();
    }
}
