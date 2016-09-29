package madson.org.opentournament.organize.setup;

import android.content.Context;

import android.graphics.Color;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.TournamentPlayerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerListAdapter extends RecyclerView.Adapter<TournamentPlayerViewHolder> {

    private BaseActivity baseActivity;
    private TournamentSetupEventListener mListener;
    private Tournament tournament;
    private List<TournamentPlayer> tournamentPlayerList = new ArrayList<>();

    public TournamentPlayerListAdapter(BaseActivity baseActivity, TournamentSetupEventListener mListener,
        Tournament tournament) {

        this.baseActivity = baseActivity;

        this.mListener = mListener;
        this.tournament = tournament;
    }

    @Override
    public TournamentPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament_player, parent, false);

        TournamentPlayerViewHolder vh = new TournamentPlayerViewHolder(this, v);

        return vh;
    }


    @Override
    public void onBindViewHolder(TournamentPlayerViewHolder holder, int position) {

        final TournamentPlayer player = tournamentPlayerList.get(position);
        holder.setPlayer(player);
        holder.getPlayerNumber().setText(String.valueOf(position + 1));
        holder.getTeamName().setText(player.getTeamname());
        holder.getFaction().setText(player.getFaction());

        String firstname = player.getFirstname();
        String nickname = player.getNickname();
        String lastname = player.getLastname();
        holder.getPlayerNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.tournament_player_name_in_row, firstname, nickname, lastname));

        // mark online player
        if (player.getPlayer_online_uuid() != null) {
            holder.getLocalIcon().setVisibility(View.GONE);
        } else {
            holder.getLocalIcon().setVisibility(View.VISIBLE);
        }

        if (player.getDroppedInRound() != 0) {
            holder.getDroppedInRound()
                .setText(baseActivity.getResources().getString(R.string.dropped_in_round, player.getDroppedInRound()));
            holder.getDroppedInRound().setVisibility(View.VISIBLE);
        }

        holder.getEditIcon().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    AddTournamentPlayerDialog dialog = new AddTournamentPlayerDialog();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                    bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT_PLAYER, player);
                    dialog.setArguments(bundle);

                    FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();
                    dialog.show(supportFragmentManager, "tournament setup new player");
                }
            });

        if (position % 2 == 0) {
            holder.getTournamentPlayerCard().setCardBackgroundColor(Color.LTGRAY);
        } else {
            holder.getTournamentPlayerCard().setCardBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {

        return tournamentPlayerList.size();
    }


    /**
     * Add new tournament player.
     *
     * @param  tournamentPlayer
     */
    public void addTournamentPlayer(TournamentPlayer tournamentPlayer) {

        if (!tournamentPlayerList.contains(tournamentPlayer)) {
            tournamentPlayerList.add(tournamentPlayer);
            notifyDataSetChanged();
        }
    }


    public void removeTournamentPlayer(TournamentPlayer tournamentPlayer) {

        int position = tournamentPlayerList.indexOf(tournamentPlayer);
        tournamentPlayerList.remove(position);
        notifyDataSetChanged();
    }


    public boolean contains(TournamentPlayer player) {

        if (player != null) {
            return tournamentPlayerList.contains(player);
        } else {
            return false;
        }
    }


    public void addTournamentPlayers(List<TournamentPlayer> localTournamentPlayers) {

        tournamentPlayerList = localTournamentPlayers;
        notifyDataSetChanged();
    }


    public TournamentSetupEventListener getmListener() {

        return mListener;
    }


    public void updateTournamentPlayer(TournamentPlayer tournamentPlayer) {

        if (tournamentPlayerList.contains(tournamentPlayer)) {
            tournamentPlayerList.remove(tournamentPlayer);
            tournamentPlayerList.add(tournamentPlayer);
            notifyDataSetChanged();
        }
    }
}
