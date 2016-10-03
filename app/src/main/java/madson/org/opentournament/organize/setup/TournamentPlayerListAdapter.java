package madson.org.opentournament.organize.setup;

import android.graphics.Color;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;

import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.online.RegisterTournamentPlayerDialog;
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

    private Tournament tournament;
    private List<TournamentPlayer> tournamentPlayerList = new ArrayList<>();

    public TournamentPlayerListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.baseActivity = baseActivity;

        this.tournament = tournament;
    }

    @Override
    public TournamentPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament_player, parent, false);

        return new TournamentPlayerViewHolder(v, baseActivity);
    }


    @Override
    public void onBindViewHolder(TournamentPlayerViewHolder holder, int position) {

        final TournamentPlayer player = tournamentPlayerList.get(position);
        holder.setPlayer(player);
        holder.getPlayerNumber().setText(String.valueOf(position + 1));

        if (player.getTeamname() != null) {
            holder.getTeamName().setText(player.getTeamname());
            holder.getTeamName().setVisibility(View.VISIBLE);
        } else {
            holder.getTeamName().setVisibility(View.GONE);
        }

        holder.getFaction().setText(player.getFaction());

        String firstname = player.getFirstname();
        String nickname = player.getNickname();
        String lastname = player.getLastname();
        holder.getPlayerNameInList()
            .setText(baseActivity.getResources().getString(R.string.player_name_in_row, firstname, nickname, lastname));

        // mark online player
        if (player.getPlayerOnlineUUID() != null) {
            holder.getLocalIcon().setVisibility(View.GONE);
        } else {
            holder.getLocalIcon().setVisibility(View.VISIBLE);
        }

        if (player.getDroppedInRound() != 0) {
            holder.getDroppedInRound()
                .setText(baseActivity.getResources().getString(R.string.dropped_in_round, player.getDroppedInRound()));
            holder.getDroppedInRound().setVisibility(View.VISIBLE);
        }

        if (position % 2 == 0) {
            holder.getTournamentPlayerCard().setCardBackgroundColor(Color.LTGRAY);
        } else {
            holder.getTournamentPlayerCard().setCardBackgroundColor(Color.WHITE);
        }

        if (!tournament.getState().equals(Tournament.TournamentState.PLANED)) {
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

            if (baseActivity.getBaseApplication().isOnline() && tournament.getOnlineUUID() != null) {
                holder.getAddListIcon().setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (player.getPlayerOnlineUUID() != null) {
                                Log.i(this.getClass().getName(), "addList");

                                AddTournamentPlayerListDialog dialog = new AddTournamentPlayerListDialog();

                                Bundle bundle = new Bundle();
                                bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                                bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT_PLAYER, player);
                                dialog.setArguments(bundle);

                                FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();

                                dialog.show(supportFragmentManager, "tournament setup new player");
                            } else {
                                Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                        R.string.cant_upload_list_for_local_players, Snackbar.LENGTH_LONG);

                                snackbar.getView()
                                .setBackgroundColor(baseActivity.getResources().getColor(R.color.colorNegative));

                                snackbar.show();
                            }
                        }
                    });
            } else {
                holder.getAddListIcon().setVisibility(View.GONE);
            }
        } else {
            holder.getEditIcon().setVisibility(View.GONE);
            holder.getAddListIcon().setVisibility(View.GONE);
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


    public void updateTournamentPlayer(TournamentPlayer tournamentPlayer) {

        if (tournamentPlayerList.contains(tournamentPlayer)) {
            tournamentPlayerList.remove(tournamentPlayer);
            tournamentPlayerList.add(tournamentPlayer);
            notifyDataSetChanged();
        }
    }


    public boolean containsPlayer(Player player) {

        for (TournamentPlayer tournamentPlayer : tournamentPlayerList) {
            if (tournamentPlayer.getPlayerOnlineUUID().equals(player.getOnlineUUID())) {
                return true;
            }
        }

        return false;
    }
}
