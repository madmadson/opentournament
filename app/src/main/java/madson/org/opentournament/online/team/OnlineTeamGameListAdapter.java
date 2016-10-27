package madson.org.opentournament.online.team;

import android.graphics.drawable.Drawable;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.BaseActivity;
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

    public OnlineTeamGameListAdapter(BaseActivity baseActivity, Tournament tournament) {

        Tournament tournament1 = tournament;

        this.gamesInTeamMatch = new ArrayList<>();
        this.baseActivity = baseActivity;

        winnerShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_winner);
        looserShape = ContextCompat.getDrawable(baseActivity, R.drawable.shape_looser);
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
                .getString(R.string.player_name_in_row, player1.getFirstNameWithMaximumCharacters(10),
                    player1.getNickNameWithMaximumCharacters(10), player1.getLastNameWithMaximumCharacters(10)));
        holder.getPlayerOneTeam().setText(player1.getTeamName());
        holder.getPlayerOneFaction().setText(player1.getFaction());

        TournamentPlayer player2 = game.getTournamentPlayerTwo();

        holder.getPlayerTwoNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player2.getFirstNameWithMaximumCharacters(10),
                    player2.getNickNameWithMaximumCharacters(10), player2.getLastNameWithMaximumCharacters(10)));
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
        }

        holder.getSwapPlayerOne().setVisibility(View.GONE);
        holder.getSwapPlayerTwo().setVisibility(View.GONE);

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


    public void addGame(Game game) {

        gamesInTeamMatch.add(game);

        notifyDataSetChanged();
    }


    public void removeGame(Game game) {

        if (gamesInTeamMatch.contains(game)) {
            gamesInTeamMatch.remove(game);

            notifyDataSetChanged();
        }
    }
}
