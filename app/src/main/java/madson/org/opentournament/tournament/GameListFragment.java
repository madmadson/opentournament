package madson.org.opentournament.tournament;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentGame;
import madson.org.opentournament.service.OngoingTournamentService;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    public static final String BUNDLE_ROUND = "round";
    private Long tournamentId;
    private int round;
    private GameListAdapter gameListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getLong(BUNDLE_TOURNAMENT_ID) != 0) {
            tournamentId = bundle.getLong(BUNDLE_TOURNAMENT_ID);
        }

        if (bundle != null && bundle.getInt(BUNDLE_ROUND) != 0) {
            round = bundle.getInt(BUNDLE_ROUND);
        }

        View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        OngoingTournamentService ongoingTournamentService = ((OpenTournamentApplication) getActivity()
                .getApplication()).getOngoingTournamentService();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.game_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<WarmachineTournamentGame> pairingsForTournament = ongoingTournamentService.getPairingsForTournament(
                tournamentId, round);

        TextView heading = (TextView) view.findViewById(R.id.heading_game_for_round);
        heading.setText(getString(R.string.heading_pairing_for_round, round));

        gameListAdapter = new GameListAdapter(pairingsForTournament);

        recyclerView.setAdapter(gameListAdapter);

        return view;
    }


    public void updateGameInList(WarmachineTournamentGame game) {

        gameListAdapter.updateGame(game);
    }

    public interface GameResultEnteredListener {

        void onResultConfirmed(WarmachineTournamentGame game);
    }
}
