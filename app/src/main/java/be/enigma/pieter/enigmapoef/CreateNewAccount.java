package be.enigma.pieter.enigmapoef;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateNewAccount extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateNewAccount => ";
    private FirebaseAuth mAuth;

    private EditText emailText;
    private EditText passwordText;

    String email;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

        emailText = (EditText) findViewById(R.id.EmailText);
        passwordText = (EditText) findViewById(R.id.PasswordText);

        findViewById(R.id.btnSignIn).setOnClickListener( this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        email = emailText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateNewAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                });
        // [END create_user_with_email]
    }


    @Override
    public void onClick(View view){
        int i = view.getId();
        if (i == R.id.btnSignIn) {
            createAccount(emailText.getText().toString(), passwordText.getText().toString());
        }
    }


    private boolean validateForm() {
        boolean valid = true;

        email = emailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailText.setError("Required.");
            valid = false;
        } else {
            emailText.setError(null);
        }

        password = passwordText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordText.setError("Required.");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    public void updateUI(FirebaseUser currentUser) {

        if (currentUser != null) {
            Intent intent = new Intent(this, Mainpage.class);
            intent.putExtra("user", currentUser.toString());
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.Toevoegen:
                //Write your code
                Log.wtf("test", "onOptionsItemSelected: toevoegen" );
                intent = new Intent(this, PoefToevoegen.class);
                startActivity(intent);


                return true;
            case R.id.Bekijken:
                //Write your code
                Log.wtf("test", "onOptionsItemSelected: bekijken" );
                intent = new Intent(this, PoefBekijken.class);
                startActivity(intent);


                return true;
            case R.id.Betalen:
                //Write your code
                Log.wtf("test", "onOptionsItemSelected: betalen" );
                intent = new Intent(Intent.ACTION_SEND);


                // Always use string resources for UI text.
                // This says something like "Share this photo with"
                String title = getResources().getString(R.string.app_name);
                // Create intent to show chooser
                Intent chooser = Intent.createChooser(intent, title);

                // Verify the intent will resolve to at least one activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }


                return true;
            case R.id.Afmelden:
                //Write your code
                Log.wtf("test", "onOptionsItemSelected: afmelden" );

                FirebaseAuth.getInstance().signOut();

                GoogleSignInClient mGoogleSignInClient;

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

                mGoogleSignInClient.signOut();
                mGoogleSignInClient.revokeAccess();


                intent = new Intent(this, MainActivity.class);

                startActivity(intent);


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
