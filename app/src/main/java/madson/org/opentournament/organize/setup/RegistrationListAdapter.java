package madson.org.opentournament.organize.setup;

import android.content.DialogInterface;

import android.graphics.Color;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.online.RegisterTournamentPlayerDialog;
import madson.org.opentournament.tasks.SaveRegistrationTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.TournamentPlayerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RegistrationListAdapter extends RecyclerView.Adapter<TournamentPlayerViewHolder> {

    private List<TournamentPlayer> playerList;

    private BaseActivity baseActivity;
    private Tournament tournament;
    private TournamentPlayerListAdapter tournamentPlayerListAdapter;

    public RegistrationListAdapter(BaseActivity baseActivity, Tournament tournament,
        TournamentPlayerListAdapter tournamentPlayerListAdapter) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
        this.tournamentPlayerListAdapter = tournamentPlayerListAdapter;

        this.playerList = new ArrayList<>();
    }

    @Override
    public TournamentPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_registration, parent, false);

        return new TournamentPlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(TournamentPlayerViewHolder viewHolder, int position) {

        final TournamentPlayer player = playerList.get(position);

        viewHolder.setPlayer(player);
        viewHolder.getPlayerNumber().setText(String.valueOf(position + 1));

        if (player.getTeamname() != null) {
            viewHolder.getTeamName().setText(player.getTeamname());
            viewHolder.getTeamName().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getTeamName().setVisibility(View.GONE);
        }

        viewHolder.getFaction().setText(player.getFaction());

        final String firstname = player.getFirstname();
        final String nickname = player.getNickname();
        final String lastname = player.getLastname();
        viewHolder.getPlayerNameInList()
            .setText(baseActivity.getResources().getString(R.string.player_name_in_row, firstname, nickname, lastname));

        if (position % 2 == 0) {
            viewHolder.getTournamentPlayerCard().setCardBackgroundColor(Color.LTGRAY);
        } else {
            viewHolder.getTournamentPlayerCard().setCardBackgroundColor(Color.WHITE);
        }

        viewHolder.getTournamentPlayerCard().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (tournamentPlayerListAdapter.contains(player)) {
                        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                R.string.player_already_is_in_tournament, Snackbar.LENGTH_LONG);

                        snackbar.getView()
                        .setBackgroundColor(baseActivity.getResources().getColor(R.color.colorNegative));

                        snackbar.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                        builder.setTitle(R.string.realy_add_player)
                        .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        new SaveRegistrationTask(baseActivity, player, tournament).execute();
                                    }
                                })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show();
                    }
                }
            });

        viewHolder.getAddListIcon().setVisibility(View.VISIBLE);

        viewHolder.getAddListIcon().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "addList");

                    ShowRegistrationArmyListDialog dialog = new ShowRegistrationArmyListDialog();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                    bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT_PLAYER, player);
                    dialog.setArguments(bundle);

                    FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();

                    dialog.show(supportFragmentManager, "tournament setup new player");
                }
            });
    }


    @Override
    public int getItemCount() {

        return playerList.size();
    }


    public void addRegistration(TournamentPlayer player) {

        playerList.add(player);
        notifyDataSetChanged();
    }


    public void removeRegistration(TournamentPlayer player) {

        playerList.remove(player);
        notifyDataSetChanged();
    }


    public void updateRegistration(TournamentPlayer player) {

        playerList.remove(player);
        playerList.add(player);
        notifyDataSetChanged();
    }
}
