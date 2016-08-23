package madson.org.opentournament.tournaments;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.CoordinatorLayout;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import madson.org.opentournament.R;
import madson.org.opentournament.players.AvailablePlayerListFragment;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentManagementDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    private EditText tournamentNameEditText;

    private CoordinatorLayout coordinatorLayout;
    private AvailablePlayerListFragment.AvailablePlayerListItemListener mListener;
    private long tournamentId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinator_main);

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(Context activity) {

        super.onAttach(activity);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournamentId = bundle.getLong(BUNDLE_TOURNAMENT_ID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_tournament_management, null);

        tournamentNameEditText = (EditText) dialogView.findViewById(R.id.tournamentName);

        builder.setView(dialogView)
            .setTitle(getString(R.string.new_tournament_title))
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        TournamentManagementDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }


    @Override
    public void onStart() {

        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);

            positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(this.getClass().getName(), "edit tournament");
                    }
                });
        }
    }
}
