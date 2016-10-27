package madson.org.opentournament.organize;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.os.Handler;

import android.support.design.widget.Snackbar;

import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.R;
import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.ArmyList;
import madson.org.opentournament.domain.Game;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentPlayer;
import madson.org.opentournament.tasks.SaveArmyListForGameTask;
import madson.org.opentournament.utility.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class SelectPlayedArmyListDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    public static final String BUNDLE_TOURNAMENT_PLAYER = "tournament_player";
    public static final String BUNDLE_GAME = "game";

    private Tournament tournament;
    private TournamentPlayer tournamentPlayer;
    private BaseActivity baseActivity;
    private Game game;
    private ArrayAdapter<String> adapter;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
            tournamentPlayer = bundle.getParcelable(BUNDLE_TOURNAMENT_PLAYER);
            game = bundle.getParcelable(BUNDLE_GAME);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_army_lists, null);
        String title = getString(R.string.select_army_lists);

        final AutoCompleteTextView autoCompleteArmyListEditText = (AutoCompleteTextView) dialogView.findViewById(
                R.id.autocomplete_select_list);
        builder.setView(dialogView)
            .setTitle(title)
            .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String armyList = autoCompleteArmyListEditText.getText().toString();

                        if (!armyList.isEmpty()) {
                            new SaveArmyListForGameTask(baseActivity, game, tournamentPlayer, armyList).execute();
                        } else {
                            Snackbar snackbar = Snackbar.make(baseActivity.getCoordinatorLayout(),
                                    R.string.nothing_saved, Snackbar.LENGTH_LONG);
                            snackbar.getView()
                            .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorWarning));
                            snackbar.show();
                        }
                    }
                });
        builder.setView(dialogView).setTitle(title).setNegativeButton(R.string.dialog_cancel, null);

        AlertDialog alertDialog = builder.create();

        final ProgressBar progressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar);

        final List<String> listOfArmyListNames = new ArrayList<>();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, listOfArmyListNames);

        if (baseActivity.getBaseApplication().isOnline()) {
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference(FirebaseReferences.TOURNAMENT_ARMY_LISTS + "/" + tournament.getGameOrSportTyp() + "/"
                        + tournament.getUuid() + "/" + tournamentPlayer.getPlayerUUID());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot armyLists : dataSnapshot.getChildren()) {
                            ArmyList armyList = armyLists.getValue(ArmyList.class);

                            if (armyList != null) {
                                listOfArmyListNames.add(armyList.getName());
                            }
                        }

                        adapter.notifyDataSetChanged();
                        autoCompleteArmyListEditText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        autoCompleteArmyListEditText.requestFocus();
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            autoCompleteArmyListEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    autoCompleteArmyListEditText.showDropDown();
                                }
                            }, 500);
                    }
                });
            autoCompleteArmyListEditText.setThreshold(0);
            autoCompleteArmyListEditText.setAdapter(adapter);

            final Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    if (adapter.getCount() == 0) {
                        progressBar.setVisibility(View.GONE);
                        autoCompleteArmyListEditText.setVisibility(View.VISIBLE);
                    }
                }
            };

            Handler handler = new Handler();
            handler.postDelayed(runnable, 10000);
        } else {
            autoCompleteArmyListEditText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        return alertDialog;
    }
}
