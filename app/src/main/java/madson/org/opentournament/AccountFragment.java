package madson.org.opentournament;

import android.content.Context;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.utility.BaseActivity;
import madson.org.opentournament.utility.Environment;


public class AccountFragment extends Fragment {

    private TextView firstnameField;
    private TextView lastnameField;
    private TextView nicknameField;
    private TextInputLayout firstname_parent;
    private TextInputLayout nickname_parent;
    private TextInputLayout lastname_parent;
    private BaseActivity baseActivity;
    private TextView welcomeTextView;
    private TextView affiliationField;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        baseActivity = (BaseActivity) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_account_management, container, false);

        firstnameField = (TextView) view.findViewById(R.id.field_firstname);
        nicknameField = (TextView) view.findViewById(R.id.field_nickname);
        lastnameField = (TextView) view.findViewById(R.id.field_lastname);
        affiliationField = (TextView) view.findViewById(R.id.field_affiliation);

        firstname_parent = (TextInputLayout) view.findViewById(R.id.firstname_parent);
        nickname_parent = (TextInputLayout) view.findViewById(R.id.nickname_parent);
        lastname_parent = (TextInputLayout) view.findViewById(R.id.lastname_parent);

        TextInputLayout affiliation_parent = (TextInputLayout) view.findViewById(R.id.affiliation_parent);

        welcomeTextView = (TextView) view.findViewById(R.id.welcome_text);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        final Button addButton = (Button) view.findViewById(R.id.button_add_as_player);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.isAnonymous()) {
            welcomeTextView.setText(getActivity().getResources().getString(R.string.please_sign_in_text));
            progressBar.setVisibility(View.GONE);
        } else {
            final Player authenticatedPlayer = baseActivity.getBaseApplication().getAuthenticatedPlayer();
            firstnameField.setVisibility(View.VISIBLE);
            nicknameField.setVisibility(View.VISIBLE);
            lastnameField.setVisibility(View.VISIBLE);
            affiliationField.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);

            if (authenticatedPlayer != null) {
                firstnameField.setText(authenticatedPlayer.getFirstName());
                nicknameField.setText(authenticatedPlayer.getNickName());
                lastnameField.setText(authenticatedPlayer.getLastName());
                affiliationField.setText(authenticatedPlayer.getMeta());

                addButton.setText(getString(R.string.change_player));

                String playerName = authenticatedPlayer.getFirstName() + " " + authenticatedPlayer.getLastName();

                welcomeTextView.setText(getActivity().getResources().getString(R.string.welcome_text, playerName));
            } else {
                welcomeTextView.setText(getActivity().getResources()
                    .getString(R.string.welcome_text,
                        user.getDisplayName() == null ? "anonymous" : user.getDisplayName()));
            }

            addButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (validateForm()) {
                            final Player authenticatedPlayer = baseActivity.getBaseApplication()
                                .getAuthenticatedPlayer();

                            Player player = new Player();
                            player.setUUID(user.getUid());
                            player.setFirstName(String.valueOf(firstnameField.getText()));
                            player.setNickName(String.valueOf(nicknameField.getText()));
                            player.setLastName(String.valueOf(lastnameField.getText()));
                            player.setMeta(String.valueOf(affiliationField.getText()));
                            player.setAuthenticatedEmail(user.getEmail());

                            if (authenticatedPlayer == null) {
                                player.setElo(1000);
                                player.setGamesCounter(0);
                            } else {
                                if (authenticatedPlayer.getElo() == 0) {
                                    player.setElo(1000);
                                    player.setGamesCounter(0);
                                } else {
                                    player.setElo(authenticatedPlayer.getElo());
                                    player.setGamesCounter(authenticatedPlayer.getGamesCounter());
                                }
                            }

                            final DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance()
                                .getReference();

                            DatabaseReference child = mFirebaseDatabaseReference.child(
                                    FirebaseReferences.PLAYERS + "/" + user.getUid());

                            child.setValue(player);

                            baseActivity.getBaseApplication().updateAuthenticatedUser();

                            firstnameField.setText(player.getFirstName());
                            nicknameField.setText(player.getNickName());
                            lastnameField.setText(player.getLastName());
                            affiliationField.setText(player.getMeta());

                            addButton.setText(getString(R.string.change_player));

                            String playerName = player.getFirstName() + " " + player.getLastName();

                            welcomeTextView.setText(
                                getActivity().getResources().getString(R.string.welcome_text, playerName));

                            Snackbar snackbar = Snackbar.make(((BaseActivity) getActivity()).getCoordinatorLayout(),
                                    R.string.success_upload_player_data, Snackbar.LENGTH_LONG);

                            snackbar.getView()
                            .setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorAccent));

                            snackbar.show();
                        }
                    }
                });

            TextView introductionText = (TextView) view.findViewById(R.id.introduction_text);
            introductionText.setText(getActivity().getResources().getString(R.string.introduction_text));
        }

        return view;
    }


    private boolean validateForm() {

        boolean val = true;

        if (firstnameField.getText().toString().length() == 0) {
            val = false;
            firstname_parent.setError(getString(R.string.validation_error_empty));
        }

        if (nicknameField.getText().toString().length() == 0) {
            val = false;
            nickname_parent.setError(getString(R.string.validation_error_empty));
        }

        if (lastnameField.getText().toString().length() == 0) {
            val = false;
            lastname_parent.setError(getString(R.string.validation_error_empty));
        }

        return val;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (baseActivity.getBaseApplication().getEnvironment() != Environment.PROD) {
            baseActivity.getToolbar().setTitle(R.string.toolbar_title_account_DEMO);
        } else {
            baseActivity.getToolbar().setTitle(R.string.toolbar_title_account);
        }

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }
}
