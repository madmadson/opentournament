package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class EditTournamentPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;

    private Tournament tournament;

    private AlertDialog dialog;

    private TournamentPlayer tournamentPlayer;

    private String teamname;
    private String faction;
    private String oldTeamName;

    public EditTournamentPlayerTask(BaseActivity baseActivity, TournamentPlayer tournamentPlayer, Tournament tournament,
        AlertDialog dialog, String teamname, String faction) {

        this.baseActivity = baseActivity;
        this.tournamentPlayer = tournamentPlayer;

        this.tournament = tournament;

        this.dialog = dialog;

        this.teamname = teamname;
        this.faction = faction;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TournamentPlayerService tournamentPlayerService = baseActivity.getBaseApplication()
                .getTournamentPlayerService();

        tournamentPlayer.setFaction(faction);

        oldTeamName = tournamentPlayer.getTeamName();

        // set only team is no team
        if (!this.teamname.equals(baseActivity.getString(R.string.no_team))) {
            tournamentPlayer.setTeamName(this.teamname);
        } else {
            tournamentPlayer.setTeamName(null);
        }

        tournamentPlayerService.editTournamentPlayer(tournamentPlayer, tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_player_updated,
                Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));

        snackbar.show();

        baseActivity.getBaseApplication().notifyUpdateTournamentPlayer(tournamentPlayer, oldTeamName);

        dialog.dismiss();
    }
}
