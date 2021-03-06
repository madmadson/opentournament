package madson.org.opentournament.tournaments;

import android.content.Intent;

import android.graphics.Color;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.online.OnlineTournamentActivity;
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

        viewHolder.getTournamentNameInList().setText(tournament.getNameWithMaximumChars(20));

        if (tournament.getDateOfTournament() != null) {
            String formattedDate = dateFormatter.format(tournament.getDateOfTournament());
            viewHolder.getTournamentDateInList().setText(formattedDate);
        }

        if (viewHolder.getTournamentLocationInList() != null) {
            viewHolder.getTournamentLocationInList().setText(tournament.getLocation());
        }

        int actualPlayers = tournament.getActualPlayers();
        int maximalPlayers = tournament.getMaxNumberOfParticipants();
        viewHolder.getTournamentPlayersInList()
            .setText(baseActivity.getResources()
                .getString(R.string.players_in_tournament, actualPlayers, maximalPlayers));

        if (tournament.getState().equals(Tournament.TournamentState.FINISHED.name())) {
            viewHolder.getTournamentState().setText(R.string.tournament_finished);
            viewHolder.getTournamentState().setTextColor(ContextCompat.getColor(baseActivity, R.color.colorNegative));
            viewHolder.getTournamentState().setVisibility(View.VISIBLE);
        } else if (tournament.getActualRound() > 0) {
            viewHolder.getTournamentState().setText(R.string.tournament_started);
            viewHolder.getTournamentState().setTextColor(ContextCompat.getColor(baseActivity, R.color.colorAction));
            viewHolder.getTournamentState().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getTournamentState().setVisibility(View.GONE);
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

                    Intent intent = new Intent(baseActivity, OnlineTournamentActivity.class);
                    intent.putExtra(OnlineTournamentActivity.EXTRA_TOURNAMENT, tournament);
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

        notifyDataSetChanged();
    }


    public void removeTournament(Tournament tournament) {

        mDataset.remove(tournament);

        notifyDataSetChanged();
    }


    public void replaceTournament(Tournament tournament) {

        int index = mDataset.indexOf(tournament);

        mDataset.set(index, tournament);
        notifyDataSetChanged();
    }


    public void deleteAllTournaments() {

        mDataset.clear();
    }
}
