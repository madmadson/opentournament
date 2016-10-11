package madson.org.opentournament.service;

import android.content.ContentValues;
import android.content.Intent;

import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.domain.TournamentTeam;

import java.util.List;
import java.util.Map;


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
     * @param  tournament  tournament
     *
     * @return  map of all players; tournament player id is key
     */
    Map<String, TournamentPlayer> getAllPlayerMapForTournament(Tournament tournament);


    /**
     * @param  tournament  tournament
     *
     * @return  map of all players; tournament player id is key
     */
    Map<String, TournamentTeam> getAllTeamMapForTournament(Tournament tournament);


    /**
     * Add Player to tournament and return new tournament player instance.
     *
     * @param  player  player instance
     * @param  tournament  tournament
     */
    void addTournamentPlayerToTournament(TournamentPlayer player, Tournament tournament);


    /**
     * Remove Player from tournament.
     *
     * @param  player
     */
    void removePlayerFromTournament(TournamentPlayer player);


    /**
     * @param  tournament
     *
     * @return
     */
    Map<TournamentTeam, List<TournamentPlayer>> getTeamMapForTournament(Tournament tournament);


    /**
     * @param  tournament
     *
     * @return
     */
    List<TournamentTeam> getTeamListForTournament(Tournament tournament);


    /**
     * all player UUID from players already in tournament.
     *
     * @param  tournament
     *
     * @return
     */
    List<String> getAllPlayersUUIDsForTournament(Tournament tournament);


    /**
     * @param  tournament
     */
    void uploadTournamentPlayers(Tournament tournament);


    /**
     * @param  player
     * @param  tournament
     */
    void dropPlayerFromTournament(TournamentPlayer player, Tournament tournament);


    /**
     * @param  tournamentPlayer
     * @param  tournament
     */
    void editTournamentPlayer(TournamentPlayer tournamentPlayer, Tournament tournament);


    /**
     * Delete all players for given tournament.
     *
     * @param  tournament
     */
    void deleteTournamentPlayersFromTournament(Tournament tournament);


    /**
     * @param  tournament
     * @param  player
     *
     * @return
     */
    boolean checkPlayerAlreadyInTournament(Tournament tournament, Player player);
}
