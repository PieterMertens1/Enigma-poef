package be.enigma.pieter.enigmapoef;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateNewAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        mAuth = FirebaseAuth.getInstance();
    }




    public void createNewAccount(View view) {
        EditText emailText = (EditText) findViewById(R.id.emailText);
        EditText passwordText = (EditText) findViewById(R.id.passText);

        String email = emailText.getText().toString().trim();
        email = encodeUserEmail(email);

        String password = passwordText.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public static final String TAG = "createAccount => ";

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
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void updateUI(FirebaseUser currentUeser) {

    }

    //mail encoderen omdat "." niet toegestaan is in firebase
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
