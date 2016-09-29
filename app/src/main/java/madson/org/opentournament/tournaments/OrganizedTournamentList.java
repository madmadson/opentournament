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
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.tasks.LoadOrganizedTournamentsTask;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public class OrganizedTournamentList extends Fragment implements OrganizeTournamentEventListener {

    private ProgressBar progressBar;
    private TextView noPlayersTextView;

    private OrganizedTournamentListAdapter tournamentListAdapter;
    private BaseActivity baseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        baseActivity = (BaseActivity) getActivity();

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
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
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ((BaseActivity) getActivity()).getToolbar().setTitle(R.string.title_organized_tournaments);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_organized_tournament_list, container, false);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.organized_tournament_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(baseActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        tournamentListAdapter = new OrganizedTournamentListAdapter(baseActivity);

        recyclerView.setAdapter(tournamentListAdapter);
        new LoadOrganizedTournamentsTask(baseActivity.getBaseApplication(), progressBar, tournamentListAdapter)
            .execute();

        return view;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        ((BaseActivity) getActivity()).getBaseApplication().registerOrganizeTournamentListener(this);
    }


    @Override
    public void onDetach() {

        super.onDetach();
        ((BaseActivity) getActivity()).getBaseApplication().unregisterOrganizeTournamentListener(this);
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
