package madson.org.opentournament.online.team;

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
import android.widget.TextView;

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
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.utility.BaseActivity;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class OnlineTeamGameListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_GAME = "parentGame";
    private Tournament tournament;
    private Game parentGame;

    private BaseActivity baseActivity;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private TextView teamMatchNotOnline;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        baseActivity = (BaseActivity) getActivity();
    }


    public static OnlineTeamGameListFragment newInstance(Game game, Tournament tournament) {

        OnlineTeamGameListFragment fragment = new OnlineTeamGameListFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_GAME, game);
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        if (bundle != null && bundle.getParcelable(BUNDLE_GAME) != null) {
            parentGame = bundle.getParcelable(BUNDLE_GAME);
        }

        final View view = inflater.inflate(R.layout.fragment_team_game_list, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        teamMatchNotOnline = (TextView) view.findViewById(R.id.team_match_not_online_yet);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.game_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        final OnlineTeamGameListAdapter gamesListAdapter = new OnlineTeamGameListAdapter(baseActivity, tournament);
        recyclerView.setAdapter(gamesListAdapter);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_GAMES + "/"
                + tournament.getGameOrSportTyp() + "/" + tournament.getUUID() + "/" + parentGame.getTournament_round());

        Query orderedGames = child.orderByChild(GameTable.COLUMN_PLAYING_FIELD);

        orderedGames.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Game game = dataSnapshot.getValue(Game.class);

                    if (game != null) {
                        mProgressBar.setVisibility(View.GONE);
                        teamMatchNotOnline.setVisibility(View.GONE);

                        if (parentGame.getUUID().equals(game.getParent_UUID())) {
                            gamesListAdapter.addGame(game);
                        }
                    }
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Game game = dataSnapshot.getValue(Game.class);

                    if (parentGame.getUUID().equals(game.getParent_UUID())) {
                        gamesListAdapter.updateGame(game);
                    }
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    Game game = dataSnapshot.getValue(Game.class);

                    if (parentGame.getUUID().equals(game.getParent_UUID())) {
                        gamesListAdapter.removeGame(game);
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
                        teamMatchNotOnline.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(ProgressBar.GONE);
                    }
                }
            }, 5000);

        return view;
    }
}
