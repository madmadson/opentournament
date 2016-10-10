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
import madson.org.opentournament.db.GameTable;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineGamesListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_UUID = "tournament_uuid";
    public static final String BUNDLE_ROUND = "round";

    private DatabaseReference mFirebaseDatabaseReference;

    private String tournament_uuid;
    private int round;
    private ProgressBar mProgressBar;
    private RecyclerView recyclerView;
    private BaseApplication baseApplication;

    public static Fragment newInstance(int round, String tournament_uuid) {

        OnlineGamesListFragment fragment = new OnlineGamesListFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_TOURNAMENT_UUID, tournament_uuid);
        args.putInt(BUNDLE_ROUND, round);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getString(BUNDLE_TOURNAMENT_UUID) != null) {
            tournament_uuid = bundle.getString(BUNDLE_TOURNAMENT_UUID);
        }

        if (bundle != null && bundle.getInt(BUNDLE_ROUND) != 0) {
            round = bundle.getInt(BUNDLE_ROUND);
        }

        View view = inflater.inflate(R.layout.fragment_online_games_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) view.findViewById(R.id.games_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_GAMES + "/"
                + tournament_uuid + "/" + round);

        Query orderedGames = child.orderByChild(GameTable.COLUMN_PLAYING_FIELD);

        baseApplication = ((BaseActivity) getActivity()).getBaseApplication();

        final OnlineGamesListAdapter gamesListAdapter = new OnlineGamesListAdapter(baseApplication);

        orderedGames.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                        Game game = playerSnapshot.getValue(Game.class);

                        if (game != null) {
                            mProgressBar.setVisibility(View.GONE);

                            gamesListAdapter.addGame(game);
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        recyclerView.setAdapter(gamesListAdapter);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (gamesListAdapter.getItemCount() == 0) {
                        mProgressBar.setVisibility(ProgressBar.GONE);
                    }
                }
            }, 5000);

        Handler handler2 = new Handler();

        handler2.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (baseApplication.getAuthenticatedPlayer() != null) {
                        int indexOfPlayer = gamesListAdapter.getIndexOfPlayer(
                                baseApplication.getAuthenticatedPlayer().getUUID());

                        if (indexOfPlayer != -1) {
                            recyclerView.scrollToPosition(indexOfPlayer);
                        }
                    }
                }
            }, 1000);

        return view;
    }
}
