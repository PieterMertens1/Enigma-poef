package be.enigma.pieter.enigmapoef;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import be.enigma.pieter.enigmapoef.models.Poef;

public class Request extends AppCompatActivity {

    private static final String TAG = "PoefBekijken => ";


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //[Checken welke user aangemeld is]
        TextView textview = (TextView) findViewById(R.id.EigenaarText);

        if (GoogleSignIn.getLastSignedInAccount(this) != null){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            textview.setText(account.getEmail());
        }
        else if (FirebaseAuth.getInstance() != null) {
            mAuth = FirebaseAuth.getInstance();
            //mDatabase = FirebaseDatabase.getInstance().getReference();
            textview.setText(mAuth.getCurrentUser().getEmail());
        }



    }

    @Override
    public void onStart() {

        super.onStart();

    }


    public void poefToevoegen(View view) {

        steekInDatabase();

    }

    private void steekInDatabase() {
        EditText bedragText = (EditText) findViewById(R.id.BedragText);
        String bedrag = bedragText.getText().toString();
        String reden = "test";
        String gebruiker;



        if (GoogleSignIn.getLastSignedInAccount(this) != null){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            gebruiker = account.getEmail();
        }
        else if (FirebaseAuth.getInstance() != null) {
            gebruiker = mAuth.getCurrentUser().getEmail();
        }
        else {
            gebruiker = "aiaiai";
        }

        gebruiker = encodeUserEmail(gebruiker);

        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", ServerValue.TIMESTAMP);


        Poef mijnpoef = new Poef(gebruiker, bedrag, reden);

        //mDatabase.child("Poef").child(gebruiker).push().setValue(mijnpoef);
        mDatabase.child("users").child(gebruiker).setValue(mijnpoef);


        TextView textView = findViewById(R.id.PoefText);
        textView.setText(mijnpoef.getGebruiker() + " " + mijnpoef.getHoeveelheid() + " " + mijnpoef.getReden() + " " + mijnpoef.getTijd() );
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


}
