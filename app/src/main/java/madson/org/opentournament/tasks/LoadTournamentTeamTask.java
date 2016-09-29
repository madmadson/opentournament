package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.organize.setup.TournamentPlayerComparator;
import madson.org.opentournament.organize.setup.TournamentPlayerListAdapter;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadTournamentTeamTask extends AsyncTask<Void, Void, Map<String, Integer>> {

    private BaseActivity baseActivity;
    private Tournament tournament;
    private ArrayAdapter<String> team_adapter;
    private Spinner teamnameSpinner;
    private TournamentPlayer tournament_player;

    private Map<String, Integer> mapOfTeams;

    public LoadTournamentTeamTask(BaseActivity baseActivity, Tournament tournament, ArrayAdapter<String> team_adapter,
        Spinner teamnameSpinner, TournamentPlayer tournament_player) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
        this.team_adapter = team_adapter;
        this.teamnameSpinner = teamnameSpinner;
        this.tournament_player = tournament_player;
    }

    @Override
    protected Map<String, Integer> doInBackground(Void... params) {

        TournamentPlayerService tournamentPlayerService = baseActivity.getBaseApplication()
                .getTournamentPlayerService();

        mapOfTeams = tournamentPlayerService.getAllTeamsForTournament(tournament);

        return mapOfTeams;
    }


    @Override
    protected void onPostExecute(Map<String, Integer> aVoid) {

        super.onPostExecute(aVoid);

        if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
            team_adapter.add(baseActivity.getString(R.string.no_team));

            for (String key : mapOfTeams.keySet()) {
                team_adapter.add(key);
            }
        } else {
            for (String key : mapOfTeams.keySet()) {
                if (mapOfTeams.get(key) < tournament.getTeamSize()) {
                    team_adapter.add(key);
                }
            }
        }

        if (team_adapter.isEmpty()) {
            teamnameSpinner.setVisibility(View.GONE);
        } else {
            teamnameSpinner.setVisibility(View.VISIBLE);
        }

        if (tournament_player != null && tournament_player.getTeamname() != null) {
            teamnameSpinner.setSelection(team_adapter.getPosition(tournament_player.getTeamname()));
        }

        team_adapter.notifyDataSetChanged();
    }
}
