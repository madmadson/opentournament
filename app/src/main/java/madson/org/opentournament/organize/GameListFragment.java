package madson.org.opentournament.organize;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.events.EndSwapPlayerEvent;
import madson.org.opentournament.events.EnterGameResultConfirmed;
import madson.org.opentournament.events.OpenTournamentEvent;
import madson.org.opentournament.events.OpenTournamentEventListener;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.PairRoundAgainEvent;
import madson.org.opentournament.events.PairingChangedEvent;
import madson.org.opentournament.events.SwapPlayerEvent;
import madson.org.opentournament.events.UndoRoundEvent;
import madson.org.opentournament.tasks.LoadGameListTask;
import madson.org.opentournament.tasks.TournamentEndTask;
import madson.org.opentournament.tasks.TournamentUploadTask;
import madson.org.opentournament.tasks.UndoRoundTask;
import madson.org.opentournament.utility.BaseActivity;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class GameListFragment extends Fragment implements OpenTournamentEventListener {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND = "round";
    private Tournament tournament;
    private int round;
    private GameListAdapter gameListAdapter;
    private Button nextRoundButton;
    private Button pairRoundAgainButton;
    private Button uploadGamesButton;
    private Button endTournamentButton;
    private ImageButton toggleActionButton;
    private FrameLayout containerForActions;
    private BaseActivity baseActivity;
    private Button undoRoundButton;
    private TextView noGamesInfo;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        baseActivity = (BaseActivity) getActivity();
        baseActivity.getBaseApplication().registerTournamentEventListener(this);
    }


    @Override
    public void onDetach() {

        super.onDetach();
        baseActivity.getBaseApplication().unregisterTournamentEventListener(this);
    }


    public static GameListFragment newInstance(int roundNumber, Tournament tournament) {

        GameListFragment fragment = new GameListFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ROUND, roundNumber);
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        if (bundle != null && bundle.getInt(BUNDLE_ROUND) != 0) {
            round = bundle.getInt(BUNDLE_ROUND);
        }

        baseActivity = (BaseActivity) getActivity();

        final View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.game_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        TextView heading = (TextView) view.findViewById(R.id.heading_game_for_round);
        heading.setText(getString(R.string.heading_games_for_round, round));

        gameListAdapter = new GameListAdapter((BaseActivity) getActivity(), round, tournament);
        recyclerView.setAdapter(gameListAdapter);

        noGamesInfo = (TextView) view.findViewById(R.id.no_games_info);

        containerForActions = (FrameLayout) view.findViewById(R.id.container_view_toggle_action);

        undoRoundButton = (Button) view.findViewById(R.id.button_undo_round);
        undoRoundButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.confirm_undo_round)
                    .setView(R.layout.dialog_undo_round)
                    .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    new UndoRoundTask(baseActivity, tournament, round).execute();
                                }
                            })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
                }
            });

        pairRoundAgainButton = (Button) view.findViewById(R.id.button_pair_again);
        pairRoundAgainButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!gameListAdapter.atLeastOneGameStarted()) {
                        ConfirmPairRoundAgainDialog dialog = new ConfirmPairRoundAgainDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConfirmPairingNewRoundDialog.BUNDLE_TOURNAMENT, tournament);
                        bundle.putInt(ConfirmPairingNewRoundDialog.BUNDLE_ROUND, round);
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

                    if (baseActivity.isConnected()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.confirm_upload_tournament)
                        .setView(R.layout.dialog_upload_games)
                        .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Toolbar toolbar = baseActivity.getToolbar();
                                        ProgressBar progressBar = (ProgressBar) toolbar.findViewById(
                                                R.id.toolbar_progress_bar);

                                        new TournamentUploadTask(baseActivity, tournament, progressBar).execute();
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
                        bundle.putInt(ConfirmPairingNewRoundDialog.BUNDLE_ROUND, round + 1);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                        dialog.show(supportFragmentManager, "confirm  pair next round");
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.not_all_games_finished_next_round)
                        .setPositiveButton(R.string.dialog_confirm, null)
                        .show();
                    }
                }
            });

        endTournamentButton = (Button) view.findViewById(R.id.button_end_tournament);
        endTournamentButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (gameListAdapter.allGamesAreFinished()) {
                        String message = "";

                        if (tournament.getTournamentTyp().equals(TournamentTyp.SOLO.name())) {
                            if ((Math.pow(2, tournament.getActualRound())) < tournament.getActualPlayers()) {
                                message = baseActivity.getResources().getString(R.string.more_round_recomendation);
                            } else {
                                message = baseActivity.getResources().getString(R.string.end_tournament_text);
                            }
                        } else {
                            int amountTeams = tournament.getActualPlayers() / tournament.getTeamSize();

                            if ((Math.pow(2, tournament.getActualRound())) < amountTeams) {
                                message = baseActivity.getResources().getString(R.string.more_round_recomendation);
                            } else {
                                message = baseActivity.getResources().getString(R.string.end_tournament_text);
                            }
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.end_tournament)
                        .setMessage(message)
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Toolbar toolbar = baseActivity.getToolbar();
                                        ProgressBar progressBar = (ProgressBar) toolbar.findViewById(
                                                R.id.toolbar_progress_bar);
                                        new TournamentEndTask(baseActivity.getBaseApplication(), tournament,
                                            progressBar).execute();
                                    }
                                })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.not_all_games_finished_end_tournament)
                        .setPositiveButton(R.string.dialog_confirm, null)
                        .show();
                    }
                }
            });
        toggleActionButton = (ImageButton) view.findViewById(R.id.button_toggle_action);
        toggleActionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (containerForActions.isShown()) {
                        containerForActions.setVisibility(View.GONE);
                        toggleActionButton.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    } else {
                        containerForActions.setVisibility(View.VISIBLE);
                        toggleActionButton.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    }
                }
            });

        if (round < tournament.getActualRound()) {
            setActionButtonsInvisible();
        }

        new LoadGameListTask(baseActivity.getBaseApplication(), tournament, round, gameListAdapter, noGamesInfo,
            nextRoundButton, endTournamentButton).execute();

        return view;
    }


    @Override
    public void handleEvent(OpenTournamentEventTag eventTag, OpenTournamentEvent parameter) {

        if (eventTag.equals(OpenTournamentEventTag.NEXT_ROUND_PAIRED)) {
            setActionButtonsInvisible();
        } else if (OpenTournamentEventTag.SAVE_GAME_RESULT_CONFIRMED.equals(eventTag)) {
            EnterGameResultConfirmed enterGameResultConfirmed = (EnterGameResultConfirmed) parameter;

            gameListAdapter.updateGame(enterGameResultConfirmed.getEnteredGame());
        } else if (OpenTournamentEventTag.PAIRING_CHANGED.equals(eventTag)) {
            PairingChangedEvent enterGameResultConfirmed = (PairingChangedEvent) parameter;

            gameListAdapter.updateGame(enterGameResultConfirmed.getGameOne());
            gameListAdapter.updateGame(enterGameResultConfirmed.getGameTwo());

            gameListAdapter.endSwapping(enterGameResultConfirmed.getGameOne());
        } else if (OpenTournamentEventTag.PAIR_ROUND_AGAIN.equals(eventTag)) {
            PairRoundAgainEvent pairRoundAgainEvent = (PairRoundAgainEvent) parameter;

            if (pairRoundAgainEvent.getRound() == round) {
                Log.i(this.getClass().getName(),
                    "pair again for: " + pairRoundAgainEvent.getRound() + " round: " + round);
                new LoadGameListTask(baseActivity.getBaseApplication(), tournament, round, gameListAdapter, noGamesInfo,
                    nextRoundButton, endTournamentButton).execute();
            }
        } else if (OpenTournamentEventTag.SWAP_PLAYER.equals(eventTag)) {
            SwapPlayerEvent swapPlayerEvent = (SwapPlayerEvent) parameter;
            Game swappedGame = swapPlayerEvent.getSwappedGame();
            int playerNumber = swapPlayerEvent.getPlayer();

            gameListAdapter.startSwapping(swappedGame, playerNumber);
        } else if (OpenTournamentEventTag.END_SWAP_PLAYER.equals(eventTag)) {
            EndSwapPlayerEvent swapPlayerEvent = (EndSwapPlayerEvent) parameter;
            Game swappedGame = swapPlayerEvent.getGame();

            gameListAdapter.endSwapping(swappedGame);
        } else if (OpenTournamentEventTag.UNDO_ROUND.equals(eventTag)) {
            UndoRoundEvent undoRoundEvent = (UndoRoundEvent) parameter;

            if (undoRoundEvent.getRound() == round) {
                setActionButtonsVisible();
            }
        } else if (OpenTournamentEventTag.UNDO_TOURNAMENT_ENDING.equals(eventTag)) {
            setActionButtonsInvisible();
        }
    }


    private void setActionButtonsVisible() {

        nextRoundButton.setVisibility(View.VISIBLE);
        pairRoundAgainButton.setVisibility(View.VISIBLE);
        endTournamentButton.setVisibility(View.VISIBLE);
        undoRoundButton.setVisibility(View.VISIBLE);
        uploadGamesButton.setVisibility(View.VISIBLE);
    }


    private void setActionButtonsInvisible() {

        nextRoundButton.setVisibility(View.GONE);
        pairRoundAgainButton.setVisibility(View.GONE);
        endTournamentButton.setVisibility(View.GONE);
        undoRoundButton.setVisibility(View.GONE);
        uploadGamesButton.setVisibility(View.GONE);
    }
}
