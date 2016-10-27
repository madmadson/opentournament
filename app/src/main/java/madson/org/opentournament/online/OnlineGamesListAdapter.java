package madson.org.opentournament.online;

import android.content.Intent;

import android.graphics.Color;

import android.graphics.drawable.Drawable;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.online.team.OnlineTeamTournamentManagementActivity;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.GameViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineGamesListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    private Drawable normalShape;
    private List<Game> gameList;

    private Tournament tournament;
    private Drawable winnerShape;
    private Drawable looserShape;

    private BaseActivity baseActivity;

    public OnlineGamesListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;

        this.gameList = new ArrayList<>();

        winnerShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_winner);
        looserShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_looser);
        normalShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_normal);
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
            .setText(baseActivity.getResources().getString(R.string.table_number, game.getPlaying_field()));

        if (game.isFinished()) {
            holder.getPlayerOneCardView()
                .setBackground(game.getParticipant_one_score() == 1 ? winnerShape : looserShape);
            holder.getPlayerTwoCardView()
                .setBackground(game.getParticipant_two_score() == 1 ? winnerShape : looserShape);
        } else {
            holder.getPlayerOneCardView().setBackground(normalShape);
            holder.getPlayerTwoCardView().setBackground(normalShape);
        }

        if (position % 2 == 0) {
            holder.getPairingRow().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightGrey));
        } else {
            holder.getPairingRow().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostWhite));
        }

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

        holder.getSwapPlayerOne().setVisibility(View.GONE);
        holder.getSwapPlayerTwo().setVisibility(View.GONE);
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

        holder.getPairingRow().setOnClickListener(new TeamManagementClickListener(tournament, game));
        holder.getPlayerOneCardView().setOnClickListener(new TeamManagementClickListener(tournament, game));
        holder.getPlayerTwoCardView().setOnClickListener(new TeamManagementClickListener(tournament, game));
    }


    private void gameForSoloTournament(GameViewHolder holder, Game game) {

        TournamentPlayer player1 = game.getTournamentPlayerOne();

        if (player1.getTeamName().isEmpty()) {
            holder.getPlayerOneTeam().setVisibility(View.GONE);
        }

        holder.getPlayerOneNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player1.getFirstNameWithMaximumCharacters(10),
                    player1.getNickNameWithMaximumCharacters(10), player1.getLastNameWithMaximumCharacters(10)));
        holder.getPlayerOneFaction().setText(player1.getFaction());

        TournamentPlayer player2 = game.getTournamentPlayerTwo();

        if (player2.getTeamName().isEmpty()) {
            holder.getPlayerTwoTeam().setVisibility(View.GONE);
        }

        holder.getPlayerTwoNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player2.getFirstNameWithMaximumCharacters(10),
                    player2.getNickNameWithMaximumCharacters(10), player2.getLastNameWithMaximumCharacters(10)));
        holder.getPlayerTwoFaction().setText(player2.getFaction());

        Player authenticatedPlayer = baseActivity.getBaseApplication().getAuthenticatedPlayer();

        if (authenticatedPlayer != null) {
            if (authenticatedPlayer.getUUID().equals(player1.getPlayerUUID())) {
                holder.getPlayerOneCardView()
                    .setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorYours));
            } else if (authenticatedPlayer.getUUID().equals(player2.getPlayerUUID())) {
                holder.getPlayerTwoCardView()
                    .setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorYours));
            }
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

        return gameList.size();
    }


    public void addGame(Game game) {

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


    public void updateGame(Game game) {

        if (gameList.contains(game)) {
            int index = gameList.indexOf(game);

            gameList.set(index, game);

            notifyDataSetChanged();
        }
    }


    public void removeGame(Game game) {

        if (gameList.contains(game)) {
            gameList.remove(game);

            notifyDataSetChanged();
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

            Intent intent = new Intent(baseActivity, OnlineTeamTournamentManagementActivity.class);

            intent.putExtra(OnlineTeamTournamentManagementActivity.EXTRA_TOURNAMENT, tournament);
            intent.putExtra(OnlineTeamTournamentManagementActivity.EXTRA_GAME, game);
            baseActivity.startActivity(intent);
        }
    }
}
