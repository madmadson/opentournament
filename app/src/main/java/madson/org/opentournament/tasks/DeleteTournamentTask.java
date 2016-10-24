package madson.org.opentournament.tasks;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.events.DeleteTournamentEvent;
import madson.org.opentournament.events.OpenTournamentEventTag;
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
public class DeleteTournamentTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private AlertDialog dialog;
    private DialogInterface confirmDeleteDialog;
    private Tournament tournament;

    public DeleteTournamentTask(BaseActivity baseActivity, AlertDialog dialog, DialogInterface confirmDeleteDialog,
        Tournament tournament) {

        this.baseActivity = baseActivity;
        this.dialog = dialog;
        this.confirmDeleteDialog = confirmDeleteDialog;
        this.tournament = tournament;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();
        TournamentPlayerService tournamentPlayerService = baseActivity.getBaseApplication()
                .getTournamentPlayerService();

        RankingService rankingService = baseActivity.getBaseApplication().getRankingService();
        OngoingTournamentService ongoingTournamentService = baseActivity.getBaseApplication()
                .getOngoingTournamentService();

        tournamentService.deleteTournament(tournament);
        tournamentPlayerService.deleteTournamentPlayersFromTournament(tournament);
        rankingService.deleteRankingsForTournament(tournament);
        ongoingTournamentService.deleteGamesForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.DELETE_TOURNAMENT, new DeleteTournamentEvent(tournament));

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_delete_tournament,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));
        snackbar.show();

        confirmDeleteDialog.dismiss();
        dialog.dismiss();
    }
}
