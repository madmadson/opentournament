package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.service.PlayerService;
import madson.org.opentournament.service.TournamentPlayerService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SaveTournamentPlayerTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Player player;
    private Tournament tournament;

    private AlertDialog dialog;

    private TournamentPlayer tournamentPlayer;
    private String firstname;
    private String nickname;
    private String lastname;
    private final String teamname;
    private String faction;

    public SaveTournamentPlayerTask(BaseActivity baseActivity, Player player, Tournament tournament, AlertDialog dialog,
        String firstname, String nickname, String lastname, String teamName, String faction) {

        this.baseActivity = baseActivity;
        this.player = player;

        this.tournament = tournament;

        this.dialog = dialog;
        this.firstname = firstname;
        this.nickname = nickname;
        this.lastname = lastname;
        this.teamname = teamName;
        this.faction = faction;
    }

    @Override
    protected Void doInBackground(Void... params) {

        TournamentPlayerService tournamentPlayerService = baseActivity.getBaseApplication()
                .getTournamentPlayerService();

        TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();

        tournamentPlayer = new TournamentPlayer();

        if (player != null) {
            tournamentPlayer.setPlayerOnlineUUID(player.getOnlineUUID());
            tournamentPlayer.setPlayerId(String.valueOf(player.get_id()));
        }

        tournamentPlayer.setFirstname(firstname);
        tournamentPlayer.setNickname(nickname);
        tournamentPlayer.setLastname(lastname);

        tournamentPlayer.setTournamentId(tournament.get_id());
        tournamentPlayer.setFaction(faction);

        // set only team is no team
        if (!teamname.equals(baseActivity.getString(R.string.no_team))) {
            tournamentPlayer.setTeamname(teamname);
        } else {
            tournamentPlayer.setTeamname("");
        }

        if (player == null) {
            Log.i(this.getClass().getName(), "add new local player.");

            Player newLocalPlayer = new Player();
            newLocalPlayer.setFirstname(firstname);
            newLocalPlayer.setNickname(nickname);
            newLocalPlayer.setLastname(lastname);

            PlayerService playerService = baseActivity.getBaseApplication().getPlayerService();
            Player newLocalPlayerWithId = playerService.createLocalPlayer(newLocalPlayer);
            tournamentPlayer.setPlayerId(String.valueOf(newLocalPlayerWithId.get_id()));
        }

        tournamentPlayerService.addTournamentPlayerToTournament(tournamentPlayer, tournament);
        tournamentService.increaseActualPlayerForTournament(tournament);

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        baseActivity.getBaseApplication().notifyAddTournamentPlayer(tournamentPlayer);

        if (player != null) {
            baseActivity.getBaseApplication().notifyRemoveAvailablePlayer(player);
        }

        Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(), R.string.success_new_player_inserted,
                Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(baseActivity.getResources().getColor(R.color.colorAccent));

        snackbar.show();

        dialog.dismiss();
    }
}
