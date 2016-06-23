package madson.org.opentournament;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import madson.org.opentournament.domain.Tournament;

import java.text.SimpleDateFormat;

import java.util.Date;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentDetailFragment extends Fragment {

    private Tournament tournament;
    private final SimpleDateFormat sdf = new SimpleDateFormat();
    private OnTournamentEditedListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getLong("tournamentId") != 0) {
            OpenTournamentDatabase dbHelper = new OpenTournamentDatabase(container.getContext());

            SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = readableDatabase.query("tournament", Tournament.ALL_COLS_FOR_TOURNAMENT, "_id  = ?",
                    new String[] { Long.toString(bundle.getLong("tournamentId")) }, null, null, null);

            if (cursor.moveToFirst()) {
                tournament = new Tournament(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), new Date(cursor.getLong(4)));
            }

            cursor.close();
            dbHelper.close();
        }

        return inflater.inflate(R.layout.fragment_tournament_detail, container, false);
    }


    @Override
    public void onStart() {

        super.onStart();

        final View view = getView();

        if (view != null) {
            final EditText tournamentNameField = (EditText) view.findViewById(R.id.tournamentName);
            final EditText tournamentDescriptionField = (EditText) view.findViewById(R.id.tournamentDescription);
            final EditText tournamentDateField = (EditText) view.findViewById(R.id.tournamentDate);
            final TextView tournamentNumberOfPlayers = (TextView) view.findViewById(R.id.tournamentPlayers);
            Button saveButton = (Button) view.findViewById(R.id.saveTournamentButton);

            if (tournament != null) {
                tournamentNameField.setText(tournament.getName());

                tournamentDescriptionField.setText(tournament.getDescription());

                tournamentDateField.setText(sdf.format(tournament.getDateOfTournament()));

                tournamentNumberOfPlayers.setText(Integer.toString(tournament.getNumberOfPlayers()));

                saveButton.setText(R.string.textEditTorunamentButton);
                saveButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            OpenTournamentDatabase dbHelper = new OpenTournamentDatabase(view.getContext());

                            SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("name", String.valueOf(tournamentNameField.getText()));
                            contentValues.put("description", String.valueOf(tournamentDescriptionField.getText()));
                            contentValues.put("date", String.valueOf(tournamentDateField.getText()));

                            writableDatabase.update("Tournament", contentValues, "_id = ?",
                                new String[] { String.valueOf(tournament.getId()) });

                            dbHelper.close();

                            Toast toast = Toast.makeText(getActivity(), R.string.existingTournamentSaved,
                                    Toast.LENGTH_SHORT);
                            toast.show();

                            if (mListener != null) {
                                mListener.onTournamentEditedClicked();
                            }
                        }
                    });
            } else {
                saveButton.setText(R.string.textSaveTorunamentButton);
                saveButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            OpenTournamentDatabase dbHelper = new OpenTournamentDatabase(view.getContext());

                            SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("name", String.valueOf(tournamentNameField.getText()));
                            contentValues.put("description", String.valueOf(tournamentDescriptionField.getText()));
                            contentValues.put("date", String.valueOf(tournamentDateField.getText()));

                            writableDatabase.insert("Tournament", null, contentValues);

                            dbHelper.close();

                            Toast toast = Toast.makeText(getActivity(), R.string.newTournamentSaved,
                                    Toast.LENGTH_SHORT);
                            toast.show();

                            if (mListener != null) {
                                mListener.onTournamentEditedClicked();
                            }
                        }
                    });
            }
        }
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (context instanceof OnTournamentEditedListener) {
            mListener = (OnTournamentEditedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnListFragmentInteractionListener");
        }
    }

    public interface OnTournamentEditedListener {

        /**
         * Event when user edit or create a tournament.
         */
        void onTournamentEditedClicked();
    }
}
