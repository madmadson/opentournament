package madson.org.opentournament.tournaments;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.online.OnlineTournamentActivity;
import madson.org.opentournament.organize.TournamentOrganizeActivity;
import madson.org.opentournament.tasks.TournamentUploadTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.TournamentViewHolder;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineTournamentListAdapter extends RecyclerView.Adapter<TournamentViewHolder> {

    private BaseActivity baseActivity;

    private List<Tournament> mDataset;
    private DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());

    public OnlineTournamentListAdapter(BaseActivity baseActivity) {

        this.baseActivity = baseActivity;

        this.mDataset = new ArrayList<>();
    }

    @Override
    public TournamentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_online_tournament, parent, false);

        return new TournamentViewHolder(v);
    }


    @Override
    public void onBindViewHolder(TournamentViewHolder viewHolder, int position) {

        final Tournament tournament = mDataset.get(position);

        viewHolder.getTournamentNameInList().setText(tournament.getName());

        if (tournament.getDateOfTournament() != null) {
            String formattedDate = dateFormatter.format(tournament.getDateOfTournament());
            viewHolder.getTournamentDateInList().setText(formattedDate);
        }

        if (viewHolder.getTournamentLocationInList() != null) {
            viewHolder.getTournamentLocationInList().setText(tournament.getLocation());
        }

        int actualPlayers = tournament.getActualPlayers();
        int maximalPlayers = tournament.getMaxNumberOfPlayers();
        viewHolder.getTournamentPlayersInList()
            .setText(baseActivity.getResources()
                .getString(R.string.players_in_tournament, actualPlayers, maximalPlayers));

        if (tournament.getState().equals(Tournament.TournamentState.FINISHED.name())) {
            viewHolder.getTournamentState().setText(R.string.tournament_finished);
            viewHolder.getTournamentState().setTextColor(Color.BLUE);
            viewHolder.getTournamentState().setVisibility(View.VISIBLE);
        } else if (tournament.getActualRound() > 0) {
            viewHolder.getTournamentState().setText(R.string.tournament_started);
            viewHolder.getTournamentState().setTextColor(Color.GREEN);
            viewHolder.getTournamentState().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getTournamentState().setVisibility(View.GONE);
        }

        if (position % 2 == 0) {
            viewHolder.getRowTournament().setBackgroundColor(Color.LTGRAY);
        } else {
            viewHolder.getRowTournament().setBackgroundColor(Color.WHITE);
        }

        viewHolder.getRowTournament().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(baseActivity, OnlineTournamentActivity.class);
                    intent.putExtra(OnlineTournamentActivity.EXTRA_TOURNAMENT_GAME_OR_SPORT_TYP,
                        tournament.getGameOrSportTyp());
                    intent.putExtra(OnlineTournamentActivity.EXTRA_TOURNAMENT_UUID, tournament.getOnlineUUID());
                    baseActivity.startActivity(intent);
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
