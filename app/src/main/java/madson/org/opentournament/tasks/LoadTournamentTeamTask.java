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
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.TournamentTeamLoadedEvent;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseActivity;

import java.util.List;
import java.util.Map;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadTournamentTeamTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Tournament tournament;

    private Map<TournamentTeam, List<TournamentPlayer>> mapOfTeams;

    public LoadTournamentTeamTask(BaseActivity baseActivity, Tournament tournament) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        TournamentPlayerService tournamentPlayerService = baseActivity.getBaseApplication()
                .getTournamentPlayerService();

        mapOfTeams = tournamentPlayerService.getTeamMapForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        TournamentTeamLoadedEvent tournamentTeamLoadedEvent = new TournamentTeamLoadedEvent(mapOfTeams);

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.TOURNAMENT_TEAM_LOADED_EVENT, tournamentTeamLoadedEvent);
    }
}
