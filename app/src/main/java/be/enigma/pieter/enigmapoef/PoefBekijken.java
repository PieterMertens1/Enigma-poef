package be.enigma.pieter.enigmapoef;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.database.PoefDAO;
import be.enigma.pieter.enigmapoef.models.Poef;
import cz.msebera.android.httpclient.Header;

import static be.enigma.pieter.enigmapoef.database.BaseDAO.getConnectie;

public class PoefBekijken extends AppCompatActivity {

    private static final String TAG = "PoefBekijken =>";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    DatabaseHelper mDatabaseHelper;
    private ListView mListView;

    TextView textview;
    String user;

    ArrayList<Poef> poeflijstMysql;

    AsyncTask<String, String, String> mAsyncTask;


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
            user = account.getEmail();
        }
        else if (FirebaseAuth.getInstance() != null) {
            mAuth = FirebaseAuth.getInstance();
            //mDatabase = FirebaseDatabase.getInstance().getReference();
            textview.setText(mAuth.getCurrentUser().getEmail());
            user = mAuth.getCurrentUser().getEmail();
        }


        mDatabaseHelper = new DatabaseHelper(this);

        poeflijstMysql = new ArrayList<>();


        mAsyncTask = new AsyncTask<String, String, String>() {


            @Override
            protected String doInBackground(String... strings) {

                PoefDAO poefDAO = new PoefDAO();
                if (getConnectie() != null) {

                    poeflijstMysql = poefDAO.getAllPoefFromUser(user);

                    Log.wtf(TAG, "doInBackground: Succes!");
                } else {
                    Log.wtf(TAG, "doInBackground: Connection is null");

                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                Log.wtf(TAG, "Poefbekijken: populateListview()" );

                populateListview();
            }
        };

        //------------------------------ get mysql data ------------------------------------------

        mAsyncTask.execute();

        //-----------------------------------------------------------------------------------------







    }

    private void populateListview() {
        System.out.print(TAG + "populateListview displaying data in the listview");


        Cursor data = mDatabaseHelper.getPoefByUser(textview.getText().toString());
        mListView= findViewById(R.id.mListView);

        /*ArrayList<String> poeflijstSQLITE = new ArrayList<>();
        while (data.moveToNext()) {
            //getstring => column 1 (zero based counting => column 1 = gebruiker)

            //listData.add(data.getString(1)); gebruiker
            //listData.add(data.getString(2)); aantal
            //listData.add(data.getString(3)); reden
            //listData.add(data.getString(4)); tijd

            String s = data.getString(4);
            s = s.substring(0, Math.min(s.length(), 10));

            String insertValue = s + ": €" + data.getString(2) + " " + data.getString(3);
            poeflijstSQLITE.add(insertValue);
        }*/

        ArrayList<Poef> poeflijstSQLITE = new ArrayList<>();

        ArrayList<String> formatedSQLITE = new ArrayList<>();
        ArrayList<String> formatedMYSQL = new ArrayList<>();

        while (data.moveToNext()) {
            Poef poef = new Poef();

            poef.setGebruiker(data.getString(1)); //listData.add(data.getString(1)); gebruiker
            poef.setHoeveelheid(data.getString(2)); //listData.add(data.getString(2)); aantal
            poef.setReden(data.getString(3)); //listData.add(data.getString(3)); reden
            poef.setTijd(data.getString(4)); //listData.add(data.getString(4)); tijd

            String tijd = poef.getTijd();
            tijd = tijd.substring(0, Math.min(tijd.length(), 10));

            String insertValue = tijd + ": €" + poef.getHoeveelheid() + " " + poef.getReden();
            formatedSQLITE.add(insertValue);
            poeflijstSQLITE.add(poef);
        }

        for (Poef poef: poeflijstMysql)
        {
            String tijd = poef.getTijd();
            tijd = tijd.substring(0, Math.min(tijd.length(), 10));

            String insertValue = tijd + ": €" + poef.getHoeveelheid() + " " + poef.getReden();
            formatedMYSQL.add(insertValue);
        }


        ArrayList<String> totaal = formatedSQLITE;
        for (String s : formatedMYSQL){
            if (!(formatedSQLITE.contains(s)))
                totaal.add(s);
        }

        // Sorting
        Collections.sort(totaal, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });

        if (totaal != new ArrayList<String>()) {
            ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, totaal);
            mListView.setAdapter(adapter);
        }

    }

    @Override
    public void onStart() {

        super.onStart();



    }

    //----------------------DEZE CODE IS OM DE SQLITE DATABASE TE SYNCHRONISEREN MET DE MYSQL DATABASE--------------------------------
   /* public void syncSQLiteMySQLDB(){

        //Create AsycHttpClient object

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<Poef> mPoeflijst = new ArrayList<>();

        Cursor poeflijst;
        poeflijst =  mDatabaseHelper.getPoefByUser(user);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        while(poeflijst.moveToNext()) {
            mPoeflijst.add(new Poef(poeflijst.getString(poeflijst.getColumnIndex("gebruiker")), poeflijst.getString(poeflijst.getColumnIndex("hoeveelheid")),poeflijst.getString(poeflijst.getColumnIndex("reden")),poeflijst.getString(poeflijst.getColumnIndex("tijd"))));
        }

        if (mPoeflijst.size() != 0) {
            if (mDatabaseHelper.dbSyncCount() !=0) {
                params.put("PoefJSON", mDatabaseHelper.composeJSONfromSQLite());
                client.post("http://192.168.2.4:9000/sqlitemysqlsync/insertuser.php",params , new AsyncHttpResponseHandler() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.wtf(TAG, "onSuccess: " + responseBody);
                        try {
                            JSONArray arr = new JSONArray(responseBody);
                            Log.i(TAG, "onSuccess: arr length: " + arr.length());
                            for (int i= 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject)arr.get(i);
                                mDatabaseHelper.updateSyncStatus(obj.get("gebruiker").toString(), obj.get("hoeveelheid").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        }
    }
*/











}
