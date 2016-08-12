package madson.org.opentournament;

import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View frag_view = inflater.inflate(R.layout.fragment_home, container, false);

        Button button = (Button) frag_view.findViewById(R.id.changeText);
        button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    EditText editText = (EditText) frag_view.findViewById(R.id.inputField);
                    editText.setText("Lalala");
                }
            });

        return frag_view;
    }
}
