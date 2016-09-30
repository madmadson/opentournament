package madson.org.opentournament.organize.setup;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.organize.TournamentEventListener;
import madson.org.opentournament.tasks.DropTournamentPlayerFromTournamentTask;
import madson.org.opentournament.tasks.RemoveTournamentPlayerFromTournamentTask;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;


/**
 * Fragment combining list of all players and tournament players.
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentSetupFragment extends Fragment implements TournamentEventListener {

    private static final String BUNDLE_TOURNAMENT = "tournament";
    private Tournament tournament;

    // both lists of players for adding to tournament
    private AvailablePlayerListFragment availablePlayerListFragment;
    private TournamentPlayerListFragment tournamentPlayerListFragment;

    /**
     * create instance of fragment.
     *
     * @param  tournament  specific tournament
     *
     * @return  new instance
     */
    public static TournamentSetupFragment newInstance(Tournament tournament) {

        TournamentSetupFragment fragment = new TournamentSetupFragment();
        Bundle args = new Bundle();

        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        ((BaseActivity) getActivity()).getBaseApplication().registerTournamentEventListener(this);
    }


    @Override
    public void onDetach() {

        super.onDetach();
        ((BaseActivity) getActivity()).getBaseApplication().unregisterTournamentEventListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tournament_setup, container, false);

        tournament = getArguments().getParcelable(BUNDLE_TOURNAMENT);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if (tournament.getState().equals(Tournament.TournamentState.PLANED.name())) {
            createAvailablePlayerListFragment();
            fragmentTransaction.replace(R.id.available_player_fragment_container, availablePlayerListFragment);
        }

        createTournamentPlayerListFragment();
        fragmentTransaction.replace(R.id.tournament_player_fragment_container, tournamentPlayerListFragment);

        Log.i(this.getClass().getName(), "show setup for  tournament " + tournament);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(),
                        "click floatingActionButton player addTournamentPlayer to tournament");

                    AddTournamentPlayerDialog dialog = new AddTournamentPlayerDialog();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
                    dialog.setArguments(bundle);

                    FragmentManager supportFragmentManager = getChildFragmentManager();
                    dialog.show(supportFragmentManager, "tournament setup new player");
                }
            });

        return view;
    }


    private void createAvailablePlayerListFragment() {

        Bundle bundleForAllPlayers = new Bundle();
        bundleForAllPlayers.putParcelable(AvailablePlayerListFragment.BUNDLE_TOURNAMENT, tournament);

        availablePlayerListFragment = new AvailablePlayerListFragment();
        availablePlayerListFragment.setArguments(bundleForAllPlayers);
    }


    private void createTournamentPlayerListFragment() {

        tournamentPlayerListFragment = new TournamentPlayerListFragment();

        Bundle bundleForTournamentPlayers = new Bundle();
        bundleForTournamentPlayers.putParcelable(TournamentPlayerListFragment.BUNDLE_TOURNAMENT, tournament);
        tournamentPlayerListFragment.setArguments(bundleForTournamentPlayers);
    }


    @Override
    public void addTournamentPlayer(TournamentPlayer tournamentPlayer) {

        tournamentPlayerListFragment.addPlayer(tournamentPlayer);
    }


    @Override
    public void removeTournamentPlayer(final TournamentPlayer tournamentPlayer) {

        if (tournament.getActualRound() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final AlertDialog confirmDialog = builder.setTitle(R.string.confirm_remove_tournament_player)
                    .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Log.i(this.getClass().getName(), "removePlayer player from tournament");

                                    BaseApplication baseApplication = ((BaseActivity) getActivity())
                                        .getBaseApplication();
                                    new RemoveTournamentPlayerFromTournamentTask(baseApplication, tournament,
                                        tournamentPlayer, tournamentPlayerListFragment, availablePlayerListFragment)
                                    .execute();
                                }
                            })
                    .setNeutralButton(R.string.dialog_cancel, null)
                    .create();
            confirmDialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final AlertDialog confirmDialog = builder.setTitle(R.string.confirm_drop_tournament_player)
                    .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    BaseApplication baseApplication = ((BaseActivity) getActivity())
                                        .getBaseApplication();
                                    new DropTournamentPlayerFromTournamentTask(baseApplication, tournament,
                                        tournamentPlayer, tournamentPlayerListFragment).execute();
                                }
                            })
                    .setNeutralButton(R.string.dialog_cancel, null)
                    .create();
            confirmDialog.show();
        }
    }


    @Override
    public void addPlayerToTournament(Player player) {

        AddTournamentPlayerDialog dialog = new AddTournamentPlayerDialog();

        Bundle bundle = new Bundle();
        bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_TOURNAMENT, tournament);
        bundle.putParcelable(AddTournamentPlayerDialog.BUNDLE_PLAYER, player);
        dialog.setArguments(bundle);

        FragmentManager supportFragmentManager = getChildFragmentManager();
        dialog.show(supportFragmentManager, this.getClass().getName());
    }


    @Override
    public void removeAvailablePlayer(Player player) {

        availablePlayerListFragment.removePlayer(player);
    }


    @Override
    public void updateTournamentPlayer(TournamentPlayer updatedPLayer) {

        tournamentPlayerListFragment.updatePlayer(updatedPLayer);
    }


    @Override
    public void startRound(int roundToStart, Tournament tournament) {

        if (roundToStart == 2) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            fragmentTransaction.remove(availablePlayerListFragment);
            fragmentTransaction.commit();
        }

        tournamentPlayerListFragment.startButtonInvisible();
    }


    @Override
    public void pairRoundAgain(int round_for_pairing) {
    }


    @Override
    public void pairingChanged(Game game1, Game game2) {
    }


    @Override
    public void enterGameResultConfirmed(Game game) {
    }
}
