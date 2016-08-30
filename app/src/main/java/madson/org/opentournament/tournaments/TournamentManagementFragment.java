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
    implements TournamentListsFragment.TournamentListItemListener, TournamentDetailFragment.OnTournamentEditedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        TournamentListsFragment tournamentListFragment = new TournamentListsFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, tournamentListFragment);
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
        intent.putExtra(OngoingTournamentActivity.EXTRA_TOURNAMENT_ID, tournament.getId());
        startActivity(intent);
    }


    @Override
    public void onOnlineTournamentListItemClicked(String onlineUUID) {

        Log.i(this.getClass().getName(), "clicked on tournament: " + onlineUUID);
    }


    @Override
    public void onTournamentEditedClicked() {

        final View view = getView();
    }
}
