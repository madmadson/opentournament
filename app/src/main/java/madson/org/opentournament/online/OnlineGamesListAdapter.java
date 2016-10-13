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
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTyp;
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
    private Tournament tournament;
    private Drawable winnerShape;
    private Drawable looserShape;

    public OnlineGamesListAdapter(BaseApplication baseApplication, Tournament tournament) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;

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

        holder.getTableNumber()
            .setText(baseApplication.getResources().getString(R.string.table_number, game.getPlaying_field()));

        if (game.isFinished()) {
            holder.getPlayerOneCardView()
                .setBackgroundDrawable(game.getParticipant_one_score() == 1 ? winnerShape : looserShape);
            holder.getPlayerTwoCardView()
                .setBackgroundDrawable(game.getParticipant_two_score() == 1 ? winnerShape : looserShape);
        }

        if (position % 2 == 0) {
            holder.getPairingRow().setBackgroundColor(Color.LTGRAY);
        } else {
            holder.getPairingRow().setBackgroundColor(Color.WHITE);
        }

        if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
            gameForSoloTournament(holder, game);
        } else {
            gameForTeamTournament(holder, game);
        }

        holder.getPlayerOneScore()
            .setText(baseApplication.getResources().getString(R.string.game_win, game.getParticipant_one_score()));
        holder.getPlayerOneControlPoints()
            .setText(baseApplication.getResources()
                .getString(R.string.game_cp, game.getParticipant_one_control_points()));
        holder.getPlayerOneVictoryPoints()
            .setText(baseApplication.getResources()
                .getString(R.string.game_vp, game.getParticipant_one_victory_points()));

        holder.getPlayerTwoScore()
            .setText(baseApplication.getResources().getString(R.string.game_win, game.getParticipant_two_score()));
        holder.getPlayerTwoControlPoints()
            .setText(baseApplication.getResources()
                .getString(R.string.game_cp, game.getParticipant_two_control_points()));
        holder.getPlayerTwoVictoryPoints()
            .setText(baseApplication.getResources()
                .getString(R.string.game_vp, game.getParticipant_two_victory_points()));
    }


    private void gameForTeamTournament(GameViewHolder holder, Game game) {

        holder.getPlayerOneFaction().setVisibility(View.GONE);
        holder.getPlayerOneTeam().setVisibility(View.GONE);

        holder.getPlayerTwoFaction().setVisibility(View.GONE);
        holder.getPlayerTwoTeam().setVisibility(View.GONE);

        holder.getPlayerOneIntermediatePoints().setVisibility(View.VISIBLE);
        holder.getPlayerOneIntermediatePoints().setText(String.valueOf(game.getParticipant_one_intermediate_points()));

        holder.getPlayerTwoIntermediatePoints().setVisibility(View.VISIBLE);
        holder.getPlayerTwoIntermediatePoints().setText(String.valueOf(game.getParticipant_two_intermediate_points()));

        holder.getPlayerOneNameInList().setText(game.getParticipantOneUUID());
        holder.getPlayerTwoNameInList().setText(game.getParticipantTwoUUID());
    }


    private void gameForSoloTournament(GameViewHolder holder, Game game) {

        TournamentPlayer player1 = game.getTournamentPlayerOne();

        holder.getPlayerOneNameInList()
            .setText(baseApplication.getResources()
                .getString(R.string.player_name_in_row, player1.getFirstName(), player1.getNickName(),
                    player1.getLastName()));
        holder.getPlayerOneFaction().setText(player1.getFaction());

        TournamentPlayer player2 = game.getTournamentPlayerTwo();

        holder.getPlayerTwoNameInList()
            .setText(baseApplication.getResources()
                .getString(R.string.player_name_in_row, player2.getFirstName(), player2.getNickName(),
                    player2.getLastName()));
        holder.getPlayerTwoFaction().setText(player2.getFaction());

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
            if (gameList.get(i).getParticipantOneUUID().equals(uuid)
                    || gameList.get(i).getParticipantTwoUUID().equals(uuid)) {
                return i;
            }
        }

        return -1;
    }
}
