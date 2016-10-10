package madson.org.opentournament.online;

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
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.viewHolder.GameViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineGamesListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    private List<Game> gameList;

    private BaseApplication baseApplication;
    private Drawable winnerShape;
    private Drawable looserShape;

    public OnlineGamesListAdapter(BaseApplication baseApplication) {

        this.baseApplication = baseApplication;

        this.gameList = new ArrayList<>();

        winnerShape = baseApplication.getResources().getDrawable(R.drawable.shape_winner);
        looserShape = baseApplication.getResources().getDrawable(R.drawable.shape_looser);
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
            .setText(baseApplication.getResources().getString(R.string.table_number, game.getPlaying_field()));

        if (game.isFinished()) {
            holder.getPlayerOneCardView()
                .setBackgroundDrawable(game.getPlayer_one_score() == 1 ? winnerShape : looserShape);
            holder.getPlayerTwoCardView()
                .setBackgroundDrawable(game.getPlayer_two_score() == 1 ? winnerShape : looserShape);
        }

        TournamentPlayer player1 = game.getPlayer1();

        holder.getPlayerOneNameInList()
            .setText(baseApplication.getResources()
                .getString(R.string.player_name_in_row, player1.getFirstName(), player1.getNickName(),
                    player1.getLastName()));
        holder.getPlayerOneFaction().setText(player1.getFaction());

        holder.getPlayerOneScore()
            .setText(baseApplication.getResources().getString(R.string.game_win, game.getPlayer_one_score()));
        holder.getPlayerOneControlPoints()
            .setText(baseApplication.getResources().getString(R.string.game_cp, game.getPlayer_one_control_points()));
        holder.getPlayerOneVictoryPoints()
            .setText(baseApplication.getResources().getString(R.string.game_vp, game.getPlayer_one_victory_points()));

        TournamentPlayer player2 = game.getPlayer2();

        holder.getPlayerTwoNameInList()
            .setText(baseApplication.getResources()
                .getString(R.string.player_name_in_row, player2.getFirstName(), player2.getNickName(),
                    player2.getLastName()));
        holder.getPlayerTwoFaction().setText(player2.getFaction());

        holder.getPlayerTwoScore()
            .setText(baseApplication.getResources().getString(R.string.game_win, game.getPlayer_two_score()));
        holder.getPlayerTwoControlPoints()
            .setText(baseApplication.getResources().getString(R.string.game_cp, game.getPlayer_two_control_points()));
        holder.getPlayerTwoVictoryPoints()
            .setText(baseApplication.getResources().getString(R.string.game_vp, game.getPlayer_two_victory_points()));

        if (position % 2 == 0) {
            holder.getPairingRow().setBackgroundColor(Color.LTGRAY);
        } else {
            holder.getPairingRow().setBackgroundColor(Color.WHITE);
        }

        if (baseApplication.getAuthenticatedPlayer() != null
                && (baseApplication.getAuthenticatedPlayer().getUUID().equals(player1.getPlayerUUID()))) {
            holder.getPlayerOneCardView().setCardBackgroundColor(Color.MAGENTA);
        }

        if (baseApplication.getAuthenticatedPlayer() != null
                && (baseApplication.getAuthenticatedPlayer().getUUID().equals(player2.getPlayerUUID()))) {
            holder.getPlayerTwoCardView().setCardBackgroundColor(Color.MAGENTA);
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


    public int getIndexOfPlayer(String uuid) {

        for (int i = 0; i < gameList.size(); i++) {
            if (gameList.get(i).getPlayerOneUUID().equals(uuid) || gameList.get(i)
                    .getPlayerTwoUUID()
                    .equals(uuid)) {
                return i;
            }
        }

        return -1;
    }
}
