package madson.org.opentournament.tournaments;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.content.DialogInterface;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.domain.TournamentTyp;
import madson.org.opentournament.tasks.DeleteTournamentTask;
import madson.org.opentournament.tasks.SaveTournamentTask;
import madson.org.opentournament.tasks.UpdateTournamentTask;
import madson.org.opentournament.utility.BaseActivity;

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
public class OrganizedTournamentEditDialog extends DialogFragment {

    public static final String BUNDLE_TOURNAMENT = "tournament";
    private EditText tournamentNameEditText;
    private EditText tournamentLocationEditText;
    private EditText tournamentDateEditText;
    private EditText tournamentMaxPlayersEditText;

    private Tournament tournament;
    private DateFormat dateFormatter;
    private DatePickerDialog datePickerDialog;

    private RadioButton tournamentTypeSoloRadio;
    private RadioButton tournamentTypeTeamRadio;
    private BaseActivity baseActivity;
    private TextView teamSize;
    private View teamsizeLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        baseActivity = (BaseActivity) getActivity();
        dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
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

        View dialogView = inflater.inflate(R.layout.dialog_add_tournament, null);

        teamsizeLayout = dialogView.findViewById(R.id.teamsize_layout);

        ImageButton teamSizeIncreaseButton = (ImageButton) dialogView.findViewById(R.id.team_size_increase);
        teamSizeIncreaseButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (Integer.valueOf(teamSize.getText().toString()) < 10) {
                        Integer integer = Integer.valueOf(teamSize.getText().toString());
                        teamSize.setText(String.valueOf(integer + 1));
                    }
                }
            });

        ImageButton teamSizeDecreaseButton = (ImageButton) dialogView.findViewById(R.id.team_size_decrease);
        teamSizeDecreaseButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (Integer.valueOf(teamSize.getText().toString()) > 2) {
                        Integer integer = Integer.valueOf(teamSize.getText().toString());
                        teamSize.setText(String.valueOf(integer - 1));
                    }
                }
            });
        teamSize = (TextView) dialogView.findViewById(R.id.team_size);

        tournamentNameEditText = (EditText) dialogView.findViewById(R.id.tournament_name);
        tournamentLocationEditText = (EditText) dialogView.findViewById(R.id.tournament_location);

        tournamentTypeSoloRadio = (RadioButton) dialogView.findViewById(R.id.radio_solo_tournament);
        tournamentTypeSoloRadio.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    teamsizeLayout.setVisibility(View.GONE);
                }
            });
        tournamentTypeTeamRadio = (RadioButton) dialogView.findViewById(R.id.radio_team_tournament);

        tournamentTypeTeamRadio.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    teamsizeLayout.setVisibility(View.VISIBLE);
                }
            });

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

                        DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                        tournamentDateEditText.setText(dateFormatter.format(dateTime.toDate()));
                    }
                }, now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());

        datePickerDialog.updateDate(now.getYear(), now.getMonthOfYear() - 1, now.getDayOfMonth());

        builder.setView(dialogView)
            .setTitle(tournament == null ? getString(R.string.dialog_organize_tournament) : "")
            .setPositiveButton(R.string.dialog_save, null)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        OrganizedTournamentEditDialog.this.getDialog().cancel();
                    }
                });

        if (tournament != null) {
            tournamentNameEditText.setText(tournament.getName());
            tournamentLocationEditText.setText(tournament.getLocation());

            String tournamentTyp = tournament.getTournamentTyp();

            if (tournamentTyp.equals(TournamentTyp.SOLO.name())) {
                tournamentTypeSoloRadio.setChecked(true);
            } else if (tournamentTyp.equals(TournamentTyp.TEAM.name())) {
                tournamentTypeTeamRadio.setChecked(true);
                teamsizeLayout.setVisibility(View.VISIBLE);
                teamSize.setText(String.valueOf(tournament.getTeamSize()));
            }

            tournamentTypeSoloRadio.setEnabled(false);
            tournamentTypeTeamRadio.setEnabled(false);

            if (tournament.getDateOfTournament() != null) {
                tournamentDateEditText.setText(dateFormatter.format(tournament.getDateOfTournament()));
            }

            tournamentMaxPlayersEditText.setText(String.valueOf(tournament.getMaxNumberOfParticipants()));
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

                        saveTournament(dialog);
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
                                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(final DialogInterface confirmDeleteDialog, int id) {

                                                new DeleteTournamentTask(baseActivity, dialog, confirmDeleteDialog,
                                                    tournament).execute();
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


    private void saveTournament(AlertDialog dialog) {

        String tournamentName = tournamentNameEditText.getText().toString();
        String location = tournamentLocationEditText.getText().toString();
        String dateString = tournamentDateEditText.getText().toString();
        String maxPlayers = tournamentMaxPlayersEditText.getText().toString();

        String tournamentType = "";

        if (tournamentTypeTeamRadio.isChecked()) {
            tournamentType = TournamentTyp.TEAM.name();
        } else if (tournamentTypeSoloRadio.isChecked()) {
            tournamentType = TournamentTyp.SOLO.name();
        }

        Date parsed_date = null;
        boolean valid = true;

        if (!dateString.isEmpty()) {
            try {
                parsed_date = dateFormatter.parse(dateString);

                DateTime oneYearBefore = DateTime.now().minusYears(1);
                DateTime oneYearAfter = DateTime.now().plusYears(1);

                if (parsed_date.before(oneYearBefore.toDate())) {
                    valid = false;
                    tournamentDateEditText.requestFocus();
                    tournamentDateEditText.setError(getContext().getString(R.string.validation_before_date));
                } else if (parsed_date.after(oneYearAfter.toDate())) {
                    valid = false;
                    tournamentDateEditText.requestFocus();
                    tournamentDateEditText.setError(getContext().getString(R.string.validation_after_date));
                } else {
                    tournamentDateEditText.setError(null);
                }
            } catch (ParseException e) {
                valid = false;
                tournamentDateEditText.requestFocus();
                tournamentDateEditText.setError(getContext().getString(R.string.validation_invalid_date));
            }
        }

        if (tournamentName.isEmpty()) {
            valid = false;
            tournamentNameEditText.requestFocus();
            tournamentNameEditText.setError(getContext().getString(R.string.validation_error_empty));
        } else {
            tournamentNameEditText.setError(null);
        }

        if (valid) {
            boolean isNewTournament = false;

            if (tournament == null) {
                isNewTournament = true;
                tournament = new Tournament();
            }

            tournament.setName(tournamentName);

            if (!location.isEmpty()) {
                tournament.setLocation(location);
            } else {
                tournament.setLocation(null);
            }

            if (!dateString.isEmpty()) {
                tournament.setDateOfTournament(parsed_date);
            } else {
                tournament.setDateOfTournament(null);
            }

            if (!maxPlayers.isEmpty()) {
                tournament.setMaxNumberOfParticipants(Integer.valueOf(maxPlayers));
            } else {
                tournament.setMaxNumberOfParticipants(0);
            }

            if (!tournamentType.isEmpty()) {
                tournament.setTournamentTyp(tournamentType);
            } else {
                tournament.setTournamentTyp(TournamentTyp.SOLO.name());
            }

            FirebaseUser currentFireBaseUser = baseActivity.getCurrentFireBaseUser();

            if (currentFireBaseUser != null) {
                tournament.setCreatorName(currentFireBaseUser.getDisplayName());
                tournament.setCreatorEmail(currentFireBaseUser.getEmail());
            } else {
                tournament.setCreatorName("anonymous");
                tournament.setCreatorEmail("anonymous");
            }

            tournament.setState(Tournament.TournamentState.PLANNED.name());

            if (tournamentType.equals(TournamentTyp.TEAM.name())) {
                tournament.setTeamSize(Integer.valueOf(teamSize.getText().toString()));
            }

            if (isNewTournament) {
                new SaveTournamentTask(baseActivity, tournament).execute();
            } else {
                new UpdateTournamentTask(baseActivity, tournament).execute();
            }

            dialog.dismiss();
        }
    }
}
