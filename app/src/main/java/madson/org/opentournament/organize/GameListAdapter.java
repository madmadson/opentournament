package madson.org.opentournament.organize;

import android.content.ClipData;
import android.content.DialogInterface;

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

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {

    private final Drawable enterShape;
    private final Drawable normalShape;
    private final Drawable startShape;
    private final Drawable winnerShape;
    private final Drawable looserShape;

    private List<Game> gamesForRound;
    private BaseActivity activity;

    public GameListAdapter(BaseActivity activity) {

        this.gamesForRound = new ArrayList<>();
        this.activity = activity;

        enterShape = activity.getResources().getDrawable(R.drawable.shape_droptarget_entered);
        normalShape = activity.getResources().getDrawable(R.drawable.shape_normal);
        startShape = activity.getResources().getDrawable(R.drawable.shape_droptarget_started);

        winnerShape = activity.getResources().getDrawable(R.drawable.shape_winner);
        looserShape = activity.getResources().getDrawable(R.drawable.shape_looser);
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

        // drag and drop for manually re pair
        holder.getPlayerOneCardView().setOnClickListener(holder);
        holder.getPlayerOneCardView().setOnLongClickListener(new GameLongClickEventListener(game, 1));
        holder.getPlayerOneCardView().setOnDragListener(new GameDragListener(game, 1));

        holder.getPlayerTwoCardView().setOnClickListener(holder);
        holder.getPlayerTwoCardView().setOnLongClickListener(new GameLongClickEventListener(game, 2));
        holder.getPlayerTwoCardView().setOnDragListener(new GameDragListener(game, 2));
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
