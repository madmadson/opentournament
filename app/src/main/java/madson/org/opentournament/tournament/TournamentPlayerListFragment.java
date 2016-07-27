package madson.org.opentournament.tournament;

import android.app.Fragment;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.players.PlayerListAdapter;
import madson.org.opentournament.service.PlayerService;
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

        View view = inflater.inflate(R.layout.fragment_player_list, container, false);

        TournamentService tournamentService = ((OpenTournamentApplication) getActivity().getApplication())
            .getTournamentService();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.player_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Player> players = tournamentService.getPlayersForTournament(tournamentId);

        playerListAdapter = new PlayerListAdapter(players);

        recyclerView.setAdapter(playerListAdapter);

        return view;
    }
}
