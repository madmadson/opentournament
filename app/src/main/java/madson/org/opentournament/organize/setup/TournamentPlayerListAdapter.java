package madson.org.opentournament.organize.setup;

import android.content.DialogInterface;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.online.RegisterTournamentPlayerDialog;
import madson.org.opentournament.tasks.DropTournamentPlayerFromTournamentTask;
import madson.org.opentournament.tasks.RemoveTournamentPlayerFromTournamentTask;
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

        return new TournamentPlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(TournamentPlayerViewHolder holder, int position) {

        final TournamentPlayer tournamentPlayer = tournamentPlayerList.get(position);
        holder.getPlayerNumber().setText(String.valueOf(position + 1));

        if (tournamentPlayer.getTeamName() != null && !tournamentPlayer.getTeamName().isEmpty()) {
            holder.getTeamName().setText(tournamentPlayer.getTeamName());
            holder.getTeamName().setVisibility(View.VISIBLE);
        } else {
            holder.getTeamName().setVisibility(View.GONE);
        }

        holder.getFaction().setText(tournamentPlayer.getFaction());

        holder.getPlayerNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, tournamentPlayer.getFirstNameWithMaximumCharacters(10),
                    tournamentPlayer.getNickNameWithMaximumCharacters(10),
                    tournamentPlayer.getLastNameWithMaximumCharacters(10)));

        // mark online tournamentPlayer
        if (tournamentPlayer.isLocal()) {
            holder.getLocalIcon().setVisibility(View.VISIBLE);
        } else {
            holder.getLocalIcon().setVisibility(View.GONE);
        }

        if (tournamentPlayer.getMeta() != null) {
            if (!tournamentPlayer.getMeta().isEmpty()) {
                holder.getAffiliation().setText(tournamentPlayer.getMeta());
                holder.getAffiliation().setVisibility(View.VISIBLE);
            } else {
                holder.getAffiliation().setVisibility(View.GONE);
            }
        } else {
            holder.getAffiliation().setVisibility(View.GONE);
        }

        if (tournamentPlayer.getGamesCounter() >= 5) {
            holder.getElo().setText(String.valueOf(tournamentPlayer.getElo()));
            holder.getElo().setVisibility(View.VISIBLE);
            holder.getEloIcon().setVisibility(View.VISIBLE);
        } else {
            holder.getElo().setVisibility(View.GONE);
            holder.getEloIcon().setVisibility(View.GONE);
        }

        if (tournamentPlayer.getDroppedInRound() != 0) {
            holder.getDroppedInRound()
                .setText(baseActivity.getResources()
                    .getString(R.string.dropped_in_round, tournamentPlayer.getDroppedInRound()));
            holder.getDroppedInRound().setVisibility(View.VISIBLE);
        }

        if (position % 2 == 0) {
            holder.getTournamentPlayerCard()
                .setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightGrey));
        } else {
            holder.getTournamentPlayerCard()
                .setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostWhite));
        }

        if (tournament.getState().equals(Tournament.TournamentState.PLANNED.name())) {
            holder.getEditIcon().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        AddTournamentPlayerDialog dialog = new AddTournamentPlayerDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                        bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT_PLAYER, tournamentPlayer);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();
                        dialog.show(supportFragmentManager, "tournament setup new tournamentPlayer");
                    }
                });

            if (baseActivity.getBaseApplication().isOnline() && tournament.getUuid() != null) {
                holder.getAddListIcon().setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (tournamentPlayer.getPlayerUUID() != null) {
                                Log.i(this.getClass().getName(), "addList");

                                AddArmyListDialog dialog = new AddArmyListDialog();

                                Bundle bundle = new Bundle();
                                bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                                bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT_PLAYER,
                                    tournamentPlayer);
                                dialog.setArguments(bundle);

                                FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();

                                dialog.show(supportFragmentManager, "tournament setup new tournamentPlayer");
                            } else {
                                Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                        R.string.cant_upload_list_for_local_players, Snackbar.LENGTH_LONG);

                                snackbar.getView()
                                .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

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

        holder.getTournamentPlayerCard().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (Tournament.TournamentState.PLANNED.name().equals(tournament.getState())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                        final AlertDialog confirmDialog = builder.setTitle(R.string.confirm_remove_tournament_player)
                            .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            Log.i(this.getClass().getName(),
                                                "removeTournamentPlayer tournamentPlayer from tournament");

                                            new RemoveTournamentPlayerFromTournamentTask(baseActivity, tournament,
                                                tournamentPlayer).execute();
                                        }
                                    })
                            .setNeutralButton(R.string.dialog_cancel, null)
                            .create();
                        confirmDialog.show();
                    } else if (Tournament.TournamentState.ONGOING.name().equals(tournament.getState())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                        final AlertDialog confirmDialog = builder.setTitle(R.string.confirm_drop_tournament_player)
                            .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            tournamentPlayer.setDroppedInRound(tournament.getActualRound());
                                            new DropTournamentPlayerFromTournamentTask(
                                                baseActivity.getBaseApplication(), tournament, tournamentPlayer)
                                            .execute();
                                            updateTournamentPlayer(tournamentPlayer);
                                        }
                                    })
                            .setNeutralButton(R.string.dialog_cancel, null)
                            .create();
                        confirmDialog.show();
                    }
                }
            });
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

        if (!tournamentPlayerList.contains(tournamentPlayer) || tournamentPlayer.getPlayerUUID() == null) {
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

        tournamentPlayerList.clear();
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


    public void clear() {

        tournamentPlayerList.clear();
        notifyDataSetChanged();
    }


    public boolean checkTeamIsFull(String teamNameOfRegistration) {

        int playersInTeam = 0;

        for (TournamentPlayer tournamentPlayer : tournamentPlayerList) {
            if (tournamentPlayer.getTeamName().equals(teamNameOfRegistration)) {
                playersInTeam++;
            }
        }

        return playersInTeam >= tournament.getTeamSize();
    }
}
