package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.events.EnterGameResultConfirmed;
import madson.org.opentournament.events.OpenTournamentEventTag;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveArmyListForGameTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Game gameToSave;
    private TournamentPlayer tournamentPlayer;
    private String armyList;
    private Game updatedGame;

    public SaveArmyListForGameTask(BaseActivity baseActivity, Game game, TournamentPlayer tournamentPlayer,
        String armyList) {

        this.baseActivity = baseActivity;
        this.gameToSave = game;
        this.tournamentPlayer = tournamentPlayer;
        this.armyList = armyList;
    }

    @Override
    protected Void doInBackground(Void... params) {

        OngoingTournamentService ongoingTournamentService = baseActivity.getBaseApplication()
                .getOngoingTournamentService();

        updatedGame = ongoingTournamentService.saveArmyList(gameToSave, tournamentPlayer, armyList);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseActivity.getBaseApplication()
            .notifyTournamentEvent(OpenTournamentEventTag.SAVE_GAME_RESULT_CONFIRMED,
                new EnterGameResultConfirmed(updatedGame));

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_save_army_list,
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorPositive));
        snackbar.show();
    }
}
