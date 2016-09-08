package madson.org.opentournament.tournaments;

import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.TournamentOrganizeActivity;
import madson.org.opentournament.utility.BaseActivity;


public class TournamentManagementFragment extends Fragment implements TournamentManagementEventListener {

    private TournamentListsFragment tournamentListsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        tournamentListsFragment = new TournamentListsFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, tournamentListsFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_tournament_management);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tournament_management, container, false);

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "click floatingActionButton tournament management");

                    TournamentManagementDialog dialog = new TournamentManagementDialog();

                    FragmentManager supportFragmentManager = getChildFragmentManager();
                    dialog.show(supportFragmentManager, "tournament management new tournament");
                }
            });

        return view;
    }


    @Override
    public void onTournamentListItemClicked(Tournament tournament) {

        Log.i(this.getClass().getName(), "clicked on tournament: " + tournament);

        Intent intent = new Intent(getContext(), TournamentOrganizeActivity.class);
        intent.putExtra(TournamentOrganizeActivity.EXTRA_TOURNAMENT, tournament);
        startActivity(intent);
    }


    @Override
    public void onTournamentChangedEvent(Tournament tournament) {

        tournamentListsFragment.getLocalTournamentListAdapter().replace(tournament);
    }


    @Override
    public void onTournamentAddedEvent(Tournament tournament) {

        tournamentListsFragment.getLocalTournamentListAdapter().add(tournament);
    }


    @Override
    public void onTournamentDeletedEvent(Tournament tournament) {

        tournamentListsFragment.getLocalTournamentListAdapter().remove(tournament);
    }


    @Override
    public void onTournamentEditClicked(Tournament tournament) {

        TournamentManagementDialog dialog = new TournamentManagementDialog();

        Bundle bundle = new Bundle();
        bundle.putParcelable(TournamentManagementDialog.BUNDLE_TOURNAMENT, tournament);
        dialog.setArguments(bundle);

        FragmentManager supportFragmentManager = getChildFragmentManager();
        dialog.show(supportFragmentManager, "tournament management edit dialog tournament");
    }


    @Override
    public void onTournamentUploadClicked(Tournament tournament) {
    }


    @Override
    public void onOnlineTournamentListItemClicked(Tournament tournament) {
    }
}
