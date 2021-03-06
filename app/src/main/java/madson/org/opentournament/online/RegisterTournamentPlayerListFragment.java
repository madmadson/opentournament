package madson.org.opentournament.online;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;

import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

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
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RegisterTournamentPlayerListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private DatabaseReference mFirebaseDatabaseReference;
    private Tournament tournament;
    private ProgressBar mProgressBar;
    private TextView noRegistrationsTextView;
    private RecyclerView recyclerView;
    private RegisterTournamentPlayerListAdapter registrationListAdapter;

    private BaseActivity baseActivity;
    private OnlineTournamentPlayerTeamExpandableListAdapter tournamentPlayerTeamExpandableListAdapter;
    private ImageView soloViewForTournamentPlayers;
    private ImageView teamViewForTournamentPlayers;

    public static Fragment newInstance(Tournament tournament) {

        RegisterTournamentPlayerListFragment fragment = new RegisterTournamentPlayerListFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        View view = inflater.inflate(R.layout.fragment_online_tournament_player_list, container, false);

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);

            mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            noRegistrationsTextView = (TextView) view.findViewById(R.id.no_tournament_players);

            recyclerView = (RecyclerView) view.findViewById(R.id.tournament_player_list_recycler_view);
            recyclerView.setHasFixedSize(true);

            final ExpandableListView teamExpandableList = (ExpandableListView) view.findViewById(
                    R.id.tournament_teams_expandableList);

            tournamentPlayerTeamExpandableListAdapter = new OnlineTournamentPlayerTeamExpandableListAdapter(
                    baseActivity, tournament);
            teamExpandableList.setAdapter(tournamentPlayerTeamExpandableListAdapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);

            Button registerButton = (Button) view.findViewById(R.id.register_for_tournament_button);

            registerButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Player authenticatedPlayer = baseActivity.getBaseApplication().getAuthenticatedPlayer();

                        if (authenticatedPlayer == null) {
                            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                    R.string.cannot_register_without_player_profile, Snackbar.LENGTH_LONG);

                            snackbar.getView()
                            .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

                            snackbar.show();
                        } else if (!registrationListAdapter.playerNotRegistered(authenticatedPlayer)) {
                            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                    R.string.you_are_already_registered, Snackbar.LENGTH_LONG);

                            snackbar.getView()
                            .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

                            snackbar.show();
                        } else {
                            RegisterTournamentPlayerDialog dialog = new RegisterTournamentPlayerDialog();

                            Bundle bundle = new Bundle();
                            bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                            bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_PLAYER, authenticatedPlayer);
                            dialog.setArguments(bundle);

                            FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();
                            dialog.show(supportFragmentManager, "tournament setup new player");
                        }
                    }
                });

            if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                recyclerView.setVisibility(View.VISIBLE);
                teamExpandableList.setVisibility(View.GONE);
            } else {
                teamExpandableList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

            soloViewForTournamentPlayers = (ImageView) view.findViewById(R.id.solo_tournament_icon);
            soloViewForTournamentPlayers.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        recyclerView.setVisibility(View.VISIBLE);
                        teamExpandableList.setVisibility(View.GONE);
                    }
                });

            teamViewForTournamentPlayers = (ImageView) view.findViewById(R.id.team_tournament_icon);
            teamViewForTournamentPlayers.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        teamExpandableList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
        }

        return view;
    }


    @Override
    public void onStart() {

        super.onStart();

        registrationListAdapter = new RegisterTournamentPlayerListAdapter(baseActivity, tournament);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.TOURNAMENT_REGISTRATIONS + "/"
                + tournament.getGameOrSportTyp() + "/" + tournament.getUuid());

        child.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    mProgressBar.setVisibility(View.GONE);

                    TournamentPlayer player = dataSnapshot.getValue(TournamentPlayer.class);
                    player.setPlayerUUID(dataSnapshot.getKey());

                    registrationListAdapter.addRegistration(player);

                    tournamentPlayerTeamExpandableListAdapter.addTournamentPlayer(player);

                    if (registrationListAdapter.getItemCount() > 0) {
                        noRegistrationsTextView.setVisibility(View.GONE);
                    }
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    TournamentPlayer player = dataSnapshot.getValue(TournamentPlayer.class);
                    player.setPlayerUUID(dataSnapshot.getKey());

                    registrationListAdapter.updateRegistration(player);
                    tournamentPlayerTeamExpandableListAdapter.updateTournamentPlayer(player);
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    TournamentPlayer player = dataSnapshot.getValue(TournamentPlayer.class);
                    player.setPlayerUUID(dataSnapshot.getKey());

                    registrationListAdapter.removeRegistration(player);
                    tournamentPlayerTeamExpandableListAdapter.removeTournamentPlayer(player);

                    if (registrationListAdapter.getItemCount() == 0) {
                        noRegistrationsTextView.setVisibility(View.VISIBLE);
                    }
                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        recyclerView.setAdapter(registrationListAdapter);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (registrationListAdapter.getItemCount() == 0) {
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        noRegistrationsTextView.setVisibility(View.VISIBLE);
                        noRegistrationsTextView.setText(R.string.no_registration_found);
                    }
                }
            }, 5000);
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }
}
