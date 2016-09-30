package madson.org.opentournament.online;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class AddListDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_TOURNAMENT_PLAYER = "tournament_player";

    private Tournament tournament;

    private BaseApplication baseApplication;
    private TournamentPlayer tournamentPlayer;
    private EditText listsinput;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseApplication = (BaseApplication) getActivity().getApplication();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
            tournamentPlayer = bundle.getParcelable(BUNDLE_TOURNAMENT_PLAYER);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_upload_lists, null);

        listsinput = (EditText) dialogView.findViewById(R.id.lists_input);

        if (tournamentPlayer.getTournamentPlayerLists() != null) {
            listsinput.setText(tournamentPlayer.getTournamentPlayerLists());
        }

        String title = getString(R.string.upload_lists);

        builder.setView(dialogView)
            .setTitle(title)
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        AddListDialog.this.getDialog().cancel();
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

                    String s = listsinput.getText().toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference(
                            FirebaseReferences.TOURNAMENT_REGISTRATIONS + "/" + tournament.getOnlineUUID() + "/"
                            + tournamentPlayer.getOnline_uuid() + "/tournamentPlayerLists");

                    reference.setValue(s);

                    dialog.dismiss();
                }
            });
    }
}
