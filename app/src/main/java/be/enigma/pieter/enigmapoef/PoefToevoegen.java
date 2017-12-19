package be.enigma.pieter.enigmapoef;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import be.enigma.pieter.enigmapoef.models.Poef;

public class PoefToevoegen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poef_toevoegen);

        TextView textview = (TextView) findViewById(R.id.naamText);


        if (GoogleSignIn.getLastSignedInAccount(this) != null){

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            textview.setText(account.getDisplayName());
        }
        else if (FirebaseAuth.getInstance() != null) {
            mAuth = FirebaseAuth.getInstance();
            textview.setText(mAuth.getCurrentUser().getEmail());
        }




    }

    public void poefToevoegen(View view) {
        EditText bedragText = (EditText) findViewById(R.id.bedragInput);
        String bedrag = bedragText.getText().toString();
        EditText redenText = (EditText) findViewById(R.id.redenInput);
        String reden = redenText.getText().toString();



        String gebruiker;

        if (GoogleSignIn.getLastSignedInAccount(this) != null){

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            gebruiker = account.getEmail();
        }
        else if (FirebaseAuth.getInstance() != null) {
            mAuth = FirebaseAuth.getInstance();
            gebruiker = mAuth.getCurrentUser().getEmail();
        }
        else {
            gebruiker = "test@test.com";
        }

        gebruiker = encodeUserEmail(gebruiker);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", ServerValue.TIMESTAMP);


        Poef mijnpoef = new Poef(gebruiker, bedrag, reden);

        mDatabase.child("Poef").child(gebruiker).push().setValue(mijnpoef);


        //mDatabase.child("Poef").child(gebruiker).child("Tijdstip").setValue(tijd);
        //mDatabase.child("Poef").child(gebruiker).child("Tijdstip").child(mijnpoef.getGebruiker()).setValue(mijnpoef);

        //mDatabase.child("Poef").child(mijnpoef.getGebruiker()).child("Hoeveelheid").setValue(mijnpoef.getHoeveelheid());
        //mDatabase.child("Poef").child(mijnpoef.getGebruiker()).child("Hoeveelheid").child("Reden").setValue(mijnpoef.getReden());




        //bedragText.setText("");
        //redenText.setText("");

    }

    //mail encoderen omdat "." niet toegestaan is in firebase
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
