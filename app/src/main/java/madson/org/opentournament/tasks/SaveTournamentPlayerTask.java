package madson.org.opentournament.tasks;

import android.content.DialogInterface;

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

import java.util.List;
import java.util.UUID;


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
    private boolean playerWithSameNAmeAlreadyIsInTournament;
    private TournamentPlayerService tournamentPlayerService;
    private TournamentService tournamentService;

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

        tournamentPlayerService = baseActivity.getBaseApplication().getTournamentPlayerService();

        tournamentService = baseActivity.getBaseApplication().getTournamentService();

        tournamentPlayer = new TournamentPlayer();

        if (player != null) {
            tournamentPlayer.setPlayerUUID(player.getUUID());
            tournamentPlayer.setLocal(player.isLocal());
        }

        tournamentPlayer.setFirstName(firstname);
        tournamentPlayer.setNickName(nickname);
        tournamentPlayer.setLastName(lastname);

        tournamentPlayer.setTournamentId(String.valueOf(tournament.get_id()));
        tournamentPlayer.setFaction(faction);

        // set only team is no team
        if (!teamname.equals(baseActivity.getString(R.string.no_team))) {
            tournamentPlayer.setTeamName(teamname);
        } else {
            tournamentPlayer.setTeamName("");
        }

        if (player == null) {
            Log.i(this.getClass().getName(), "add new local player.");

            Player newLocalPlayer = new Player();
            newLocalPlayer.setFirstName(firstname);
            newLocalPlayer.setNickName(nickname);
            newLocalPlayer.setLastName(lastname);

            String uuid = UUID.randomUUID().toString();
            newLocalPlayer.setUUID(uuid);

            PlayerService playerService = baseActivity.getBaseApplication().getPlayerService();
            playerService.createLocalPlayer(newLocalPlayer);
            tournamentPlayer.setLocal(true);
            tournamentPlayer.setPlayerUUID(uuid);
        }

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
