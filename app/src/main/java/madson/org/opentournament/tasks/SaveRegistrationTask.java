package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveRegistrationTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Tournament tournament;
    private TournamentPlayer tournamentPlayer;

    public SaveRegistrationTask(BaseActivity baseActivity, TournamentPlayer tournamentPlayer, Tournament tournament) {

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

        baseActivity.getBaseApplication().notifyRegistrationAddToTournament(tournamentPlayer);

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_new_player_inserted,
                Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));

        snackbar.show();
    }
}
