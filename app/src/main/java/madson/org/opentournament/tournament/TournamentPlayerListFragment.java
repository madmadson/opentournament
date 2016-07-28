package madson.org.opentournament.tournament;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.players.AvailablePlayerListAdapter;
import madson.org.opentournament.players.AvailablePlayerListFragment;
import madson.org.opentournament.service.OngoingTournamentService;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    private Long tournamentId;
    private TournamentPlayerListAdapter tournamentPlayerListAdapter;

    private TournamentPlayerListItemListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getLong(BUNDLE_TOURNAMENT_ID) != 0) {
            tournamentId = bundle.getLong(BUNDLE_TOURNAMENT_ID);
        }

        View view = inflater.inflate(R.layout.fragment_tournament_player_list, container, false);

        OngoingTournamentService ongoingTournamentService = ((OpenTournamentApplication) getActivity()
                .getApplication()).getOngoingTournamentService();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tournament_player_list_recycler_view);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Player> players = ongoingTournamentService.getPlayersForTournament(tournamentId);

        if (mListener != null) {
            tournamentPlayerListAdapter = new TournamentPlayerListAdapter(players, mListener);
            recyclerView.setAdapter(tournamentPlayerListAdapter);
        } else {
            throw new RuntimeException("Listener for tournamentPlayerListAdapter missing");
        }

        return view;
    }


    public void addPlayer(Player player) {

        Log.i(this.getClass().getName(), "add player to tournament player list: " + player);

        if (tournamentPlayerListAdapter != null) {
            tournamentPlayerListAdapter.add(player);
        }
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (getParentFragment() instanceof TournamentPlayerListItemListener) {
            mListener = (TournamentPlayerListItemListener) getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().toString()
                + " must implement TournamentPlayerListItemListener");
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }

    public interface TournamentPlayerListItemListener {

        void onTournamentPlayerListItemClicked(Player player);
    }
}
