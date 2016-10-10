package madson.org.opentournament.organize.setup;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.os.Handler;

import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.organize.ConfirmPairingNewRoundDialog;
import madson.org.opentournament.organize.TournamentEventListener;
import madson.org.opentournament.tasks.AddDummyPlayerTask;
import madson.org.opentournament.tasks.LoadTournamentPlayerTask;
import madson.org.opentournament.tasks.LoadTournamentPlayerTeamTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerListFragment extends Fragment implements TournamentEventListener {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private Tournament tournament;

    private TextView noTournamentPlayersTextView;
    private Button startButton;
    private BaseActivity baseActivity;
    private ProgressBar progressbar;
    private RecyclerView registrationRecyclerView;
    private ProgressBar progressbarRegistration;
    private RegistrationListAdapter registrationListAdapter;

    private ImageView soloViewForTournamentPlayers;
    private ImageView teamViewForTournamentPlayers;

    private TournamentPlayerListAdapter tournamentPlayerListAdapter;
    private TournamentPlayerTeamListAdapter tournamentPlayerTeamListAdapter;
    private TextView headingRegisteredPlayers;

    public static TournamentPlayerListFragment newInstance(Tournament tournament) {

        TournamentPlayerListFragment fragment = new TournamentPlayerListFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
        baseActivity.getBaseApplication().registerTournamentEventListener(this);
    }


    @Override
    public void onDetach() {

        super.onDetach();
        baseActivity.getBaseApplication().unregisterTournamentEventListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        View view = inflater.inflate(R.layout.fragment_tournament_player_list, container, false);
        noTournamentPlayersTextView = (TextView) view.findViewById(R.id.no_tournament_players);
        progressbar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressbarRegistration = (ProgressBar) view.findViewById(R.id.progressBarRegistration);
        headingRegisteredPlayers = (TextView) view.findViewById(R.id.heading_registered_players);

        registrationRecyclerView = (RecyclerView) view.findViewById(R.id.tournament_registration_list_recycler_view);
        registrationRecyclerView.setHasFixedSize(true);

        // 3 recyclerViews
        final RecyclerView tournamentPlayersRecyclerView = (RecyclerView) view.findViewById(
                R.id.tournament_player_list_recycler_view);
        tournamentPlayersRecyclerView.setHasFixedSize(true);

        final ExpandableListView teamExpandableList = (ExpandableListView) view.findViewById(
                R.id.tournament_teams_expandableList);

        // 3 layout managers
        LinearLayoutManager linearLayoutManagerRegistration = new LinearLayoutManager(getActivity());
        registrationRecyclerView.setLayoutManager(linearLayoutManagerRegistration);

        LinearLayoutManager linearLayoutManagerTournamentPlayer = new LinearLayoutManager(baseActivity);
        tournamentPlayersRecyclerView.setLayoutManager(linearLayoutManagerTournamentPlayer);

        // 3 adapters
        tournamentPlayerListAdapter = new TournamentPlayerListAdapter(baseActivity, tournament);
        tournamentPlayersRecyclerView.setAdapter(tournamentPlayerListAdapter);

        tournamentPlayerTeamListAdapter = new TournamentPlayerTeamListAdapter(baseActivity, tournament);
        teamExpandableList.setAdapter(tournamentPlayerTeamListAdapter);

        registrationListAdapter = new RegistrationListAdapter(baseActivity, tournament, tournamentPlayerListAdapter);
        registrationRecyclerView.setAdapter(registrationListAdapter);

        if (baseActivity.isConnected()) {
            if (tournament.getState().equals(Tournament.TournamentState.PLANED.name())) {
                loadRegistrations();
            } else {
                progressbarRegistration.setVisibility(View.GONE);
                headingRegisteredPlayers.setVisibility(View.GONE);
            }
        } else {
            progressbarRegistration.setVisibility(View.GONE);
            headingRegisteredPlayers.setVisibility(View.GONE);
        }

        tournamentPlayerTeamListAdapter.clear();
        new LoadTournamentPlayerTask(baseActivity.getBaseApplication(), tournament, progressbar,
            tournamentPlayerListAdapter, noTournamentPlayersTextView).execute();

        tournamentPlayerListAdapter.clear();
        new LoadTournamentPlayerTeamTask(baseActivity.getBaseApplication(), tournament, progressbar,
            tournamentPlayerTeamListAdapter, noTournamentPlayersTextView).execute();

        if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
            tournamentPlayersRecyclerView.setVisibility(View.VISIBLE);
            teamExpandableList.setVisibility(View.GONE);
        } else {
            teamExpandableList.setVisibility(View.VISIBLE);
            tournamentPlayersRecyclerView.setVisibility(View.GONE);
        }

        startButton = (Button) view.findViewById(R.id.start_tournament_button);
        startButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    startTournamentClicked();
                }
            });

        if (tournament.getActualRound() > 0) {
            startButton.setVisibility(View.GONE);
        }

        soloViewForTournamentPlayers = (ImageView) view.findViewById(R.id.solo_tournament_icon);
        soloViewForTournamentPlayers.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    tournamentPlayersRecyclerView.setVisibility(View.VISIBLE);
                    teamExpandableList.setVisibility(View.GONE);
                }
            });

        teamViewForTournamentPlayers = (ImageView) view.findViewById(R.id.team_tournament_icon);
        teamViewForTournamentPlayers.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    teamExpandableList.setVisibility(View.VISIBLE);
                    tournamentPlayersRecyclerView.setVisibility(View.GONE);
                }
            });

        return view;
    }


    private void startTournamentClicked() {

        if (tournamentPlayerListAdapter.getItemCount() == 0) {
            Snackbar snackbar = Snackbar.make(((BaseActivity) getActivity()).getCoordinatorLayout(),
                    R.string.cant_start_tournament_without_players, Snackbar.LENGTH_LONG);

            snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorNegative));

            snackbar.show();
        } else if (tournamentPlayerListAdapter.getItemCount() % 2 == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.uneven_player_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                BaseApplication application = (BaseApplication) getActivity().getApplication();
                                new AddDummyPlayerTask(application, tournament, tournamentPlayerListAdapter).execute();
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
        } else {
            ConfirmStartTournamentDialog dialog = new ConfirmStartTournamentDialog();

            Bundle bundle = new Bundle();
            bundle.putParcelable(ConfirmStartTournamentDialog.BUNDLE_TOURNAMENT, tournament);
            dialog.setArguments(bundle);

            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
            dialog.show(supportFragmentManager, "confirm start tournament");
        }
    }


    private void loadRegistrations() {

        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_REGISTRATIONS + "/"
                + tournament.getOnlineUUID());

        child.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    progressbarRegistration.setVisibility(View.GONE);

                    TournamentPlayer player = dataSnapshot.getValue(TournamentPlayer.class);
                    player.setPlayerUUID(dataSnapshot.getKey());

                    if (!tournamentPlayerListAdapter.contains(player)) {
                        registrationListAdapter.addRegistration(player);
                    }
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    TournamentPlayer player = dataSnapshot.getValue(TournamentPlayer.class);
                    player.setPlayerUUID(dataSnapshot.getKey());

                    registrationListAdapter.updateRegistration(player);
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    TournamentPlayer player = dataSnapshot.getValue(TournamentPlayer.class);
                    player.setPlayerUUID(dataSnapshot.getKey());

                    registrationListAdapter.removeRegistration(player);
                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if (registrationListAdapter.getItemCount() == 0) {
                    progressbarRegistration.setVisibility(View.GONE);
                }
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 5000);
    }


    public void startButtonInvisible() {

        startButton.setVisibility(View.GONE);
    }


    @Override
    public void startRound(int roundToStart, Tournament tournament) {
    }


    @Override
    public void pairRoundAgain(int round_for_pairing) {
    }


    @Override
    public void pairingChanged(Game game1, Game game2) {
    }


    @Override
    public void enterGameResultConfirmed(Game game) {
    }


    @Override
    public void addTournamentPlayer(TournamentPlayer tournamentPlayer) {

        tournamentPlayerListAdapter.addTournamentPlayer(tournamentPlayer);
        tournamentPlayerTeamListAdapter.addTournamentPlayer(tournamentPlayer);

        if (tournamentPlayerListAdapter.getItemCount() > 0) {
            noTournamentPlayersTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public void removeTournamentPlayer(TournamentPlayer tournamentPlayer) {

        tournamentPlayerListAdapter.removeTournamentPlayer(tournamentPlayer);
        tournamentPlayerTeamListAdapter.removeTournamentPlayer(tournamentPlayer);

        if (tournamentPlayerListAdapter.getItemCount() == 0) {
            noTournamentPlayersTextView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void addPlayerToTournament(Player player) {
    }


    @Override
    public void removeAvailablePlayer(Player player) {
    }


    @Override
    public void updateTournamentPlayer(TournamentPlayer updatedPLayer, String oldTeamName) {

        tournamentPlayerListAdapter.updateTournamentPlayer(updatedPLayer);
        tournamentPlayerTeamListAdapter.updateTournamentPlayer(updatedPLayer, oldTeamName);
    }


    @Override
    public void addRegistration(TournamentPlayer player) {

        tournamentPlayerListAdapter.addTournamentPlayer(player);
        tournamentPlayerTeamListAdapter.addTournamentPlayer(player);

        registrationListAdapter.removeRegistration(player);
    }
}
