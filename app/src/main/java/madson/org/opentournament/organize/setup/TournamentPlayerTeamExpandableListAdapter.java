package madson.org.opentournament.organize.setup;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;

import android.util.Log;

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
import madson.org.opentournament.online.RegisterTournamentPlayerDialog;
import madson.org.opentournament.tasks.DropTournamentPlayerFromTournamentTask;
import madson.org.opentournament.tasks.RemoveTournamentPlayerFromTournamentTask;
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
public class TournamentPlayerTeamExpandableListAdapter extends BaseExpandableListAdapter {

    private BaseActivity baseActivity;

    private Tournament tournament;
    private Map<TournamentTeam, List<TournamentPlayer>> allTeamsWithPlayersForTournament;
    private List<TournamentTeam> tournamentTeams;

    public TournamentPlayerTeamExpandableListAdapter(BaseActivity baseActivity, Tournament tournament) {

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

            TextView fullNameTextView = (TextView) convertView.findViewById(R.id.tournament_player_fullname);

            fullNameTextView.setText(baseActivity.getResources()
                .getString(R.string.player_name_in_row, tournamentPlayer.getFirstNameWithMaximumCharacters(10),
                    tournamentPlayer.getNickNameWithMaximumCharacters(10),
                    tournamentPlayer.getLastNameWithMaximumCharacters(10)));

            TextView faction = (TextView) convertView.findViewById(R.id.tournament_player_row_faction);
            faction.setText(tournamentPlayer.getFaction());

            TextView affiliation = (TextView) convertView.findViewById(R.id.tournament_player_affiliation);
            affiliation.setText(tournamentPlayer.getMeta());

            convertView.findViewById(R.id.tournament_player_teamname).setVisibility(View.GONE);

            TextView dropped = (TextView) convertView.findViewById(R.id.dropped_in_round);

            if (tournamentPlayer.getDroppedInRound() != 0) {
                dropped.setText(baseActivity.getResources()
                    .getString(R.string.dropped_in_round, tournamentPlayer.getDroppedInRound()));
                dropped.setVisibility(View.VISIBLE);
            }

            ImageView localIcon = (ImageView) convertView.findViewById(R.id.tournament_player_row_local_icon);

            if (tournamentPlayer.isLocal()) {
                localIcon.setVisibility(View.VISIBLE);
            } else {
                localIcon.setVisibility(View.GONE);
            }

            TextView meta = (TextView) convertView.findViewById(R.id.tournament_player_affiliation);

            if (tournamentPlayer.getMeta() != null) {
                if (!tournamentPlayer.getMeta().isEmpty()) {
                    meta.setText(tournamentPlayer.getMeta());
                    meta.setVisibility(View.VISIBLE);
                } else {
                    meta.setVisibility(View.GONE);
                }
            } else {
                meta.setVisibility(View.GONE);
            }

            TextView elo = (TextView) convertView.findViewById(R.id.tournament_player_elo);
            ImageView elo_icon = (ImageView) convertView.findViewById(R.id.tournament_player_elo_icon);

            if (tournamentPlayer.getGamesCounter() >= 5) {
                elo.setText(String.valueOf(tournamentPlayer.getElo()));
                elo.setVisibility(View.VISIBLE);
                elo_icon.setVisibility(View.VISIBLE);
            } else {
                elo.setVisibility(View.GONE);
                elo_icon.setVisibility(View.GONE);
            }

            CardView card = (CardView) convertView.findViewById(R.id.tournament_player_row_card_view);

            if (childPosition % 2 == 0) {
                card.setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorTurquoise));
            } else {
                card.setCardBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorLightTurquoise));
            }

            ImageView editIcon = (ImageView) convertView.findViewById(R.id.tournament_player_row_edit_icon);
            ImageView addListIcon = (ImageView) convertView.findViewById(R.id.tournament_player_row_add_List);

            if (tournament.getState().equals(Tournament.TournamentState.PLANNED.name())) {
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

                if (baseActivity.getBaseApplication().isOnline() && tournament.getUuid() != null) {
                    addListIcon.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                if (tournamentPlayer.getPlayerUUID() != null) {
                                    Log.i(this.getClass().getName(), "addList");

                                    AddArmyListDialog dialog = new AddArmyListDialog();

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
                                    .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

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

            card.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (tournament.getActualRound() == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                            final AlertDialog confirmDialog = builder.setTitle(
                                        R.string.confirm_remove_tournament_player)
                                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Log.i(this.getClass().getName(),
                                                    "removeTournamentPlayer tournamentPlayer from tournament");

                                                new RemoveTournamentPlayerFromTournamentTask(baseActivity, tournament,
                                                    tournamentPlayer).execute();
                                            }
                                        })
                                .setNeutralButton(R.string.dialog_cancel, null)
                                .create();
                            confirmDialog.show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                            final AlertDialog confirmDialog = builder.setTitle(R.string.confirm_drop_tournament_player)
                                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                tournamentPlayer.setDroppedInRound(tournament.getActualRound());
                                                new DropTournamentPlayerFromTournamentTask(
                                                    baseActivity.getBaseApplication(), tournament, tournamentPlayer)
                                                .execute();
                                                updateTournamentPlayer(tournamentPlayer,
                                                    tournamentPlayer.getTeamName());
                                            }
                                        })
                                .setNeutralButton(R.string.dialog_cancel, null)
                                .create();
                            confirmDialog.show();
                        }
                    }
                });
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
