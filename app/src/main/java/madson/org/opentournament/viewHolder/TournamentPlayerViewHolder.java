package madson.org.opentournament.viewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import madson.org.opentournament.R;


/**
 * Write some fancy Javadoc!
 *
 * @author  Tobias Matt - tmatt@contargo.net
 */
public class TournamentPlayerViewHolder extends RecyclerView.ViewHolder {

    private final TextView affiliation;
    private ImageView addListIcon;
    private ImageView editIcon;
    private TextView faction;

    private TextView playerNameInList;
    private TextView playerNumber;
    private TextView teamName;

    private ImageView localIcon;
    private TextView droppedInRound;
    private CardView tournamentPlayerCard;

    /**
     * used by online tournament list player.
     *
     * @param  v
     */
    public TournamentPlayerViewHolder(View v) {

        super(v);

        tournamentPlayerCard = (CardView) v.findViewById(R.id.tournament_player_row_card_view);

        playerNumber = (TextView) v.findViewById(R.id.tournament_player_row_player_number);
        playerNameInList = (TextView) v.findViewById(R.id.tournament_player_fullname);
        teamName = (TextView) v.findViewById(R.id.tournament_player_teamname);
        faction = (TextView) v.findViewById(R.id.tournament_player_row_faction);
        droppedInRound = (TextView) v.findViewById(R.id.dropped_in_round);
        affiliation = (TextView) v.findViewById(R.id.tournament_player_affiliation);

        localIcon = (ImageView) v.findViewById(R.id.tournament_player_row_local_icon);
        editIcon = (ImageView) v.findViewById(R.id.tournament_player_row_edit_icon);
        addListIcon = (ImageView) v.findViewById(R.id.tournament_player_row_add_List);
    }

    public TextView getPlayerNameInList() {

        return playerNameInList;
    }


    public TextView getPlayerNumber() {

        return playerNumber;
    }


    public ImageView getLocalIcon() {

        return localIcon;
    }


    public TextView getTeamName() {

        return teamName;
    }


    public TextView getFaction() {

        return faction;
    }


    public TextView getDroppedInRound() {

        return droppedInRound;
    }


    public CardView getTournamentPlayerCard() {

        return tournamentPlayerCard;
    }


    public ImageView getEditIcon() {

        return editIcon;
    }


    public ImageView getAddListIcon() {

        return addListIcon;
    }


    public TextView getAffiliation() {

        return affiliation;
    }
}
