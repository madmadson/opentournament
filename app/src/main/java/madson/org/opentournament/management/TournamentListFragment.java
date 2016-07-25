package madson.org.opentournament.management;

import android.content.Context;

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

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.TournamentService;

import java.util.List;


public class TournamentListFragment extends Fragment {

    public static final String TAG = "tournament_list_fragment";

    private TournamentService tournamentService;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public TournamentListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (tournamentService == null) {
            tournamentService = ((OpenTournamentApplication) getActivity().getApplication()).getTournamentService();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tournament_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_reclcyer_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Tournament> tournaments = tournamentService.getTournaments();

        TournamentListAdapter tournamentListAdapter = new TournamentListAdapter(tournaments);

        recyclerView.setAdapter(tournamentListAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (getParentFragment() instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().toString()
                + " must implement OnListFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {

        void onTournamentListItemClicked(long id);
    }

    public class TournamentListAdapter extends RecyclerView.Adapter<TournamentListAdapter.ViewHolder> {

        private List<Tournament> mDataset;

        public TournamentListAdapter(List<Tournament> myDataset) {

            mDataset = myDataset;
        }

        @Override
        public TournamentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tournament_list_row, parent, false);

            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }


        @Override
        public void onBindViewHolder(TournamentListAdapter.ViewHolder holder, int position) {

            final Tournament tournament = mDataset.get(position);
            holder.tournamentNameInList.setText(tournament.getName());
            holder.startTournamentButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(v.getClass().getName(), "tournament Stared");
                        // mListener.onTournamentListItemClicked(id);
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

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tournamentNameInList;
            public ImageButton startTournamentButton;

            public ViewHolder(View v) {

                super(v);

                tournamentNameInList = (TextView) v.findViewById(R.id.tournamentNameInList);
                startTournamentButton = (ImageButton) v.findViewById(R.id.startTournamentButton);
            }
        }
    }
}
