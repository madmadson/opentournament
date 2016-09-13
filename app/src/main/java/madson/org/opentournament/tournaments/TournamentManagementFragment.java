package madson.org.opentournament.tournaments;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.TournamentOrganizeActivity;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;


public class TournamentManagementFragment extends Fragment implements TournamentManagementEventListener {

    private TournamentListsFragment tournamentListsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        tournamentListsFragment = new TournamentListsFragment();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, tournamentListsFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ((BaseActivity) getActivity()).getToolbar().setTitle(R.string.title_tournament_management);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tournament_management, container, false);

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.i(this.getClass().getName(), "click floatingActionButton tournament management");

                    TournamentManagementDialog dialog = new TournamentManagementDialog();

                    FragmentManager supportFragmentManager = getChildFragmentManager();
                    dialog.show(supportFragmentManager, "tournament management new tournament");
                }
            });

        return view;
    }


    @Override
    public void onTournamentListItemClicked(final Tournament tournament) {

        Intent intent = new Intent(getContext(), TournamentOrganizeActivity.class);
        intent.putExtra(TournamentOrganizeActivity.EXTRA_TOURNAMENT, tournament);
        startActivity(intent);
    }


    @Override
    public void onTournamentChangedEvent(Tournament tournament) {

        tournamentListsFragment.getLocalTournamentListAdapter().replace(tournament);
    }


    @Override
    public void onTournamentAddedEvent(Tournament tournament) {

        tournamentListsFragment.getLocalTournamentListAdapter().add(tournament);
    }


    @Override
    public void onTournamentDeletedEvent(Tournament tournament) {

        tournamentListsFragment.getLocalTournamentListAdapter().remove(tournament);
    }


    @Override
    public void onTournamentEditClicked(Tournament tournament) {

        TournamentManagementDialog dialog = new TournamentManagementDialog();

        Bundle bundle = new Bundle();
        bundle.putParcelable(TournamentManagementDialog.BUNDLE_TOURNAMENT, tournament);
        dialog.setArguments(bundle);

        FragmentManager supportFragmentManager = getChildFragmentManager();
        dialog.show(supportFragmentManager, "tournament management edit dialog tournament");
    }


    @Override
    public void onTournamentUploadClicked(final Tournament tournament) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_upload_tournament)
            .setMessage(R.string.confirm_upload_tournament_text)
            .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Runnable runnable = new Runnable() {

                                @Override
                                public void run() {

                                    TournamentService tournamentService =
                                        ((BaseApplication) getActivity().getApplication()).getTournamentService();

                                    if (tournament.getOnlineUUID() == null) {
                                        Tournament uploadedTournament = tournamentService.uploadTournament(tournament);

                                        onTournamentChangedEvent(uploadedTournament);
                                    } else {
                                        tournamentService.updateTournamentInFirebase(tournament);
                                    }

                                    CoordinatorLayout coordinatorLayout = ((BaseActivity) getActivity())
                                        .getCoordinatorLayout();
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                            R.string.success_upload_tournament, Snackbar.LENGTH_LONG);
                                    snackbar.getView()
                                    .setBackgroundColor(getContext().getResources().getColor(R.color.colorPositive));
                                    snackbar.show();
                                }
                            };
                            runnable.run();
                        }
                    })
            .setNegativeButton(R.string.dialog_cancel, null);

        builder.show();
    }


    @Override
    public void onOnlineTournamentListItemClicked(Tournament tournament) {

        // TODO: create own activity for "passiv" tournament watching
    }
}
