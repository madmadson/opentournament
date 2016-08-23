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
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.ongoing.OngoingTournamentActivity;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


public class TournamentListFragment extends Fragment {

    public static final String TAG = "tournament_list_fragment";

    private TournamentListItemListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public TournamentListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tournament_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        TournamentService tournamentService = ((BaseApplication) getActivity().getApplication()).getTournamentService();

        List<Tournament> tournaments = tournamentService.getTournaments();

        TournamentListHeaderFragment headerFragment = new TournamentListHeaderFragment();

        getChildFragmentManager().beginTransaction().add(R.id.row_header_container, headerFragment).commit();

        TournamentListAdapter tournamentListAdapter = new TournamentListAdapter(tournaments);

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
    }

    public class TournamentListAdapter extends RecyclerView.Adapter<TournamentListAdapter.ViewHolder> {

        private List<Tournament> mDataset;

        public TournamentListAdapter(List<Tournament> myDataset) {

            mDataset = myDataset;
        }

        @Override
        public TournamentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tournament, parent, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }


        @Override
        public void onBindViewHolder(TournamentListAdapter.ViewHolder holder, int position) {

            final Tournament tournament = mDataset.get(position);
            holder.setTournament(tournament);
            holder.getTournamentNameInList().setText(tournament.getName());
            holder.getTournamentPlayersInList().setText(String.valueOf(tournament.getMaxNumberOfPlayers()));
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

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageButton startTournamentButton;
            private TextView tournamentNameInList;
            private TextView tournamentPlayersInList;
            private Tournament tournament;

            public ViewHolder(View v) {

                super(v);
                v.setOnClickListener(this);

                startTournamentButton = (ImageButton) v.findViewById(R.id.start_tournament_button);
                tournamentNameInList = (TextView) v.findViewById(R.id.tournament_name);
                tournamentPlayersInList = (TextView) v.findViewById(R.id.amount_players);
            }

            @Override
            public void onClick(View v) {

                mListener.onTournamentListItemClicked(tournament.getId());
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
        }
    }
}
