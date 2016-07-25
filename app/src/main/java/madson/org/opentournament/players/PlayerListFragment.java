package madson.org.opentournament.players;

import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;

import madson.org.opentournament.R;

import java.util.List;


public class PlayerListFragment extends Fragment {

    public static final String TAG = "player_list_fragment";
    private OnListFragmentInteractionListener mListener;
    private Cursor cursor;
    private SQLiteDatabase readableDatabase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public PlayerListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tournament_list, container, false);

//        ListView listView = (ListView) view.findViewById(R.id.tournament_list_view);
//
//        TournamentService tournamentService = ((OpenTournamentApplication) getActivity().getApplication())
//            .getTournamentService();
//
//        List<Player> playersForTournament = tournamentService.getPlayersForTournament(1L);

        return view;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (getParentFragment() instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().toString()
                + " must implement OnListFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        cursor.close();
        readableDatabase.close();
    }

    public interface OnListFragmentInteractionListener {

        void onTournamentListItemClicked(long id);
    }

    public class MyAdapter extends ArrayAdapter {

        public MyAdapter(Context context, int resource, List objects) {

            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View item = super.getView(position, convertView, parent);

            item.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (mListener != null) {
                            Cursor cursor = (Cursor) getItem(position);
                            int id = cursor.getInt(cursor.getColumnIndex("_id"));
                            mListener.onTournamentListItemClicked(id);
                        }
                    }
                });

            View startTournamentButton = item.findViewById(R.id.startTournamentButton);

            startTournamentButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

//                        Cursor cursor = (Cursor) getItem(position);
//                        int id = cursor.getInt(cursor.getColumnIndex("_id"));
//
//                        Log.i("TournamentList", "startTournamentClicked for tournament: " + id);
//

                    }
                });

            return item;
        }
    }
}
