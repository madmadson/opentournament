package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v4.content.ContextCompat;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.events.AddTournamentEvent;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveTournamentTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Tournament tournamentToSave;
    private Tournament insertedTournament;

    public SaveTournamentTask(BaseActivity baseActivity, Tournament tournamentToSave) {

        this.baseActivity = baseActivity;
        this.tournamentToSave = tournamentToSave;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();
        insertedTournament = tournamentService.createTournament(tournamentToSave);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_create_tournament,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));
        snackbar.show();

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.ADD_TOURNAMENT, new AddTournamentEvent(insertedTournament));
    }
}
