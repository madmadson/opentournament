package madson.org.opentournament.tasks;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTeam;
import madson.org.opentournament.organize.setup.ConfirmStartTournamentDialog;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class CheckTeamTournamentStartedTask extends AsyncTask<Void, Void, Void> {

    private final BaseActivity baseActivity;
    private final Tournament tournament;

    private boolean unevenTeams;
    private boolean someTeamsNotFull;
    private List<String> teamsNotFull;

    public CheckTeamTournamentStartedTask(BaseActivity baseActivity, Tournament tournament) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
    }

    @Override
    protected Void doInBackground(Void... params) {

        unevenTeams = false;

        Map<TournamentTeam, List<TournamentPlayer>> teamMapForTournament = baseActivity.getBaseApplication()
                .getTournamentPlayerService()
                .getTeamMapForTournament(tournament);

        if (teamMapForTournament.size() % 2 == 1) {
            // uneven amount of teams
            unevenTeams = true;
        }

        someTeamsNotFull = false;

        teamsNotFull = new ArrayList<>();

        for (List<TournamentPlayer> tournamentPlayers : teamMapForTournament.values()) {
            if (tournamentPlayers.size() != tournament.getTeamSize()) {
                someTeamsNotFull = true;
                teamsNotFull.add(tournamentPlayers.get(0).getTeamName() + " size: (" + tournamentPlayers.size() + "/"
                    + tournament.getTeamSize() + ")");
            }
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        if (unevenTeams) {
            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.uneven_teams,
                    Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorNegative));

            snackbar.show();
        } else if (someTeamsNotFull) {
            LayoutInflater inflater = baseActivity.getLayoutInflater();

            View v = inflater.inflate(R.layout.dialog_teams_not_full, null);

            ListView list = (ListView) v.findViewById(R.id.list_of_not_full_teams);

            ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(baseActivity,
                    android.R.layout.simple_list_item_1, teamsNotFull);
            list.setAdapter(stringArrayAdapter);

            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
            builder.setTitle(R.string.teams_not_full).setView(v).setPositiveButton(R.string.dialog_ok, null).show();
        } else {
            ConfirmStartTournamentDialog dialog = new ConfirmStartTournamentDialog();

            Bundle bundle = new Bundle();
            bundle.putParcelable(ConfirmStartTournamentDialog.BUNDLE_TOURNAMENT, tournament);
            dialog.setArguments(bundle);

            FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();
            dialog.show(supportFragmentManager, "confirm start tournament");
        }
    }
}
