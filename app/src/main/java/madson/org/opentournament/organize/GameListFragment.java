package madson.org.opentournament.organize;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.tasks.TournamentUploadTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameListFragment extends Fragment implements TournamentEventListener {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND = "round";
    private Tournament tournament;
    private int round;
    private GameListAdapter gameListAdapter;
    private Button nextRoundButton;
    private Button pairRoundAgainButton;
    private Button uploadGamesButton;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        ((BaseActivity) getActivity()).getBaseApplication().registerTournamentEventListener(this);
    }


    @Override
    public void onDetach() {

        super.onDetach();
        ((BaseActivity) getActivity()).getBaseApplication().unregisterTournamentEventListener(this);
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

        final View view = inflater.inflate(R.layout.fragment_game_list, container, false);

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

        pairRoundAgainButton = (Button) view.findViewById(R.id.button_pair_again);
        pairRoundAgainButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!gameListAdapter.atLeastOneGameStarted()) {
                        ConfirmPairRoundAgainDialog dialog = new ConfirmPairRoundAgainDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConfirmPairingNewRoundDialog.BUNDLE_TOURNAMENT, tournament);
                        bundle.putInt(ConfirmPairingNewRoundDialog.BUNDLE_ROUND_TO_DISPLAY, round);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                        dialog.show(supportFragmentManager, "confirm  pair round again");
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.at_least_one_game_started)
                        .setPositiveButton(R.string.dialog_confirm, null)
                        .show();
                    }
                }
            });

        uploadGamesButton = (Button) view.findViewById(R.id.button_upload_games);
        uploadGamesButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (((BaseActivity) getActivity()).isConnected()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.confirm_upload_tournament)
                        .setView(R.layout.dialog_upload_games)
                        .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Toolbar toolbar = ((BaseActivity) getActivity()).getToolbar();
                                        ProgressBar progressBar = (ProgressBar) toolbar.findViewById(
                                                R.id.toolbar_progress_bar);

                                        new TournamentUploadTask((BaseApplication) getActivity().getApplication(),
                                            tournament, progressBar).execute();
                                    }
                                })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.offline_text).setPositiveButton(R.string.dialog_confirm, null).show();
                    }
                }
            });

        nextRoundButton = (Button) view.findViewById(R.id.button_start_next_round);
        nextRoundButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (gameListAdapter.allGamesAreFinished()) {
                        ConfirmPairingNewRoundDialog dialog = new ConfirmPairingNewRoundDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConfirmPairingNewRoundDialog.BUNDLE_TOURNAMENT, tournament);
                        bundle.putInt(ConfirmPairingNewRoundDialog.BUNDLE_ROUND_TO_DISPLAY, round + 1);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                        dialog.show(supportFragmentManager, "confirm  pair next round");
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.not_all_games_fineshed)
                        .setPositiveButton(R.string.dialog_confirm, null)
                        .show();
                    }
                }
            });

        return view;
    }


    public void updateGameInList(Game game) {

        gameListAdapter.updateGame(game);
    }


    @Override
    public void startRound(int roundToStart) {

        if (roundToStart == round + 1) {
            nextRoundButton.setVisibility(View.GONE);
            pairRoundAgainButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void pairRoundAgain(int round_for_pairing) {

        // nothing

    }

    public interface GameResultEnteredListener {

        void onResultConfirmed(Game game);
    }
}
