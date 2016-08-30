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
import madson.org.opentournament.ongoing.OngoingTournamentActivity;
import madson.org.opentournament.ongoing.RankingListFragment;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;


public class TournamentManagementFragment extends Fragment
    implements TournamentListsFragment.TournamentListItemListener,
        TournamentManagementDialog.TournamentChangedListener {

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
                    dialog.setTargetFragment(TournamentManagementFragment.this, 1);

                    FragmentManager supportFragmentManager = getChildFragmentManager();
                    dialog.show(supportFragmentManager, "tournament management new tournament");
                }
            });

        return view;
    }


    @Override
    public void onTournamentListItemClicked(long id) {

        // get actual tournament to get actual state
        TournamentService tournamentService = ((BaseApplication) getActivity().getApplication()).getTournamentService();
        Tournament tournament = tournamentService.getTournamentForId(id);

        RankingListFragment rankingListFragment = new RankingListFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(RankingListFragment.BUNDLE_TOURNAMENT_ID, id);

        rankingListFragment.setArguments(bundle);

        Log.i(this.getClass().getName(), "clicked on tournament: " + tournament);

        Intent intent = new Intent(getContext(), OngoingTournamentActivity.class);
        intent.putExtra(OngoingTournamentActivity.EXTRA_TOURNAMENT_ID, tournament.get_id());
        startActivity(intent);
    }


    @Override
    public void onOnlineTournamentListItemClicked(String onlineUUID) {

        Log.i(this.getClass().getName(), "clicked on tournament: " + onlineUUID);
    }


    @Override
    public void tournamentChangedEvent(Tournament tournament) {

        tournamentListsFragment.getLocalTournamentListAdapter().replace(tournament);
    }


    @Override
    public void tournamentAddedEvent(Tournament tournament) {

        tournamentListsFragment.getLocalTournamentListAdapter().add(tournament);
    }


    @Override
    public void tournamentDeletedEvent(Tournament tournament) {

        tournamentListsFragment.getLocalTournamentListAdapter().remove(tournament);
    }
}
