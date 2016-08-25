package madson.org.opentournament.tournaments;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;

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
import madson.org.opentournament.utility.BaseApplication;

import java.text.DateFormat;

import java.util.List;
import java.util.Locale;


public class TournamentListsFragment extends Fragment {

    public static final String TOURNAMENTS_CHILD = "tournaments";

    private static TournamentListItemListener mListener;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Tournament, TournamentViewHolder> mFirebaseTournamentAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mOnlineTournamentRecyclerView;
    private DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
    private FirebaseUser currentUser;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public TournamentListsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tournament_list, container, false);

        // tournaments from server

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mOnlineTournamentRecyclerView = (RecyclerView) view.findViewById(R.id.online_tournament_list_recycler_view);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseTournamentAdapter = new FirebaseRecyclerAdapter<Tournament, TournamentViewHolder>(Tournament.class,
                R.layout.row_tournament, TournamentViewHolder.class,
                mFirebaseDatabaseReference.child(TOURNAMENTS_CHILD)) {

            @Override
            protected void populateViewHolder(TournamentViewHolder viewHolder, final Tournament tournament,
                int position) {

                mProgressBar.setVisibility(ProgressBar.GONE);

                viewHolder.setTournament(tournament);
                viewHolder.getTournamentNameInList().setText(tournament.getName());

                if (tournament.getDateOfTournament() != null) {
                    String formattedDate = dateFormatter.format(tournament.getDateOfTournament());
                    viewHolder.getTournamentDateInList().setText(formattedDate);
                }

                viewHolder.getTournamentLocationInList().setText(tournament.getLocation());

                viewHolder.getTournamentPlayersInList()
                    .setText("0/" + String.valueOf(tournament.getMaxNumberOfPlayers()));

                if (currentUser != null && currentUser.getEmail().equals(tournament.getCreatorEmail())) {
                    viewHolder.getStartTournamentButton().setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                Log.i(v.getClass().getName(), "tournament Stared:" + tournament);

                                Intent intent = new Intent(getContext(), OngoingTournamentActivity.class);
                                intent.putExtra(OngoingTournamentActivity.EXTRA_TOURNAMENT_ID, tournament.getId());
                                startActivity(intent);
                            }
                        });
                } else {
                    // only creator should do action
                    viewHolder.getStartTournamentButton().setVisibility(View.INVISIBLE);
                }
            }
        };

        mOnlineTournamentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOnlineTournamentRecyclerView.setAdapter(mFirebaseTournamentAdapter);

        // local tournaments
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.local_tournament_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        TournamentService tournamentService = ((BaseApplication) getActivity().getApplication()).getTournamentService();
        List<Tournament> localTournaments = tournamentService.getTournaments();

        TournamentListHeaderFragment headerFragment = new TournamentListHeaderFragment();

        getChildFragmentManager().beginTransaction().add(R.id.row_tournament_header_container, headerFragment).commit();

        TournamentListAdapter tournamentListAdapter = new TournamentListAdapter(localTournaments);

        recyclerView.setAdapter(tournamentListAdapter);

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
        mListener = null;
    }

    public interface TournamentListItemListener {

        void onTournamentListItemClicked(long id);


        void onOnlineTournamentListItemClicked(String onlineUUID);
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
            holder.getStartTournamentButton().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(v.getClass().getName(), "tournament Stared:" + tournament);

                        Intent intent = new Intent(getContext(), OngoingTournamentActivity.class);
                        intent.putExtra(OngoingTournamentActivity.EXTRA_TOURNAMENT_ID, tournament.getId());
                        startActivity(intent);
                    }
                });
        }


        @Override
        public int getItemCount() {

            return mDataset.size();
        }


        public void add(int position, Tournament item) {

            mDataset.add(position, item);
            notifyItemInserted(position);
        }


        public void remove(Tournament item) {

            int position = mDataset.indexOf(item);
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class TournamentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageButton startTournamentButton;
        private TextView tournamentNameInList;
        private TextView tournamentPlayersInList;
        private TextView tournamentLocationInList;
        private TextView tournamentDateInList;
        private Tournament tournament;

        public TournamentViewHolder(View v) {

            super(v);
            v.setOnClickListener(this);

            startTournamentButton = (ImageButton) v.findViewById(R.id.start_tournament_button);
            tournamentNameInList = (TextView) v.findViewById(R.id.tournament_name);
            tournamentPlayersInList = (TextView) v.findViewById(R.id.amount_players);
            tournamentLocationInList = (TextView) v.findViewById(R.id.tournament_location);
            tournamentDateInList = (TextView) v.findViewById(R.id.tournament_date);
        }

        @Override
        public void onClick(View v) {

            Log.i(this.getClass().getName(), "clicked on tournament");

            if (tournament.get_id() != 0) {
                mListener.onTournamentListItemClicked(tournament.getId());
            } else {
                mListener.onOnlineTournamentListItemClicked(tournament.getOnlineUUID());
            }
        }


        public TextView getTournamentNameInList() {

            return tournamentNameInList;
        }


        public ImageButton getStartTournamentButton() {

            return startTournamentButton;
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
