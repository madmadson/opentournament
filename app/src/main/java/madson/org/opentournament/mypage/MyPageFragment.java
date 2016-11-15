package madson.org.opentournament.mypage;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.GameOrSportTyp;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.Environment;


public class MyPageFragment extends Fragment {

    private ProgressBar progressBar;

    private DatabaseReference mFirebaseDatabaseReference;

    private BaseActivity baseActivity;

    private Query soloQuery;
    private Query teamQuery;
    private RecyclerView myRegistrationRecyclerView;
    private TextView noRegistration;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (baseActivity.getBaseApplication().getEnvironment() != Environment.PROD) {
            baseActivity.getToolbar().setTitle(R.string.toolbar_title_mypage_DEMO);
        } else {
            baseActivity.getToolbar().setTitle(R.string.toolbar_title_mypage);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noRegistration = (TextView) view.findViewById(R.id.no_registrations);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (baseActivity.getBaseApplication().isOnline() && user != null && !user.isAnonymous()) {
            myRegistrationRecyclerView = (RecyclerView) view.findViewById(R.id.my_registration_list_recycler_view);
            myRegistrationRecyclerView.setHasFixedSize(true);
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(baseActivity);
            myRegistrationRecyclerView.setLayoutManager(linearLayoutManager);

            final MyPageRegistrationListAdapter myRegistrationListAdapter = new MyPageRegistrationListAdapter(
                    baseActivity);

            final Player authenticatedPlayer = baseActivity.getBaseApplication().getAuthenticatedPlayer();

            // do this configurable for other sport games
            final DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYER_REGISTRATIONS
                    + "/" + authenticatedPlayer.getUUID());

            final ChildEventListener eventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    progressBar.setVisibility(View.GONE);
                    noRegistration.setVisibility(View.GONE);

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    myRegistrationListAdapter.addTournament(tournament);
                }


                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    myRegistrationListAdapter.replaceTournament(tournament);
                }


                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    Tournament tournament = dataSnapshot.getValue(Tournament.class);
                    myRegistrationListAdapter.removeTournament(tournament);

                    if (myRegistrationListAdapter.getItemCount() == 0) {
                        noRegistration.setVisibility(View.GONE);
                    }
                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            child.addChildEventListener(eventListener);

            myRegistrationRecyclerView.setAdapter(myRegistrationListAdapter);

            final Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    if (myRegistrationListAdapter.getItemCount() == 0) {
                        progressBar.setVisibility(View.GONE);
                        noRegistration.setVisibility(View.VISIBLE);
                    }
                }
            };

            Handler handler = new Handler();
            handler.postDelayed(runnable, 10000);
        } else {
            TextView offlineText = (TextView) view.findViewById(R.id.offline_text);
            offlineText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            noRegistration.setVisibility(View.GONE);
        }

        return view;
    }
}
