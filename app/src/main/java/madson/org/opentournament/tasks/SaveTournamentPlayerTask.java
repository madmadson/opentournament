package madson.org.opentournament.tasks;

import android.content.DialogInterface;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.events.AddTournamentPlayerEvent;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveTournamentPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;

    private Tournament tournament;

    private AlertDialog dialog;

    private TournamentPlayer tournamentPlayer;

    private boolean playerWithSameNAmeAlreadyIsInTournament;
    private TournamentPlayerService tournamentPlayerService;
    private TournamentService tournamentService;

    public SaveTournamentPlayerTask(BaseActivity baseActivity, Tournament tournament, TournamentPlayer tournamentPlayer,
        AlertDialog dialog) {

        this.baseActivity = baseActivity;

        this.tournament = tournament;
        this.tournamentPlayer = tournamentPlayer;

        this.dialog = dialog;
    }

    @Override
    protected Void doInBackground(Void... params) {

        tournamentPlayerService = baseActivity.getBaseApplication().getTournamentPlayerService();
        tournamentService = baseActivity.getBaseApplication().getTournamentService();

        List<TournamentPlayer> allPlayersForTournament = tournamentPlayerService.getAllPlayersForTournament(tournament);

        playerWithSameNAmeAlreadyIsInTournament = false;

        for (TournamentPlayer tournamentPlayer1 : allPlayersForTournament) {
            if (tournamentPlayer1.getFirstName().equals(tournamentPlayer.getFirstName())
                    && tournamentPlayer1.getNickName().equals(tournamentPlayer.getNickName())
                    && tournamentPlayer1.getLastName().equals(tournamentPlayer.getLastName())) {
                playerWithSameNAmeAlreadyIsInTournament = true;

                break;
            }
        }

        if (!playerWithSameNAmeAlreadyIsInTournament) {
            tournamentPlayerService.addTournamentPlayerToTournament(tournamentPlayer, tournament);
            tournamentService.increaseActualPlayerForTournament(tournament);
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        if (!playerWithSameNAmeAlreadyIsInTournament) {
            onPostExecuteDoing();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
            builder.setTitle(R.string.player_with_same_name_already_in_tournament)
                .setPositiveButton(R.string.dialog_I_know_this, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                tournamentPlayerService.addTournamentPlayerToTournament(tournamentPlayer, tournament);
                                tournamentService.increaseActualPlayerForTournament(tournament);
                                onPostExecuteDoing();
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
        }
    }


    private void onPostExecuteDoing() {

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.ADD_TOURNAMENT_PLAYER,
                new AddTournamentPlayerEvent(tournamentPlayer));

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_new_player_inserted,
                Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));

        snackbar.show();

        dialog.dismiss();
    }
}
