package madson.org.opentournament.organize;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.online.RegisterTournamentPlayerDialog;
import madson.org.opentournament.organize.setup.ShowRegistrationArmyListDialog;
import madson.org.opentournament.tasks.SaveGameResultTask;
import madson.org.opentournament.tasks.SaveTeamGameResultTask;
import madson.org.opentournament.utility.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class EnterResultForGameDialog extends DialogFragment {

    public static final String BUNDLE_GAME = "game";
    public static final String BUNDLE_TOURNAMENT = "tournament";

    private static final Integer MIN_CONTROL_POINTS = 0;
    private static final Integer MAX_CONTROL_POINTS = 5;

    private static final Integer MIN_VICTORY_POINTS = 0;
    private static final Integer MAX_VICTORY_POINTS = 1000;

    private enum WaysOfScoring {

        VICTORY_POINTS,
        CONTROL_POINTS
    }

    private enum IncreaseOrDecrease {

        INCREASE,
        DECREASE
    }

    private Tournament tournament;

    private BaseActivity baseActivity;

    private EditText text_player_two_victory_points;
    private EditText text_player_two_control_points;
    private EditText text_player_one_victory_points;
    private EditText text_player_one_control_points;

    private Game game;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            game = bundle.getParcelable(BUNDLE_GAME);
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_enter_result_game, null);

        builder.setView(dialogView)
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        EnterResultForGameDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }


    @Override
    public void onStart() {

        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            final TextView text_name_player_one = (TextView) dialog.findViewById(R.id.result_player_one_name);
            final TextView text_name_player_two = (TextView) dialog.findViewById(R.id.result_player_two_name);

            final Button button_win_player_one = (Button) dialog.findViewById(R.id.result_player_one_win_button);
            final Button button_win_player_two = (Button) dialog.findViewById(R.id.result_player_two_win_button);

            // ControlPoints Player one
            final ImageButton button_increase_player_one_control_points = (ImageButton) dialog.findViewById(
                    R.id.result_player_one_inc_control_points);
            text_player_one_control_points = (EditText) dialog.findViewById(R.id.result_player_one_control_points);

            final ImageButton button_decrease_player_one_control_points = (ImageButton) dialog.findViewById(
                    R.id.result_player_one_dec_control_points);

            // VictoryPoints Player one
            final ImageButton button_increase_player_one_victory_points = (ImageButton) dialog.findViewById(
                    R.id.result_player_one_inc_victory_points);
            text_player_one_victory_points = (EditText) dialog.findViewById(R.id.result_player_one_victory_points);

            final ImageButton button_decrease_player_one_victory_points = (ImageButton) dialog.findViewById(
                    R.id.result_player_one_dec_victory_points);

            // ControlPoints Player two
            final ImageButton button_increase_player_two_control_points = (ImageButton) dialog.findViewById(
                    R.id.result_player_two_inc_control_points);
            text_player_two_control_points = (EditText) dialog.findViewById(R.id.result_player_two_control_points);

            final ImageButton button_decrease_player_two_control_points = (ImageButton) dialog.findViewById(
                    R.id.result_player_two_dec_control_points);

            // VictoryPoints Player two
            final ImageButton button_increase_player_two_victory_points = (ImageButton) dialog.findViewById(
                    R.id.result_player_two_inc_victory_points);
            text_player_two_victory_points = (EditText) dialog.findViewById(R.id.result_player_two_victory_points);

            final ImageButton button_decrease_player_two_victory_points = (ImageButton) dialog.findViewById(
                    R.id.result_player_two_dec_victory_points);

            final TournamentPlayer player1 = (TournamentPlayer) game.getParticipantOne();
            final TournamentPlayer player2 = (TournamentPlayer) game.getParticipantTwo();

            text_name_player_one.setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row_three_lines, player1.getFirstNameWithMaximumCharacters(10),
                    player1.getNickNameWithMaximumCharacters(10), player1.getLastNameWithMaximumCharacters(10)));

            text_name_player_two.setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row_three_lines, player2.getFirstNameWithMaximumCharacters(10),
                    player2.getNickNameWithMaximumCharacters(10), player2.getLastNameWithMaximumCharacters(10)));

            if (game.getParticipant_one_score() == 1) {
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorWin));
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorLoose));
            }

            if (game.getParticipant_two_score() == 1) {
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorWin));
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorLoose));
            }

            text_player_one_control_points.setText(String.valueOf(game.getParticipant_one_control_points()));
            text_player_one_victory_points.setText(String.valueOf(game.getParticipant_one_victory_points()));

            text_player_two_control_points.setText(String.valueOf(game.getParticipant_two_control_points()));
            text_player_two_victory_points.setText(String.valueOf(game.getParticipant_two_victory_points()));

            addClickForPlayerOneWinListener(text_name_player_one, text_name_player_two, button_win_player_one);
            addPlayerTwoClickWonListener(text_name_player_one, text_name_player_two, button_win_player_two);

            // PlayerOne ControlPoints
            addPointsForPlayerClickListener(1, button_increase_player_one_control_points,
                text_player_one_control_points, WaysOfScoring.CONTROL_POINTS, IncreaseOrDecrease.INCREASE);
            addPointsForPlayerClickListener(1, button_decrease_player_one_control_points,
                text_player_one_control_points, WaysOfScoring.CONTROL_POINTS, IncreaseOrDecrease.DECREASE);

            // PlayerOne VictoryPoints
            addPointsForPlayerClickListener(1, button_increase_player_one_victory_points,
                text_player_one_victory_points, WaysOfScoring.VICTORY_POINTS, IncreaseOrDecrease.INCREASE);
            addPointsOnLongPressed(1, button_increase_player_one_victory_points, text_player_one_victory_points,
                WaysOfScoring.VICTORY_POINTS, IncreaseOrDecrease.INCREASE);
            addPointsForPlayerClickListener(1, button_decrease_player_one_victory_points,
                text_player_one_victory_points, WaysOfScoring.VICTORY_POINTS, IncreaseOrDecrease.DECREASE);
            addPointsOnLongPressed(1, button_decrease_player_one_victory_points, text_player_one_victory_points,
                WaysOfScoring.VICTORY_POINTS, IncreaseOrDecrease.DECREASE);

            // PlayerTwo ControlPoints
            addPointsForPlayerClickListener(2, button_increase_player_two_control_points,
                text_player_two_control_points, WaysOfScoring.CONTROL_POINTS, IncreaseOrDecrease.INCREASE);
            addPointsForPlayerClickListener(2, button_decrease_player_two_control_points,
                text_player_two_control_points, WaysOfScoring.CONTROL_POINTS, IncreaseOrDecrease.DECREASE);

            // PlayerTwo VictoryPoints
            addPointsForPlayerClickListener(2, button_increase_player_two_victory_points,
                text_player_two_victory_points, WaysOfScoring.VICTORY_POINTS, IncreaseOrDecrease.INCREASE);
            addPointsOnLongPressed(2, button_increase_player_two_victory_points, text_player_two_victory_points,
                WaysOfScoring.VICTORY_POINTS, IncreaseOrDecrease.INCREASE);
            addPointsForPlayerClickListener(2, button_decrease_player_two_victory_points,
                text_player_two_victory_points, WaysOfScoring.VICTORY_POINTS, IncreaseOrDecrease.DECREASE);
            addPointsOnLongPressed(2, button_decrease_player_two_victory_points, text_player_two_victory_points,
                WaysOfScoring.VICTORY_POINTS, IncreaseOrDecrease.DECREASE);

            viewConfirmButton(dialog);

            ImageView player_one_playedList = (ImageView) dialog.findViewById(R.id.player_one_list);
            player_one_playedList.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(this.getClass().getName(), "addList");

                        SelectPlayedArmyListDialog dialog = new SelectPlayedArmyListDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(SelectPlayedArmyListDialog.BUNDLE_TOURNAMENT, tournament);
                        bundle.putParcelable(SelectPlayedArmyListDialog.BUNDLE_TOURNAMENT_PLAYER, player1);
                        bundle.putParcelable(SelectPlayedArmyListDialog.BUNDLE_GAME, game);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();

                        dialog.show(supportFragmentManager, "tournament setup new player");
                    }
                });

            ImageView player_two_playedList = (ImageView) dialog.findViewById(R.id.player_two_list);
            player_two_playedList.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(this.getClass().getName(), "addList");

                        SelectPlayedArmyListDialog dialog = new SelectPlayedArmyListDialog();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(SelectPlayedArmyListDialog.BUNDLE_TOURNAMENT, tournament);
                        bundle.putParcelable(SelectPlayedArmyListDialog.BUNDLE_TOURNAMENT_PLAYER, player2);
                        bundle.putParcelable(SelectPlayedArmyListDialog.BUNDLE_GAME, game);
                        dialog.setArguments(bundle);

                        FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();

                        dialog.show(supportFragmentManager, "tournament setup new player");
                    }
                });
        }
    }


    private void addPointsForPlayerClickListener(final int player_number, ImageButton button,
        final EditText text_player_one_control_points, final WaysOfScoring controlOrVictory,
        final IncreaseOrDecrease increaseOrDecrease) {

        button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (increaseOrDecrease.equals(IncreaseOrDecrease.INCREASE)) {
                        increasePointsForPlayer(player_number, text_player_one_control_points, controlOrVictory, 1);
                    } else if (increaseOrDecrease.equals(IncreaseOrDecrease.DECREASE)) {
                        decreasePointsForPlayer(player_number, text_player_one_control_points, controlOrVictory, 1);
                    }
                }
            });
    }


    private void addPointsOnLongPressed(final int player_number, final ImageButton button,
        final EditText edit_text_player_points, final WaysOfScoring controlOrVictory,
        final IncreaseOrDecrease increaseOrDecrease) {

        button.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {

                            @Override
                            public void run() {

                                if (button.isPressed()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                            @Override
                                            public void run() {

                                                if (increaseOrDecrease.equals(IncreaseOrDecrease.INCREASE)) {
                                                    increasePointsForPlayer(player_number, edit_text_player_points,
                                                        controlOrVictory, 10);
                                                } else if (increaseOrDecrease.equals(IncreaseOrDecrease.DECREASE)) {
                                                    decreasePointsForPlayer(player_number, edit_text_player_points,
                                                        controlOrVictory, 10);
                                                }
                                            }
                                        });
                                } else {
                                    timer.cancel();
                                }
                            }
                        }, 100, 200);

                    return false;
                }
            });
    }


    private void increasePointsForPlayer(int player_number, EditText editText_points, WaysOfScoring scoring,
        int amount) {

        if (WaysOfScoring.CONTROL_POINTS.equals(scoring)) {
            Integer controlPoints = Integer.valueOf(editText_points.getText().toString());
            int increasedControlPoints = controlPoints + amount;

            if (increasedControlPoints <= MAX_CONTROL_POINTS) {
                editText_points.setText(String.valueOf(increasedControlPoints));

                if (player_number == 1) {
                    game.setParticipant_one_control_points(increasedControlPoints);
                } else if (player_number == 2) {
                    game.setParticipant_two_control_points(increasedControlPoints);
                }
            }
        } else if (WaysOfScoring.VICTORY_POINTS.equals(scoring)) {
            Integer victoryPoints = Integer.valueOf(editText_points.getText().toString());
            int increasedVictoryPoints = victoryPoints + amount;

            if (increasedVictoryPoints <= MAX_VICTORY_POINTS) {
                editText_points.setText(String.valueOf(increasedVictoryPoints));

                if (player_number == 1) {
                    game.setParticipant_one_victory_points(increasedVictoryPoints);
                } else if (player_number == 2) {
                    game.setParticipant_two_victory_points(increasedVictoryPoints);
                }
            }
        }
    }


    private void decreasePointsForPlayer(int player_number, EditText editText_points, WaysOfScoring scoring,
        int amount) {

        if (WaysOfScoring.CONTROL_POINTS.equals(scoring)) {
            Integer controlPoints = Integer.valueOf(editText_points.getText().toString());
            int decreasedControlPoints = controlPoints - amount;

            if (decreasedControlPoints >= MIN_CONTROL_POINTS) {
                editText_points.setText(String.valueOf(decreasedControlPoints));

                if (player_number == 1) {
                    game.setParticipant_one_control_points(decreasedControlPoints);
                } else if (player_number == 2) {
                    game.setParticipant_two_control_points(decreasedControlPoints);
                }
            }
        } else if (WaysOfScoring.VICTORY_POINTS.equals(scoring)) {
            Integer victoryPoints = Integer.valueOf(editText_points.getText().toString());
            int decreasedVictoryPoints = victoryPoints - amount;

            if (decreasedVictoryPoints >= MIN_VICTORY_POINTS) {
                editText_points.setText(String.valueOf(decreasedVictoryPoints));

                if (player_number == 1) {
                    game.setParticipant_one_victory_points(decreasedVictoryPoints);
                } else if (player_number == 2) {
                    game.setParticipant_two_victory_points(decreasedVictoryPoints);
                }
            }
        }
    }


    private void addPlayerTwoClickWonListener(final TextView text_name_player_one, final TextView text_name_player_two,
        Button button_win_player_two) {

        button_win_player_two.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    setPlayerToWinner(2, text_name_player_one, text_name_player_two);
                }
            });
    }


    private void addClickForPlayerOneWinListener(final TextView text_name_player_one,
        final TextView text_name_player_two, Button button_win_player_one) {

        button_win_player_one.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    setPlayerToWinner(1, text_name_player_one, text_name_player_two);
                }
            });
    }


    private void setPlayerToWinner(int player_number, final TextView text_name_player_one,
        final TextView text_name_player_two) {

        if (player_number == 1) {
            if (game.getParticipant_one_score() == 1) {
                game.setParticipant_one_score(0);
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorLoose));

                return;
            }

            game.setParticipant_one_score(1);
            game.setParticipant_two_score(0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorWin, null));
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorLoose, null));
            } else {
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorWin));
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorLoose));
            }
        } else if (player_number == 2) {
            if (game.getParticipant_two_score() == 1) {
                game.setParticipant_two_score(0);
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorLoose));

                return;
            }

            game.setParticipant_two_score(1);
            game.setParticipant_one_score(0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorLoose, null));
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorWin, null));
            } else {
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorLoose));
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorWin));
            }
        }
    }


    private void viewConfirmButton(final AlertDialog confirm_dialog) {

        Button positive = confirm_dialog.getButton(Dialog.BUTTON_POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "click confirm result of game");

                    if (game.getParticipant_one_score() == 0 && game.getParticipant_two_score() == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);

                        builder.setTitle(R.string.dialof_confirm_draw)
                        .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface sure_draw_dialog, int id) {

                                        saveGameResult(confirm_dialog, sure_draw_dialog);
                                    }
                                })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface sure_draw_dialog, int id) {

                                    sure_draw_dialog.cancel();
                                }
                            });
                        builder.show();
                    } else {
                        saveGameResult(confirm_dialog, null);
                    }
                }
            });
    }


    private void saveGameResult(AlertDialog confirm_dialog, DialogInterface sure_draw_dialog) {

        game.setParticipant_one_control_points(Integer.parseInt(text_player_one_control_points.getText().toString()));
        game.setParticipant_one_victory_points(Integer.parseInt(text_player_one_victory_points.getText().toString()));

        game.setParticipant_two_control_points(Integer.parseInt(text_player_two_control_points.getText().toString()));
        game.setParticipant_two_victory_points(Integer.parseInt(text_player_two_victory_points.getText().toString()));

        if (game.getParent_UUID() == null) {
            new SaveGameResultTask(baseActivity, game, confirm_dialog, sure_draw_dialog).execute();
        } else {
            new SaveTeamGameResultTask(baseActivity, game, confirm_dialog, sure_draw_dialog, tournament).execute();
        }
    }
}
