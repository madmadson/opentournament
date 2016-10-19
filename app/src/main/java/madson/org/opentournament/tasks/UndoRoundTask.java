package madson.org.opentournament.tasks;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.UndoRoundEvent;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.RankingService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class UndoRoundTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;

    private Tournament tournament;
    private int round;

    public UndoRoundTask(BaseActivity baseActivity, Tournament tournament, int round) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
        this.round = round;
    }

    @Override
    protected Void doInBackground(Void... params) {

        OngoingTournamentService ongoingTournamentService = baseActivity.getBaseApplication()
                .getOngoingTournamentService();
        RankingService rankingService = baseActivity.getBaseApplication().getRankingService();
        TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();

        ongoingTournamentService.deleteGamesForRound(tournament, round);
        rankingService.deleteRankingForRound(tournament, round);
        tournamentService.updateActualRound(tournament, round - 1);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_undo_round,
                Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));

        snackbar.show();

        UndoRoundEvent undoRoundEvent = new UndoRoundEvent((round - 1));
        baseActivity.getBaseApplication().notifyTournamentEvent(OpenTournamentEventTag.UNDO_ROUND, undoRoundEvent);
    }
}
