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
import java.util.ArrayList;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.database.PoefDAO;
import be.enigma.pieter.enigmapoef.models.Poef;

public class PoefBekijken extends AppCompatActivity {

    private static final String TAG = "PoefBekijken =>";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    DatabaseHelper mDatabaseHelper;
    private ListView mListView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poef_bekijken);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //[Checken welke user aangemeld is]
        TextView textview = (TextView) findViewById(R.id.eigenaarText);

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

        Cursor data = mDatabaseHelper.getData();
         mListView= findViewById(R.id.mListView);

        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            //getstring => column 1 (zero based counting => column 1 = gebruiker)
            listData.add(data.getString(1));
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

    public void GetData(View view) {

















        //Deze code is om gegevens op te halen uit de mysql database maar de connectie wordt momenteel nog niet gemaakt
/*        PoefDAO poefDAO = new PoefDAO();

        ArrayList<Poef> test = poefDAO.geefAllePoef();


        TextView poefText = findViewById(R.id.PoefText);
        poefText.setText(test.toString());*/
    }




    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }



}
