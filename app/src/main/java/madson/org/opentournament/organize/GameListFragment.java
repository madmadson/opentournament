package madson.org.opentournament.organize;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.warmachine.Game;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND = "round";
    private Tournament tournament;
    private int round;
    private GameListAdapter gameListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        if (bundle != null && bundle.getInt(BUNDLE_ROUND) != 0) {
            round = bundle.getInt(BUNDLE_ROUND);
        }

        View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.game_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        TextView heading = (TextView) view.findViewById(R.id.heading_game_for_round);
        heading.setText(getString(R.string.heading_games_for_round, round));

        gameListAdapter = new GameListAdapter(getActivity());
        recyclerView.setAdapter(gameListAdapter);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                OngoingTournamentService ongoingTournamentService = ((BaseApplication) getActivity().getApplication())
                    .getOngoingTournamentService();

                List<Game> gamesForRound = ongoingTournamentService.getGamesForRound(tournament, round);
                gameListAdapter.setGames(gamesForRound);
            }
        };
        runnable.run();

        Button startTournamentButton = (Button) view.findViewById(R.id.button_start_next_round);
        startTournamentButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    ConfirmPairingNewRoundDialog dialog = new ConfirmPairingNewRoundDialog();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ConfirmPairingNewRoundDialog.BUNDLE_TOURNAMENT, tournament);
                    bundle.putInt(ConfirmPairingNewRoundDialog.BUNDLE_ROUND_TO_DISPLAY, round + 1);
                    dialog.setArguments(bundle);

                    FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                    dialog.show(supportFragmentManager, "confirm  pair next round tournament");
                }
            });

        return view;
    }


    public void updateGameInList(Game game) {

        gameListAdapter.updateGame(game);
    }

    public interface GameResultEnteredListener {

        void onResultConfirmed(Game game);
    }
}
