package madson.org.opentournament.tournament;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.management.TournamentDetailFragment;
import madson.org.opentournament.players.PlayerListFragment;
import madson.org.opentournament.service.TournamentService;


public class OngoingTournamentManagementFragment extends Fragment {

    public static final String TAG = "ongoing_tournament_management_fragment";
    private static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    private Tournament tournament;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            long aLong = bundle.getLong(BUNDLE_TOURNAMENT_ID);
            TournamentService tournamentService = ((OpenTournamentApplication) getActivity().getApplication())
                .getTournamentService();
            tournament = tournamentService.getTournamentForId(aLong);
        }

        return inflater.inflate(R.layout.fragment_ongoing_tournament_management, container, false);
    }


    @Override
    public void onStart() {

        super.onStart();

        final View view = getView();

        if (view != null) {
            Bundle bundleForAllPlayers = new Bundle();
            bundleForAllPlayers.putLong(PlayerListFragment.BUNDLE_TOURNAMENT_ID, tournament.getId());

            PlayerListFragment playerListFragment = new PlayerListFragment();
            playerListFragment.setArguments(bundleForAllPlayers);

            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.left_fragment_container, playerListFragment);

            if (view.findViewById(R.id.right_fragment_container) != null) {
                TournamentPlayerListFragment tournamentPlayerListFragment = new TournamentPlayerListFragment();
                Bundle bundleForTournamentPlayers = new Bundle();
                bundleForTournamentPlayers.putLong(TournamentDetailFragment.BUNDLE_TOURNAMENT_ID, tournament.getId());
                tournamentPlayerListFragment.setArguments(bundleForTournamentPlayers);

                fragmentTransaction.replace(R.id.right_fragment_container, tournamentPlayerListFragment);
            }

            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }
    }
}
