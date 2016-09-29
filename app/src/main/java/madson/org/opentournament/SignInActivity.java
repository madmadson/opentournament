/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package madson.org.opentournament;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.design.widget.TextInputLayout;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Random;


public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;

    private CallbackManager callbackManager;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEmailField;
    private EditText mPasswordField;

    private TextInputLayout email_parent;
    private TextInputLayout password_parent;

    private Button email_sign_in_button;
    private Button email_create_account_button;

    private Button button_anonymous_sign_in;
    private LoginButton mFacebookSignInButton;
    private SignInButton mGoogleSignInButton;
    private ProgressBar progressBar;
    private View divider;
    private View oneClickHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email_parent = (TextInputLayout) findViewById(R.id.email_parent);
        password_parent = (TextInputLayout) findViewById(R.id.password_parent);
        email_parent = (TextInputLayout) findViewById(R.id.email_parent);

        button_anonymous_sign_in = (Button) findViewById(R.id.button_anonymous_sign_in);

        email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);
        email_create_account_button = (Button) findViewById(R.id.email_create_account_button);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        divider = findViewById(R.id.divider1);
        oneClickHeading = findViewById(R.id.one_click_heading);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // Assign fields
        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mFacebookSignInButton = (LoginButton) findViewById(R.id.facebook_login_button);

        findViewById(R.id.button_anonymous_sign_in).setOnClickListener(this);

        // Set click listeners
        mGoogleSignInButton.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        mFacebookSignInButton.setReadPermissions("email", "public_profile");
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback());

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
                    getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // username password

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        findViewById(R.id.email_sign_in_button).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!validateForm()) {
                        return;
                    }

                    mFirebaseAuth.signInWithEmailAndPassword(mEmailField.getText().toString(),
                            mPasswordField.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    if (task.getException() != null) {
                                        String fail_message = task.getException().getMessage();
                                        Toast.makeText(SignInActivity.this, fail_message, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(SignInActivity.this, R.string.success_login, Toast.LENGTH_LONG)
                                    .show();
                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                }
                            }
                        });
                }
            });
        email_create_account_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    LayoutInflater inflater = SignInActivity.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_create_account, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                    final AlertDialog dialog = builder.setTitle(R.string.create_account)
                        .setView(dialogView)
                        .setPositiveButton(R.string.dialog_save, null)
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .create();
                    dialog.show();

                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                final EditText emailForNewAccount = (EditText) dialogView.findViewById(
                                        R.id.create_account_field_email);

                                if (TextUtils.isEmpty(emailForNewAccount.getText())) {
                                    emailForNewAccount.setError("Required.");
                                } else {
                                    emailForNewAccount.setError(null);

                                    Random rnd = new Random();
                                    int n = 100000 + rnd.nextInt(900000);
                                    mFirebaseAuth.createUserWithEmailAndPassword(
                                            emailForNewAccount.getText().toString(), String.valueOf(n))
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (!task.isSuccessful()) {
                                                    if (task.getException() != null) {
                                                        String fail_message = task.getException().getMessage();
                                                        Toast.makeText(SignInActivity.this, fail_message,
                                                            Toast.LENGTH_LONG)
                                                        .show();
                                                    }
                                                } else {
                                                    Toast.makeText(SignInActivity.this, R.string.success_create_account,
                                                        Toast.LENGTH_LONG)
                                                    .show();
                                                    mFirebaseAuth.sendPasswordResetEmail(
                                                        emailForNewAccount.getText().toString());
                                                }

                                                dialog.dismiss();
                                            }
                                        });
                                }
                            }
                        });
                }
                ;
            });
    }


    private boolean validateForm() {

        boolean valid = true;

        String email = mEmailField.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }


    @Override
    public void onStart() {

        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onStop() {

        super.onStop();

        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @NonNull
    private FacebookCallback<LoginResult> facebookCallback() {

        return new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }


            @Override
            public void onCancel() {

                Log.d(TAG, "facebook:onCancel");
            }


            @Override
            public void onError(FacebookException error) {

                Log.d(TAG, "facebook:onError", error);
            }
        };
    }


    @Override
    public void onClick(View v) {

        int i = v.getId();

        if (i == R.id.google_sign_in_button) {
            startLoading();
            googleSignIn();
        } else if (i == R.id.button_anonymous_sign_in) {
            startLoading();
            signInAnonymously(this);
        }
    }


    private void startLoading() {

        password_parent.setVisibility(View.GONE);
        email_parent.setVisibility(View.GONE);
        email_sign_in_button.setVisibility(View.GONE);
        email_create_account_button.setVisibility(View.GONE);
        button_anonymous_sign_in.setVisibility(View.GONE);
        mFacebookSignInButton.setVisibility(View.GONE);
        mGoogleSignInButton.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
        oneClickHeading.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
    }


    private void signInAnonymously(final Context context) {

        Task<AuthResult> authResultTask = mFirebaseAuth.signInAnonymously();

        OnCompleteListener<AuthResult> signInAnonymously = new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Log.i(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                    Toast.makeText(SignInActivity.this, R.string.success_login, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(context, MainActivity.class));
                } else {
                    Log.e(TAG, "signInAnonymously", task.getException());
                }
            }
        };

        Task<AuthResult> authResultTask1 = authResultTask.addOnCompleteListener(this, signInAnonymously);

        authResultTask1.addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {

                    if (mFirebaseAuth.getCurrentUser() == null) {
                        Log.i(this.getClass().getName(), "no connection possible to firebase");
                        Toast.makeText(SignInActivity.this, R.string.toast_no_connection, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(context, MainActivity.class));
                    }
                }
            });
    }


    private void googleSignIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                handleAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            Log.i(TAG, "Facebook sign in ");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d(TAG, "handleGoogleSignInAccount:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, onCompleteSignInListener());
    }


    private void handleFacebookAccessToken(AccessToken token) {

        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, onCompleteSignInListener());
    }


    @NonNull
    private OnCompleteListener<AuthResult> onCompleteSignInListener() {

        return new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignInActivity.this, R.string.success_login, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                }
            }
        };
    }
}
