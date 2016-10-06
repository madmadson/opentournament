package madson.org.opentournament.tournaments;

import android.content.Context;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.tasks.LoadOrganizedTournamentsTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.Environment;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public class OrganizedTournamentList extends Fragment implements OrganizeTournamentEventListener {

    private OrganizedTournamentListAdapter tournamentListAdapter;
    private BaseActivity baseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        if (baseActivity.getBaseApplication().getEnvironment() != Environment.PROD) {
            baseActivity.getToolbar().setTitle(R.string.toolbar_title_organized_tournaments_DEMO);
        } else {
            baseActivity.getToolbar().setTitle(R.string.toolbar_title_organized_tournaments);
        }

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();

        floatingActionButton.setVisibility(View.VISIBLE);

        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "click floatingActionButton tournament management");

                    OrganizedTournamentEditDialog dialog = new OrganizedTournamentEditDialog();

                    FragmentManager supportFragmentManager = getChildFragmentManager();
                    dialog.show(supportFragmentManager, "tournament management new tournament");
                }
            });
        new LoadOrganizedTournamentsTask(baseActivity.getBaseApplication(), progressBar, tournamentListAdapter)
            .execute();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_organized_tournament_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.organized_tournament_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(baseActivity.getApplication());
        recyclerView.setLayoutManager(linearLayoutManager);
        tournamentListAdapter = new OrganizedTournamentListAdapter(baseActivity);

        recyclerView.setAdapter(tournamentListAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
        baseActivity.getBaseApplication().registerOrganizeTournamentListener(this);
    }


    @Override
    public void onDetach() {

        super.onDetach();
        baseActivity.getBaseApplication().unregisterOrganizeTournamentListener(this);
    }


    @Override
    public void onTournamentChangedEvent(Tournament tournament) {

        tournamentListAdapter.replaceTournament(tournament);
    }


    @Override
    public void onTournamentAddedEvent(Tournament tournament) {

        tournamentListAdapter.addTournament(tournament);
    }


    @Override
    public void onTournamentDeletedEvent(Tournament tournament) {

        tournamentListAdapter.removeTournament(tournament);
    }
}
