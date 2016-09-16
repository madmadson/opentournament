package madson.org.opentournament.organize.setup;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.ConfirmPairingNewRoundDialog;
import madson.org.opentournament.organize.TournamentEventListener;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerListFragment extends Fragment implements TournamentEventListener {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private Tournament tournament;
    private TournamentPlayerListAdapter tournamentPlayerListAdapter;
    private TournamentSetupEventListener mListener;

    private DatabaseReference mFirebaseDatabaseReference;

    private TextView noTournamentPlayersTextView;
    private Button startButton;

    public static TournamentPlayerListFragment newInstance(Tournament tournament) {

        TournamentPlayerListFragment fragment = new TournamentPlayerListFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ((BaseActivity) getActivity()).getBaseApplication().registerTournamentEventListener(this);
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        ((BaseActivity) getActivity()).getBaseApplication().unregisterTournamentEventListener(this);
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        // mlistener to call when player is clicked
        if (getParentFragment() instanceof TournamentSetupFragment) {
            mListener = (TournamentSetupEventListener) getParentFragment();
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();

        if (mListener != null) {
            mListener = null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        View view = inflater.inflate(R.layout.fragment_tournament_player_list, container, false);
        noTournamentPlayersTextView = (TextView) view.findViewById(R.id.no_tournament_players);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_player_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        tournamentPlayerListAdapter = new TournamentPlayerListAdapter(getActivity(), mListener);
        recyclerView.setAdapter(tournamentPlayerListAdapter);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                TournamentPlayerService tournamentPlayerService = ((BaseApplication) getActivity().getApplication())
                    .getTournamentPlayerService();
                List<TournamentPlayer> localTournamentPlayers = tournamentPlayerService.getAllPlayersForTournament(
                        tournament);

                if (localTournamentPlayers.size() > 0) {
                    noTournamentPlayersTextView.setVisibility(View.GONE);
                    Collections.sort(localTournamentPlayers, new TournamentPlayerComparator());
                    tournamentPlayerListAdapter.addTournamentPlayers(localTournamentPlayers);
                }
            }
        };
        runnable.run();

        startButton = (Button) view.findViewById(R.id.start_tournament_button);
        startButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (tournamentPlayerListAdapter.getItemCount() % 2 == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.uneven_player_message)
                        .setPositiveButton(R.string.dialog_confirm_and_start, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Runnable runnable = new Runnable() {

                                            @Override
                                            public void run() {

                                                TournamentPlayerService tournamentPlayerService =
                                                    ((BaseApplication) getActivity().getApplication())
                                                    .getTournamentPlayerService();

                                                TournamentPlayer dummyTournamentPlayer = new TournamentPlayer();
                                                dummyTournamentPlayer.setFirstname("Dummy");
                                                dummyTournamentPlayer.setNickname("THE HAMMER");
                                                dummyTournamentPlayer.setLastname("Player");
                                                dummyTournamentPlayer.setDummy(true);

                                                tournamentPlayerService.addTournamentPlayerToTournament(
                                                    dummyTournamentPlayer, tournament);

                                                ((BaseApplication) getActivity().getApplication())
                                                .getTournamentService().increaseActualPlayerForTournament(tournament);
                                                tournamentPlayerListAdapter.addTournamentPlayer(dummyTournamentPlayer);
                                            }
                                        };
                                        runnable.run();
                                    }
                                })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show();
                    } else {
                        ConfirmPairingNewRoundDialog dialog = new ConfirmPairingNewRoundDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConfirmPairingNewRoundDialog.BUNDLE_TOURNAMENT, tournament);
                        bundle.putInt(ConfirmPairingNewRoundDialog.BUNDLE_ROUND_TO_DISPLAY, 1);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                        dialog.show(supportFragmentManager, "confirm start tournament");
                    }
                }
            });

        if (tournament.getActualRound() > 0) {
            startButton.setVisibility(View.GONE);
        }

        return view;
    }


    private void loadOnlineTournamentPlayers() {

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        final List<String> onlineTournamentPlayersUUIDs = new ArrayList<>();

        final ValueEventListener tournamentPlayerListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    onlineTournamentPlayersUUIDs.add(playerSnapshot.getKey());

                    DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_PLAYERS
                            + "/" + tournament.getOnlineUUID() + "/" + playerSnapshot.getKey());
                    child.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                TournamentPlayer player = dataSnapshot.getValue(TournamentPlayer.class);

                                if (player != null && !tournamentPlayerListAdapter.contains(player)) {
                                    Log.i(this.getClass().getName(), "online player added: " + player);
                                    addPlayer(player);
                                }

                                noTournamentPlayersTextView.setVisibility(View.GONE);
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                }

                if (!dataSnapshot.getChildren().iterator().hasNext()) {
                    noTournamentPlayersTextView.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(this.getClass().getName(), "failed to load online players");
            }
        };

        DatabaseReference child = mFirebaseDatabaseReference.child("tournaments/" + tournament.getOnlineUUID()
                + "/tournament_players");

        child.addValueEventListener(tournamentPlayerListener);
    }


    public void addPlayer(TournamentPlayer player) {

        Log.i(this.getClass().getName(), "addTournamentPlayer player to tournament player list: " + player);

        tournamentPlayerListAdapter.addTournamentPlayer(player);

        if (tournamentPlayerListAdapter.getItemCount() > 0) {
            noTournamentPlayersTextView.setVisibility(View.GONE);
        }
    }


    public void removePlayer(TournamentPlayer player) {

        Log.i(this.getClass().getName(), "remove TournamentPlayer: " + player);

        tournamentPlayerListAdapter.removeTournamentPlayer(player);

        if (tournamentPlayerListAdapter.getItemCount() == 0) {
            noTournamentPlayersTextView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void startRound(int roundToStart, Tournament tournament) {

        if (roundToStart == 1) {
            startButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void pairRoundAgain(int round_for_pairing) {

        // nothing
    }


    @Override
    public void pairingChanged(Game game1, Game game2) {

        // nothing
    }


    @Override
    public void enterGameResultConfirmed(Game game) {

        // nothing
    }
}
