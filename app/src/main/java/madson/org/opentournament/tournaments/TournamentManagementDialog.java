package madson.org.opentournament.tournaments;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.CoordinatorLayout;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.players.AvailablePlayerListFragment;
import madson.org.opentournament.service.OngoingTournamentService;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.Locale;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentManagementDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    private EditText tournamentNameEditText;
    private EditText tournamentLocationEditText;
    private EditText tournamentDateEditText;
    private EditText tournamentMaxPlayersEditText;

    private CoordinatorLayout coordinatorLayout;
    private AvailablePlayerListFragment.AvailablePlayerListItemListener mListener;
    private long tournamentId;
    private DateFormat dateFormatter;
    private DatePickerDialog datePickerDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinator_main);

        dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(Context activity) {

        super.onAttach(activity);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournamentId = bundle.getLong(BUNDLE_TOURNAMENT_ID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_tournament_management, null);

        tournamentNameEditText = (EditText) dialogView.findViewById(R.id.tournament_name);
        tournamentLocationEditText = (EditText) dialogView.findViewById(R.id.tournament_location);

        tournamentDateEditText = (EditText) dialogView.findViewById(R.id.tournament_date);
        tournamentMaxPlayersEditText = (EditText) dialogView.findViewById(R.id.tournament_max_players);
        tournamentDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View view, boolean hasFocus) {

                    if (hasFocus) {
                        datePickerDialog.show();
                    } else {
                        datePickerDialog.dismiss();
                    }
                }
            });
        tournamentDateEditText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    datePickerDialog.show();
                }
            });

        DateTime now = DateTime.now();
        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        DateTime dateTime = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
                        tournamentDateEditText.setText(dateFormatter.format(dateTime.toDate()));
                    }
                }, now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());

        datePickerDialog.updateDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());

        builder.setView(dialogView)
            .setTitle(getString(R.string.new_tournament_title))
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        TournamentManagementDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }


    @Override
    public void onStart() {

        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);

            positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.i(this.getClass().getName(), "save tournament");

                        String tournamentName = tournamentNameEditText.getText().toString();
                        String location = tournamentLocationEditText.getText().toString();
                        String date = tournamentDateEditText.getText().toString();
                        Date parsed_date = null;

                        try {
                            parsed_date = dateFormatter.parse(date);
                        } catch (ParseException e) {
                            Log.i(this.getClass().getName(), "Failed to parse date");
                        }

                        String maxPlayers = tournamentMaxPlayersEditText.getText().toString();

                        if (!tournamentName.isEmpty()) {
                            Tournament tournament = new Tournament();
                            tournament.setName(tournamentName);

                            if (!location.isEmpty()) {
                                tournament.setLocation(location);
                            } else {
                                tournament.setLocation("unknown");
                            }

                            if (!date.isEmpty()) {
                                tournament.setDateOfTournament(parsed_date);
                            } else {
                                tournament.setDateOfTournament(null);
                            }

                            if (!maxPlayers.isEmpty()) {
                                tournament.setMaxNumberOfPlayers(Integer.valueOf(maxPlayers));
                            } else {
                                tournament.setMaxNumberOfPlayers(0);
                            }

                            FirebaseUser currentFireBaseUser = ((BaseActivity) getActivity()).getCurrentFireBaseUser();

                            if (currentFireBaseUser != null) {
                                tournament.setCreatorName(currentFireBaseUser.getDisplayName());
                                tournament.setCreatorEmail(currentFireBaseUser.getEmail());
                            } else {
                                tournament.setCreatorName("anonymous");
                                tournament.setCreatorEmail("anonymous");
                            }

                            TournamentService tournamentService = ((BaseApplication) getActivity().getApplication())
                                .getTournamentService();

                            // tournamentService.createTournament(tournament);

                            tournamentService.pushTournamentToFirebase(tournament);

                            dialog.dismiss();
                        } else {
                            Log.i(this.getClass().getName(), "validation failed");

                            if (tournamentName.isEmpty()) {
                                tournamentNameEditText.setError(
                                    getContext().getString(R.string.validation_error_empty));
                            } else {
                                tournamentNameEditText.setError(null);
                            }
                        }
                    }
                });
        }
    }
}
