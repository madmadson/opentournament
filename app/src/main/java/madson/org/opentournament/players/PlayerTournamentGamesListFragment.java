package madson.org.opentournament.players;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;

import android.support.design.widget.FloatingActionButton;

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

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.utility.BaseActivity;


public class PlayerTournamentGamesListFragment extends Fragment {

    public static final String BUNDLE_PLAYER = "player";
    public static final String BUNDLE_TOURNAMENT = "tournament";

    private BaseActivity baseActivity;

    private DatabaseReference mFirebaseDatabaseReference;
    private ProgressBar progressBar;

    private RecyclerView playerTournamentGamesRecyclerView;
    private PlayerTournamentGamesListAdapter playerTournamentGamesListAdapter;

    private Player player;
    private Tournament tournament;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FloatingActionButton floatingActionButton = baseActivity.getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_PLAYER) != null) {
            player = bundle.getParcelable(BUNDLE_PLAYER);
        }

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_player_tournament_games_list, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        TextView heading = (TextView) view.findViewById(R.id.heading_player_tournament_games);
        heading.setText(baseActivity.getString(R.string.heading_player_tournament_games,
                tournament.getNameWithMaximumChars(15)));

        playerTournamentGamesRecyclerView = (RecyclerView) view.findViewById(
                R.id.player_tournament_games_list_recycler_view);
        playerTournamentGamesRecyclerView.setHasFixedSize(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        playerTournamentGamesRecyclerView.setLayoutManager(linearLayoutManager);

        playerTournamentGamesListAdapter = new PlayerTournamentGamesListAdapter(baseActivity);

        loadPlayerTournaments();

        return view;
    }


    private void loadPlayerTournaments() {

        if (baseActivity.getBaseApplication().isOnline()) {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            // do this configurable for other sport games
            DatabaseReference gamesOfTournament = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYER_TOURNAMENTS
                    + "/" + tournament.getGameOrSportTyp() + "/" + player.getUUID() + "/" + tournament.getUuid()
                    + "/games");

            ChildEventListener gamesEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    progressBar.setVisibility(View.GONE);

                    Game game = dataSnapshot.getValue(Game.class);
                    game.setUUID(dataSnapshot.getKey());

                    playerTournamentGamesListAdapter.addGame(game);
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Game game = dataSnapshot.getValue(Game.class);
                    game.setUUID(dataSnapshot.getKey());
                    playerTournamentGamesListAdapter.replaceGame(game);
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    Game game = dataSnapshot.getValue(Game.class);
                    game.setUUID(dataSnapshot.getKey());
                    playerTournamentGamesListAdapter.removeGame(game);
                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            gamesOfTournament.addChildEventListener(gamesEventListener);

            playerTournamentGamesRecyclerView.setAdapter(playerTournamentGamesListAdapter);

            final Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    if (playerTournamentGamesListAdapter.getItemCount() == 0) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            };

            Handler handler = new Handler();
            handler.postDelayed(runnable, 10000);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
