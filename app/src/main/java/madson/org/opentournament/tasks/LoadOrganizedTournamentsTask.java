package madson.org.opentournament.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.tournaments.OrganizedTournamentListAdapter;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadOrganizedTournamentsTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;

    private ProgressBar progressBar;
    private OrganizedTournamentListAdapter adapter;
    private List<Tournament> tournaments;

    public LoadOrganizedTournamentsTask(BaseApplication baseApplication, ProgressBar progressBar,
        OrganizedTournamentListAdapter adapter) {

        this.baseApplication = baseApplication;

        this.progressBar = progressBar;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected Void doInBackground(Void... params) {

        TournamentService tournamentService = baseApplication.getTournamentService();

        tournaments = tournamentService.getTournaments();

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        adapter.addTournaments(tournaments);

        progressBar.setVisibility(View.GONE);
    }
}
