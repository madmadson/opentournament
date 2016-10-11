package madson.org.opentournament.organize.team;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.GameListAdapter;
import madson.org.opentournament.tasks.LoadGameListForTeamMatchTask;
import madson.org.opentournament.tasks.LoadGameListTask;
import madson.org.opentournament.utility.BaseActivity;


/**
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TeamGameListFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_GAME = "game";
    private Tournament tournament;
    private Game game;

    private TeamGameListAdapter gameListAdapter;
    private BaseActivity baseActivity;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        baseActivity = (BaseActivity) getActivity();
    }


    public static TeamGameListFragment newInstance(Game game, Tournament tournament) {

        TeamGameListFragment fragment = new TeamGameListFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_GAME, game);
        args.putParcelable(BUNDLE_TOURNAMENT, tournament);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getParcelable(BUNDLE_TOURNAMENT) != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
        }

        if (bundle != null && bundle.getParcelable(BUNDLE_GAME) != null) {
            game = bundle.getParcelable(BUNDLE_GAME);
        }

        baseActivity = (BaseActivity) getActivity();

        final View view = inflater.inflate(R.layout.fragment_team_game_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.game_list_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        gameListAdapter = new TeamGameListAdapter(baseActivity);
        recyclerView.setAdapter(gameListAdapter);

        new LoadGameListForTeamMatchTask(baseActivity.getBaseApplication(), tournament, game, gameListAdapter)
            .execute();

        return view;
    }
}
