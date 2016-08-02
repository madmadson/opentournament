package madson.org.opentournament.tournament;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.TournamentService;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class NextRoundFragment extends Fragment {

    private static final java.lang.String BUNDLE_TOURNAMENT_ID = "tournament_id";
    private Tournament tournament;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            long aLong = bundle.getLong(BUNDLE_TOURNAMENT_ID);
            TournamentService tournamentService = ((OpenTournamentApplication) getActivity().getApplication())
                .getTournamentService();
            tournament = tournamentService.getTournamentForId(aLong);
        }

        return inflater.inflate(R.layout.fragment_next_round_fragment, container, false);
    }
}
