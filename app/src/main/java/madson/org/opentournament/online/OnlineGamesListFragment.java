package madson.org.opentournament.online;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
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
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineGamesListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND = "round";

    private DatabaseReference mFirebaseDatabaseReference;

    private Tournament tournament;
    private int round;
    private ProgressBar mProgressBar;
    private RecyclerView recyclerView;
    private BaseActivity baseActivity;

    public static Fragment newInstance(int round, Tournament tournament) {

        OnlineGamesListFragment fragment = new OnlineGamesListFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        args.putInt(BUNDLE_ROUND, round);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
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

        View view = inflater.inflate(R.layout.fragment_online_games_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) view.findViewById(R.id.games_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_GAMES + "/"
                + tournament.getGameOrSportTyp() + "/" + tournament.getUUID() + "/" + round);

        Query orderedGames = child.orderByChild(GameTable.COLUMN_PLAYING_FIELD);

        final OnlineGamesListAdapter gamesListAdapter = new OnlineGamesListAdapter(baseActivity, tournament);

        orderedGames.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Game game = dataSnapshot.getValue(Game.class);

                    if (game != null) {
                        mProgressBar.setVisibility(View.GONE);

                        if (game.getParent_UUID() == null) {
                            gamesListAdapter.addGame(game);
                        }
                    }
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Game game = dataSnapshot.getValue(Game.class);

                    if (game != null) {
                        mProgressBar.setVisibility(View.GONE);

                        if (game.getParent_UUID() == null) {
                            gamesListAdapter.updateGame(game);
                        }
                    }
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    Game game = dataSnapshot.getValue(Game.class);

                    if (game != null) {
                        mProgressBar.setVisibility(View.GONE);

                        if (game.getParent_UUID() == null) {
                            gamesListAdapter.removeGame(game);
                        }
                    }
                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
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

                    Player authenticatedPlayer = baseActivity.getBaseApplication().getAuthenticatedPlayer();

                    if (authenticatedPlayer != null) {
                        int indexOfPlayer = gamesListAdapter.getIndexOfPlayer(authenticatedPlayer.getUUID());

                        if (indexOfPlayer != -1) {
                            recyclerView.scrollToPosition(indexOfPlayer);
                        }
                    }
                }
            }, 1000);

        return view;
    }
}
