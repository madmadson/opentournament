package madson.org.opentournament.online;

import android.content.Context;

import android.graphics.Color;

import android.support.v4.content.ContextCompat;

import android.support.v7.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTeam;
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
public class OnlineTournamentPlayerTeamExpandableListAdapter extends BaseExpandableListAdapter {

    private BaseActivity baseActivity;

    private Tournament tournament;
    private Map<TournamentTeam, List<TournamentPlayer>> allTeamsWithPlayersForTournament;
    private List<TournamentTeam> tournamentTeams;

    private List<TournamentPlayer> allRegistrations;

    public OnlineTournamentPlayerTeamExpandableListAdapter(BaseActivity baseActivity, Tournament tournament) {

        this.baseActivity = baseActivity;

        this.tournament = tournament;

        tournamentTeams = new ArrayList<>();
        allRegistrations = new ArrayList<>();
        allTeamsWithPlayersForTournament = new HashMap<>();
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

            if (team.getName() != null && !team.getName().isEmpty()) {
                teamName.setText(team.getName() + " (" + getChildrenCount(groupPosition) + ")");
            } else {
                teamName.setText(baseActivity.getResources().getString(R.string.no_team) + " ("
                    + getChildrenCount(groupPosition) + ")");
            }

            View layout = convertView.findViewById(R.id.expandableList_layout);
            TextView teamNumber = (TextView) convertView.findViewById(R.id.expandableList_team_number);

            teamNumber.setText(String.valueOf(groupPosition + 1));

            if (groupPosition % 2 == 0) {
                layout.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightGrey));
            } else {
                layout.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAlmostWhite));
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

            String firstname = tournamentPlayer.getFirstName();
            String nickname = tournamentPlayer.getNickName();
            String lastname = tournamentPlayer.getLastName();
            fullname.setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, firstname, nickname, lastname));

            TextView faction = (TextView) convertView.findViewById(R.id.tournament_player_row_faction);
            faction.setText(tournamentPlayer.getFaction());

            TextView affiliation = (TextView) convertView.findViewById(R.id.tournament_player_affiliation);
            affiliation.setText(tournamentPlayer.getMeta());

            convertView.findViewById(R.id.tournament_player_teamname).setVisibility(View.GONE);

            ImageView localIcon = (ImageView) convertView.findViewById(R.id.tournament_player_row_local_icon);
            localIcon.setVisibility(View.GONE);

            CardView card = (CardView) convertView.findViewById(R.id.tournament_player_row_card_view);

            if (baseActivity.getBaseApplication().getAuthenticatedPlayer() != null
                    && baseActivity.getBaseApplication().getAuthenticatedPlayer().getUUID()
                    .equals(tournamentPlayer.getPlayerUUID())) {
                card.setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorYours));
            } else {
                if (childPosition % 2 == 0) {
                    card.setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorTurquoise));
                } else {
                    card.setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightTurquoise));
                }
            }

            ImageView editButton = (ImageView) convertView.findViewById(R.id.tournament_player_row_edit_icon);
            editButton.setVisibility(View.GONE);

            ImageView addListButton = (ImageView) convertView.findViewById(R.id.tournament_player_row_add_List);
            addListButton.setVisibility(View.GONE);
        }

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }


    public void addTournamentPlayer(TournamentPlayer player) {

        TournamentTeam tournamentTeam = new TournamentTeam(player.getTeamName());

        if (tournamentTeams.contains(tournamentTeam)) {
            final List<TournamentPlayer> tournamentPlayers = allTeamsWithPlayersForTournament.get(tournamentTeam);
            tournamentPlayers.add(player);
        } else {
            tournamentTeams.add(tournamentTeam);

            List<TournamentPlayer> tournamentPlayers = new ArrayList<>();
            tournamentPlayers.add(player);
            allTeamsWithPlayersForTournament.put(tournamentTeam, tournamentPlayers);
        }

        allRegistrations.add(player);

        notifyDataSetChanged();
    }


    public void removeTournamentPlayer(TournamentPlayer player) {

        TournamentTeam tournamentTeam = new TournamentTeam(player.getTeamName());

        final List<TournamentPlayer> tournamentPlayers = allTeamsWithPlayersForTournament.get(tournamentTeam);
        tournamentPlayers.remove(player);

        if (tournamentPlayers.size() == 0) {
            tournamentTeams.remove(tournamentTeam);
            allTeamsWithPlayersForTournament.remove(tournamentTeam);
        }

        allRegistrations.remove(player);

        notifyDataSetChanged();
    }


    public void updateTournamentPlayer(TournamentPlayer player) {

        for (TournamentPlayer reg : allRegistrations) {
            if (reg.getPlayerUUID().equals(player.getPlayerUUID())) {
                removeTournamentPlayer(reg);

                break;
            }
        }

        addTournamentPlayer(player);
    }
}
