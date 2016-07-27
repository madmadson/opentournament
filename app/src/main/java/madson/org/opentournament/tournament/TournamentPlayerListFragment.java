package madson.org.opentournament.tournament;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.players.PlayerListAdapter;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.TournamentService;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    private Long tournamentId;
    private PlayerListAdapter playerListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getLong(BUNDLE_TOURNAMENT_ID) != 0) {
            tournamentId = bundle.getLong(BUNDLE_TOURNAMENT_ID);
        }

        View view = inflater.inflate(R.layout.fragment_tournament_player_list, container, false);

        OngoingTournamentService ongoingTournamentService = ((OpenTournamentApplication) getActivity()
                .getApplication()).getOngoingTournamentService();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_player_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Player> players = ongoingTournamentService.getPlayersForTournament(tournamentId);

        playerListAdapter = new PlayerListAdapter(players);

        recyclerView.setAdapter(playerListAdapter);

        return view;
    }
}
