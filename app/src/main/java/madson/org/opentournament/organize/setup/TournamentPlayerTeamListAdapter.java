package madson.org.opentournament.organize.setup;

import android.content.Context;

import android.graphics.Color;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.ArmyList;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTeam;
import madson.org.opentournament.online.RegisterTournamentPlayerDialog;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.viewHolder.TournamentPlayerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerTeamListAdapter extends BaseExpandableListAdapter {

    private BaseActivity baseActivity;

    private Tournament tournament;
    private Map<TournamentTeam, List<TournamentPlayer>> allTeamsWithPlayersForTournament;
    private List<TournamentTeam> tournamentTeams;

    public TournamentPlayerTeamListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.baseActivity = baseActivity;

        this.tournament = tournament;

        tournamentTeams = new ArrayList<>();
        allTeamsWithPlayersForTournament = new HashMap<>();
    }

    public void addAllTeams(Map<TournamentTeam, List<TournamentPlayer>> allTeamsWithPlayersForTournament) {

        this.allTeamsWithPlayersForTournament = allTeamsWithPlayersForTournament;
        tournamentTeams.clear();
        tournamentTeams.addAll(allTeamsWithPlayersForTournament.keySet());
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {

        if (tournamentTeams != null) {
            return tournamentTeams.size();
        } else {
            return 0;
        }
    }


    @Override
    public int getChildrenCount(int groupPosition) {

        if (allTeamsWithPlayersForTournament.get(tournamentTeams.get(groupPosition)) != null) {
            return allTeamsWithPlayersForTournament.get(tournamentTeams.get(groupPosition)).size();
        } else {
            return 0;
        }
    }


    @Override
    public Object getGroup(int groupPosition) {

        return tournamentTeams.get(groupPosition);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return allTeamsWithPlayersForTournament.get(tournamentTeams.get(groupPosition)).get(childPosition);
    }


    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }


    @Override
    public boolean hasStableIds() {

        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (!tournamentTeams.isEmpty()) {
            TournamentTeam team = (TournamentTeam) getGroup(groupPosition);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) baseActivity.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_tournament_team_list_group, null);
            }

            TextView teamName = (TextView) convertView.findViewById(R.id.expandableList_team_name);

            if (team.getTeamName() != null && !team.getTeamName().isEmpty()) {
                teamName.setText(team.getTeamName() + " (" + getChildrenCount(groupPosition) + ")");
            } else {
                teamName.setText(baseActivity.getResources().getString(R.string.no_team) + " ("
                    + getChildrenCount(groupPosition) + ")");
            }

            View layout = convertView.findViewById(R.id.expandableList_layout);

            if (groupPosition % 2 == 0) {
                layout.setBackgroundColor(Color.LTGRAY);
            } else {
                layout.setBackgroundColor(Color.WHITE);
            }
        }

        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
        ViewGroup parent) {

        if (!tournamentTeams.isEmpty()) {
            final TournamentPlayer tournamentPlayer = (TournamentPlayer) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) baseActivity.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_tournament_player, null);
            }

            TextView playerNumber = (TextView) convertView.findViewById(R.id.tournament_player_row_player_number);
            playerNumber.setText(String.valueOf(childPosition + 1));

            TextView fullname = (TextView) convertView.findViewById(R.id.tournament_player_fullname);

            String firstname = tournamentPlayer.getFirstname();
            String nickname = tournamentPlayer.getNickname();
            String lastname = tournamentPlayer.getLastname();
            fullname.setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, firstname, nickname, lastname));

            TextView faction = (TextView) convertView.findViewById(R.id.tournament_player_row_faction);
            faction.setText(tournamentPlayer.getFaction());

            convertView.findViewById(R.id.tournament_player_teamname).setVisibility(View.GONE);

            TextView dropped = (TextView) convertView.findViewById(R.id.dropped_in_round);

            if (tournamentPlayer.getDroppedInRound() != 0) {
                dropped.setText(baseActivity.getResources()
                    .getString(R.string.dropped_in_round, tournamentPlayer.getDroppedInRound()));
                dropped.setVisibility(View.VISIBLE);
            }

            ImageView localIcon = (ImageView) convertView.findViewById(R.id.tournament_player_row_local_icon);

            if (tournamentPlayer.getPlayerId() != null && tournamentPlayer.getPlayerId().equals("0")) {
                localIcon.setVisibility(View.GONE);
            } else {
                localIcon.setVisibility(View.VISIBLE);
            }

            CardView card = (CardView) convertView.findViewById(R.id.tournament_player_row_card_view);

            if (childPosition % 2 == 0) {
                card.setCardBackgroundColor(Color.LTGRAY);
            } else {
                card.setCardBackgroundColor(Color.WHITE);
            }

            ImageView editIcon = (ImageView) convertView.findViewById(R.id.tournament_player_row_edit_icon);
            ImageView addListIcon = (ImageView) convertView.findViewById(R.id.tournament_player_row_add_List);

            if (tournament.getState().equals(Tournament.TournamentState.PLANED.name())) {
                editIcon.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            AddTournamentPlayerDialog dialog = new AddTournamentPlayerDialog();

                            Bundle bundle = new Bundle();
                            bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                            bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT_PLAYER, tournamentPlayer);
                            dialog.setArguments(bundle);

                            FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();
                            dialog.show(supportFragmentManager, "tournament setup new player");
                        }
                    });

                if (baseActivity.getBaseApplication().isOnline() && tournament.getOnlineUUID() != null) {
                    addListIcon.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                if (tournamentPlayer.getPlayerOnlineUUID() != null) {
                                    Log.i(this.getClass().getName(), "addList");

                                    AddTournamentPlayerListDialog dialog = new AddTournamentPlayerListDialog();

                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                                    bundle.putParcelable(RegisterTournamentPlayerDialog.BUNDLE_TOURNAMENT_PLAYER,
                                        tournamentPlayer);
                                    dialog.setArguments(bundle);

                                    FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();

                                    dialog.show(supportFragmentManager, "tournament setup new player");
                                } else {
                                    Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                            R.string.cant_upload_list_for_local_players, Snackbar.LENGTH_LONG);

                                    snackbar.getView()
                                    .setBackgroundColor(baseActivity.getResources().getColor(R.color.colorNegative));

                                    snackbar.show();
                                }
                            }
                        });
                } else {
                    addListIcon.setVisibility(View.GONE);
                }
            } else {
                editIcon.setVisibility(View.GONE);
                addListIcon.setVisibility(View.GONE);
            }
        }

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }


    public void clear() {

        tournamentTeams.clear();
        allTeamsWithPlayersForTournament.clear();
    }


    public void addTournamentPlayer(TournamentPlayer player) {

        TournamentTeam tournamentTeam = new TournamentTeam(player.getTeamname());

        if (tournamentTeams.contains(tournamentTeam)) {
            final List<TournamentPlayer> tournamentPlayers = allTeamsWithPlayersForTournament.get(tournamentTeam);
            tournamentPlayers.add(player);
        } else {
            tournamentTeams.add(tournamentTeam);

            List<TournamentPlayer> tournamentPlayers = new ArrayList<>();
            tournamentPlayers.add(player);
            allTeamsWithPlayersForTournament.put(tournamentTeam, tournamentPlayers);
        }

        notifyDataSetChanged();
    }


    public void removeTournamentPlayer(TournamentPlayer player) {

        TournamentTeam tournamentTeam = new TournamentTeam(player.getTeamname());

        final List<TournamentPlayer> tournamentPlayers = allTeamsWithPlayersForTournament.get(tournamentTeam);
        tournamentPlayers.remove(player);

        if (tournamentPlayers.size() == 0) {
            tournamentTeams.remove(tournamentTeam);
            allTeamsWithPlayersForTournament.remove(tournamentTeam);
        }

        notifyDataSetChanged();
    }


    public void updateTournamentPlayer(TournamentPlayer player, String oldTeamName) {

        TournamentTeam oldTournamentTeam = new TournamentTeam(oldTeamName);

        final List<TournamentPlayer> tournamentPlayers = allTeamsWithPlayersForTournament.get(oldTournamentTeam);
        tournamentPlayers.remove(player);

        if (tournamentPlayers.size() == 0) {
            tournamentTeams.remove(oldTournamentTeam);
            allTeamsWithPlayersForTournament.remove(oldTournamentTeam);
        }

        addTournamentPlayer(player);
    }
}
