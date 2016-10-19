package madson.org.opentournament.organize;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.Snackbar;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.tasks.PairNewRoundTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class ConfirmPairRoundAgainDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND_TO_DISPLAY = "round_for_pairing";

    private Tournament tournament;
    private int round_for_pairing;
    private Map<String, PairingOption> pairingOptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    private String getStringResourceByName(String aString) {

        Context context = getContext();
        int resId = context.getResources().getIdentifier(aString, "string", context.getPackageName());

        return getString(resId);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
            round_for_pairing = bundle.getInt(BUNDLE_ROUND_TO_DISPLAY);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm_pairing, null);

        if ((Math.pow(2, tournament.getActualRound())) > tournament.getActualPlayers()) {
            dialogView.findViewById(R.id.confirm_dialog_too_much_rounds).setVisibility(View.VISIBLE);
        }

        LinearLayout container = (LinearLayout) dialogView.findViewById(R.id.pairing_options_container);

        pairingOptions = ((BaseApplication) getActivity().getApplication()).getPairingOptionsForTournament(tournament);

        for (final PairingOption option : pairingOptions.values()) {
            View pairingOption = inflater.inflate(R.layout.view_pairing_option, null);

            TextView pairingOptionText = (TextView) pairingOption.findViewById(R.id.view_pairing_text);

            String pairingOptionName = option.getPairingOptionName();
            pairingOptionText.setText(getStringResourceByName(pairingOptionName));

            final ToggleButton toggleButton = (ToggleButton) pairingOption.findViewById(
                    R.id.view_pairing_toggle_button);
            toggleButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (toggleButton.isChecked()) {
                            option.setActive(true);
                        } else {
                            option.setActive(false);
                        }
                    }
                });

            toggleButton.setChecked(option.isDefaultOption());

            container.addView(pairingOption);
        }

        String title = getString(R.string.confirm_pair_round_again_title, round_for_pairing);

        builder.setView(dialogView)
            .setTitle(title)
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ConfirmPairRoundAgainDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }


    @Override
    public void onStart() {

        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            viewConfirmButton(dialog);
        }
    }


    private void viewConfirmButton(final AlertDialog dialog) {

        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    BaseApplication application = (BaseApplication) getActivity().getApplication();
                    Toolbar toolbar = ((BaseActivity) getActivity()).getToolbar();

                    Snackbar snackbar = Snackbar.make(((BaseActivity) getActivity()).getCoordinatorLayout(),
                            R.string.empty, Snackbar.LENGTH_LONG);

                    ProgressBar progressBar = (ProgressBar) toolbar.findViewById(R.id.toolbar_progress_bar);

                    new PairNewRoundTask(application, tournament, snackbar, progressBar, true, pairingOptions)
                    .execute();

                    dialog.dismiss();
                }
            });
    }
}
