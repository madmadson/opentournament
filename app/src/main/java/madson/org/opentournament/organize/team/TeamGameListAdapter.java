package madson.org.opentournament.organize.team;

import android.content.ClipData;
import android.content.DialogInterface;

import android.graphics.Color;

import android.graphics.drawable.Drawable;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.EnterResultForGameDialog;
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
public class TeamGameListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    private final Drawable enterShape;
    private final Drawable normalShape;
    private final Drawable startShape;
    private final Drawable winnerShape;
    private final Drawable looserShape;

    private List<Game> gamesInTeamMatch;
    private BaseActivity baseActivity;
    private Tournament tournament;

    public TeamGameListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.tournament = tournament;

        this.gamesInTeamMatch = new ArrayList<>();
        this.baseActivity = baseActivity;

        enterShape = baseActivity.getResources().getDrawable(R.drawable.shape_droptarget_entered);
        normalShape = baseActivity.getResources().getDrawable(R.drawable.shape_normal);
        startShape = baseActivity.getResources().getDrawable(R.drawable.shape_droptarget_started);

        winnerShape = baseActivity.getResources().getDrawable(R.drawable.shape_winner);
        looserShape = baseActivity.getResources().getDrawable(R.drawable.shape_looser);
    }

    public void setGames(List<Game> games) {

        this.gamesInTeamMatch = games;

        notifyDataSetChanged();
    }


    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_game, parent, false);

        return new GameViewHolder(v);
    }


    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {

        final Game game = gamesInTeamMatch.get(position);

        holder.getTableNumber()
            .setText(baseActivity.getResources().getString(R.string.table_number, game.getPlaying_field()));

        TournamentPlayer player1 = (TournamentPlayer) game.getParticipantOne();

        holder.getPlayerOneNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player1.getFirstName(), player1.getNickName(),
                    player1.getLastName()));
        holder.getPlayerOneTeam().setText(player1.getTeamName());
        holder.getPlayerOneFaction().setText(player1.getFaction());

        TournamentPlayer player2 = (TournamentPlayer) game.getParticipantTwo();

        holder.getPlayerTwoNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player2.getFirstName(), player2.getNickName(),
                    player2.getLastName()));
        holder.getPlayerTwoTeam().setText(player2.getTeamName());
        holder.getPlayerTwoFaction().setText(player2.getFaction());

        holder.getPairingRow().setOnClickListener(new OpenEnterGameResultClickListener(game));
        holder.getPlayerOneCardView().setOnClickListener(new OpenEnterGameResultClickListener(game));
        holder.getPlayerTwoCardView().setOnClickListener(new OpenEnterGameResultClickListener(game));

        holder.getPlayerOneScore()
            .setText(baseActivity.getResources().getString(R.string.game_win, game.getParticipant_one_score()));
        holder.getPlayerOneControlPoints()
            .setText(baseActivity.getResources().getString(R.string.game_cp, game.getParticipant_one_control_points()));
        holder.getPlayerOneVictoryPoints()
            .setText(baseActivity.getResources().getString(R.string.game_vp, game.getParticipant_one_victory_points()));

        holder.getPlayerTwoScore()
            .setText(baseActivity.getResources().getString(R.string.game_win, game.getParticipant_two_score()));
        holder.getPlayerTwoControlPoints()
            .setText(baseActivity.getResources().getString(R.string.game_cp, game.getParticipant_two_control_points()));
        holder.getPlayerTwoVictoryPoints()
            .setText(baseActivity.getResources().getString(R.string.game_vp, game.getParticipant_two_victory_points()));

        if (position % 2 == 0) {
            holder.getPairingRow().setBackgroundColor(Color.LTGRAY);
        } else {
            holder.getPairingRow().setBackgroundColor(Color.WHITE);
        }

        if (game.isFinished()) {
            holder.getPlayerOneCardView()
                .setBackgroundDrawable(game.getParticipant_one_score() == 1 ? winnerShape : looserShape);
            holder.getPlayerTwoCardView()
                .setBackgroundDrawable(game.getParticipant_two_score() == 1 ? winnerShape : looserShape);

            holder.getPlayerOneCardView().setOnLongClickListener(null);
            holder.getPlayerOneCardView().setOnDragListener(null);

            holder.getPlayerTwoCardView().setOnLongClickListener(null);
            holder.getPlayerTwoCardView().setOnDragListener(null);
        } else {
            holder.getPlayerOneCardView().setBackgroundDrawable(normalShape);
            holder.getPlayerTwoCardView().setBackgroundDrawable(normalShape);

            holder.getPlayerOneCardView().setOnLongClickListener(new GameLongClickEventListener(game, 1));
            holder.getPlayerOneCardView().setOnDragListener(new GameDragListener(game, 1));

            holder.getPlayerTwoCardView().setOnLongClickListener(new GameLongClickEventListener(game, 2));
            holder.getPlayerTwoCardView().setOnDragListener(new GameDragListener(game, 2));
        }
    }


    @Override
    public int getItemCount() {

        return gamesInTeamMatch.size();
    }


    public void updateGame(Game game) {

        if (gamesInTeamMatch.contains(game)) {
            int indexOfGame = gamesInTeamMatch.indexOf(game);

            gamesInTeamMatch.remove(game);
            gamesInTeamMatch.add(indexOfGame, game);
            notifyDataSetChanged();
        }
    }

    private class OpenEnterGameResultClickListener implements View.OnClickListener {

        private Game game;

        public OpenEnterGameResultClickListener(Game game) {

            this.game = game;
        }

        @Override
        public void onClick(View v) {

            EnterResultForGameDialog dialog = new EnterResultForGameDialog();

            Bundle resultForPairingResult = new Bundle();
            resultForPairingResult.putParcelable(EnterResultForGameDialog.BUNDLE_GAME, game);
            resultForPairingResult.putParcelable(EnterResultForGameDialog.BUNDLE_TOURNAMENT, tournament);
            dialog.setArguments(resultForPairingResult);

            FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();
            dialog.show(supportFragmentManager, "enterGameResultDialog");
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
                        draggedPlayer = (TournamentPlayer) draggedGame.getParticipantOne();
                        draggedPlayerOpponent = (TournamentPlayer) draggedGame.getParticipantTwo();
                    } else {
                        draggedPlayer = (TournamentPlayer) draggedGame.getParticipantTwo();
                        draggedPlayerOpponent = (TournamentPlayer) draggedGame.getParticipantOne();
                    }

                    TournamentPlayer droppedPlayer = null;
                    TournamentPlayer droppedPlayerOpponent = null;
                    if (droppedPlayerIndex == 1) {
                        droppedPlayer = (TournamentPlayer) droppedGame.getParticipantOne();
                        droppedPlayerOpponent = (TournamentPlayer) droppedGame.getParticipantTwo();
                    } else {
                        droppedPlayer = (TournamentPlayer) droppedGame.getParticipantTwo();
                        droppedPlayerOpponent = (TournamentPlayer) droppedGame.getParticipantOne();
                    }

                    final TournamentPlayer draggedPlayerFinal = draggedPlayer;
                    final TournamentPlayer droppedPlayerFinal = droppedPlayer;

                    if (draggedPlayer.getListOfOpponentsIds().contains(droppedPlayerOpponent.getPlayerUUID())) {
                        String playerOne = baseActivity.getResources()
                                .getString(R.string.player_name_in_row, draggedPlayer.getFirstName(),
                                    draggedPlayer.getNickName(), draggedPlayer.getLastName());
                        String playerTwo = baseActivity.getResources()
                                .getString(R.string.player_name_in_row, droppedPlayerOpponent.getFirstName(),
                                    droppedPlayerOpponent.getNickName(), droppedPlayerOpponent.getLastName());

                        Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                                baseActivity.getResources()
                                    .getString(R.string.player_already_played_each_other, playerOne, playerTwo),
                                Snackbar.LENGTH_LONG);
                        snackbar.getView()
                            .setBackgroundColor(baseActivity.getResources().getColor(R.color.colorNeutral));
                        snackbar.show();
                    } else if (droppedPlayer.getListOfOpponentsIds().contains(draggedPlayerOpponent.getPlayerUUID())) {
                        String playerOne = baseActivity.getResources()
                                .getString(R.string.player_name_in_row, droppedPlayer.getFirstName(),
                                    droppedPlayer.getNickName(), droppedPlayer.getLastName());
                        String playerTwo = baseActivity.getResources()
                                .getString(R.string.player_name_in_row, draggedPlayerOpponent.getFirstName(),
                                    draggedPlayerOpponent.getNickName(), draggedPlayerOpponent.getLastName());
                        Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                                baseActivity.getResources()
                                    .getString(R.string.player_already_played_each_other, playerOne, playerTwo),
                                Snackbar.LENGTH_LONG);
                        snackbar.getView()
                            .setBackgroundColor(baseActivity.getResources().getColor(R.color.colorNeutral));

                        snackbar.show();
                    } else {
                        if (!droppedPlayerFinal.equals(draggedPlayer)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                            builder.setTitle(R.string.confirm_swap_player)
                                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (playerOneOrTwo.equals("1")) {
                                                    draggedGame.setParticipantOne(droppedPlayerFinal);
                                                } else {
                                                    draggedGame.setParticipantTwo(droppedPlayerFinal);
                                                }

                                                if (droppedPlayerIndex == 1) {
                                                    droppedGame.setParticipantOne(draggedPlayerFinal);
                                                } else {
                                                    droppedGame.setParticipantTwo(draggedPlayerFinal);
                                                }

                                                Toolbar toolbar = baseActivity.getToolbar();
                                                ProgressBar progressBar = (ProgressBar) toolbar.findViewById(
                                                        R.id.toolbar_progress_bar);
                                                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                                                        baseActivity.getResources()
                                                            .getString(R.string.player_swapped_successfully),
                                                        Snackbar.LENGTH_LONG);
                                                snackbar.getView()
                                                .setBackgroundColor(
                                                    baseActivity.getResources().getColor(R.color.colorPositive));
                                                new SwapPlayersTask((BaseApplication) baseActivity.getApplication(),
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
