package madson.org.opentournament;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import madson.org.opentournament.db.FirebaseReferences;
import madson.org.opentournament.domain.Player;
import madson.org.opentournament.utility.BaseActivity;

import static madson.org.opentournament.R.id.nickname_parent;


public class AccountFragment extends Fragment {

    private TextView firstnameField;
    private TextView lastnameField;
    private TextView nicknameField;
    private TextInputLayout firstname_parent;
    private TextInputLayout nickname_parent;
    private TextInputLayout lastname_parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FloatingActionButton floatingActionButton = ((BaseActivity) getActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_account_management, container, false);

        firstnameField = (TextView) view.findViewById(R.id.field_firstname);
        nicknameField = (TextView) view.findViewById(R.id.field_nickname);
        lastnameField = (TextView) view.findViewById(R.id.field_lastname);

        firstname_parent = (TextInputLayout) view.findViewById(R.id.firstname_parent);
        nickname_parent = (TextInputLayout) view.findViewById(R.id.nickname_parent);
        lastname_parent = (TextInputLayout) view.findViewById(R.id.lastname_parent);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        final Button addButton = (Button) view.findViewById(R.id.button_add_as_player);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.isAnonymous()) {
            TextView welcomeText = (TextView) view.findViewById(R.id.welcomeText);
            welcomeText.setText(getActivity().getResources().getString(R.string.please_sign_in_text));
            progressBar.setVisibility(View.GONE);
        } else {
            final TextView welcomeText = (TextView) view.findViewById(R.id.welcomeText);
            welcomeText.setText(getActivity().getResources()
                .getString(R.string.welcome_text, user.getDisplayName() == null ? "anonymous" : user.getDisplayName()));

            final DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference child = mFirebaseDatabaseReference.child(FirebaseReferences.PLAYERS + "/"
                    + user.getUid());

            child.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Player player = dataSnapshot.getValue(Player.class);

                        progressBar.setVisibility(View.GONE);

                        firstnameField.setVisibility(View.VISIBLE);
                        nicknameField.setVisibility(View.VISIBLE);
                        lastnameField.setVisibility(View.VISIBLE);
                        addButton.setVisibility(View.VISIBLE);

                        if (player != null) {
                            firstnameField.setText(player.getFirstname());
                            nicknameField.setText(player.getNickname());
                            lastnameField.setText(player.getLastname());

                            addButton.setText(getString(R.string.change_player));

                            String playerName = player.getFirstname() + " " + player.getLastname();

                            welcomeText.setText(
                                getActivity().getResources().getString(R.string.welcome_text, playerName));
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            addButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (validateForm()) {
                            Player player = new Player();
                            player.setOnlineUUID(user.getUid());
                            player.setFirstname(String.valueOf(firstnameField.getText()));
                            player.setNickname(String.valueOf(nicknameField.getText()));
                            player.setLastname(String.valueOf(lastnameField.getText()));
                            player.setAuth_email(user.getEmail());

                            DatabaseReference child = mFirebaseDatabaseReference.child(
                                    FirebaseReferences.PLAYERS + "/" + user.getUid());

                            child.setValue(player);

                            firstnameField.setText(player.getFirstname());
                            nicknameField.setText(player.getNickname());
                            lastnameField.setText(player.getLastname());

                            addButton.setText(getString(R.string.change_player));

                            String playerName = player.getFirstname() + " " + player.getLastname();

                            welcomeText.setText(
                                getActivity().getResources().getString(R.string.welcome_text, playerName));

                            Snackbar snackbar = Snackbar.make(((BaseActivity) getActivity()).getCoordinatorLayout(),
                                    R.string.success_upload_player_data, Snackbar.LENGTH_LONG);

                            snackbar.getView()
                            .setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));

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

        ((BaseActivity) getActivity()).getToolbar().setTitle(R.string.title_account_management);
    }
}
