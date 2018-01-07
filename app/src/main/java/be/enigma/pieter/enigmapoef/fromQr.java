package be.enigma.pieter.enigmapoef;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.database.PoefDAO;
import be.enigma.pieter.enigmapoef.models.Poef;

import static be.enigma.pieter.enigmapoef.database.BaseDAO.getConnectie;

public class fromQr extends AppCompatActivity {

    private static final String TAG = "FromQR => " ;
    private DatabaseHelper mDatabaseHelper;
    String value;

    String gebruiker;
    String hoeveelheid;
    String reden;
    String tijd;

    TextView gebruikerText;
    TextView hoeveelheidText;
    TextView redenText;
    TextView tijdText;


    TextView gebruikerDbText;
    TextView hoeveelheidDbText;
    TextView redenDbText;
    TextView tijdDbText;

    private ProgressBar progressBar;

    Poef poef = new Poef();
    int id = 0;

    private static AsyncTask<String, String, String> mAsyncTask;
    private static AsyncTask<String, String, String> mAsyncTask2;
    private static AsyncTask<String, String, String> mAsyncTask3;

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

        progressBar = findViewById(R.id.progressBar_cyclic);
        progressBar.setIndeterminate(true);


        gebruikerText = findViewById(R.id.GebruikerText);
        hoeveelheidText = findViewById(R.id.HoeveelheidText);
        redenText = findViewById(R.id.RedenText);
        tijdText = findViewById(R.id.TijdText);


        gebruikerDbText = findViewById(R.id.GebruikerDbText);
        hoeveelheidDbText = findViewById(R.id.BedragDbText);
        redenDbText = findViewById(R.id.RedenDbText);
        tijdDbText = findViewById(R.id.TijdDbText);

       mAsyncTask = new AsyncTask<String, String, String>() {


            @Override
            protected String doInBackground(String... strings) {

                PoefDAO poefDAO = new PoefDAO();
                if (getConnectie() != null) {
                    //insert poef into database

                    Log.wtf(TAG, "doInBackground: Poef before dao:" + poef.toString());
                    id = poefDAO.getId(poef);


                    Log.wtf(TAG, "doInBackground: Succes!");
                } else {
                    Log.wtf(TAG, "doInBackground: Connection is null");

                }
                return null;
            }


            @Override
            protected void onPostExecute(String result) {
                Log.wtf(TAG, "OnCreate: ID: " + id);

                if(id != 0) {

                    //gebruikerDbText.setText(Integer.toString(id) + "goedzo");
                }
                else {
                    //gebruikerDbText.setText(Integer.toString(id) + " niet goed");
                }

            }
        };
        mAsyncTask2 = new AsyncTask<String, String, String>() {


            @Override
            protected String doInBackground(String... strings) {

                PoefDAO poefDAO = new PoefDAO();
                if (getConnectie() != null && id !=0) {
                    //insert poef into database


                    poef = poefDAO.geefPoefVanId(id);

                    Log.wtf(TAG, "doInBackground2: " + poef.toString());
                    Log.wtf(TAG, "doInBackground2: Succes!");
                } else {
                    Log.wtf(TAG, "doInBackground2: Connection is null");

                }
                return null;
            }


            @Override
            protected void onPostExecute(String result) {
                Log.wtf(TAG, "OnCreate: ID: " + id);

                if(id != 0) {

                    gebruikerDbText.setText(poef.getGebruiker());
                    hoeveelheidDbText.setText(poef.getHoeveelheid());
                    redenDbText.setText(poef.getReden());
                    tijdDbText.setText(poef.getTijd());
                    //gebruikerDbText.setText(Integer.toString(id) + "goedzo");
                }
                else {
                    //gebruikerDbText.setText(Integer.toString(id) + " niet goed");
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        };
        mAsyncTask3 = new AsyncTask<String, String, String>() {


            @Override
            protected String doInBackground(String... strings) {

                PoefDAO poefDAO = new PoefDAO();
                if (getConnectie() != null) {

                    poefDAO.setChecked(poef);


                    Log.wtf(TAG, "doInBackground3: Succes!");
                } else {
                    Log.wtf(TAG, "doInBackground3: Connection is null");

                }
                return null;
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        gebruiker = value.split("%",4)[0];
        hoeveelheid = value.split("%",4)[1];
        reden = value.split("%",4)[2];
        tijd = value.split("%",4)[3];


        gebruikerText.setText(gebruiker);
        hoeveelheidText.setText(hoeveelheid);
        redenText.setText(reden);
        tijdText.setText(tijd);

        poef = new Poef(gebruiker, hoeveelheid, reden, tijd);


        mAsyncTask.execute("");
        mAsyncTask2.execute("");
        Log.wtf(TAG, "AddData: after");


    }

    public void btnAccept (View view) {
        //dit add naar de sqlite database, moet ook nog naar de mysql database!
        AddData(gebruiker, hoeveelheid, reden, tijd);

        mAsyncTask3.execute("");


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
