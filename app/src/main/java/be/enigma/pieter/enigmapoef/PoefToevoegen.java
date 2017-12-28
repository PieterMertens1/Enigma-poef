package be.enigma.pieter.enigmapoef;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.models.Poef;

public class PoefToevoegen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private DatabaseHelper mDatabaseHelper;

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


        mDatabaseHelper = new DatabaseHelper(this);

    }


    public void poefToevoegen(View view) {


        TextView naamText = findViewById(R.id.naamText);

        EditText bedragText = (EditText) findViewById(R.id.bedragInput);
        EditText redenText = (EditText) findViewById(R.id.redenInput);


        final String gebruiker = naamText.getText().toString();
        final String hoeveelheid = bedragText.getText().toString();
        final String reden = redenText.getText().toString();

        Poef mijnPoefPoef = new Poef(gebruiker, hoeveelheid, reden);
        final String tijd = mijnPoefPoef.getTijd();


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.rgb(0,100,0));
        textView.setTextSize(25);
        textView.setText("Bent u zeker dat u: €" + hoeveelheid + " aan uw poef wilt toevoegen?");

        alert.setView(textView);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Do something with value!
                AddData(gebruiker, hoeveelheid, reden, tijd);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();




    }


    public void AddData(String gebruiker, String hoeveelheid, String reden, String tijd) {
        boolean insertData = mDatabaseHelper.addData(gebruiker, hoeveelheid, reden, tijd);

        if (insertData) {
            System.out.print("Data succesfully inserted");
        }
        else {
            System.out.print("Something went wrong in Request.java when inserting the data");
        }
    }

    /*public void poefToevoegen(View view) {
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


        //mDatabase = FirebaseDatabase.getInstance().getReference();


        //Map<String, Object> map = new HashMap<>();
        //map.put("timestamp", ServerValue.TIMESTAMP);


        Poef mijnpoef = new Poef(gebruiker, bedrag, reden);

        //mDatabase.child("Poef").child(gebruiker).push().setValue(mijnpoef);




        bedragText.setText("");
        redenText.setText("");





    }*/

    //mail encoderen omdat "." niet toegestaan is in firebase
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
