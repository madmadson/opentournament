package madson.org.opentournament.online;

import android.graphics.Color;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.TournamentPlayerViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Recylerview for online players. First list is empty. Will be filled via callback.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RegisterTournamentPlayerListAdapter extends RecyclerView.Adapter<TournamentPlayerViewHolder> {

    private List<TournamentPlayer> playerList;

    private BaseActivity baseActivity;
    private Tournament tournament;

    public RegisterTournamentPlayerListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;

        this.playerList = new ArrayList<>();
    }

    @Override
    public TournamentPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament_player, parent, false);

        return new TournamentPlayerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(TournamentPlayerViewHolder viewHolder, int position) {

        final TournamentPlayer player = playerList.get(position);

        viewHolder.getPlayerNumber().setText(String.valueOf(position + 1));

        viewHolder.getPlayerNameInList()
            .setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, player.getFirstNameWithMaximumCharacters(10),
                    player.getNickNameWithMaximumCharacters(10), player.getLastNameWithMaximumCharacters(10)));

        if (player.getTeamName() != null) {
            viewHolder.getTeamName().setText(player.getTeamNameWithMaximumCharacters(15));
            viewHolder.getTeamName().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getTeamName().setVisibility(View.GONE);
        }

        viewHolder.getFaction().setText(player.getFaction());

        if (position % 2 == 0) {
            viewHolder.getTournamentPlayerCard()
                .setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightGrey));
        } else {
            viewHolder.getTournamentPlayerCard()
                .setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostWhite));
        }

        if (baseActivity.getBaseApplication().getAuthenticatedPlayer() != null
                && baseActivity.getBaseApplication().getAuthenticatedPlayer().getUUID().equals(
                    player.getPlayerUUID())) {
            viewHolder.getEditIcon().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(this.getClass().getName(), "edit registration");

                        RegisterTournamentPlayerDialog dialog = new RegisterTournamentPlayerDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                        bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_PLAYER,
                            baseActivity.getBaseApplication().getAuthenticatedPlayer());
                        bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT_PLAYER, player);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();
                        dialog.show(supportFragmentManager, "tournament setup new player");
                    }
                });

            viewHolder.getAddListIcon().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(this.getClass().getName(), "addList");

                        AddRegistrationListDialog dialog = new AddRegistrationListDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                        bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT_PLAYER, player);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();

                        dialog.show(supportFragmentManager, "tournament setup new player");
                    }
                });

            viewHolder.getTournamentPlayerCard()
                .setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorTurquoise));
            viewHolder.getTournamentPlayerCard()
                .setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

            viewHolder.getPlayerNumber().setTextSize(20);
            viewHolder.getPlayerNumber().setTextColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostBlack));

            viewHolder.getTeamName().setTextSize(20);
            viewHolder.getTeamName().setTextColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostBlack));

            viewHolder.getFaction().setTextSize(20);
            viewHolder.getFaction().setTextColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostBlack));

            viewHolder.getPlayerNameInList().setTextSize(20);
            viewHolder.getPlayerNameInList()
                .setTextColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostBlack));

            viewHolder.getAddListIcon().setVisibility(View.VISIBLE);
            viewHolder.getEditIcon().setVisibility(View.VISIBLE);

            viewHolder.getAddListIcon()
                .setImageDrawable(ContextCompat.getDrawable(baseActivity, R.drawable.ic_insert_drive_file_black_48dp));

            viewHolder.getEditIcon()
                .setImageDrawable(ContextCompat.getDrawable(baseActivity, R.drawable.ic_build_black_48dp));
        } else {
            viewHolder.getEditIcon().setVisibility(View.GONE);
            viewHolder.getAddListIcon().setVisibility(View.GONE);

            FrameLayout.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(16, 4, 16, 4);
            viewHolder.getTournamentPlayerCard().setLayoutParams(layoutParams);

            viewHolder.getPlayerNumber().setTextSize(12);
            viewHolder.getPlayerNumber().setTextColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostBlack));

            viewHolder.getTeamName().setTextSize(12);
            viewHolder.getTeamName().setTextColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostBlack));

            viewHolder.getFaction().setTextSize(12);
            viewHolder.getFaction().setTextColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostBlack));

            viewHolder.getPlayerNameInList().setTextSize(12);
            viewHolder.getPlayerNameInList()
                .setTextColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostBlack));
        }
    }


    @Override
    public int getItemCount() {

        return playerList.size();
    }


    public void addRegistration(TournamentPlayer player) {

        playerList.add(player);
        notifyDataSetChanged();
    }


    public boolean playerNotRegistered(Player authenticatedPlayer) {

        for (TournamentPlayer player : playerList) {
            if (player.getPlayerUUID().equals(authenticatedPlayer.getUUID())) {
                return false;
            }
        }

        return true;
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
