package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentUploadTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication application;
    private Tournament tournament;

    public TournamentUploadTask(BaseApplication application, Tournament tournament) {

        this.application = application;
        this.tournament = tournament;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TournamentService tournamentService = application.getTournamentService();
        TournamentPlayerService tournamentPlayerService = application.getTournamentPlayerService();
        RankingService rankingService = application.getRankingService();
        OngoingTournamentService ongoingTournamentService = application.getOngoingTournamentService();

        Tournament uploadedTournament = tournamentService.uploadTournament(this.tournament);
        tournamentPlayerService.uploadTournamentPlayers(uploadedTournament);
        rankingService.uploadRankingsForTournament(uploadedTournament);
        ongoingTournamentService.uploadGames(uploadedTournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
    }
}
