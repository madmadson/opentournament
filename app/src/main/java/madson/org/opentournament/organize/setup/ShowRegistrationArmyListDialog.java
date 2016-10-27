package madson.org.opentournament.organize.setup;

import android.app.Dialog;

import android.os.Bundle;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.ExpandableListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.ArmyList;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.online.ArmyListExpandableListAdapter;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class ShowRegistrationArmyListDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_TOURNAMENT_PLAYER = "tournament_player";

    private Tournament tournament;

    private TournamentPlayer tournamentPlayer;

    private ExpandableListView exListView;
    private List<String> listDataHeader;
    private Map<String, ArmyList> listDataChild;

    private ArmyListExpandableListAdapter listAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
            tournamentPlayer = bundle.getParcelable(BUNDLE_TOURNAMENT_PLAYER);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_show_army_lists, null);
        String title = getString(R.string.show_army_lists);

        builder.setView(dialogView).setTitle(title).setPositiveButton(R.string.dialog_confirm, null);

        AlertDialog alertDialog = builder.create();

        exListView = (ExpandableListView) dialogView.findViewById(R.id.expandable_armylist_list_view);

        listDataHeader = new ArrayList<>();

        listDataChild = new HashMap<>();

        listAdapter = new ArmyListExpandableListAdapter((BaseActivity) getActivity(), listDataHeader, listDataChild,
                tournament, tournamentPlayer, null, null, null);

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(FirebaseReferences.TOURNAMENT_ARMY_LISTS + "/" + tournament.getGameOrSportTyp() + "/"
                    + tournament.getUuid() + "/" + tournamentPlayer.getPlayerUUID());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot armyLists : dataSnapshot.getChildren()) {
                        ArmyList armyList = armyLists.getValue(ArmyList.class);

                        if (armyList != null) {
                            listDataHeader.add(armyList.getName());
                            listDataChild.put(listDataHeader.get(listAdapter.getGroupCount() - 1), armyList);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        exListView.setAdapter(listAdapter);

        return alertDialog;
    }
}
