package madson.org.opentournament;

import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class TournamentListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private Cursor cursor;
    private SQLiteDatabase readableDatabase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
     * changes).
     */
    public TournamentListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tournament_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.tournament_list_view);

        Cursor cursorForTournaments = getCursorForTournaments();

        SimpleCursorAdapter simpleCursorAdapter = new MyAdapter(getActivity(), R.layout.tournament_list_row,
                cursorForTournaments, new String[] { "name" }, new int[] { R.id.tournamentNameInList }, 0);

        listView.setAdapter(simpleCursorAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnListFragmentInteractionListener");
        }
    }


    public Cursor getCursorForTournaments() {

        OpenTournamentDatabase dbHelper = new OpenTournamentDatabase(getActivity());

        readableDatabase = dbHelper.getReadableDatabase();

        cursor = readableDatabase.query("tournament", new String[] { "_id", "name" }, null, null, null, null, null);

        return cursor;
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

    public class MyAdapter extends SimpleCursorAdapter {

        public MyAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {

            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            super.bindView(view, context, cursor);
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = super.getView(position, convertView, parent);

            view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (mListener != null) {
                            Cursor cursor = (Cursor) getItem(position);
                            int id = cursor.getInt(cursor.getColumnIndex("_id"));
                            mListener.onTournamentListItemClicked(id);
                        }
                    }
                });

            return view;
        }
    }
}
