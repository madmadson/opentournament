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
import madson.org.opentournament.online.OnlineGamesListAdapter;
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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.game_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        final OnlineTeamGameListAdapter gamesListAdapter = new OnlineTeamGameListAdapter(baseActivity, tournament);
        recyclerView.setAdapter(gamesListAdapter);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_GAMES + "/"
                + tournament.getUUID() + "/" + parentGame.getTournament_round());

        Query orderedGames = child.orderByChild(GameTable.COLUMN_PLAYING_FIELD);

        orderedGames.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                        Game game = playerSnapshot.getValue(Game.class);

                        if (game != null) {
                            mProgressBar.setVisibility(View.GONE);

                            if (parentGame.getUUID().equals(game.getParent_UUID())) {
                                gamesListAdapter.addGame(game);
                            }
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

        return view;
    }
}