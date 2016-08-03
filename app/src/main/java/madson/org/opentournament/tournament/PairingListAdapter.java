package madson.org.opentournament.tournament;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import madson.org.opentournament.R;
import madson.org.opentournament.domain.warmachine.WarmachineTournamentPairing;

import java.util.List;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class PairingListAdapter extends RecyclerView.Adapter<PairingListAdapter.ViewHolder> {

    private List<WarmachineTournamentPairing> pairingsForTournament;

    public PairingListAdapter(List<WarmachineTournamentPairing> pairingsForTournament) {

        this.pairingsForTournament = pairingsForTournament;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pairing_list_row, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final WarmachineTournamentPairing pairing = pairingsForTournament.get(position);
        holder.setPairing(pairing);
        holder.getPlayerOneNameInList().setText(String.valueOf(pairing.getPlayer_one_full_name()));
        holder.getPlayerOneScore().setText("SC: " + String.valueOf(pairing.getPlayer_one_score()));
        holder.getPlayerOneControlPoints().setText("CP: " + String.valueOf(pairing.getPlayer_one_control_points()));
        holder.getPlayerOneVictoryPoints().setText("VP: " + String.valueOf(pairing.getPlayer_one_victory_points()));

        holder.getPlayerTwoNameInList().setText(String.valueOf(pairing.getPlayer_two_full_name()));
        holder.getPlayerTwoScore().setText("SC: " + String.valueOf(pairing.getPlayer_two_score()));
        holder.getPlayerTwoControlPoints().setText("CP: " + String.valueOf(pairing.getPlayer_two_control_points()));
        holder.getPlayerTwoVictoryPoints().setText("VP: " + String.valueOf(pairing.getPlayer_two_victory_points()));
    }


    @Override
    public int getItemCount() {

        return pairingsForTournament.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private WarmachineTournamentPairing pairing;
        private TextView playerOneNameInList;
        private TextView playerOneScore;
        private TextView playerOneControlPoints;
        private TextView playerOneVictoryPoints;

        private TextView playerTwoNameInList;
        private TextView playerTwoScore;
        private TextView playerTwoControlPoints;
        private TextView playerTwoVictoryPoints;

        public ViewHolder(View v) {

            super(v);

            playerOneNameInList = (TextView) v.findViewById(R.id.pairing_player_one_name);
            playerOneScore = (TextView) v.findViewById(R.id.pairing_player_one_score);
            playerOneControlPoints = (TextView) v.findViewById(R.id.pairing_player_one_control_points);
            playerOneVictoryPoints = (TextView) v.findViewById(R.id.pairing_player_one_victory_points);

            playerTwoNameInList = (TextView) v.findViewById(R.id.pairing_player_two_name);
            playerTwoScore = (TextView) v.findViewById(R.id.pairing_player_two_score);
            playerTwoControlPoints = (TextView) v.findViewById(R.id.pairing_player_two_control_points);
            playerTwoVictoryPoints = (TextView) v.findViewById(R.id.pairing_player_two_victory_points);
        }

        @Override
        public void onClick(View v) {
        }


        public void setPairing(WarmachineTournamentPairing pairing) {

            this.pairing = pairing;
        }


        public TextView getPlayerOneNameInList() {

            return playerOneNameInList;
        }


        public TextView getPlayerTwoNameInList() {

            return playerTwoNameInList;
        }


        public TextView getPlayerTwoVictoryPoints() {

            return playerTwoVictoryPoints;
        }


        public TextView getPlayerTwoControlPoints() {

            return playerTwoControlPoints;
        }


        public TextView getPlayerTwoScore() {

            return playerTwoScore;
        }


        public TextView getPlayerOneVictoryPoints() {

            return playerOneVictoryPoints;
        }


        public TextView getPlayerOneControlPoints() {

            return playerOneControlPoints;
        }


        public TextView getPlayerOneScore() {

            return playerOneScore;
        }
    }
}
