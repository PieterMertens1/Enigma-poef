package be.enigma.pieter.enigmapoef;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class fromQr extends AppCompatActivity {

    String value;

    String gebruiker;
    String hoeveelheid;
    String reden;
    String tijd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_qr);




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
}
