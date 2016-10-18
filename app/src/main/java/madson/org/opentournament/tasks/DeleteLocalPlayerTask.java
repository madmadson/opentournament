package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.events.DeleteLocalPlayerEvent;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class DeleteLocalPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Player player;

    public DeleteLocalPlayerTask(BaseActivity baseActivity, Player player) {

        this.baseActivity = baseActivity;
        this.player = player;
    }

    @Override
    protected Void doInBackground(Void... params) {

        PlayerService playerService = baseActivity.getBaseApplication().getPlayerService();

        playerService.deleteLocalPlayer(player);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.DELETE_LOCAL_PLAYER, new DeleteLocalPlayerEvent(player));

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_delete_local_player,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }
}
