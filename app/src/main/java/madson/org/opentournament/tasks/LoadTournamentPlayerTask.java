package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentPlayerComparator;
import madson.org.opentournament.organize.setup.TournamentPlayerListAdapter;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.Collections;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadTournamentPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private Tournament tournament;

    private ProgressBar progressBar;
    private TournamentPlayerListAdapter tournamentPlayerListAdapter;
    private TextView noTournamentPlayersTextView;
    private List<TournamentPlayer> localTournamentPlayers;

    public LoadTournamentPlayerTask(BaseApplication baseApplication, Tournament tournament, ProgressBar progressBar,
        TournamentPlayerListAdapter tournamentPlayerListAdapter, TextView noTournamentPlayersTextView) {

        this.baseApplication = baseApplication;
        this.tournament = tournament;
        this.progressBar = progressBar;
        this.tournamentPlayerListAdapter = tournamentPlayerListAdapter;
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
        localTournamentPlayers = tournamentPlayerService.getAllPlayersForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (localTournamentPlayers.size() > 0) {
            noTournamentPlayersTextView.setVisibility(View.GONE);
            Collections.sort(localTournamentPlayers, new TournamentPlayerComparator());
            tournamentPlayerListAdapter.addTournamentPlayers(localTournamentPlayers);
        }
    }
}
