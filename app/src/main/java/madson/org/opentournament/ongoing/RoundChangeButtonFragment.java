package madson.org.opentournament.ongoing;

import android.graphics.drawable.Drawable;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.warmachine.Game;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.utility.BaseApplication;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class RoundChangeButtonFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_ROUND_TO_DISPLAY = "round_to_display";
    public static final String BUNDLE_NEXT_OR_PREVIOUS = "next_or_previous";

    public enum NextOrPrevious {

        PREVIOUS,
        NEXT
    }

    private Tournament tournament;
    private int round_to_display;
    private NextOrPrevious next_or_previous;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
            round_to_display = bundle.getInt(BUNDLE_ROUND_TO_DISPLAY);
            next_or_previous = NextOrPrevious.valueOf(bundle.getString(BUNDLE_NEXT_OR_PREVIOUS));
        }

        return inflater.inflate(R.layout.fragment_change_round_button_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        final OngoingTournamentActivity activity = (OngoingTournamentActivity) getActivity();

        final OngoingTournamentService ongoingTournamentService = ((BaseApplication) getActivity().getApplication())
            .getOngoingTournamentService();

        final List<Game> pairingsForRoundToDisplay = ongoingTournamentService.getGamesForRound(tournament,
                round_to_display);

        Button changeRoundButton = (Button) view.findViewById(R.id.change_round_button);

        if (next_or_previous.equals(NextOrPrevious.NEXT) && pairingsForRoundToDisplay.isEmpty()) {
            changeRoundButton.setText(getString(R.string.button_pair_round, round_to_display));
        } else {
            changeRoundButton.setText(getString(R.string.button_change_round, round_to_display));
        }

        setChevron(changeRoundButton);

        changeRoundButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(),
                        "click round_to_display (" + round_to_display + ") for tournament: " + tournament);

                    if (next_or_previous.equals(NextOrPrevious.NEXT)) {
                        final List<Game> pairingsForRoundToDisplay = ongoingTournamentService.getGamesForRound(
                                tournament, round_to_display);

                        if (pairingsForRoundToDisplay.isEmpty()) {
                            if (ongoingTournamentService.checkAllGamesAreFinishedForRound(tournament,
                                        round_to_display - 1)) {
                                ConfirmPairingNewRoundDialog dialog = new ConfirmPairingNewRoundDialog();

                                Bundle bundleForConfirmPairNewRoundDialog = new Bundle();
                                bundleForConfirmPairNewRoundDialog.putParcelable(
                                    ConfirmPairingNewRoundDialog.BUNDLE_TOURNAMENT, tournament);
                                bundleForConfirmPairNewRoundDialog.putInt(
                                    ConfirmPairingNewRoundDialog.BUNDLE_ROUND_TO_DISPLAY, round_to_display);
                                dialog.setArguments(bundleForConfirmPairNewRoundDialog);

                                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                                dialog.show(supportFragmentManager, "ConfirmPairingDialog");
                            } else {
                                Log.i(this.getClass().getName(), "not all games are finished");
                            }
                        }
                    }

                    activity.setRoundTabToRoundNumber(round_to_display);
                }
            });
    }


    private void setChevron(Button nextRoundButton) {

        Drawable chevron;

        if (next_or_previous.equals(NextOrPrevious.NEXT)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                chevron = getContext().getResources().getDrawable(R.drawable.ic_chevron_right_black_24dp, null);
            } else {
                chevron = getContext().getResources().getDrawable(R.drawable.ic_chevron_right_black_24dp);
            }

            nextRoundButton.setCompoundDrawablesWithIntrinsicBounds(null, null, chevron, null);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                chevron = getContext().getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp, null);
            } else {
                chevron = getContext().getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp);
            }

            nextRoundButton.setCompoundDrawablesWithIntrinsicBounds(chevron, null, null, null);
        }
    }
}
