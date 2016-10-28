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
import madson.org.opentournament.viewHolder.PlayerTournamentViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PlayerTournamentListAdapter extends RecyclerView.Adapter<PlayerTournamentViewHolder> {

    private BaseActivity baseActivity;
    private Player player;

    private List<Tournament> mDataset;

    public PlayerTournamentListAdapter(BaseActivity baseActivity, Player player) {

        this.baseActivity = baseActivity;
        this.player = player;

        this.mDataset = new ArrayList<>();
    }

    @Override
    public PlayerTournamentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player_tournament, parent, false);

        return new PlayerTournamentViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PlayerTournamentViewHolder viewHolder, int position) {

        final Tournament tournament = mDataset.get(position);

        viewHolder.getTournamentNameInList().setText(tournament.getNameWithMaximumChars(20));

        int actualPlayers = tournament.getActualPlayers();

        if (tournament.getRanking() != null) {
            int rank = tournament.getRanking().getRank();

            if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
                int teamCounter = actualPlayers / tournament.getTeamSize();

                viewHolder.getRanking().setText(rank + "/" + teamCounter);
            } else if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                viewHolder.getRanking().setText(rank + "/" + actualPlayers);
            }
        }

        if (tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
            viewHolder.getTeamIcon().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getTeamIcon().setVisibility(View.GONE);
        }

        if (position % 2 == 0) {
            viewHolder.getRowTournament()
                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightGrey));
        } else {
            viewHolder.getRowTournament()
                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostWhite));
        }

        viewHolder.getRowTournament().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(PlayerTournamentGamesListFragment.BUNDLE_PLAYER, player);
                    bundle.putParcelable(PlayerTournamentGamesListFragment.BUNDLE_TOURNAMENT, tournament);

                    PlayerTournamentGamesListFragment playerTournamentGamesFragment =
                        new PlayerTournamentGamesListFragment();
                    playerTournamentGamesFragment.setArguments(bundle);
                    baseActivity.replaceFragment(playerTournamentGamesFragment);
                }
            });
    }


    @Override
    public int getItemCount() {

        return mDataset.size();
    }


    public void addTournament(Tournament tournament) {

        mDataset.add(tournament);
        Collections.sort(mDataset, new TournamentComparator());
        notifyDataSetChanged();
    }


    public void removeTournament(Tournament tournament) {

        mDataset.remove(tournament);
        Collections.sort(mDataset, new TournamentComparator());
        notifyDataSetChanged();
    }


    public void replaceTournament(Tournament tournament) {

        int index = mDataset.indexOf(tournament);

        mDataset.set(index, tournament);
        Collections.sort(mDataset, new TournamentComparator());
        notifyDataSetChanged();
    }
}
