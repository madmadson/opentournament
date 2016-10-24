package madson.org.opentournament.tasks;

import android.content.DialogInterface;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.events.EnterGameResultConfirmed;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveGameResultTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Game gameToSave;
    private AlertDialog confirm_dialog;
    private DialogInterface sure_draw_dialog;

    public SaveGameResultTask(BaseActivity baseActivity, Game gameToSave, AlertDialog confirm_dialog,
        DialogInterface sure_draw_dialog) {

        this.baseActivity = baseActivity;
        this.gameToSave = gameToSave;
        this.confirm_dialog = confirm_dialog;
        this.sure_draw_dialog = sure_draw_dialog;
    }

    @Override
    protected Void doInBackground(Void... params) {

        gameToSave.setFinished(true);

        OngoingTournamentService ongoingTournamentService = baseActivity.getBaseApplication()
                .getOngoingTournamentService();

        ongoingTournamentService.saveGameResult(gameToSave);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.SAVE_GAME_RESULT_CONFIRMED,
                new EnterGameResultConfirmed(gameToSave));

        if (sure_draw_dialog != null) {
            sure_draw_dialog.dismiss();
        }

        if (confirm_dialog != null) {
            confirm_dialog.dismiss();
        }

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_save_game_result,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));
        snackbar.show();
    }
}
