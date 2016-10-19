package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class UndoEndTournamentTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;

    private Tournament tournament;
    private int round;

    public UndoEndTournamentTask(BaseActivity baseActivity, Tournament tournament, int round) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
        this.round = round;
    }

    @Override
    protected Void doInBackground(Void... params) {

        RankingService rankingService = baseActivity.getBaseApplication().getRankingService();
        TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();

        rankingService.deleteRankingForRound(tournament, round);
        tournamentService.updateActualRound(tournament, round - 1);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_undo_tournament_ending,
                Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));

        snackbar.show();

        baseActivity.getBaseApplication().notifyTournamentEvent(OpenTournamentEventTag.UNDO_TOURNAMENT_ENDING, null);
    }
}
