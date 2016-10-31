package madson.org.opentournament.organize;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.drawable.Drawable;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

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
import madson.org.opentournament.events.EndSwapPlayerEvent;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.SwapPlayerEvent;
import madson.org.opentournament.organize.team.TeamTournamentManagementActivity;
import madson.org.opentournament.tasks.DeleteGamesTask;
import madson.org.opentournament.tasks.SwapPlayersTask;
import madson.org.opentournament.utility.BaseActivity;
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

    private Game gameOneToSwap;
    private int playerNumberGameOneToSwap;

    public GameListAdapter(BaseActivity baseActivity, int round, Tournament tournament) {

        this.round = round;
        this.tournament = tournament;

        this.gamesForRound = new ArrayList<>();
        this.baseActivity = baseActivity;

        enterShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_droptarget_entered);
        normalShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_normal);
        startShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_droptarget_started);

        winnerShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_winner);
        looserShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_looser);

        startedShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_started);
    }

    public void updateGame(Game game) {

        if (gamesForRound.contains(game)) {
            int indexOfGame = gamesForRound.indexOf(game);

            if (game.getUUID().equals(gamesForRound.get(indexOfGame).getUUID())) {
                gamesForRound.remove(game);

                gamesForRound.add(indexOfGame, game);
                notifyDataSetChanged();
            }
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
            holder.getPairingRow().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightGrey));
        } else {
            holder.getPairingRow().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostWhite));
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

            if (game.getParticipant_one_intermediate_points() == 0
                    && game.getParticipant_two_intermediate_points() == 0) {
                holder.getPlayerOneCardView().setOnLongClickListener(new GameLongClickEventListener(game, 1));
                holder.getPlayerOneCardView().setOnDragListener(new GameDragListener(game, 1));

                holder.getPlayerTwoCardView().setOnLongClickListener(new GameLongClickEventListener(game, 2));
                holder.getPlayerTwoCardView().setOnDragListener(new GameDragListener(game, 2));

                holder.getSwapPlayerOne().setVisibility(View.VISIBLE);
                holder.getSwapPlayerTwo().setVisibility(View.VISIBLE);
            }
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

        if (game.getParticipant_one_intermediate_points() != 0 || game.getParticipant_two_intermediate_points() != 0) {
            holder.getSwapPlayerOne().setVisibility(View.GONE);
            holder.getSwapPlayerTwo().setVisibility(View.GONE);
            holder.getPlayerOneCardView().setOnLongClickListener(null);
            holder.getPlayerOneCardView().setOnDragListener(null);

            holder.getPlayerTwoCardView().setOnLongClickListener(null);
            holder.getPlayerTwoCardView().setOnDragListener(null);
        }

        holder.getPairingRow().setOnClickListener(new TeamManagementClickListener(tournament, game));
        holder.getPlayerOneCardView().setOnClickListener(new TeamManagementClickListener(tournament, game));
        holder.getPlayerTwoCardView().setOnClickListener(new TeamManagementClickListener(tournament, game));
    }


    private void gameForSoloTournament(GameViewHolder holder, final Game game) {

        TournamentPlayer player1 = (TournamentPlayer) game.getParticipantOne();

        holder.getPlayerOneNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player1.getFirstNameWithMaximumCharacters(10),
                    player1.getNickNameWithMaximumCharacters(10), player1.getLastNameWithMaximumCharacters(10)));
        holder.getPlayerOneFaction().setText(player1.getFaction());

        TournamentPlayer player2 = (TournamentPlayer) game.getParticipantTwo();

        holder.getPlayerTwoNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player2.getFirstNameWithMaximumCharacters(10),
                    player2.getNickNameWithMaximumCharacters(10), player2.getLastNameWithMaximumCharacters(10)));
        holder.getPlayerTwoFaction().setText(player2.getFaction());

        holder.getPlayerOneCardView().setOnClickListener(new OpenEnterGameResultClickListener(game));
        holder.getPlayerTwoCardView().setOnClickListener(new OpenEnterGameResultClickListener(game));

        holder.getPlayerOneTeam().setVisibility(View.GONE);
        holder.getPlayerTwoTeam().setVisibility(View.GONE);

        holder.getPlayerOneFaction().setVisibility(View.GONE);
        holder.getPlayerTwoFaction().setVisibility(View.GONE);

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


    public void startSwapping(Game swappedGame, int playerNumber) {

        if (gamesForRound.contains(swappedGame)) {
            List<Game> swappingGames = new ArrayList<>();

            for (Game game : gamesForRound) {
                if (!game.isFinished()
                        && (game.getParticipant_one_intermediate_points() == 0
                            && game.getParticipant_two_intermediate_points() == 0)) {
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

            this.gamesForRound = swappingGames;

            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.click_for_swapping,
                    Snackbar.LENGTH_LONG);

            snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));

            snackbar.show();

            notifyDataSetChanged();
        }
    }


    public void endSwapping(Game swappedGame) {

        if (gamesForRound.contains(swappedGame)) {
            List<Game> swappingGames = new ArrayList<>();

            for (Game game : gamesForRound) {
                game.setSwappable(false);
                game.setStartSwappingPlayerOne(false);
                game.setStartSwappingPlayerTwo(false);

                swappingGames.add(game);
            }

            gameOneToSwap = null;
            playerNumberGameOneToSwap = 0;

            this.gamesForRound = swappingGames;

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

                            TournamentParticipant gameOneParticipantOne = gameOneToSwap.getParticipantOne();
                            TournamentParticipant gameOneParticipantTwo = gameOneToSwap.getParticipantTwo();

                            boolean swappingValid = newPairingsDoesNotPlayedAgainstEachOther(gameTwoToSwap,
                                    playerNumberGameTwoToSwap);

                            if (swappingValid) {
                                if (TournamentTyp.TEAM.name().equals(tournament.getState())) {
                                    new DeleteGamesTask(baseActivity.getBaseApplication(), gameOneToSwap, gameTwoToSwap)
                                    .execute();
                                }

                                if (playerNumberGameOneToSwap == 1) {
                                    if (playerNumberGameTwoToSwap == 1) {
                                        gameOneToSwap.setParticipantOne(gameTwoToSwap.getParticipantOne());
                                    } else {
                                        gameOneToSwap.setParticipantOne(gameTwoToSwap.getParticipantTwo());
                                    }
                                } else {
                                    if (playerNumberGameTwoToSwap == 1) {
                                        gameOneToSwap.setParticipantTwo(gameTwoToSwap.getParticipantOne());
                                    } else {
                                        gameOneToSwap.setParticipantTwo(gameTwoToSwap.getParticipantTwo());
                                    }
                                }

                                if (playerNumberGameTwoToSwap == 1) {
                                    if (playerNumberGameOneToSwap == 1) {
                                        gameTwoToSwap.setParticipantOne(gameOneParticipantOne);
                                    } else {
                                        gameTwoToSwap.setParticipantOne(gameOneParticipantTwo);
                                    }
                                } else {
                                    if (playerNumberGameOneToSwap == 1) {
                                        gameTwoToSwap.setParticipantTwo(gameOneParticipantOne);
                                    } else {
                                        gameTwoToSwap.setParticipantTwo(gameOneParticipantTwo);
                                    }
                                }

                                Toolbar toolbar = baseActivity.getToolbar();
                                ProgressBar progressBar = (ProgressBar) toolbar.findViewById(R.id.toolbar_progress_bar);
                                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                                        baseActivity.getResources().getString(R.string.player_swapped_successfully),
                                        Snackbar.LENGTH_LONG);
                                snackbar.getView()
                                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));
                                new SwapPlayersTask(baseActivity.getBaseApplication(), gameOneToSwap, gameTwoToSwap,
                                    progressBar, snackbar).execute();
                            }
                        }
                    })
            .setNegativeButton(R.string.dialog_cancel, null)
            .show();
    }


    private boolean newPairingsDoesNotPlayedAgainstEachOther(Game gameTwoToSwap, int playerNumberGameTwoToSwap) {

        if (playerNumberGameOneToSwap == 1 && playerNumberGameTwoToSwap == 1) {
            if (gameOneToSwap.getParticipantOne()
                    .getListOfOpponentsUUIDs()
                    .contains(gameTwoToSwap.getParticipantTwo().getUuid())) {
                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                        baseActivity.getResources()
                            .getString(R.string.player_already_played_each_other,
                                gameOneToSwap.getParticipantOne().getName(),
                                gameTwoToSwap.getParticipantTwo().getName()), Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));
                snackbar.show();

                return false;
            } else if (gameTwoToSwap.getParticipantOne()
                    .getListOfOpponentsUUIDs()
                    .contains(gameOneToSwap.getParticipantTwo().getUuid())) {
                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                        baseActivity.getResources()
                            .getString(R.string.player_already_played_each_other,
                                gameTwoToSwap.getParticipantOne().getName(),
                                gameOneToSwap.getParticipantTwo().getName()), Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

                snackbar.show();

                return false;
            }
        } else if (playerNumberGameOneToSwap == 2 && playerNumberGameTwoToSwap == 1) {
            if (gameOneToSwap.getParticipantTwo()
                    .getListOfOpponentsUUIDs()
                    .contains(gameTwoToSwap.getParticipantTwo().getUuid())) {
                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                        baseActivity.getResources()
                            .getString(R.string.player_already_played_each_other,
                                gameOneToSwap.getParticipantTwo().getName(),
                                gameTwoToSwap.getParticipantTwo().getName()), Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));
                snackbar.show();

                return false;
            } else if (gameTwoToSwap.getParticipantOne()
                    .getListOfOpponentsUUIDs()
                    .contains(gameOneToSwap.getParticipantOne().getUuid())) {
                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                        baseActivity.getResources()
                            .getString(R.string.player_already_played_each_other,
                                gameTwoToSwap.getParticipantOne().getName(),
                                gameOneToSwap.getParticipantOne().getName()), Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

                snackbar.show();

                return false;
            }
        } else if (playerNumberGameOneToSwap == 1 && playerNumberGameTwoToSwap == 2) {
            if (gameOneToSwap.getParticipantOne()
                    .getListOfOpponentsUUIDs()
                    .contains(gameTwoToSwap.getParticipantOne().getUuid())) {
                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                        baseActivity.getResources()
                            .getString(R.string.player_already_played_each_other,
                                gameOneToSwap.getParticipantOne().getName(),
                                gameTwoToSwap.getParticipantOne().getName()), Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));
                snackbar.show();

                return false;
            } else if (gameTwoToSwap.getParticipantTwo()
                    .getListOfOpponentsUUIDs()
                    .contains(gameOneToSwap.getParticipantTwo().getUuid())) {
                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                        baseActivity.getResources()
                            .getString(R.string.player_already_played_each_other,
                                gameTwoToSwap.getParticipantTwo().getName(),
                                gameOneToSwap.getParticipantTwo().getName()), Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

                snackbar.show();

                return false;
            }
        } else if (playerNumberGameOneToSwap == 2 && playerNumberGameTwoToSwap == 2) {
            if (gameOneToSwap.getParticipantTwo()
                    .getListOfOpponentsUUIDs()
                    .contains(gameTwoToSwap.getParticipantOne().getUuid())) {
                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                        baseActivity.getResources()
                            .getString(R.string.player_already_played_each_other,
                                gameOneToSwap.getParticipantTwo().getName(),
                                gameTwoToSwap.getParticipantOne().getName()), Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));
                snackbar.show();

                return false;
            } else if (gameTwoToSwap.getParticipantOne()
                    .getListOfOpponentsUUIDs()
                    .contains(gameOneToSwap.getParticipantOne().getUuid())) {
                Snackbar snackbar = Snackbar.make((baseActivity).getCoordinatorLayout(),
                        baseActivity.getResources()
                            .getString(R.string.player_already_played_each_other,
                                gameTwoToSwap.getParticipantOne().getName(),
                                gameOneToSwap.getParticipantOne().getName()), Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

                snackbar.show();

                return false;
            }
        }

        return true;
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
