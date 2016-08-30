package madson.org.opentournament.ongoing;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament_id";
    private Tournament tournament;
    private WarmachineTournamentPlayerListAdapter tournamentPlayerListAdapter;

    private TextView heading;

    public static TournamentPlayerListFragment newInstance(Tournament tournament) {

        TournamentPlayerListFragment fragment = new TournamentPlayerListFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        View view = inflater.inflate(R.layout.fragment_tournament_player_list, container, false);

        OngoingTournamentService ongoingTournamentService = ((BaseApplication) getActivity().getApplication())
            .getOngoingTournamentService();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_player_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<TournamentPlayer> players = ongoingTournamentService.getRankingForRound(tournament.get_id(), 0);

        heading = (TextView) view.findViewById(R.id.heading_tournament_players);
        heading.setText(getString(R.string.heading_tournament_player, players.size()));

        tournamentPlayerListAdapter = new WarmachineTournamentPlayerListAdapter(players, null);

        recyclerView.setAdapter(tournamentPlayerListAdapter);

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "click floatingActionButton tournament player");
                }
            });

        return view;
    }


    public void addPlayer(final Player player) {

        Log.i(this.getClass().getName(), "add player to tournament player list: " + player);

        if (tournamentPlayerListAdapter != null) {
            tournamentPlayerListAdapter.add(player);

            heading.setText(getString(R.string.heading_tournament_player, tournamentPlayerListAdapter.getItemCount()));

            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    Log.i(this.getClass().getName(), "add player to tournament ");

                    BaseApplication application = (BaseApplication) getActivity().getApplication();
                    application.getOngoingTournamentService().addPlayerToTournament(player, tournament.get_id());
                }
            };
            runnable.run();
        }
    }


    public void removePlayer() {

        heading.setText(getString(R.string.heading_tournament_player, tournamentPlayerListAdapter.getItemCount()));
    }

    public interface TournamentPlayerListItemListener {

        void onTournamentPlayerListItemClicked(long player_id);
    }
}
