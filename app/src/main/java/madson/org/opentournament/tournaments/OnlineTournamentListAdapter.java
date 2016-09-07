package madson.org.opentournament.tournaments;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;

import org.w3c.dom.Text;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineTournamentListAdapter extends RecyclerView.Adapter<OnlineTournamentListAdapter.OnlineTournamentViewHolder> {

    private List<Tournament> mDataset;
    private DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
    private FirebaseUser currentUser;
    private TournamentManagementEventListener mListener;

    public OnlineTournamentListAdapter(TournamentManagementEventListener mListener) {

        this.mListener = mListener;

        this.mDataset = new ArrayList<>();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public OnlineTournamentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament, parent, false);

        return new OnlineTournamentViewHolder(v);
    }


    @Override
    public void onBindViewHolder(OnlineTournamentViewHolder viewHolder, int position) {

        final Tournament tournament = mDataset.get(position);
        viewHolder.setTournament(tournament);
        viewHolder.getTournamentNameInList().setText(tournament.getName());

        if (tournament.getDateOfTournament() != null) {
            String formattedDate = dateFormatter.format(tournament.getDateOfTournament());
            viewHolder.getTournamentDateInList().setText(formattedDate);
        }

        viewHolder.getTournamentLocationInList().setText(tournament.getLocation());

        viewHolder.getTournamentPlayersInList()
            .setText(String.valueOf(tournament.getActualPlayers()) + "/"
                + String.valueOf(tournament.getMaxNumberOfPlayers()));

        if (currentUser != null && currentUser.getEmail() != null) {
            if (currentUser.getEmail().equals(tournament.getCreatorEmail())) {
                viewHolder.getEditTournamentButton().setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            mListener.onTournamentEditClicked(tournament);
                        }
                    });
            } else {
                // only creator should do action
                viewHolder.getEditTournamentButton().setVisibility(View.INVISIBLE);
            }
        } else {
            // no user no fun
            viewHolder.getEditTournamentButton().setVisibility(View.INVISIBLE);
        }
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

    public class OnlineTournamentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageButton editTournamentButton;
        private TextView tournamentNameInList;
        private TextView tournamentPlayersInList;
        private TextView tournamentLocationInList;
        private TextView tournamentDateInList;
        private Tournament tournament;

        public OnlineTournamentViewHolder(View v) {

            super(v);
            v.setOnClickListener(this);

            editTournamentButton = (ImageButton) v.findViewById(R.id.button_edit_tournament);
            tournamentNameInList = (TextView) v.findViewById(R.id.tournament_name);
            tournamentPlayersInList = (TextView) v.findViewById(R.id.amount_players);
            tournamentLocationInList = (TextView) v.findViewById(R.id.tournament_location);
            tournamentDateInList = (TextView) v.findViewById(R.id.tournament_date);
        }

        @Override
        public void onClick(View v) {

            Log.i(this.getClass().getName(), "clicked on online tournament");

            mListener.onTournamentListItemClicked(tournament);
        }


        public TextView getTournamentNameInList() {

            return tournamentNameInList;
        }


        public ImageButton getEditTournamentButton() {

            return editTournamentButton;
        }


        public void setTournament(Tournament tournament) {

            this.tournament = tournament;
        }


        public TextView getTournamentPlayersInList() {

            return tournamentPlayersInList;
        }


        public TextView getTournamentLocationInList() {

            return tournamentLocationInList;
        }


        public TextView getTournamentDateInList() {

            return tournamentDateInList;
        }
    }
}
