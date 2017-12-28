package be.enigma.pieter.enigmapoef;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;

public class fromQr extends AppCompatActivity {

    private DatabaseHelper mDatabaseHelper;
    String value;

    String gebruiker;
    String hoeveelheid;
    String reden;
    String tijd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_qr);

        mDatabaseHelper = new DatabaseHelper(this);


        //------------ hier gaan we ResultText invullen
        TextView resultText = findViewById(R.id.ResultText);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("Value");
            resultText.setText(value);

        }
        else {
            resultText.setText(null);
        }

        TextView gebruikerText = findViewById(R.id.GebruikerText);
        TextView hoeveelheidText = findViewById(R.id.HoeveelheidText);
        TextView redenText = findViewById(R.id.RedenText);
        TextView tijdText = findViewById(R.id.TijdText);


        gebruiker = value.split("%",4)[0];
        hoeveelheid = value.split("%",4)[1];
        reden = value.split("%",4)[2];
        tijd = value.split("%",4)[3];
                //= value.substring(value.length() - 22);

        gebruikerText.setText(gebruiker);
        hoeveelheidText.setText(hoeveelheid);
        redenText.setText(reden);
        tijdText.setText(tijd);
    }


    public void btnAccept (View view) {
        //dit add naar de sqlite database, moet ook nog naar de mysql database!
        AddData(gebruiker, hoeveelheid, reden, tijd);
    }

    public void AddData(String gebruiker, String hoeveelheid, String reden, String tijd) {
        boolean insertData = mDatabaseHelper.addData(gebruiker, hoeveelheid, reden, tijd);

        if (insertData) {
            System.out.print("Data succesfully inserted");
        }
        else {
            System.out.print("Something went wrong in Request.java when inserting the data");
        }

        Intent intent = new Intent(this, PoefToevoegen.class);
        startActivity(intent);
    }

    public void btnDecline (View view) {
        Intent intent = new Intent(this, PoefToevoegen.class);
        startActivity(intent);
    }



}
