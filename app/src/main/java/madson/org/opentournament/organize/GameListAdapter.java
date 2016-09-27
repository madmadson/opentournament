package madson.org.opentournament.organize;

import android.content.ClipData;
import android.content.DialogInterface;

import android.graphics.Color;

import android.graphics.drawable.Drawable;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.tasks.SwapPlayersTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.viewHolder.GameViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    private final Drawable enterShape;
    private final Drawable normalShape;
    private final Drawable startShape;
    private final Drawable winnerShape;
    private final Drawable looserShape;

    private List<Game> gamesForRound;
    private BaseActivity activity;
    private int round;

    public GameListAdapter(BaseActivity activity, int round) {

        this.round = round;

        this.gamesForRound = new ArrayList<>();
        this.activity = activity;

        enterShape = activity.getResources().getDrawable(R.drawable.shape_droptarget_entered);
        normalShape = activity.getResources().getDrawable(R.drawable.shape_normal);
        startShape = activity.getResources().getDrawable(R.drawable.shape_droptarget_started);

        winnerShape = activity.getResources().getDrawable(R.drawable.shape_winner);
        looserShape = activity.getResources().getDrawable(R.drawable.shape_looser);
    }

    public void updateGameForRound(Game game) {

        if (game.getTournament_round() == round) {
            int indexOfGame = gamesForRound.indexOf(game);

            gamesForRound.remove(game);
            gamesForRound.add(indexOfGame, game);
            notifyDataSetChanged();
        }
    }


    public void setGamesForRound(List<Game> games, int round) {

        if (this.round == round) {
            this.gamesForRound = games;

            notifyDataSetChanged();
        }
    }


    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_game, parent, false);

        return new GameViewHolder(this, v);
    }


    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {

        final Game game = gamesForRound.get(position);
        holder.setGame(game);

        holder.getTableNumber()
            .setText(activity.getResources().getString(R.string.table_number, game.getPlaying_field()));

        if (game.isFinished()) {
            holder.getPlayerOneCardView()
                .setBackgroundDrawable(game.getPlayer_one_score() == 1 ? winnerShape : looserShape);
            holder.getPlayerTwoCardView()
                .setBackgroundDrawable(game.getPlayer_two_score() == 1 ? winnerShape : looserShape);
        }

        TournamentPlayer player1 = game.getPlayer1();

        holder.getPlayerOneNameInList()
            .setText(activity.getResources()
                .getString(R.string.tournament_player_name_in_row, player1.getFirstname(), player1.getNickname(),
                    player1.getLastname()));
        holder.getPlayerOneFaction().setText(player1.getFaction());

        holder.getPlayerOneScore()
            .setText(activity.getResources().getString(R.string.game_win, game.getPlayer_one_score()));
        holder.getPlayerOneControlPoints()
            .setText(activity.getResources().getString(R.string.game_cp, game.getPlayer_one_control_points()));
        holder.getPlayerOneVictoryPoints()
            .setText(activity.getResources().getString(R.string.game_vp, game.getPlayer_one_victory_points()));

        TournamentPlayer player2 = game.getPlayer2();

        holder.getPlayerTwoNameInList()
            .setText(activity.getResources()
                .getString(R.string.tournament_player_name_in_row, player2.getFirstname(), player2.getNickname(),
                    player2.getLastname()));
        holder.getPlayerTwoFaction().setText(player2.getFaction());

        holder.getPlayerTwoScore()
            .setText(activity.getResources().getString(R.string.game_win, game.getPlayer_two_score()));
        holder.getPlayerTwoControlPoints()
            .setText(activity.getResources().getString(R.string.game_cp, game.getPlayer_two_control_points()));
        holder.getPlayerTwoVictoryPoints()
            .setText(activity.getResources().getString(R.string.game_vp, game.getPlayer_two_victory_points()));

        holder.getPlayerOneCardView().setOnClickListener(holder);
        holder.getPlayerTwoCardView().setOnClickListener(holder);

        // drag and drop for manually re pair
        if (!game.isFinished()) {
            holder.getPlayerOneCardView().setOnLongClickListener(new GameLongClickEventListener(game, 1));
            holder.getPlayerOneCardView().setOnDragListener(new GameDragListener(game, 1));

            holder.getPlayerTwoCardView().setOnLongClickListener(new GameLongClickEventListener(game, 2));
            holder.getPlayerTwoCardView().setOnDragListener(new GameDragListener(game, 2));
        } else {
            holder.getPlayerOneCardView().setOnLongClickListener(null);
            holder.getPlayerOneCardView().setOnDragListener(null);

            holder.getPlayerTwoCardView().setOnLongClickListener(null);
            holder.getPlayerTwoCardView().setOnDragListener(null);
        }

        if (position % 2 == 0) {
            holder.getPairingRow().setBackgroundColor(Color.LTGRAY);
        } else {
            holder.getPairingRow().setBackgroundColor(Color.WHITE);
        }
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

    private class GameLongClickEventListener implements View.OnLongClickListener {

        private Game draggedGame;
        private int player;

        public GameLongClickEventListener(Game game, int player) {

            this.draggedGame = game;

            this.player = player;
        }

        @Override
        public boolean onLongClick(View v) {

            ClipData playerId = ClipData.newPlainText("player", String.valueOf(player));
            v.startDrag(playerId, // the data to be dragged
                new View.DragShadowBuilder(v), // the drag shadow builder
                draggedGame, 0 // flags (not currently used, set to 0)
                );

            return false;
        }
    }

    private class GameDragListener implements View.OnDragListener {

        private Game droppedGame;
        private int droppedPlayerIndex;

        /**
         * @param  droppedGame  game of drag element
         * @param  droppedPlayerIndex  new opponent for dragged player
         */
        public GameDragListener(Game droppedGame, int droppedPlayerIndex) {

            this.droppedGame = droppedGame;
            this.droppedPlayerIndex = droppedPlayerIndex;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {

            int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:

                    v.setBackgroundDrawable(startShape);

                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundDrawable(enterShape);
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundDrawable(startShape);
                    break;

                case DragEvent.ACTION_DROP:

                    final Game draggedGame = (Game) event.getLocalState();
                    final CharSequence playerOneOrTwo = event.getClipData().getItemAt(0).getText();

                    TournamentPlayer draggedPlayer = null;
                    TournamentPlayer draggedPlayerOpponent = null;
                    if (playerOneOrTwo.equals("1")) {
                        draggedPlayer = draggedGame.getPlayer1();
                        draggedPlayerOpponent = draggedGame.getPlayer2();
                    } else {
                        draggedPlayer = draggedGame.getPlayer2();
                        draggedPlayerOpponent = draggedGame.getPlayer1();
                    }

                    TournamentPlayer droppedPlayer = null;
                    TournamentPlayer droppedPlayerOpponent = null;
                    if (droppedPlayerIndex == 1) {
                        droppedPlayer = droppedGame.getPlayer1();
                        droppedPlayerOpponent = droppedGame.getPlayer2();
                    } else {
                        droppedPlayer = droppedGame.getPlayer2();
                        droppedPlayerOpponent = droppedGame.getPlayer1();
                    }

                    final TournamentPlayer draggedPlayerFinal = draggedPlayer;
                    final TournamentPlayer droppedPlayerFinal = droppedPlayer;

                    if (draggedPlayer.getListOfOpponentsIds().contains(droppedPlayerOpponent.getRealPlayerId())) {
                        String playerOne = activity.getResources()
                                .getString(R.string.tournament_player_name_in_row, draggedPlayer.getFirstname(),
                                    draggedPlayer.getNickname(), draggedPlayer.getLastname());
                        String playerTwo = activity.getResources()
                                .getString(R.string.tournament_player_name_in_row, droppedPlayerOpponent.getFirstname(),
                                    droppedPlayerOpponent.getNickname(), droppedPlayerOpponent.getLastname());

                        Snackbar snackbar = Snackbar.make((activity).getCoordinatorLayout(),
                                activity.getResources()
                                    .getString(R.string.player_already_played_each_other, playerOne, playerTwo),
                                Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(activity.getResources().getColor(R.color.colorNeutral));
                        snackbar.show();
                    } else if (droppedPlayer.getListOfOpponentsIds().contains(
                                draggedPlayerOpponent.getRealPlayerId())) {
                        String playerOne = activity.getResources()
                                .getString(R.string.tournament_player_name_in_row, droppedPlayer.getFirstname(),
                                    droppedPlayer.getNickname(), droppedPlayer.getLastname());
                        String playerTwo = activity.getResources()
                                .getString(R.string.tournament_player_name_in_row, draggedPlayerOpponent.getFirstname(),
                                    draggedPlayerOpponent.getNickname(), draggedPlayerOpponent.getLastname());
                        Snackbar snackbar = Snackbar.make((activity).getCoordinatorLayout(),
                                activity.getResources()
                                    .getString(R.string.player_already_played_each_other, playerOne, playerTwo),
                                Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(activity.getResources().getColor(R.color.colorNeutral));

                        snackbar.show();
                    } else {
                        if (!droppedPlayerFinal.equals(draggedPlayer)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle(R.string.confirm_swap_player)
                                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (playerOneOrTwo.equals("1")) {
                                                    draggedGame.setPlayer1(droppedPlayerFinal);
                                                } else {
                                                    draggedGame.setPlayer2(droppedPlayerFinal);
                                                }

                                                if (droppedPlayerIndex == 1) {
                                                    droppedGame.setPlayer1(draggedPlayerFinal);
                                                } else {
                                                    droppedGame.setPlayer2(draggedPlayerFinal);
                                                }

                                                Toolbar toolbar = activity.getToolbar();
                                                ProgressBar progressBar = (ProgressBar) toolbar.findViewById(
                                                        R.id.toolbar_progress_bar);
                                                Snackbar snackbar = Snackbar.make((activity).getCoordinatorLayout(),
                                                        activity.getResources()
                                                            .getString(R.string.player_swapped_successfully),
                                                        Snackbar.LENGTH_LONG);
                                                snackbar.getView()
                                                .setBackgroundColor(
                                                    activity.getResources().getColor(R.color.colorPositive));
                                                new SwapPlayersTask((BaseApplication) activity.getApplication(),
                                                    draggedGame, droppedGame, progressBar, snackbar).execute();
                                            }
                                        })
                                .setNegativeButton(R.string.dialog_cancel, null)
                                .show();
                        }
                    }

                    break;

                case DragEvent.ACTION_DRAG_ENDED:

                    v.setBackgroundDrawable(normalShape);
                    break;

                default:
                    break;
            }

            return true;
        }
    }
}
