package be.enigma.pieter.enigmapoef;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.database.PoefDAO;
import be.enigma.pieter.enigmapoef.models.Poef;

import static be.enigma.pieter.enigmapoef.database.BaseDAO.getConnectie;

public class PoefBekijken extends AppCompatActivity {

    private static final String TAG = "PoefBekijken =>";
    private FirebaseAuth mAuth;

    DatabaseHelper mDatabaseHelper;
    private ListView mListView;

    TextView textview;
    TextView totaalPoefText;
    String user;

    ArrayList<Poef> poeflijstMysql;

    AsyncTask<String, String, String> mAsyncTask;

    private float totaalPoef = 0;

    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poef_bekijken);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }


        mAuth = FirebaseAuth.getInstance();

        //[Checken welke user aangemeld is]
        textview = (TextView) findViewById(R.id.eigenaarText);
        totaalPoefText = findViewById(R.id.TotaalText);
        progressBar = findViewById(R.id.progressBar_cyclic);
        progressBar.setIndeterminate(true);

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

    public void populateListview() {
        System.out.print(TAG + "populateListview displaying data in the listview");


        Cursor data = mDatabaseHelper.getPoefByUser(user);
        mListView= findViewById(R.id.mListView);

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


            if (!(poef.getHoeveelheid().equals("") || poef.getHoeveelheid() == null)) {
                totaalPoef += Float.parseFloat(poef.getHoeveelheid());
            }
            else {
                totaalPoef += 0;
            }


            String insertValue = tijd + ": €" + poef.getHoeveelheid() + " " + poef.getReden();
            formatedSQLITE.add(insertValue);
            poeflijstSQLITE.add(poef);
        }

        for (Poef poef: poeflijstMysql)
        {
            String tijd = poef.getTijd();
            tijd = tijd.substring(0, Math.min(tijd.length(), 10));

            if (!(poef.getHoeveelheid().equals("") || poef.getHoeveelheid() == null)) {
                totaalPoef += Float.parseFloat(poef.getHoeveelheid());
            }
            else {
                totaalPoef += 0;
            }

            String insertValue = tijd + ": €" + poef.getHoeveelheid() + " " + poef.getReden();
            formatedMYSQL.add(insertValue);
        }


        ArrayList<String> totaal = formatedSQLITE;
        for (String s : formatedMYSQL){
            if (!(formatedSQLITE.contains(s)))
                totaal.add(s);
        }

        // Sorting is ni volledig juist (word wel gesorteerd op dag maar niet op sec door de formatting)
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

        Log.wtf(TAG, "populateListview: Totaal: " + totaalPoef);

        totaalPoefText.setText(getString(R.string.totaal_poef) + Float.toString(totaalPoef/2)); // omdat 2 keer toegevoegd in 2 verschillende databases is de totaalpoef dubbel zo hoog als moet
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {

        super.onStart();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.Toevoegen:
                //Write your code
                Log.wtf("test", "onOptionsItemSelected: toevoegen" );
                intent = new Intent(this, PoefToevoegen.class);
                startActivity(intent);


                return true;
            case R.id.Bekijken:
                //Write your code
                Log.wtf("test", "onOptionsItemSelected: bekijken" );
                intent = new Intent(this, PoefBekijken.class);
                startActivity(intent);


                return true;
            case R.id.Betalen:
                //Write your code
                Log.wtf("test", "onOptionsItemSelected: betalen" );
                intent = new Intent(Intent.ACTION_SEND);


                // Always use string resources for UI text.
                // This says something like "Share this photo with"
                String title = getResources().getString(R.string.app_name);
                // Create intent to show chooser
                Intent chooser = Intent.createChooser(intent, title);

                // Verify the intent will resolve to at least one activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }


                return true;
            case R.id.Afmelden:
                //Write your code
                Log.wtf("test", "onOptionsItemSelected: afmelden" );

                FirebaseAuth.getInstance().signOut();

                GoogleSignInClient mGoogleSignInClient;

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

                mGoogleSignInClient.signOut();
                mGoogleSignInClient.revokeAccess();


                intent = new Intent(this, MainActivity.class);

                startActivity(intent);


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
