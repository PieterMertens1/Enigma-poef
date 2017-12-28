package be.enigma.pieter.enigmapoef;

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity => " ;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    CallbackManager mCallbackManager;
    private int RC_SIGN_IN = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();


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


        // Initialize Facebook Login button
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
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            if (v.getId() == R.id.sign_in_button) {signInGoogle();}
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUIFirebase(currentUser);
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
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

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        updateUIGoogle(account);

    }




    public void signIn(View view) {

        EditText emailText = (EditText) findViewById(R.id.emailText);
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
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    //Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                TextView errorText = (TextView) findViewById(R.id.errorText);
                                String error = "De aanmeldingsgegevens kloppen niet.";
                                errorText.setText(error);
                            }

                            // ...
                        }
                    });
        }
    }


    public void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }



    private void updateUIFirebase(FirebaseUser currentUser) {

            Intent intent = new Intent(this, Mainpage.class);
            intent.putExtra("user", currentUser.toString());
            startActivity(intent);

    }

    private void updateUIGoogle( GoogleSignInAccount account) {

        //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);

        Intent intent = new Intent(this, Mainpage.class);
        intent.putExtra("user", account.toString());
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
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

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
            Intent intent = new Intent(this, Mainpage.class);
            intent.putExtra("user", user.toString());
            startActivity(intent);
        }
        else {
            Log.d(TAG, "updateUI: UpdateUI niet gelukt");
        }

    }


    //code om tekst to ontvangen, controleren en door te sturen via een intent

   /* public void sendMessage (View view) {
        EditText textView = (EditText) findViewById(R.id.textView);
        String bedrag = textView.getText().toString();
        TextView errorText = (TextView) findViewById(R.id.errorText);
        EditText opmerkingText = (EditText) findViewById(R.id.opmerkingText);
        String opmerking = opmerkingText.getText().toString();


        if (bedrag.isEmpty() || bedrag.equals("0") || opmerking.isEmpty() || opmerking.equals(" ")) {
            errorText.setText("De in te geven poef moet hoger zijn dan €0 en gelieve de reden tot poef in te geven");
        }
        else {
            Intent intent = new Intent(this, DisplayMessageActivity.class);

            intent.putExtra(EXTRA_MESSAGE, bedrag);
            intent.putExtra("test", opmerking);
            startActivity(intent);
        }




    }
    */
}
