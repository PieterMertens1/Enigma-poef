package be.enigma.pieter.enigmapoef;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.*;

import be.enigma.pieter.enigmapoef.database.DatabaseProperties;
import be.enigma.pieter.enigmapoef.database.PoefDAO;
import be.enigma.pieter.enigmapoef.models.Poef;

import static be.enigma.pieter.enigmapoef.database.BaseDAO.getConnectie;

public class Request extends AppCompatActivity {

    private static final String TAG = "PoefBekijken => ";


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static AsyncTask<String, String, String> mAsyncTask;


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



    public void AddData(final Poef poef) {
        Log.wtf(TAG, "AddData: before");

        mAsyncTask = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                PoefDAO poefDAO = new PoefDAO();


                if (getConnectie()!= null ) {
                    poefDAO.insert(poef);

                    Log.wtf(TAG, "doInBackground: This should work");
                    //return "Succes!";
                }
                else {
                    Log.wtf(TAG, "doInBackground: Connection is null");
                    //return "connection is null";
                }
                return null;
            }


        };

        mAsyncTask.execute("");
        Log.wtf(TAG, "AddData: after");

    }



    public void toListView(View view) {
            Intent intent = new Intent(this, PoefBekijken.class);
            startActivity(intent);
    }








    public void poefToevoegen(View view) {


        TextView eigenaarText = findViewById(R.id.EigenaarText);
        TextView poefText = findViewById(R.id.PoefText);
        EditText bedragInvoer = findViewById(R.id.BedragText);

        String eigenaar = eigenaarText.getText().toString();
        String hoeveelheid = bedragInvoer.getText().toString();
        String reden = "test";
        String tijd = "";

        Poef mijnpoef = new Poef(eigenaar, hoeveelheid, reden, tijd);

        if (mijnpoef != new Poef())

        if (!(eigenaar.isEmpty()) || !(hoeveelheid.isEmpty()) ) {
            //poefText.setText(mijnpoef.toString() + " Aangemaakt op: " + tijd + " is doorgestuurd naar addData\n");
            AddData(mijnpoef);
            Log.wtf(TAG, "poefToevoegen: test");
        }
        else {
            poefText.setText("Er is iets misgelopen");
        }








    }


























   /* private void steekInDatabase() {

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


        Poef mijnpoef = new Poef(gebruiker, bedrag, reden,tijd);

        //mDatabase.child("Poef").child(gebruiker).push().setValue(mijnpoef);
        mDatabase.child("users").child(gebruiker).setValue(mijnpoef);


        TextView textView = findViewById(R.id.PoefText);
        textView.setText(mijnpoef.getGebruiker() + " " + mijnpoef.getHoeveelheid() + " " + mijnpoef.getReden() + " " + mijnpoef.getTijd() );
    }
*/
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


}
