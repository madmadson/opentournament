package madson.org.opentournament.tasks;

import android.content.DialogInterface;

import android.os.AsyncTask;

import android.support.v7.app.AlertDialog;

import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.GameListAdapter;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveGameResultTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private Game gameToSave;
    private AlertDialog confirm_dialog;
    private DialogInterface sure_draw_dialog;

    public SaveGameResultTask(BaseApplication baseApplication, Game gameToSave, AlertDialog confirm_dialog,
        DialogInterface sure_draw_dialog) {

        this.baseApplication = baseApplication;
        this.gameToSave = gameToSave;
        this.confirm_dialog = confirm_dialog;
        this.sure_draw_dialog = sure_draw_dialog;
    }

    @Override
    protected Void doInBackground(Void... params) {

        gameToSave.setFinished(true);

        OngoingTournamentService ongoingTournamentService = baseApplication.getOngoingTournamentService();

        ongoingTournamentService.saveGameResult(gameToSave);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseApplication.notifyGameResultEntered(gameToSave);

        if (sure_draw_dialog != null) {
            sure_draw_dialog.dismiss();
        }

        if (confirm_dialog != null) {
            confirm_dialog.dismiss();
        }
    }
}
