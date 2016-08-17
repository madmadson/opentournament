package madson.org.opentournament.about;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import madson.org.opentournament.R;
import madson.org.opentournament.utility.BaseFragment;


/**
 * Displays information about the app and the used libraries.
 *
 * @author  Sascha Leist - leist@synyx.de
 * @author  Tobias Knell - knell@synyx.de
 */
public class AboutFragment extends BaseFragment {

    public static final String TAG = "AboutFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.about_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.about_recyclerview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getBaseActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(new AboutAdapter(getBaseActivity()));

        return view;
    }
}
