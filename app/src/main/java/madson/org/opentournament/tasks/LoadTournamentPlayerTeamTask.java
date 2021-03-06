package madson.org.opentournament.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTeam;
import madson.org.opentournament.organize.setup.TournamentPlayerTeamExpandableListAdapter;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadTournamentPlayerTeamTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private Tournament tournament;

    private ProgressBar progressBar;
    private TournamentPlayerTeamExpandableListAdapter tournamentPlayerTeamExpandableListAdapter;
    private TextView noTournamentPlayersTextView;
    private Map<TournamentTeam, List<TournamentPlayer>> allTeamsForTournament;

    public LoadTournamentPlayerTeamTask(BaseApplication baseApplication, Tournament tournament, ProgressBar progressBar,
        TournamentPlayerTeamExpandableListAdapter tournamentPlayerTeamExpandableListAdapter,
        TextView noTournamentPlayersTextView) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.progressBar = progressBar;
        this.tournamentPlayerTeamExpandableListAdapter = tournamentPlayerTeamExpandableListAdapter;
        this.noTournamentPlayersTextView = noTournamentPlayersTextView;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected Void doInBackground(Void... params) {

        TournamentPlayerService tournamentPlayerService = baseApplication.getTournamentPlayerService();
        allTeamsForTournament = tournamentPlayerService.getTeamMapForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (allTeamsForTournament.size() > 0) {
            noTournamentPlayersTextView.setVisibility(View.GONE);

            tournamentPlayerTeamExpandableListAdapter.addAllTeams(allTeamsForTournament);
        }
    }
}
