package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class UpdateTournamentTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Tournament tournamentToSave;

    public UpdateTournamentTask(BaseActivity baseActivity, Tournament tournamentToSave) {

        this.baseActivity = baseActivity;
        this.tournamentToSave = tournamentToSave;
    }

    @Override
    protected Void doInBackground(Void... params) {

        final TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();
        tournamentService.updateTournament(tournamentToSave);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseActivity.getBaseApplication().notifyTournamentEdited(tournamentToSave);

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_update_tournament,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorPositive));
        snackbar.show();
    }
}
