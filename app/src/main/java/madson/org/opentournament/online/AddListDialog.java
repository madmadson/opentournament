package madson.org.opentournament.online;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.ExpandableListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.ArmyList;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.BaseApplication;
import madson.org.opentournament.utility.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private ExpandableListView exListView;
    private List<String> listDataHeader;
    private Map<String, ArmyList> listDataChild;

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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_upload_army_lists, null);
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

        AlertDialog alertDialog = builder.create();

        exListView = (ExpandableListView) dialogView.findViewById(R.id.expandable_armylist_list_view);

        prepareListData();

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild,
                tournament, tournamentPlayer);

        exListView.setAdapter(listAdapter);

        return alertDialog;
    }


    /*
     * Preparing the list data
     */
    private void prepareListData() {

        listDataHeader = new ArrayList<>();

        listDataChild = new HashMap<>();

        // Adding child data
        listDataHeader.add("List 1");
        listDataHeader.add("List 2");
        listDataHeader.add("List 3");

        listDataChild.put(listDataHeader.get(0), new ArmyList()); // Header, Child data
        listDataChild.put(listDataHeader.get(1), new ArmyList());
        listDataChild.put(listDataHeader.get(2), new ArmyList());
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

                    dialog.dismiss();
                }
            });
    }
}
