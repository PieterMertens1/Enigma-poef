package be.enigma.pieter.enigmapoef;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Mainpage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);



/*        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String opmerking = intent.getStringExtra("test");

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);
        textView.setText(message);
        textView2.setText(opmerking);*/
    }


    public void poefToevoegen(View view) {
        Intent intent = new Intent(this, PoefToevoegen.class);
        startActivity(intent);
    }

    public void poefBekijken(View view) {
        Intent intent = new Intent(this, PoefBekijken.class);
        startActivity(intent);
    }

    public void logOUt(View view) {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInClient mGoogleSignInClient;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess();


        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

}
