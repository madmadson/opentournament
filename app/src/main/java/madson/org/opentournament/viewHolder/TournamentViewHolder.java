package madson.org.opentournament.viewHolder;

import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.EnterResultForGameDialog;
import madson.org.opentournament.organize.GameListAdapter;
import madson.org.opentournament.organize.TournamentOrganizeActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentViewHolder extends RecyclerView.ViewHolder {

    private ImageView teamIcon;
    private TextView tournamentState;
    private View rowTournament;
    private TextView tournamentNameInList;
    private TextView tournamentPlayersInList;
    private TextView tournamentLocationInList;
    private TextView tournamentDateInList;

    private ImageButton uploadTournamentButton;
    private ImageButton editTournamentButton;

    public TournamentViewHolder(View v) {

        super(v);

        rowTournament = v.findViewById(R.id.row_tournament);

        editTournamentButton = (ImageButton) v.findViewById(R.id.button_edit_tournament);
        uploadTournamentButton = (ImageButton) v.findViewById(R.id.button_upload_tournament);
        tournamentNameInList = (TextView) v.findViewById(R.id.tournament_name);
        tournamentPlayersInList = (TextView) v.findViewById(R.id.amount_players);
        tournamentLocationInList = (TextView) v.findViewById(R.id.tournament_location);
        tournamentDateInList = (TextView) v.findViewById(R.id.tournament_date);
        tournamentState = (TextView) v.findViewById(R.id.tournament_state);

        teamIcon = (ImageView) v.findViewById(R.id.team_tournament_icon);
    }

    public TextView getTournamentNameInList() {

        return tournamentNameInList;
    }


    public TextView getTournamentPlayersInList() {

        return tournamentPlayersInList;
    }


    public TextView getTournamentLocationInList() {

        return tournamentLocationInList;
    }


    public TextView getTournamentDateInList() {

        return tournamentDateInList;
    }


    public TextView getTournamentState() {

        return tournamentState;
    }


    public View getRowTournament() {

        return rowTournament;
    }


    public ImageButton getUploadTournamentButton() {

        return uploadTournamentButton;
    }


    public ImageButton getEditTournamentButton() {

        return editTournamentButton;
    }


    public ImageView getTeamIcon() {

        return teamIcon;
    }
}
