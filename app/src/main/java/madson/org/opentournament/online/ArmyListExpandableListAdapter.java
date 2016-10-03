package madson.org.opentournament.online;

import android.content.Context;

import android.support.design.widget.Snackbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.ArmyList;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.utility.BaseActivity;

import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */

public class ArmyListExpandableListAdapter extends BaseExpandableListAdapter {

    private final BaseActivity baseActivity;
    private final List<String> listDataHeader;
    private final Map<String, ArmyList> listDataChild;
    private final Tournament tournament;
    private final TournamentPlayer tournamentPlayer;
    private ImageButton addListButton;

    public ArmyListExpandableListAdapter(BaseActivity baseActivity, List<String> listDataHeader,
        Map<String, ArmyList> listChildData, Tournament tournament, TournamentPlayer tournamentPlayer,
        ImageButton addListButton) {

        this.baseActivity = baseActivity;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.tournament = tournament;
        this.tournamentPlayer = tournamentPlayer;
        this.addListButton = addListButton;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return this.listDataChild.get(this.listDataHeader.get(groupPosition));
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }


    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
        ViewGroup parent) {

        final ArmyList armyList = (ArmyList) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.baseActivity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dialog_army_lists_list_item, null);
        }

        final EditText listName = (EditText) convertView.findViewById(R.id.list_name);
        final EditText list = (EditText) convertView.findViewById(R.id.list);
        Button uploadButton = (Button) convertView.findViewById(R.id.upload_list_button);

        if (addListButton != null) {
            uploadButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        armyList.setList(list.getText().toString());
                        armyList.setName(listName.getText().toString());

                        int listPosition = groupPosition + 1;
                        DatabaseReference addListReference = FirebaseDatabase.getInstance()
                            .getReference(
                                FirebaseReferences.TOURNAMENT_ARMY_LISTS + "/" + tournament.getOnlineUUID()
                                + "/" + tournamentPlayer.getPlayerOnlineUUID() + "/" + listPosition);

                        addListReference.setValue(armyList);

                        addListButton.setVisibility(View.VISIBLE);

                        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                R.string.success_upload_list, Snackbar.LENGTH_LONG);
                        snackbar.getView()
                        .setBackgroundColor(baseActivity.getResources().getColor(R.color.colorPositive));
                        snackbar.show();
                    }
                });
        } else {
            uploadButton.setVisibility(View.GONE);
            listName.setEnabled(false);
            list.setEnabled(false);
        }

        listName.setText(armyList.getName());
        list.setText(armyList.getList());

        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {

        return 1;
    }


    @Override
    public Object getGroup(int groupPosition) {

        return this.listDataHeader.get(groupPosition);
    }


    @Override
    public int getGroupCount() {

        return this.listDataHeader.size();
    }


    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.baseActivity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.dialog_army_lists_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.expandable_armylist_list_header);

        lblListHeader.setText(headerTitle);

        return convertView;
    }


    @Override
    public boolean hasStableIds() {

        return false;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }
}