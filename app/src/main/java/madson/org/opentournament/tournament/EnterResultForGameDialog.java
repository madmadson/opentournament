package madson.org.opentournament.tournament;

import android.app.Dialog;

import android.content.DialogInterface;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentGame;
import madson.org.opentournament.service.OngoingTournamentService;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class EnterResultForGameDialog extends DialogFragment {

    public static final String BUNDLE_PAIRING_ID = "pairing_id";
    private static final Integer MIN_CONTROL_POINTS = 0;
    private static final Integer MAX_CONTROL_POINTS = 5;

    private static final Integer MIN_VICTORY_POINTS = 0;
    private static final Integer MAX_VICTORY_POINTS = 1000;
    private long pairing_id;

    private WarmachineTournamentGame game;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            pairing_id = bundle.getLong(BUNDLE_PAIRING_ID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        OngoingTournamentService ongoingTournamentService = ((OpenTournamentApplication) getActivity()
                .getApplication()).getOngoingTournamentService();
        game = ongoingTournamentService.getPairingForTournament(pairing_id);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_enter_result_pairing, null);

        builder.setView(dialogView)
            .setPositiveButton(R.string.confirm, null)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

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

            final ImageButton button_increase_player_one = (ImageButton) dialog.findViewById(
                    R.id.result_player_one__inc_control_points);

            final EditText text_player_one_control_points = (EditText) dialog.findViewById(
                    R.id.result_player_one_control_points);

            final ImageButton button_decrease_player_one = (ImageButton) dialog.findViewById(
                    R.id.result_player_one__dec_control_points);

            text_name_player_one.setText(game.getPlayer_one_full_name());
            text_name_player_two.setText(game.getPlayer_two_full_name());

            if (game.getPlayer_one_score() == 1) {
                setPlayerToWinner(1, text_name_player_one, text_name_player_two);
            }

            if (game.getPlayer_two_score() == 1) {
                setPlayerToWinner(2, text_name_player_one, text_name_player_two);
            }

            text_player_one_control_points.setText(String.valueOf(game.getPlayer_one_control_points()));

            addClickForPlayerOneWinListener(text_name_player_one, text_name_player_two, button_win_player_one);
            addPlayerTwoClickWonListener(text_name_player_one, text_name_player_two, button_win_player_two);

            addIncreaseControlPointsForPlayerOneClickListener(button_increase_player_one,
                text_player_one_control_points);

            addIncreaseControlPointsOnLongPressed(button_increase_player_one, text_player_one_control_points);

            addDecreaseControlPointsForPlayerOneClickListener(button_decrease_player_one,
                text_player_one_control_points);

            addDecreaseControlPointsOnLongPressed(button_decrease_player_one, text_player_one_control_points);

            viewConfirmButton(dialog);
        }
    }


    private void addDecreaseControlPointsForPlayerOneClickListener(ImageButton button_decrease_player_one,
        final EditText text_player_one_control_points) {

        button_decrease_player_one.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    decreaseControlPointsForPlayer(1, text_player_one_control_points);
                }
            });
    }


    private void addIncreaseControlPointsForPlayerOneClickListener(ImageButton button_increase_player_one,
        final EditText text_player_one_control_points) {

        button_increase_player_one.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    increaseControlPointsForPlayer(1, text_player_one_control_points);
                }
            });
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
            game.setPlayer_one_score(1);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorWin, null));
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorLoose, null));
            } else {
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorWin));
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorLoose));
            }
        } else if (player_number == 2) {
            game.setPlayer_two_score(1);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorLoose, null));
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorWin, null));
            } else {
                text_name_player_one.setTextColor(getResources().getColor(R.color.colorLoose));
                text_name_player_two.setTextColor(getResources().getColor(R.color.colorWin));
            }
        }
    }


    private void addIncreaseControlPointsOnLongPressed(final ImageButton button_increase_player_one,
        final EditText text_player_one_control_points) {

        button_increase_player_one.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {

                            @Override
                            public void run() {

                                if (button_increase_player_one.isPressed()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                            @Override
                                            public void run() {

                                                increaseControlPointsForPlayer(1, text_player_one_control_points);
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


    private void increaseControlPointsForPlayer(int player_number, EditText editText_control_points) {

        Integer controlPoints = Integer.valueOf(editText_control_points.getText().toString());

        if (controlPoints < MAX_CONTROL_POINTS) {
            int increasedControlPoints = controlPoints + 1;

            editText_control_points.setText(String.valueOf(increasedControlPoints));

            if (player_number == 1) {
                game.setPlayer_one_control_points(increasedControlPoints);
            } else if (player_number == 2) {
                game.setPlayer_two_control_points(increasedControlPoints);
            }
        }
    }


    private void decreaseControlPointsForPlayer(int player_number, EditText editText_control_points) {

        Integer controlPoints = Integer.valueOf(editText_control_points.getText().toString());

        if (controlPoints > MIN_CONTROL_POINTS) {
            int decreasedControlPoints = controlPoints - 1;

            editText_control_points.setText(String.valueOf(decreasedControlPoints));

            if (player_number == 1) {
                game.setPlayer_one_control_points(decreasedControlPoints);
            } else if (player_number == 2) {
                game.setPlayer_two_control_points(decreasedControlPoints);
            }
        }
    }


    private void addDecreaseControlPointsOnLongPressed(final ImageButton button_decrease_player_one,
        final EditText text_player_one_control_points) {

        button_decrease_player_one.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {

                            @Override
                            public void run() {

                                if (button_decrease_player_one.isPressed()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                            @Override
                                            public void run() {

                                                decreaseControlPointsForPlayer(1, text_player_one_control_points);
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


    private void viewConfirmButton(final AlertDialog dialog) {

        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "click confirm result of game");

                    OngoingTournamentActivity activity = (OngoingTournamentActivity) getActivity();

                    OngoingTournamentService ongoingTournamentService =
                        ((OpenTournamentApplication) getActivity().getApplication()).getOngoingTournamentService();

                    ongoingTournamentService.saveGameResult(game);

                    dialog.dismiss();
                }
            });
    }
}
