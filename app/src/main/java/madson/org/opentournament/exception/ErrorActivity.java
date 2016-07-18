package madson.org.opentournament.exception;

import android.app.Activity;

import android.os.Bundle;

import android.widget.TextView;

import madson.org.opentournament.R;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class ErrorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_error);

        String error_intent = getIntent().getStringExtra("error");

        if (error_intent != null) {
            TextView error = (TextView) findViewById(R.id.error);

            error.setText(error_intent);
        }
    }
}
