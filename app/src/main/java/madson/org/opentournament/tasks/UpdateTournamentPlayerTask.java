package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.events.UpdateTournamentPlayerEvent;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class UpdateTournamentPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;

    private Tournament tournament;

    private AlertDialog dialog;

    private TournamentPlayer tournamentPlayer;

    private String oldTeamName;

    public UpdateTournamentPlayerTask(BaseActivity baseActivity, TournamentPlayer tournamentPlayer,
        Tournament tournament, AlertDialog dialog, String oldTeamName) {

        this.baseActivity = baseActivity;
        this.tournamentPlayer = tournamentPlayer;

        this.tournament = tournament;

        this.dialog = dialog;

        this.oldTeamName = oldTeamName;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TournamentPlayerService tournamentPlayerService = baseActivity.getBaseApplication()
                .getTournamentPlayerService();

        tournamentPlayerService.updateTournamentPlayer(tournamentPlayer, tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_player_updated,
                Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));

        snackbar.show();

        UpdateTournamentPlayerEvent updateTournamentEvent = new UpdateTournamentPlayerEvent(oldTeamName,
                tournamentPlayer);

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.UPDATE_TOURNAMENT_PLAYER, updateTournamentEvent);

        dialog.dismiss();
    }
}
