package madson.org.opentournament.tasks;

import android.os.AsyncTask;
import android.util.Log;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Save given player in local db.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveLocalPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Player player;

    public SaveLocalPlayerTask(BaseActivity baseActivity, Player player) {

        this.baseActivity = baseActivity;
        this.player = player;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.i(this.getClass().getName(), "add new local player.");

        PlayerService playerService = baseActivity.getBaseApplication().getPlayerService();
        playerService.createLocalPlayer(player);

        return null;
    }
}
