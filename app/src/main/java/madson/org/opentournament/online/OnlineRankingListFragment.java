package madson.org.opentournament.online;

import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentRanking;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineRankingListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND = "round";

    private DatabaseReference mFirebaseDatabaseReference;

    private Tournament tournament;
    private int round;
    private ProgressBar mProgressBar;

    public static Fragment newInstance(int round, Tournament tournament) {

        OnlineRankingListFragment fragment = new OnlineRankingListFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        args.putInt(BUNDLE_ROUND, round);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        if (bundle != null && bundle.getInt(BUNDLE_ROUND) != 0) {
            round = bundle.getInt(BUNDLE_ROUND);
        }

        View view = inflater.inflate(R.layout.fragment_online_ranking_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.ranking_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_RANKINGS + "/"
                + tournament.getGameOrSportTyp() + "/" + tournament.getUuid() + "/" + round);

        Query orderedGames = child.orderByChild("rank");
        final OnlineRankingListAdapter onlineRankingListAdapter = new OnlineRankingListAdapter(getActivity(),
                tournament);

        orderedGames.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot rankingSnapshot : dataSnapshot.getChildren()) {
                        TournamentRanking ranking = rankingSnapshot.getValue(TournamentRanking.class);

                        if (ranking != null) {
                            mProgressBar.setVisibility(View.GONE);
                            onlineRankingListAdapter.addRanking(ranking);
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        recyclerView.setAdapter(onlineRankingListAdapter);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (onlineRankingListAdapter.getItemCount() == 0) {
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                }
            }, 5000);

        return view;
    }
}
