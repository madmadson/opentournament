package madson.org.opentournament.management;

import android.content.ContentValues;
import android.content.Context;

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

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.service.TournamentService;

import java.text.SimpleDateFormat;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentDetailFragment extends Fragment {

    public static final String BUNDLE_TOURNAMENT_ID = "tournament_id";
    private Tournament tournament;
    private final SimpleDateFormat sdf = new SimpleDateFormat();
    private OnTournamentEditedListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            long aLong = bundle.getLong(BUNDLE_TOURNAMENT_ID);
            TournamentService tournamentService = ((OpenTournamentApplication) getActivity().getApplication())
                .getTournamentService();
            tournament = tournamentService.getTournamentForId(aLong);
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

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("name", String.valueOf(tournamentNameField.getText()));
                            contentValues.put("description", String.valueOf(tournamentDescriptionField.getText()));
                            contentValues.put("date", String.valueOf(tournamentDateField.getText()));

                            TournamentService tournamentService =
                                ((OpenTournamentApplication) getActivity().getApplication()).getTournamentService();
                            tournamentService.editTournament((long) tournament.getId(), contentValues);

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

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("name", String.valueOf(tournamentNameField.getText()));
                            contentValues.put("description", String.valueOf(tournamentDescriptionField.getText()));
                            contentValues.put("date", String.valueOf(tournamentDateField.getText()));

                            TournamentService tournamentService =
                                ((OpenTournamentApplication) getActivity().getApplication()).getTournamentService();
                            tournamentService.createTournament(contentValues);

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

        if (getParentFragment() instanceof OnTournamentEditedListener) {
            mListener = (OnTournamentEditedListener) getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().toString()
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
