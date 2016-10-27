package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v4.content.ContextCompat;

import android.view.View;

import android.widget.ProgressBar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.UpdateTournamentEvent;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentUploadTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Tournament tournament;
    private ProgressBar progressBar;
    private Tournament actualTournament;

    public TournamentUploadTask(BaseActivity baseActivity, Tournament tournament, ProgressBar progressBar) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {

        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected Void doInBackground(Void... params) {

        TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();
        TournamentPlayerService tournamentPlayerService = baseActivity.getBaseApplication()
                .getTournamentPlayerService();
        RankingService rankingService = baseActivity.getBaseApplication().getRankingService();
        OngoingTournamentService ongoingTournamentService = baseActivity.getBaseApplication()
                .getOngoingTournamentService();

        actualTournament = tournamentService.getTournamentForId(tournament.getUuid());
        actualTournament.setUploadedRound(actualTournament.getActualRound());
        tournamentService.setUploadedRound(actualTournament);

        Tournament uploadedTournament = tournamentService.uploadTournament(actualTournament);
        tournamentPlayerService.uploadTournamentPlayers(uploadedTournament);
        rankingService.uploadRankingsForTournament(uploadedTournament);
        ongoingTournamentService.uploadGames(uploadedTournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        progressBar.setVisibility(View.GONE);

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.UPDATE_TOURNAMENT,
                new UpdateTournamentEvent(actualTournament));

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_upload_tournament,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));
        snackbar.show();
    }
}
