package madson.org.opentournament.tournaments;

import android.content.Context;

import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;

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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class TournamentListsFragment extends Fragment {

    private TournamentManagementEventListener mListener;
    private DatabaseReference mFirebaseDatabaseReference;
    private ProgressBar mProgressBar;
    private TextView noOnlineTournamentsFoundTextView;
    private DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());

    private TournamentListAdapter localTournamentListAdapter;
    private RecyclerView mOnlineTournamentsRecyclerView;
    private OnlineTournamentListAdapter onlineTournmantListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_tournament_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noOnlineTournamentsFoundTextView = (TextView) view.findViewById(R.id.no_online_tournaments);

        if (((BaseActivity) getActivity()).isConnected()) {
            mOnlineTournamentsRecyclerView = (RecyclerView) view.findViewById(
                    R.id.online_tournament_list_recycler_view);
            mOnlineTournamentsRecyclerView.setHasFixedSize(true);
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mOnlineTournamentsRecyclerView.setLayoutManager(linearLayoutManager);
            onlineTournmantListAdapter = new OnlineTournamentListAdapter(getActivity(), mListener);

            DatabaseReference firebaseTournaments = mFirebaseDatabaseReference.getRef()
                    .child(FirebaseReferences.TOURNAMENTS + "/" + GameOrSportTyp.WARMACHINE.name());
            firebaseTournaments.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String tournament_online_uuid = dataSnapshot.getKey();

                        Tournament tournament = dataSnapshot.getValue(Tournament.class);

                        if (tournament != null) {
                            tournament.setOnlineUUID(tournament_online_uuid);
                            onlineTournmantListAdapter.addTournament(tournament);
                        }

                        mProgressBar.setVisibility(View.GONE);

                        if (onlineTournmantListAdapter.getItemCount() > 0) {
                            noOnlineTournamentsFoundTextView.setVisibility(View.GONE);
                        }
                    }


                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        String tournament_online_uuid = dataSnapshot.getKey();

                        Tournament tournament = dataSnapshot.getValue(Tournament.class);

                        if (tournament != null) {
                            tournament.setOnlineUUID(tournament_online_uuid);
                            onlineTournmantListAdapter.replaceTournament(tournament);
                        }
                    }


                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                        String tournament_online_uuid = dataSnapshot.getKey();

                        Tournament tournament = dataSnapshot.getValue(Tournament.class);

                        if (tournament != null) {
                            tournament.setOnlineUUID(tournament_online_uuid);
                            onlineTournmantListAdapter.removeTournament(tournament);
                        }

                        if (onlineTournmantListAdapter.getItemCount() == 0) {
                            noOnlineTournamentsFoundTextView.setVisibility(View.VISIBLE);
                        }
                    }


                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            mOnlineTournamentsRecyclerView.setAdapter(onlineTournmantListAdapter);

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    if (onlineTournmantListAdapter.getItemCount() == 0) {
                        mProgressBar.setVisibility(View.GONE);
                        noOnlineTournamentsFoundTextView.setVisibility(View.VISIBLE);
                    }
                }
            };

            Handler handler = new Handler();
            handler.postDelayed(runnable, 10000);
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
        localTournamentListAdapter = new TournamentListAdapter(mListener);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                TournamentService tournamentService = ((BaseApplication) getActivity().getApplication())
                    .getTournamentService();
                List<Tournament> localTournaments = tournamentService.getTournaments();
                Collections.sort(localTournaments, new TournamentComparator());
                localTournamentListAdapter.addTournaments(localTournaments);
            }
        };
        runnable.run();

        recyclerView.setAdapter(localTournamentListAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (getParentFragment() instanceof TournamentManagementEventListener) {
            mListener = (TournamentManagementEventListener) getParentFragment();
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();

        if (mListener == null) {
            mListener = null;
        }
    }


    public TournamentListAdapter getLocalTournamentListAdapter() {

        return localTournamentListAdapter;
    }

    public class TournamentListAdapter extends RecyclerView.Adapter<TournamentViewHolder> {

        private List<Tournament> mDataset = new ArrayList<>();
        private TournamentManagementEventListener mListener;

        public TournamentListAdapter(TournamentManagementEventListener mListener) {

            this.mListener = mListener;
        }

        @Override
        public TournamentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_organised_tournament, parent, false);
            TournamentViewHolder vh = new TournamentViewHolder(v);

            return vh;
        }


        @Override
        public void onBindViewHolder(TournamentViewHolder holder, int position) {

            final Tournament tournament = mDataset.get(position);

            holder.setTournament(tournament);
            holder.getTournamentNameInList().setText(tournament.getName());

            if (holder.getTournamentLocationInList() != null) {
                holder.getTournamentLocationInList().setText(tournament.getLocation());
            }

            if (holder.getTournamentDateInList() != null && tournament.getDateOfTournament() != null) {
                String formattedDate = dateFormatter.format(tournament.getDateOfTournament());
                holder.getTournamentDateInList().setText(formattedDate);
            }

            holder.getTournamentPlayersInList()
                .setText(getActivity().getResources()
                    .getString(R.string.players_in_tournament, tournament.getActualPlayers(),
                        tournament.getMaxNumberOfPlayers()));

            if (holder.getEditTournamentButton() != null) {
                holder.getEditTournamentButton().setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            mListener.onTournamentEditClicked(tournament);
                        }
                    });
            }

            if (holder.getUploadTournamentButton() != null) {
                holder.getUploadTournamentButton().setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            mListener.onTournamentUploadClicked(tournament);
                        }
                    });
            }

            if (holder.getTournamentState() != null) {
                if (tournament.getState().equals(Tournament.TournamentState.FINISHED.name())) {
                    holder.getTournamentState().setText(R.string.tournament_finished);
                } else if (tournament.getActualRound() > 0) {
                    holder.getTournamentState().setText(R.string.tournament_started);
                }
            }

            if (position % 2 == 0) {
                holder.getRowTournament().setBackgroundColor(Color.LTGRAY);
            } else {
                holder.getRowTournament().setBackgroundColor(Color.WHITE);
            }
        }


        @Override
        public int getItemCount() {

            return mDataset.size();
        }


        public void add(Tournament item) {

            mDataset.add(item);
            Collections.sort(mDataset, new TournamentComparator());
            notifyDataSetChanged();
        }


        public void replace(Tournament item) {

            int position = mDataset.indexOf(item);
            mDataset.set(position, item);
            Collections.sort(mDataset, new TournamentComparator());
            notifyItemChanged(position, item);
        }


        public void addTournaments(List<Tournament> listOfTournaments) {

            mDataset = listOfTournaments;

            notifyDataSetChanged();
        }


        public void remove(Tournament item) {

            int position = mDataset.indexOf(item);
            mDataset.remove(position);
            Collections.sort(mDataset, new TournamentComparator());
            notifyDataSetChanged();
        }
    }

    public class TournamentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View rowTournament;
        private TextView tournamentState;
        private ImageButton uploadTournamentButton;
        private ImageButton editTournamentButton;
        private TextView tournamentNameInList;
        private TextView tournamentPlayersInList;
        private TextView tournamentLocationInList;
        private TextView tournamentDateInList;
        private Tournament tournament;

        public TournamentViewHolder(View v) {

            super(v);
            v.setOnClickListener(this);

            rowTournament = v.findViewById(R.id.row_tournament);

            editTournamentButton = (ImageButton) v.findViewById(R.id.button_edit_tournament);
            uploadTournamentButton = (ImageButton) v.findViewById(R.id.button_upload_tournament);
            tournamentNameInList = (TextView) v.findViewById(R.id.tournament_name);
            tournamentPlayersInList = (TextView) v.findViewById(R.id.amount_players);
            tournamentLocationInList = (TextView) v.findViewById(R.id.tournament_location);
            tournamentDateInList = (TextView) v.findViewById(R.id.tournament_date);
            tournamentState = (TextView) v.findViewById(R.id.tournament_state);
        }

        @Override
        public void onClick(View v) {

            Log.i(this.getClass().getName(), "clicked on tournament");

            mListener.onTournamentListItemClicked(tournament);
        }


        public TextView getTournamentNameInList() {

            return tournamentNameInList;
        }


        public ImageButton getUploadTournamentButton() {

            return uploadTournamentButton;
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


        public TextView getTournamentState() {

            return tournamentState;
        }


        public View getRowTournament() {

            return rowTournament;
        }
    }
}
