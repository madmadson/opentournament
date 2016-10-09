package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.view.View;

import android.widget.Filter;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.setup.LocalPlayerListAdapter;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadAllLocalPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseApplication baseApplication;
    private LocalPlayerListAdapter localPlayerListAdapter;
    private String filterString;
    private TextView noLocalTournamentPlayersTextView;
    private List<Player> allLocalPlayers;

    public LoadAllLocalPlayerTask(BaseApplication baseApplication, LocalPlayerListAdapter localPlayerListAdapter,
        String filterString, TextView noLocalTournamentPlayersTextView) {

        this.baseApplication = baseApplication;
        this.localPlayerListAdapter = localPlayerListAdapter;
        this.filterString = filterString;
        this.noLocalTournamentPlayersTextView = noLocalTournamentPlayersTextView;
    }

    @Override
    protected Void doInBackground(Void... params) {

        allLocalPlayers = baseApplication.getPlayerService().getAllLocalPlayers();

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        localPlayerListAdapter.addPlayerList(allLocalPlayers);
        localPlayerListAdapter.getFilter().filter(filterString, new Filter.FilterListener() {

                @Override
                public void onFilterComplete(int count) {

                    if (count == 0) {
                        noLocalTournamentPlayersTextView.setVisibility(View.VISIBLE);
                    } else {
                        noLocalTournamentPlayersTextView.setVisibility(View.GONE);
                    }
                }
            });
    }
}
