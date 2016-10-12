package madson.org.opentournament.tasks;

import android.content.DialogInterface;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.TournamentEventTag;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveGameResultTask extends AsyncTask<Void, Void, Void> {

    private TournamentEventTag tag;
    private BaseActivity baseActivity;
    private Game gameToSave;
    private AlertDialog confirm_dialog;
    private DialogInterface sure_draw_dialog;
    private Tournament tournament;
    private Game teamMatch;

    public SaveGameResultTask(TournamentEventTag tag, BaseActivity baseActivity, Game gameToSave,
        AlertDialog confirm_dialog, DialogInterface sure_draw_dialog, Tournament tournament) {

        this.tag = tag;

        this.baseActivity = baseActivity;
        this.gameToSave = gameToSave;
        this.confirm_dialog = confirm_dialog;
        this.sure_draw_dialog = sure_draw_dialog;
        this.tournament = tournament;
    }

    @Override
    protected Void doInBackground(Void... params) {

        gameToSave.setFinished(true);

        OngoingTournamentService ongoingTournamentService = baseActivity.getBaseApplication()
                .getOngoingTournamentService();

        ongoingTournamentService.saveGameResult(gameToSave);

        if (TournamentEventTag.TEAM_TOURNAMENT_GAME_RESULT_ENTERED.equals(tag)) {
            teamMatch = ongoingTournamentService.updateTeamMatch(gameToSave, tournament);
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseActivity.getBaseApplication().notifyGameResultEntered(tag, gameToSave);

        if (teamMatch != null) {
            baseActivity.getBaseApplication().notifyGameResultEntered(tag, teamMatch);
        }

        if (sure_draw_dialog != null) {
            sure_draw_dialog.dismiss();
        }

        if (confirm_dialog != null) {
            confirm_dialog.dismiss();
        }

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_save_game_result,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorPositive));
        snackbar.show();
    }
}
