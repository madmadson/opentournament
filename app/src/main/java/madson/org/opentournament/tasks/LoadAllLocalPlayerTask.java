package madson.org.opentournament.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Filter;
import android.widget.TextView;

import java.util.List;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.setup.LocalPlayerListAdapter;
import madson.org.opentournament.utility.BaseApplication;


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
    private Tournament tournament;
    private List<Player> allLocalPlayers;

    public LoadAllLocalPlayerTask(BaseApplication baseApplication, LocalPlayerListAdapter localPlayerListAdapter,
        String filterString, TextView noLocalTournamentPlayersTextView, Tournament tournament) {

        this.baseApplication = baseApplication;
        this.localPlayerListAdapter = localPlayerListAdapter;
        this.filterString = filterString;
        this.noLocalTournamentPlayersTextView = noLocalTournamentPlayersTextView;
        this.tournament = tournament;
    }

    @Override
    protected Void doInBackground(Void... params) {

        allLocalPlayers = baseApplication.getPlayerService().getAllLocalPlayers();

        List<TournamentPlayer> allPlayersForTournament = baseApplication.getTournamentPlayerService()
                .getAllPlayersForTournament(tournament);

        for (TournamentPlayer player : allPlayersForTournament) {
            Player player1 = new Player(player.getPlayerUUID());

            if (allLocalPlayers.contains(player1)) {
                allLocalPlayers.remove(player1);
            }
        }

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
