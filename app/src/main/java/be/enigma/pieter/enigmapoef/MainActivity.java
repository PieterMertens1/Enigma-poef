package be.enigma.pieter.enigmapoef;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import be.enigma.pieter.enigmapoef.database.PoefDAO;

import static be.enigma.pieter.enigmapoef.database.BaseDAO.getConnectie;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity => " ;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    //CallbackManager mCallbackManager;
    private int RC_SIGN_IN = 103;
    FirebaseUser currentUser;
    GoogleSignInAccount account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();


        //----------------------------------------------------GOOGLE SIGN IN------------------------------------------------------
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //button declaraties en koppeling aan listeners
        SignInButton sign_in_google = findViewById(R.id.sign_in_button);

        sign_in_google.setOnClickListener(myhandler1);
        //----------------------------------------------------GOOGLE SIGN IN------------------------------------------------------


        /*// Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton;


        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");


        // If using in a fragment
        //loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "facebook:onError", exception);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });*/



    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }*/

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            if (v.getId() == R.id.sign_in_button) {
                Log.wtf(TAG, "onClick: click on sign in with google");
                signInGoogle();
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        if (currentUser != null) {
            Log.wtf(TAG, "updateUIFirebase");
            updateUIFirebase(currentUser);
        }


        if (account != null) {
            Log.wtf(TAG, "updateUIGoogle");
            updateUIGoogle(account);
        }
    }


    public void createAccount(View view) {

        Intent intent = new Intent(this, CreateNewAccount.class);
        startActivity(intent);
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUIGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }




    public void signIn(View view) {

        EditText emailText = (EditText) findViewById(R.id.EmailText);
        EditText passwordText = (EditText) findViewById(R.id.passText);

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        TextView errorText = (TextView) findViewById(R.id.errorText);

        if (email.isEmpty()) {

            String error = "Gelieve een emailaddres in te geven.";
            errorText.setText(error);
        }
        else if(password.isEmpty()) {
            String error = "Gelieve een wachtwoord in te geven.";
            errorText.setText(error);
        }
        else if (email.isEmpty() || password.isEmpty()) {
            String error  = "Gelieve de aanmeldingsgegevens in te vullen.";
            errorText.setText(error);
        }
        else {


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        private static final String TAG = "signIn => ";

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUIFirebase(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                TextView errorText = (TextView) findViewById(R.id.errorText);
                                String error = "De aanmeldingsgegevens kloppen niet.";
                                errorText.setText(error);
                            }

                            // ...
                        }
                    });
        }
    }

    private void updateUIFirebase(FirebaseUser currentUser) {

        Log.wtf(TAG, "updateUIFirebase");
        Intent intent = new Intent(this, Mainpage.class);
        intent.putExtra("user", currentUser.toString());
        startActivity(intent);
    }

    private void updateUIGoogle( GoogleSignInAccount account) {

        //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);

        Log.wtf(TAG, "updateUIGoogle: start");
        Intent intent = new Intent(this, Mainpage.class);

        if (account.toString() != null) {
            intent.putExtra("user", account.toString());
        }
        Log.wtf(TAG, "signInGoogle: RC_SIGN_IN:" + RC_SIGN_IN);
        startActivity(intent);
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.wtf(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.wtf(TAG, "signInWithCredential:failure", task.getException());

                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            Log.wtf(TAG, "updateUI:user to mainpage");
            Intent intent = new Intent(this, Mainpage.class);
            intent.putExtra("user", user.toString());
            startActivity(intent);
        }
        else {
            Log.wtf(TAG, "updateUI: UpdateUI niet gelukt");
        }

    }
}
