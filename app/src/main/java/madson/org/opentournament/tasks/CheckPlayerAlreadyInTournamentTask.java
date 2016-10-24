package madson.org.opentournament.tasks;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.setup.AddTournamentPlayerDialog;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class CheckPlayerAlreadyInTournamentTask extends AsyncTask<Void, Void, Void> {

    private final BaseActivity baseActivity;
    private final Tournament tournament;
    private final Player player;
    private boolean alreadyInTournament;

    public CheckPlayerAlreadyInTournamentTask(BaseActivity baseActivity, Tournament tournament, Player player) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
        this.player = player;
    }

    @Override
    protected Void doInBackground(Void... params) {

        alreadyInTournament = baseActivity.getBaseApplication().getTournamentPlayerService()
            .checkPlayerAlreadyInTournament(tournament, player);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        if (alreadyInTournament) {
            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                    R.string.player_already_is_in_tournament, Snackbar.LENGTH_LONG);

            snackbar.getView().setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));

            snackbar.show();
        } else {
            AddTournamentPlayerDialog dialog = new AddTournamentPlayerDialog();

            Bundle bundle = new Bundle();
            bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
            bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_PLAYER, player);
            dialog.setArguments(bundle);

            FragmentManager supportFragmentManager = baseActivity.getSupportFragmentManager();
            dialog.show(supportFragmentManager, this.getClass().getName());
        }
    }
}
