package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.events.AddRegistrationEvent;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;

import java.util.HashMap;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class AddRegistrationTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Tournament tournament;
    private TournamentPlayer tournamentPlayer;

    public AddRegistrationTask(BaseActivity baseActivity, TournamentPlayer tournamentPlayer, Tournament tournament) {

        this.baseActivity = baseActivity;
        this.tournamentPlayer = tournamentPlayer;

        this.tournament = tournament;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TournamentPlayerService tournamentPlayerService = baseActivity.getBaseApplication()
                .getTournamentPlayerService();

        TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();

        tournamentPlayerService.addTournamentPlayerToTournament(tournamentPlayer, tournament);
        tournamentService.increaseActualPlayerForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.ADD_REGISTRATION, new AddRegistrationEvent(tournamentPlayer));

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_new_player_inserted,
                Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));

        snackbar.show();
    }
}
