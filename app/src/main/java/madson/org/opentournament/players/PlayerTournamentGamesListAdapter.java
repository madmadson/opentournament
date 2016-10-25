package madson.org.opentournament.players;

import android.os.Bundle;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.tournaments.TournamentComparator;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.PlayerTournamentGameViewHolder;
import madson.org.opentournament.viewHolder.PlayerTournamentViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerTournamentGamesListAdapter extends RecyclerView.Adapter<PlayerTournamentGameViewHolder> {

    private BaseActivity baseActivity;

    private List<Game> gamesOfTournament;

    public PlayerTournamentGamesListAdapter(BaseActivity baseActivity) {

        this.baseActivity = baseActivity;

        this.gamesOfTournament = new ArrayList<>();
    }

    @Override
    public PlayerTournamentGameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player_tournament_game, parent, false);

        return new PlayerTournamentGameViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PlayerTournamentGameViewHolder viewHolder, int position) {

        final Game game = gamesOfTournament.get(position);

        viewHolder.getPlayerOne()
            .setText(baseActivity.getString(R.string.player_name_in_row,
                    game.getTournamentPlayerOne().getFirstNameWithMaximumCharacters(10),
                    game.getTournamentPlayerOne().getNickNameWithMaximumCharacters(10),
                    game.getTournamentPlayerOne().getLastNameWithMaximumCharacters(10)));

        viewHolder.getPlayerTwo()
            .setText(baseActivity.getString(R.string.player_name_in_row,
                    game.getTournamentPlayerTwo().getFirstNameWithMaximumCharacters(10),
                    game.getTournamentPlayerTwo().getNickNameWithMaximumCharacters(10),
                    game.getTournamentPlayerTwo().getLastNameWithMaximumCharacters(10)));

        if (position % 2 == 0) {
            viewHolder.getRowGame().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightGrey));
        } else {
            viewHolder.getRowGame().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostWhite));
        }
    }


    @Override
    public int getItemCount() {

        return gamesOfTournament.size();
    }


    public void addGame(Game game) {

        gamesOfTournament.add(game);

        notifyDataSetChanged();
    }


    public void replaceGame(Game game) {

        int index = gamesOfTournament.indexOf(game);

        gamesOfTournament.set(index, game);

        notifyDataSetChanged();
    }


    public void removeGame(Game game) {

        gamesOfTournament.remove(game);

        notifyDataSetChanged();
    }
}
