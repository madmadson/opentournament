package madson.org.opentournament.organize.team;

import android.content.ClipData;
import android.content.DialogInterface;

import android.graphics.drawable.Drawable;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

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
import madson.org.opentournament.domain.TournamentParticipant;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.events.EndSwapPlayerEvent;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.SwapPlayerEvent;
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
    private final Drawable startedShape;

    private List<Game> gamesInTeamMatch;
    private BaseActivity baseActivity;
    private Tournament tournament;

    private Game gameOneToSwap;
    private int playerNumberGameOneToSwap;

    public TeamGameListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.tournament = tournament;

        this.gamesInTeamMatch = new ArrayList<>();
        this.baseActivity = baseActivity;

        enterShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_droptarget_entered);
        normalShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_normal);
        startShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_droptarget_started);

        winnerShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_winner);
        looserShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_looser);

        startedShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_started);
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
        holder.getSwapPlayerOne().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (game.isStartSwappingPlayerOne()) {
                        baseActivity.getBaseApplication()
                        .notifyTournamentEvent(OpenTournamentEventTag.END_SWAP_PLAYER, new EndSwapPlayerEvent(game));
                    } else {
                        baseActivity.getBaseApplication()
                        .notifyTournamentEvent(OpenTournamentEventTag.SWAP_PLAYER, new SwapPlayerEvent(game, 1));
                    }
                }
            });

        holder.getSwapPlayerTwo().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (game.isStartSwappingPlayerTwo()) {
                        baseActivity.getBaseApplication()
                        .notifyTournamentEvent(OpenTournamentEventTag.END_SWAP_PLAYER, new EndSwapPlayerEvent(game));
                    } else {
                        baseActivity.getBaseApplication()
                        .notifyTournamentEvent(OpenTournamentEventTag.SWAP_PLAYER, new SwapPlayerEvent(game, 2));
                    }
                }
            });

        holder.getTableNumber()
            .setText(baseActivity.getResources().getString(R.string.table_number, game.getPlaying_field()));

        TournamentPlayer player1 = (TournamentPlayer) game.getParticipantOne();

        holder.getPlayerOneNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player1.getFirstNameWithMaximumCharacters(10),
                    player1.getNickNameWithMaximumCharacters(10), player1.getLastNameWithMaximumCharacters(10)));
        holder.getPlayerOneTeam().setText(player1.getTeamName());
        holder.getPlayerOneFaction().setText(player1.getFaction());

        TournamentPlayer player2 = (TournamentPlayer) game.getParticipantTwo();

        holder.getPlayerTwoNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player2.getFirstNameWithMaximumCharacters(10),
                    player2.getNickNameWithMaximumCharacters(10), player2.getLastNameWithMaximumCharacters(10)));
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
            holder.getPairingRow().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorTurquoise));
        } else {
            holder.getPairingRow()
                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightTurquoise));
        }

        if (game.isFinished()) {
            holder.getPlayerOneCardView()
                .setBackground(game.getParticipant_one_score() == 1 ? winnerShape : looserShape);
            holder.getPlayerTwoCardView()
                .setBackground(game.getParticipant_two_score() == 1 ? winnerShape : looserShape);

            holder.getPlayerOneCardView().setOnLongClickListener(null);
            holder.getPlayerOneCardView().setOnDragListener(null);

            holder.getPlayerTwoCardView().setOnLongClickListener(null);
            holder.getPlayerTwoCardView().setOnDragListener(null);

            holder.getSwapPlayerOne().setVisibility(View.GONE);
            holder.getSwapPlayerTwo().setVisibility(View.GONE);
        } else {
            holder.getPlayerOneCardView().setBackground(normalShape);
            holder.getPlayerTwoCardView().setBackground(normalShape);

            holder.getPlayerOneCardView().setOnLongClickListener(new GameLongClickEventListener(game, 1));
            holder.getPlayerOneCardView().setOnDragListener(new GameDragListener(game, 1));

            holder.getPlayerTwoCardView().setOnLongClickListener(new GameLongClickEventListener(game, 2));
            holder.getPlayerTwoCardView().setOnDragListener(new GameDragListener(game, 2));
        }

        if (game.isSwappable()) {
            holder.getPlayerOneCardView().setBackground(enterShape);
            holder.getPlayerTwoCardView().setBackground(enterShape);
            holder.getPlayerOneCardView().setOnLongClickListener(null);
            holder.getPlayerOneCardView().setOnDragListener(null);

            holder.getPlayerTwoCardView().setOnLongClickListener(null);
            holder.getPlayerTwoCardView().setOnDragListener(null);

            holder.getPlayerOneCardView().setOnClickListener(new SwapPlayerConfirmedClicked(game, 1));
            holder.getPlayerTwoCardView().setOnClickListener(new SwapPlayerConfirmedClicked(game, 2));
        }

        if (game.isStartSwappingPlayerOne()) {
            holder.getPlayerOneCardView().setBackground(startedShape);
        }

        if (game.isStartSwappingPlayerTwo()) {
            holder.getPlayerTwoCardView().setBackground(startedShape);
        }

        if (game.getParticipant_one_army_list() != null && !game.getParticipant_one_army_list().isEmpty()) {
            holder.getPlayerOneArmyListIcon().setVisibility(View.VISIBLE);
            holder.getPlayerOneArmyList().setVisibility(View.VISIBLE);
            holder.getPlayerOneArmyList().setText(game.getParticipantOneArmyListWithMaximumCharacters(15));
        } else {
            holder.getPlayerOneArmyList().setVisibility(View.GONE);
            holder.getPlayerOneArmyListIcon().setVisibility(View.GONE);
        }

        if (game.getParticipant_two_army_list() != null && !game.getParticipant_two_army_list().isEmpty()) {
            holder.getPlayerTwoArmyListIcon().setVisibility(View.VISIBLE);
            holder.getPlayerTwoArmyList().setVisibility(View.VISIBLE);
            holder.getPlayerTwoArmyList().setText(game.getParticipantTwoArmyListWithMaximumCharacters(15));
        } else {
            holder.getPlayerTwoArmyListIcon().setVisibility(View.GONE);
            holder.getPlayerTwoArmyList().setVisibility(View.GONE);
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


    public void startSwapping(Game swappedGame, int playerNumber) {

        if (gamesInTeamMatch.contains(swappedGame)) {
            List<Game> swappingGames = new ArrayList<>();

            for (Game game : gamesInTeamMatch) {
                if (!game.isFinished()) {
                    if (game.equals(swappedGame)) {
                        if (playerNumber == 1) {
                            game.setStartSwappingPlayerOne(true);
                            game.setStartSwappingPlayerTwo(false);
                            playerNumberGameOneToSwap = 1;
                        } else if (playerNumber == 2) {
                            game.setStartSwappingPlayerTwo(true);
                            game.setStartSwappingPlayerOne(false);
                            playerNumberGameOneToSwap = 2;
                        }

                        gameOneToSwap = game;
                    } else {
                        game.setStartSwappingPlayerOne(false);
                        game.setStartSwappingPlayerTwo(false);
                    }

                    game.setSwappable(true);
                    swappingGames.add(game);
                } else {
                    swappingGames.add(game);
                }
            }

            this.gamesInTeamMatch = swappingGames;

            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.click_for_swapping,
                    Snackbar.LENGTH_LONG);

            snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));

            snackbar.show();

            notifyDataSetChanged();
        }
    }


    public void endSwapping(Game swappedGame) {

        if (gamesInTeamMatch.contains(swappedGame)) {
            List<Game> swappingGames = new ArrayList<>();

            for (Game game : gamesInTeamMatch) {
                game.setSwappable(false);
                game.setStartSwappingPlayerOne(false);
                game.setStartSwappingPlayerTwo(false);

                swappingGames.add(game);
            }

            gameOneToSwap = null;
            playerNumberGameOneToSwap = 0;

            this.gamesInTeamMatch = swappingGames;

            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.swapping_canceled,
                    Snackbar.LENGTH_LONG);

            snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

            snackbar.show();

            notifyDataSetChanged();
        }
    }


    private void showSwapPlayerDialog(final Game gameTwoToSwap, final int playerNumberGameTwoToSwap) {

        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
        builder.setTitle(R.string.confirm_swap_player)
            .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            TournamentPlayer gameOneParticipantOne = gameOneToSwap.getTournamentPlayerOne();
                            TournamentPlayer gameOneParticipantTwo = gameOneToSwap.getTournamentPlayerTwo();

                            Game newGameOneCopy = new Game(gameOneToSwap);
                            Game newGameTwoCopy = new Game(gameTwoToSwap);

                            if (playerNumberGameOneToSwap == 1) {
                                if (playerNumberGameTwoToSwap == 1) {
                                    newGameOneCopy.setTournamentPlayerOne(newGameTwoCopy.getTournamentPlayerOne());
                                    newGameOneCopy.setParticipantOne(newGameTwoCopy.getTournamentPlayerOne());
                                    newGameOneCopy.setParticipantOneUUID(newGameTwoCopy.getParticipantOneUUID());
                                } else {
                                    newGameOneCopy.setTournamentPlayerOne(newGameTwoCopy.getTournamentPlayerTwo());
                                    newGameOneCopy.setParticipantOne(newGameTwoCopy.getTournamentPlayerTwo());
                                    newGameOneCopy.setParticipantOneUUID(newGameTwoCopy.getParticipantTwoUUID());
                                }
                            } else {
                                if (playerNumberGameTwoToSwap == 1) {
                                    newGameOneCopy.setTournamentPlayerTwo(newGameTwoCopy.getTournamentPlayerOne());
                                    newGameOneCopy.setParticipantTwo(newGameTwoCopy.getTournamentPlayerOne());
                                    newGameOneCopy.setParticipantTwoUUID(newGameTwoCopy.getParticipantOneUUID());
                                } else {
                                    newGameOneCopy.setTournamentPlayerTwo(newGameTwoCopy.getTournamentPlayerTwo());
                                    newGameOneCopy.setParticipantTwo(newGameTwoCopy.getTournamentPlayerTwo());
                                    newGameOneCopy.setParticipantTwoUUID(newGameTwoCopy.getParticipantTwoUUID());
                                }
                            }

                            if (playerNumberGameTwoToSwap == 1) {
                                if (playerNumberGameOneToSwap == 1) {
                                    newGameTwoCopy.setTournamentPlayerOne(gameOneParticipantOne);
                                    newGameTwoCopy.setParticipantOne(gameOneParticipantOne);
                                    newGameTwoCopy.setParticipantOneUUID(gameOneParticipantOne.getUuid());
                                } else {
                                    newGameTwoCopy.setTournamentPlayerOne(gameOneParticipantTwo);
                                    newGameTwoCopy.setParticipantOne(gameOneParticipantTwo);
                                    newGameTwoCopy.setParticipantOneUUID(gameOneParticipantTwo.getUuid());
                                }
                            } else {
                                if (playerNumberGameOneToSwap == 1) {
                                    newGameTwoCopy.setTournamentPlayerTwo(gameOneParticipantOne);
                                    newGameTwoCopy.setParticipantTwo(gameOneParticipantOne);
                                    newGameTwoCopy.setParticipantTwoUUID(gameOneParticipantOne.getUuid());
                                } else {
                                    newGameTwoCopy.setTournamentPlayerTwo(gameOneParticipantTwo);
                                    newGameTwoCopy.setParticipantTwo(gameOneParticipantTwo);
                                    newGameTwoCopy.setParticipantTwoUUID(gameOneParticipantTwo.getUuid());
                                }
                            }

                            boolean swappingValid = true;

                            if (newGameOneCopy.getTournamentPlayerOne()
                                    .getTeamName()
                                    .equals(newGameOneCopy.getTournamentPlayerTwo().getTeamName())) {
                                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                                        baseActivity.getResources()
                                            .getString(R.string.players_are_in_same_team,
                                                newGameOneCopy.getParticipantOne().getName(),
                                                newGameOneCopy.getParticipantTwo().getName()), Snackbar.LENGTH_LONG);
                                snackbar.getView()
                                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorNegative));
                                snackbar.show();
                                swappingValid = false;
                            } else if (newGameTwoCopy.getTournamentPlayerOne()
                                    .getTeamName()
                                    .equals(newGameTwoCopy.getTournamentPlayerTwo().getTeamName())) {
                                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                                        baseActivity.getResources()
                                            .getString(R.string.players_are_in_same_team,
                                                newGameTwoCopy.getParticipantOne().getName(),
                                                newGameTwoCopy.getParticipantTwo().getName()), Snackbar.LENGTH_LONG);
                                snackbar.getView()
                                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorNegative));
                                snackbar.show();
                                swappingValid = false;
                            }

                            if (swappingValid) {
                                Toolbar toolbar = baseActivity.getToolbar();
                                ProgressBar progressBar = (ProgressBar) toolbar.findViewById(R.id.toolbar_progress_bar);
                                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                                        baseActivity.getResources().getString(R.string.player_swapped_successfully),
                                        Snackbar.LENGTH_LONG);
                                snackbar.getView()
                                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));
                                new SwapPlayersTask(baseActivity.getBaseApplication(), newGameOneCopy, newGameTwoCopy,
                                    progressBar, snackbar).execute();
                            }
                        }
                    })
            .setNegativeButton(R.string.dialog_cancel, null)
            .show();
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

        private int droppedParticipantIndex;
        private Game droppedGame;

        /**
         * @param  droppedGame  game of drag element
         * @param  droppedParticipantIndex  new opponent for dragged player
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

                    v.setBackground(startShape);

                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(enterShape);
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackground(startShape);
                    break;

                case DragEvent.ACTION_DROP:

                    gameOneToSwap = (Game) event.getLocalState();
                    playerNumberGameOneToSwap = Integer.valueOf(event.getClipData().getItemAt(0).getText().toString());

                    showSwapPlayerDialog(droppedGame, droppedParticipantIndex);

                    break;

                case DragEvent.ACTION_DRAG_ENDED:

                    v.setBackground(normalShape);
                    break;

                default:
                    break;
            }

            return true;
        }
    }

    private class SwapPlayerConfirmedClicked implements View.OnClickListener {

        private Game gameTwoToSwap;
        private int playerNumberGameTwoToSwap;

        public SwapPlayerConfirmedClicked(Game gameTwoToSwap, int gameTwoPlayerNumber) {

            this.gameTwoToSwap = gameTwoToSwap;
            this.playerNumberGameTwoToSwap = gameTwoPlayerNumber;
        }

        @Override
        public void onClick(View v) {

            showSwapPlayerDialog(gameTwoToSwap, playerNumberGameTwoToSwap);
        }
    }
}
