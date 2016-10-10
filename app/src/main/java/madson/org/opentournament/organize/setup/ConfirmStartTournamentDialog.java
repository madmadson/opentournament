package madson.org.opentournament.organize.setup;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.PairingOption;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.TournamentOrganizeActivity;
import madson.org.opentournament.tasks.LoadTournamentTask;
import madson.org.opentournament.tasks.PairNewRoundTask;
import madson.org.opentournament.tasks.StartTournamentTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class ConfirmStartTournamentDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";

    private Tournament tournament;
    private Map<String, PairingOption> pairingOptions;
    private BaseApplication baseApplication;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        baseApplication = (BaseApplication) getActivity().getApplication();
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
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm_start_tournament, null);

        new LoadTournamentTask(baseApplication, dialogView, tournament).execute();

        LinearLayout container = (LinearLayout) dialogView.findViewById(R.id.pairing_options_container);

        pairingOptions = baseApplication.getPairingOptionsForTournament(tournament);

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

            toggleButton.setChecked(option.isDefaultPairing());

            container.addView(pairingOption);
        }

        String title = getString(R.string.confirm_start_tournament_title);

        builder.setView(dialogView)
            .setTitle(title)
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ConfirmStartTournamentDialog.this.getDialog().cancel();
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

                    new StartTournamentTask((BaseActivity) getActivity(), tournament, pairingOptions).execute();

                    dialog.dismiss();
                }
            });
    }
}
