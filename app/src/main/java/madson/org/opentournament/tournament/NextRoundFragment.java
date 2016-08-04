package madson.org.opentournament.tournament;

import android.content.Context;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentPairing;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.TournamentService;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class NextRoundFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    private Tournament tournament;

    private NextRoundEventListener mListener;

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


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Button nextRoundButton = (Button) view.findViewById(R.id.next_round_button);

        nextRoundButton.setText(getString(R.string.button_pair_next_round, tournament.getActualRound() + 1));

        nextRoundButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(),
                        "click next round (" + (tournament.getActualRound() + 1) + ") for tournament: " + tournament);

                    OngoingTournamentService ongoingTournamentService =
                        ((OpenTournamentApplication) getActivity().getApplication()).getOngoingTournamentService();

                    List<WarmachineTournamentPairing> pairingsForRound =
                        ongoingTournamentService.getPairingsForTournament(tournament.getId(),
                            tournament.getActualRound() + 1);

                    if (pairingsForRound.isEmpty()) {
                        ongoingTournamentService.createPairingForRound(tournament.getId(),
                            tournament.getActualRound() + 1);
                    }

                    if (mListener != null) {
                        mListener.onNextRound(tournament.getActualRound() + 1);
                    } else {
                        throw new RuntimeException("Listener for tournamentPlayerListAdapter missing");
                    }
                }
            });
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (getParentFragment() instanceof NextRoundEventListener) {
            mListener = (NextRoundEventListener) getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().toString()
                + " must implement NextRoundEventListener");
        }
    }

    public interface NextRoundEventListener {

        void onNextRound(int round);
    }
}
