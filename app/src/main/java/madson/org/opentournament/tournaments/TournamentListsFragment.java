package madson.org.opentournament.tournaments;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.ongoing.OngoingTournamentActivity;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.text.DateFormat;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class TournamentListsFragment extends Fragment {

    public static final String TOURNAMENTS_CHILD = "tournaments";

    private static TournamentListItemListener mListener;
    private DatabaseReference mFirebaseDatabaseReference;
    private ProgressBar mProgressBar;
    private TextView noOnlineTournamentsFoundTextView;
    private RecyclerView mOnlineTournamentRecyclerView;
    private DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
    private FirebaseUser currentUser;

    private TournamentListAdapter localTournamentListAdapter;
    private FirebaseRecyclerAdapter<Tournament, TournamentViewHolder> mFirebaseTournamentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_tournament_list, container, false);

        // tournaments from server

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noOnlineTournamentsFoundTextView = (TextView) view.findViewById(R.id.no_online_tournaments);

        if (((BaseActivity) getActivity()).isConnected()) {
            mOnlineTournamentRecyclerView = (RecyclerView) view.findViewById(R.id.online_tournament_list_recycler_view);
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            mFirebaseTournamentAdapter = new FirebaseRecyclerAdapter<Tournament, TournamentViewHolder>(Tournament.class,
                    R.layout.row_tournament, TournamentViewHolder.class,
                    mFirebaseDatabaseReference.child(TOURNAMENTS_CHILD)) {

                @Override
                public int getItemCount() {

                    if (super.getItemCount() == 0) {
                        noOnlineTournamentsFoundTextView.setVisibility(ProgressBar.GONE);
                    }

                    return super.getItemCount();
                }


                @Override
                protected void populateViewHolder(TournamentViewHolder viewHolder, final Tournament tournament,
                    int position) {

                    mProgressBar.setVisibility(ProgressBar.GONE);
                    noOnlineTournamentsFoundTextView.setVisibility(View.GONE);

                    viewHolder.setTournament(tournament);
                    viewHolder.getTournamentNameInList().setText(tournament.getName());

                    if (tournament.getDateOfTournament() != null) {
                        String formattedDate = dateFormatter.format(tournament.getDateOfTournament());
                        viewHolder.getTournamentDateInList().setText(formattedDate);
                    }

                    viewHolder.getTournamentLocationInList().setText(tournament.getLocation());

                    viewHolder.getTournamentPlayersInList()
                        .setText("0/" + String.valueOf(tournament.getMaxNumberOfPlayers()));

                    if (currentUser != null && currentUser.getEmail() != null) {
                        if (currentUser.getEmail().equals(tournament.getCreatorEmail())) {
                            viewHolder.getEditTournamentButton().setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        editTournamentDialog(v, tournament);
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
            };

            mOnlineTournamentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mOnlineTournamentRecyclerView.setAdapter(mFirebaseTournamentAdapter);
        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);

            TextView offline_text = (TextView) view.findViewById(R.id.offline_text);
            offline_text.setVisibility(View.VISIBLE);
            noOnlineTournamentsFoundTextView.setVisibility(View.GONE);
        }

        // local tournaments
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.local_tournament_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        TournamentService tournamentService = ((BaseApplication) getActivity().getApplication()).getTournamentService();
        List<Tournament> localTournaments = tournamentService.getTournaments();

        TournamentListHeaderFragment headerFragment = new TournamentListHeaderFragment();

        getChildFragmentManager().beginTransaction().add(R.id.row_tournament_header_container, headerFragment).commit();

        Collections.sort(localTournaments, new TournamentListComparator());

        localTournamentListAdapter = new TournamentListAdapter(localTournaments);

        recyclerView.setAdapter(localTournamentListAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (getParentFragment() instanceof TournamentListItemListener) {
            mListener = (TournamentListItemListener) getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().toString()
                + " must implement TournamentListItemListener");
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();

        if (mListener == null) {
            mListener = null;
        }
    }


    public FirebaseRecyclerAdapter<Tournament, TournamentViewHolder> getOnlineTournamentAdapter() {

        return mFirebaseTournamentAdapter;
    }


    public TournamentListAdapter getLocalTournamentListAdapter() {

        return localTournamentListAdapter;
    }


    private void editTournamentDialog(View v, Tournament tournament) {

        Log.i(v.getClass().getName(), "tournament edit:" + tournament);

        TournamentManagementDialog dialog = new TournamentManagementDialog();

        if (getParentFragment() instanceof TournamentManagementFragment) {
            dialog.setTargetFragment(getParentFragment(), 1);
        } else {
            new RuntimeException("parent must be management fragement");
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(TournamentManagementDialog.BUNDLE_TOURNAMENT, tournament);
        dialog.setArguments(bundle);

        FragmentManager supportFragmentManager = getChildFragmentManager();
        dialog.show(supportFragmentManager, "tournament management edit dialog tournament");
    }

    public interface TournamentListItemListener {

        void onTournamentListItemClicked(Tournament tournament);
    }

    public class TournamentListAdapter extends RecyclerView.Adapter<TournamentViewHolder> {

        private List<Tournament> mDataset;

        public TournamentListAdapter(List<Tournament> myDataset) {

            mDataset = myDataset;
        }

        @Override
        public TournamentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament, parent, false);
            TournamentViewHolder vh = new TournamentViewHolder(v);

            return vh;
        }


        @Override
        public void onBindViewHolder(TournamentViewHolder holder, int position) {

            final Tournament tournament = mDataset.get(position);

            holder.setTournament(tournament);
            holder.getTournamentNameInList().setText(tournament.getName());
            holder.getTournamentLocationInList().setText(tournament.getLocation());

            if (tournament.getDateOfTournament() != null) {
                String formattedDate = dateFormatter.format(tournament.getDateOfTournament());
                holder.getTournamentDateInList().setText(formattedDate);
            }

            holder.getTournamentPlayersInList().setText("0/" + String.valueOf(tournament.getMaxNumberOfPlayers()));
            holder.getEditTournamentButton().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        editTournamentDialog(v, tournament);
                    }
                });
        }


        @Override
        public int getItemCount() {

            return mDataset.size();
        }


        public void add(Tournament item) {

            mDataset.add(0, item);
            notifyItemInserted(0);
        }


        public void replace(Tournament item) {

            int position = mDataset.indexOf(item);
            mDataset.set(position, item);
            notifyItemChanged(position, item);
        }


        public void remove(Tournament item) {

            int position = mDataset.indexOf(item);
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class TournamentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageButton editTournamentButton;
        private TextView tournamentNameInList;
        private TextView tournamentPlayersInList;
        private TextView tournamentLocationInList;
        private TextView tournamentDateInList;
        private Tournament tournament;

        public TournamentViewHolder(View v) {

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

            Log.i(this.getClass().getName(), "clicked on tournament");

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

    private class TournamentListComparator implements Comparator<Tournament> {

        @Override
        public int compare(Tournament t1, Tournament t2) {

            return t1.getDateOfTournament().getTime() > t2.getDateOfTournament().getTime() ? 1 : 0;
        }
    }
}
