package madson.org.opentournament;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, TournamentManagementActivity.class);

                    startActivity(intent);
                }
            }
        };

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(onItemClickListener);
    }
}
