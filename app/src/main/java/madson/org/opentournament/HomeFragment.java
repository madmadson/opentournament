package madson.org.opentournament;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;


public class HomeFragment extends Fragment {

    public static final String TAG = "home_fragment";

    public HomeFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        AdapterView.OnItemClickListener onMainMenuClickedListener = new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//            }
//        };
//
//        ListView listView = (ListView) container.findViewById(R.id.home_menu);
//        listView.setOnItemClickListener(onMainMenuClickedListener);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
