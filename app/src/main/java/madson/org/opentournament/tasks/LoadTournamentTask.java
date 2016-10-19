package madson.org.opentournament.tasks;

import android.os.AsyncTask;

import android.support.v4.view.ViewPager;

import android.view.View;

import android.widget.ProgressBar;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.Tournament;
import madson.org.opentournament.organize.TournamentOrganizeActivity;
import madson.org.opentournament.service.TournamentService;
import madson.org.opentournament.utility.BaseActivity;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class LoadTournamentTask extends AsyncTask<Void, Void, Void> {

    private BaseActivity baseActivity;
    private Tournament tournament;
    private TournamentOrganizeActivity.SectionsPagerAdapter adapter;
    private ViewPager pager;
    private ProgressBar progressBar;
    private float widthOfScreen;
    private Tournament actualTournament;
    private View dialogView;

    public LoadTournamentTask(BaseActivity baseActivity, Tournament tournament,
        TournamentOrganizeActivity.SectionsPagerAdapter adapter, ViewPager pager, ProgressBar progressBar,
        float widthOfScreen) {

        this.baseActivity = baseActivity;
        this.tournament = tournament;
        this.adapter = adapter;
        this.pager = pager;
        this.progressBar = progressBar;
        this.widthOfScreen = widthOfScreen;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected Void doInBackground(Void... params) {

        TournamentService tournamentService = baseActivity.getBaseApplication().getTournamentService();
        actualTournament = tournamentService.getTournamentForId(tournament.getUUID());

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);

        if (adapter != null)
            adapter.setTournamentToOrganize(actualTournament);

        if (pager != null) {
            if (widthOfScreen < 720) {
                pager.setCurrentItem((actualTournament.getActualRound() * 2) - 1);
            } else {
                pager.setCurrentItem(actualTournament.getActualRound());
            }
        }

        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if (dialogView != null) {
            if ((Math.pow(2, actualTournament.getActualRound() + 1)) > actualTournament.getActualPlayers()) {
                dialogView.findViewById(R.id.confirm_dialog_too_much_rounds).setVisibility(View.VISIBLE);
            }
        }
    }
}
