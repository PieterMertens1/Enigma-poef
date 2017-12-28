package be.enigma.pieter.enigmapoef;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.database.PoefDAO;
import be.enigma.pieter.enigmapoef.models.Poef;

public class PoefBekijken extends AppCompatActivity {

    private static final String TAG = "PoefBekijken =>";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    DatabaseHelper mDatabaseHelper;
    private ListView mListView;

    TextView textview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poef_bekijken);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //[Checken welke user aangemeld is]
        textview = (TextView) findViewById(R.id.eigenaarText);

        if (GoogleSignIn.getLastSignedInAccount(this) != null){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            textview.setText(account.getEmail());
        }
        else if (FirebaseAuth.getInstance() != null) {
            mAuth = FirebaseAuth.getInstance();
            //mDatabase = FirebaseDatabase.getInstance().getReference();
            textview.setText(mAuth.getCurrentUser().getEmail());
        }


        mDatabaseHelper = new DatabaseHelper(this);
        
        populateListview();

    }

    private void populateListview() {
        System.out.print(TAG + "populateListview displaying data in the listview");

        //--------- Eerst lokale mDatabaseHelper gelijk zetten met remote mysql server ------------







        //-----------------------------------------------------------------------------------------



        Cursor data = mDatabaseHelper.getPoefByUser(textview.getText().toString());
         mListView= findViewById(R.id.mListView);

        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            //getstring => column 1 (zero based counting => column 1 = gebruiker)

            //listData.add(data.getString(1)); gebruiker
            //listData.add(data.getString(2)); aantal
            //listData.add(data.getString(3)); reden
            //listData.add(data.getString(4)); tijd

            String s = data.getString(4);
            s = s.substring(0, Math.min(s.length(), 10));

            String insertValue = s + ": €" + data.getString(2) + " " + data.getString(3);
            listData.add(insertValue);
        }

        if (listData != new ArrayList<String>()) {
            ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
            mListView.setAdapter(adapter);
        }


    }

    @Override
    public void onStart() {

        super.onStart();



    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }



}
