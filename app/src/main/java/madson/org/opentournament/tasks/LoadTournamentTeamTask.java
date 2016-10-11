package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTeam;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseActivity;

import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadTournamentTeamTask extends AsyncTask<Void, Void, Map<TournamentTeam, List<TournamentPlayer>>> {

    private BaseActivity baseActivity;
    private Tournament tournament;
    private ArrayAdapter<String> team_adapter;
    private Spinner teamnameSpinner;
    private TournamentPlayer tournament_player;

    private Map<TournamentTeam, List<TournamentPlayer>> mapOfTeams;

    public LoadTournamentTeamTask(BaseActivity baseActivity, Tournament tournament, ArrayAdapter<String> team_adapter,
        Spinner teamNameSpinner, TournamentPlayer tournament_player) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
        this.team_adapter = team_adapter;
        this.teamnameSpinner = teamNameSpinner;
        this.tournament_player = tournament_player;
    }

    @Override
    protected Map<TournamentTeam, List<TournamentPlayer>> doInBackground(Void... params) {

        TournamentPlayerService tournamentPlayerService = baseActivity.getBaseApplication()
                .getTournamentPlayerService();

        mapOfTeams = tournamentPlayerService.getTeamMapForTournament(tournament);

        return mapOfTeams;
    }


    @Override
    protected void onPostExecute(Map<TournamentTeam, List<TournamentPlayer>> aVoid) {

        super.onPostExecute(aVoid);

        if (!tournament.getTournamentTyp().equals(TournamentTyp.TEAM.name())) {
            team_adapter.add(baseActivity.getString(R.string.no_team));

            for (TournamentTeam key : mapOfTeams.keySet()) {
                if (!key.getTeamName().isEmpty()) {
                    team_adapter.add(key.getTeamName());
                }
            }
        } else {
            for (TournamentTeam key : mapOfTeams.keySet()) {
                if (mapOfTeams.get(key).size() < tournament.getTeamSize()) {
                    team_adapter.add(key.getTeamName());
                }
            }
        }

        if (team_adapter.isEmpty()) {
            teamnameSpinner.setVisibility(View.GONE);
        } else {
            teamnameSpinner.setVisibility(View.VISIBLE);
        }

        if (tournament_player != null && tournament_player.getTeamName() != null) {
            teamnameSpinner.setSelection(team_adapter.getPosition(tournament_player.getTeamName()));
        }

        team_adapter.notifyDataSetChanged();
    }
}
