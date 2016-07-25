package madson.org.opentournament.tournament;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;

import madson.org.opentournament.OpenTournamentApplication;
import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.exception.GeneralExceptionHandler;
import madson.org.opentournament.service.TournamentService;


public class TournamentPlayActivity extends AppCompatActivity {

    public static final String EXTRA_TOURNAMENT_ID = "tournament_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Thread.setDefaultUncaughtExceptionHandler(new GeneralExceptionHandler(this));

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        long tournamentId = (long) extras.get(EXTRA_TOURNAMENT_ID);

        if (tournamentId != 0) {
            Log.i(this.getClass().toString(), "tournament started with id " + tournamentId);

            ActionBar supportActionBar = getSupportActionBar();

            TournamentService tournamentService = ((OpenTournamentApplication) getApplication()).getTournamentService();
            Tournament tournament = tournamentService.getTournamentForId(tournamentId);

            supportActionBar.setTitle(tournament.getName());
        } else {
            throw new RuntimeException();
        }
    }
}
