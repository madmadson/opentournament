package madson.org.opentournament.organize;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;

import android.graphics.drawable.Drawable;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import madson.org.opentournament.domain.TournamentParticipant;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.organize.team.TeamTournamentManagementActivity;
import madson.org.opentournament.tasks.SwapPlayersTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.utility.TournamentEventTag;
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
    private final Drawable startedShape;

    private List<Game> gamesForRound;
    private BaseActivity baseActivity;
    private int round;
    private Tournament tournament;

    public GameListAdapter(BaseActivity baseActivity, int round, Tournament tournament) {

        this.round = round;
        this.tournament = tournament;

        this.gamesForRound = new ArrayList<>();
        this.baseActivity = baseActivity;

        enterShape = baseActivity.getResources().getDrawable(R.drawable.shape_droptarget_entered);
        normalShape = baseActivity.getResources().getDrawable(R.drawable.shape_normal);
        startShape = baseActivity.getResources().getDrawable(R.drawable.shape_droptarget_started);

        winnerShape = baseActivity.getResources().getDrawable(R.drawable.shape_winner);
        looserShape = baseActivity.getResources().getDrawable(R.drawable.shape_looser);

        startedShape = baseActivity.getResources().getDrawable(R.drawable.shape_started);
    }

    public void updateGame(Game game) {

        if (gamesForRound.contains(game)) {
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

        return new GameViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final GameViewHolder holder, int position) {

        final Game game = gamesForRound.get(position);

        holder.getSwapPlayerOne().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    holder.getPlayerOneCardView().setBackgroundDrawable(startShape);
                }
            });

        holder.getSwapPlayerTwo().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    holder.getPlayerTwoCardView().setBackgroundDrawable(startShape);
                }
            });

        holder.getTableNumber()
            .setText(baseActivity.getResources().getString(R.string.table_number, game.getPlaying_field()));

        if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
            gameForSoloTournament(holder, game);
        } else {
            gameForTeamTournament(holder, game);
        }

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


    private void gameForTeamTournament(GameViewHolder holder, final Game game) {

        holder.getPlayerOneNameInList().setText(game.getParticipantOneUUID());
        holder.getPlayerTwoNameInList().setText(game.getParticipantTwoUUID());

        holder.getPlayerOneFaction().setVisibility(View.GONE);
        holder.getPlayerOneTeam().setVisibility(View.GONE);

        holder.getPlayerTwoFaction().setVisibility(View.GONE);
        holder.getPlayerTwoTeam().setVisibility(View.GONE);

        holder.getPlayerOneIntermediatePoints().setVisibility(View.VISIBLE);
        holder.getPlayerOneIntermediatePoints().setText(String.valueOf(game.getParticipant_one_intermediate_points()));

        holder.getPlayerTwoIntermediatePoints().setVisibility(View.VISIBLE);
        holder.getPlayerTwoIntermediatePoints().setText(String.valueOf(game.getParticipant_two_intermediate_points()));

        holder.getPairingRow().setOnClickListener(new TeamManagementClickListener(tournament, game));
        holder.getPlayerOneCardView().setOnClickListener(new TeamManagementClickListener(tournament, game));
        holder.getPlayerTwoCardView().setOnClickListener(new TeamManagementClickListener(tournament, game));
    }


    private void gameForSoloTournament(GameViewHolder holder, final Game game) {

        TournamentPlayer player1 = (TournamentPlayer) game.getParticipantOne();

        holder.getPlayerOneNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player1.getFirstName(), player1.getNickName(),
                    player1.getLastName()));
        holder.getPlayerOneFaction().setText(player1.getFaction());

        TournamentPlayer player2 = (TournamentPlayer) game.getParticipantTwo();

        holder.getPlayerTwoNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player2.getFirstName(), player2.getNickName(),
                    player2.getLastName()));
        holder.getPlayerTwoFaction().setText(player2.getFaction());

        holder.getPairingRow().setOnClickListener(new OpenEnterGameResultClickListener(game));
        holder.getPlayerOneCardView().setOnClickListener(new OpenEnterGameResultClickListener(game));
        holder.getPlayerTwoCardView().setOnClickListener(new OpenEnterGameResultClickListener(game));

        holder.getPlayerOneTeam().setVisibility(View.GONE);
        holder.getPlayerTwoTeam().setVisibility(View.GONE);

        holder.getPlayerOneFaction().setVisibility(View.GONE);
        holder.getPlayerTwoFaction().setVisibility(View.GONE);
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

            FragmentManager supportFragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
            dialog.show(supportFragmentManager, "enterGameResultDialog");
        }
    }

    private class TeamManagementClickListener implements View.OnClickListener {

        private Tournament tournament;
        private Game game;

        public TeamManagementClickListener(Tournament tournament, Game game) {

            this.tournament = tournament;

            this.game = game;
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(baseActivity, TeamTournamentManagementActivity.class);

            intent.putExtra(TeamTournamentManagementActivity.EXTRA_TOURNAMENT, tournament);
            intent.putExtra(TeamTournamentManagementActivity.EXTRA_GAME, game);
            baseActivity.startActivity(intent);
        }
    }

    private class GameLongClickEventListener implements View.OnLongClickListener {

        private Game draggedGame;
        private int participant;

        public GameLongClickEventListener(Game game, int participant) {

            this.draggedGame = game;

            this.participant = participant;
        }

        @Override
        public boolean onLongClick(View v) {

            ClipData participantId = ClipData.newPlainText("participant", String.valueOf(participant));
            v.startDrag(participantId, // the data to be dragged
                new View.DragShadowBuilder(v), // the drag shadow builder
                draggedGame, 0 // flags (not currently used, set to 0)
                );

            return false;
        }
    }

    private class GameDragListener implements View.OnDragListener {

        private Game droppedGame;
        private int droppedParticipantIndex;

        /**
         * @param  droppedGame  game of drag element
         * @param  droppedParticipantIndex  new opponent for dragged participant
         */
        public GameDragListener(Game droppedGame, int droppedParticipantIndex) {

            this.droppedGame = droppedGame;
            this.droppedParticipantIndex = droppedParticipantIndex;
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
                    final CharSequence participantOneOrTwo = event.getClipData().getItemAt(0).getText();

                    TournamentParticipant draggedParticipant = null;
                    TournamentParticipant draggedParticipantOpponent = null;
                    if (participantOneOrTwo.equals("1")) {
                        draggedParticipant = draggedGame.getParticipantOne();
                        draggedParticipantOpponent = draggedGame.getParticipantTwo();
                    } else {
                        draggedParticipant = draggedGame.getParticipantTwo();
                        draggedParticipantOpponent = draggedGame.getParticipantOne();
                    }

                    TournamentParticipant droppedParticipant = null;
                    TournamentParticipant droppedParticipantOpponent = null;
                    if (droppedParticipantIndex == 1) {
                        droppedParticipant = droppedGame.getParticipantOne();
                        droppedParticipantOpponent = droppedGame.getParticipantTwo();
                    } else {
                        droppedParticipant = droppedGame.getParticipantTwo();
                        droppedParticipantOpponent = droppedGame.getParticipantOne();
                    }

                    final TournamentParticipant draggedParticipantFinal = draggedParticipant;
                    final TournamentParticipant droppedParticipantFinal = droppedParticipant;

                    if (draggedParticipant.getListOfOpponentsUUIDs().contains(droppedParticipantOpponent.getUuid())) {
                        Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                                baseActivity.getResources()
                                    .getString(R.string.player_already_played_each_other, draggedParticipant.getName(),
                                        droppedParticipant.getName()), Snackbar.LENGTH_LONG);
                        snackbar.getView()
                            .setBackgroundColor(baseActivity.getResources().getColor(R.color.colorNeutral));
                        snackbar.show();
                    } else if (droppedParticipant.getListOfOpponentsUUIDs()
                            .contains(draggedParticipantOpponent.getUuid())) {
                        Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                                baseActivity.getResources()
                                    .getString(R.string.player_already_played_each_other, droppedParticipant.getName(),
                                        draggedParticipant.getName()), Snackbar.LENGTH_LONG);
                        snackbar.getView()
                            .setBackgroundColor(baseActivity.getResources().getColor(R.color.colorNeutral));

                        snackbar.show();
                    } else {
                        if (!droppedParticipantFinal.equals(draggedParticipant)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                            builder.setTitle(R.string.confirm_swap_player)
                                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (participantOneOrTwo.equals("1")) {
                                                    draggedGame.setParticipantOne(droppedParticipantFinal);
                                                } else {
                                                    draggedGame.setParticipantTwo(droppedParticipantFinal);
                                                }

                                                if (droppedParticipantIndex == 1) {
                                                    droppedGame.setParticipantOne(draggedParticipantFinal);
                                                } else {
                                                    droppedGame.setParticipantTwo(draggedParticipantFinal);
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
