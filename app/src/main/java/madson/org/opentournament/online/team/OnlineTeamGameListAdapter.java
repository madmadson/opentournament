package madson.org.opentournament.online.team;

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
public class OnlineTeamGameListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    private final Drawable winnerShape;
    private final Drawable looserShape;

    private List<Game> gamesInTeamMatch;
    private BaseActivity baseActivity;
    private Tournament tournament;

    public OnlineTeamGameListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.tournament = tournament;

        this.gamesInTeamMatch = new ArrayList<>();
        this.baseActivity = baseActivity;

        winnerShape = baseActivity.getResources().getDrawable(R.drawable.shape_winner);
        looserShape = baseActivity.getResources().getDrawable(R.drawable.shape_looser);
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

        TournamentPlayer player1 = game.getTournamentPlayerOne();

        holder.getPlayerOneNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player1.getFirstName(), player1.getNickName(),
                    player1.getLastName()));
        holder.getPlayerOneTeam().setText(player1.getTeamName());
        holder.getPlayerOneFaction().setText(player1.getFaction());

        TournamentPlayer player2 = game.getTournamentPlayerTwo();

        holder.getPlayerTwoNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player2.getFirstName(), player2.getNickName(),
                    player2.getLastName()));
        holder.getPlayerTwoTeam().setText(player2.getTeamName());
        holder.getPlayerTwoFaction().setText(player2.getFaction());

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


    public void addGame(Game game) {

        gamesInTeamMatch.add(game);

        notifyDataSetChanged();
    }
}