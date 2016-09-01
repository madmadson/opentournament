package madson.org.opentournament.service;

import android.content.ContentValues;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;

import java.util.List;


/**
 * All services for managing players with tournaments.
 *
 * <p>Players depend not on tournaments. TournamentPlayers have references to tournament and player. They also have meta
 * data such as teamname</p>
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public interface TournamentPlayerService {

    /**
     * @param  tournament  tournament
     *
     * @return  list of all players
     */
    List<TournamentPlayer> getAllPlayersForTournament(Tournament tournament);


    /**
     * Add Player to tournament and return new tournament player instance.
     *
     * @param  player  player instance
     * @param  tournament  tournament
     */
    TournamentPlayer addPlayerToTournament(Player player, Tournament tournament);


    /**
     * Remove Player from tournament. If it is a online tournaments player will be removed immediately.
     *
     * @param  player
     * @param  tournament
     */
    void removePlayerFromTournament(Player player, Tournament tournament);
}
