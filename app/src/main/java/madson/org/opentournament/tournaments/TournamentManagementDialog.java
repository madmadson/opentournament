package madson.org.opentournament.tournaments;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.players.TournamentSetupFragment;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.BaseApplication;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.Locale;


/**
 * Dialog for create/update/delete tournaments. If parent Fragment is implements listener for manipulating lists, call
 * listener when manipulate tournaments
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentManagementDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    private EditText tournamentNameEditText;
    private EditText tournamentLocationEditText;
    private EditText tournamentDateEditText;
    private EditText tournamentMaxPlayersEditText;
    private CheckBox tournamentOnlineCheckbox;

    // for snackbars
    private CoordinatorLayout coordinatorLayout;

    private Tournament tournament;
    private DateFormat dateFormatter;
    private DatePickerDialog datePickerDialog;
    private FirebaseUser currentUser;
    private TournamentManagementEventListener mListener;
    private TextView tournamentInfoConvertToOnlineTournament;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinator_main);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (getParentFragment() instanceof TournamentManagementEventListener) {
            mListener = (TournamentManagementEventListener) getParentFragment();
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();

        if (mListener != null) {
            mListener = null;
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            tournament = bundle.getParcelable(BUNDLE_TOURNAMENT);
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
            .setTitle(tournament == null ? getString(R.string.dialog_new_tournament) : "")
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        TournamentManagementDialog.this.getDialog().cancel();
                    }
                });

        tournamentOnlineCheckbox = (CheckBox) dialogView.findViewById(R.id.tournament_online);
        tournamentInfoConvertToOnlineTournament = (TextView) dialogView.findViewById(
                R.id.tournament_convert_to_online_tournament);

        if (currentUser == null || currentUser.getEmail() == null) {
            tournamentOnlineCheckbox.setEnabled(false);
            tournamentOnlineCheckbox.setChecked(false);
            dialogView.findViewById(R.id.tournament_anonymous_no_online_text).setVisibility(View.VISIBLE);
        }

        if (tournament != null) {
            tournamentNameEditText.setText(tournament.getName());
            tournamentLocationEditText.setText(tournament.getLocation());

            if (tournament.getDateOfTournament() != null) {
                tournamentDateEditText.setText(dateFormatter.format(tournament.getDateOfTournament()));
            }

            tournamentMaxPlayersEditText.setText(String.valueOf(tournament.getMaxNumberOfPlayers()));

            if (tournament.getOnlineUUID() != null) {
                tournamentOnlineCheckbox.setVisibility(View.GONE);
            } else {
                tournamentInfoConvertToOnlineTournament.setVisibility(View.VISIBLE);
            }
        }

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
                            if (tournament == null) {
                                createNewTournament(tournamentName, location, date, parsed_date, maxPlayers);
                            } else {
                                updateTournament(tournamentName, location, date, parsed_date, maxPlayers);
                            }

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

            if (tournament != null) {
                View deleteTournamentButton = dialog.findViewById(R.id.dialog_delete_tournament_button);

                if (deleteTournamentButton != null) {
                    deleteTournamentButton.setVisibility(View.VISIBLE);
                    deleteTournamentButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                builder.setTitle(R.string.dialog_confirm_delete_tournament)
                                .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface sure_draw_dialog, int id) {

                                                TournamentService tournamentService =
                                                    ((BaseApplication) getActivity().getApplication())
                                                    .getTournamentService();

                                                Log.i(this.getClass().getName(), "tournament deletion confirmed");

                                                if (tournament.getOnlineUUID() == null) {
                                                    Log.e(this.getClass().getName(), "tournament deletion offline");

                                                    tournamentService.deleteTournament(tournament.get_id());

                                                    if (mListener != null) {
                                                        mListener.onTournamentDeletedEvent(tournament);
                                                    }
                                                } else {
                                                    Log.e(this.getClass().getName(), "tournament deletion online");
                                                    tournamentService.removeTournamentInFirebase(tournament);
                                                }

                                                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                        R.string.success_delete_tournament, Snackbar.LENGTH_LONG);
                                                snackbar.getView()
                                                .setBackgroundColor(
                                                    getContext().getResources().getColor(R.color.colorAccent));
                                                snackbar.show();

                                                sure_draw_dialog.dismiss();
                                                dialog.dismiss();
                                            }
                                        })
                                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface delete_confirm_dialog, int id) {

                                            delete_confirm_dialog.cancel();
                                        }
                                    });
                                builder.show();
                            }
                        });
                }
            }
        }
    }


    /**
     * ************************* UPDATE TOURNAMENT.*************************
     */
    private void updateTournament(String tournamentName, String location, String date, Date parsed_date,
        String maxPlayers) {

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

        final TournamentService tournamentService = ((BaseApplication) getActivity().getApplication())
            .getTournamentService();

        // convert to online tournament
        if (tournamentOnlineCheckbox.isChecked()) {
            if (tournament.getOnlineUUID() == null) {
                // first update local
                tournamentService.updateTournament(tournament);

                if (mListener != null) {
                    mListener.onTournamentChangedEvent(tournament);
                }

                // after push to firebase with meta data
                FirebaseUser currentFireBaseUser = ((BaseActivity) getActivity()).getCurrentFireBaseUser();

                if (currentFireBaseUser != null) {
                    tournament.setCreatorName(currentFireBaseUser.getDisplayName());
                    tournament.setCreatorEmail(currentFireBaseUser.getEmail());
                } else {
                    tournament.setCreatorName("anonymous");
                    tournament.setCreatorEmail("anonymous");
                }

                tournamentService.setTournamentToFirebase(tournament);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.success_convert_to_online_tournament,
                        Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
                snackbar.show();
            } else {
                tournamentService.updateTournamentInFirebase(tournament);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.success_save_online_tournament,
                        Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
                snackbar.show();
            }
        } else {
            tournamentService.updateTournament(tournament);

            if (mListener != null) {
                mListener.onTournamentChangedEvent(tournament);
            }
        }
    }


    /**
     * *****************NEW TOURNAMENT.****************************
     */
    private void createNewTournament(String tournamentName, String location, String date, Date parsed_date,
        String maxPlayers) {

        Tournament newTournament = new Tournament();

        newTournament.setName(tournamentName);

        if (!location.isEmpty()) {
            newTournament.setLocation(location);
        } else {
            newTournament.setLocation("unknown");
        }

        if (!date.isEmpty()) {
            newTournament.setDateOfTournament(parsed_date);
        } else {
            newTournament.setDateOfTournament(null);
        }

        if (!maxPlayers.isEmpty()) {
            newTournament.setMaxNumberOfPlayers(Integer.valueOf(maxPlayers));
        } else {
            newTournament.setMaxNumberOfPlayers(0);
        }

        TournamentService tournamentService = ((BaseApplication) getActivity().getApplication()).getTournamentService();

        if (tournamentOnlineCheckbox.isChecked()) {
            FirebaseUser currentFireBaseUser = ((BaseActivity) getActivity()).getCurrentFireBaseUser();

            if (currentFireBaseUser != null) {
                newTournament.setCreatorName(currentFireBaseUser.getDisplayName());
                newTournament.setCreatorEmail(currentFireBaseUser.getEmail());
            } else {
                newTournament.setCreatorName("anonymous");
                newTournament.setCreatorEmail("anonymous");
            }

            tournamentService.setTournamentToFirebase(newTournament);
        } else {
            tournamentService.createTournament(newTournament);

            if (mListener != null) {
                mListener.onTournamentAddedEvent(newTournament);
            }
        }
    }
}
