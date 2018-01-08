package be.enigma.pieter.enigmapoef;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.Result;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.database.PoefDAO;
import be.enigma.pieter.enigmapoef.database.UserDAO;
import be.enigma.pieter.enigmapoef.models.Poef;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
import static be.enigma.pieter.enigmapoef.database.BaseDAO.getConnectie;

public class PoefToevoegen extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final String TAG = "PoefToevoegen => ";
    private FirebaseAuth mAuth;

    private static AsyncTask<String, String, String> mAsyncTask;
    private static AsyncTask<String, String, String> mAsyncTask2;


    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;

    private DatabaseHelper mDatabaseHelper;
    private Button Scanbutton;

    String gebruiker;
    int isBestuur = 0;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poef_toevoegen);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        TextView textview = (TextView) findViewById(R.id.naamText);
        Scanbutton = findViewById(R.id.ScanButton);


        if (GoogleSignIn.getLastSignedInAccount(this) != null){

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            textview.setText(account.getDisplayName());
            gebruiker = account.getEmail();

        }
        else if (FirebaseAuth.getInstance() != null) {
            mAuth = FirebaseAuth.getInstance();
            textview.setText(mAuth.getCurrentUser().getEmail());
            gebruiker = mAuth.getCurrentUser().getEmail();
        }

        mScannerView = new ZXingScannerView(this);
        mDatabaseHelper = new DatabaseHelper(this);

        progressBar = findViewById(R.id.progressBar_cyclic);
        progressBar.setIndeterminate(true);


        mAsyncTask2 = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                UserDAO userDAO = new UserDAO();

                if (getConnectie() != null) {
                    isBestuur = userDAO.checkIfBestuur(gebruiker);

                    Log.wtf(TAG, "doInBackground: Succes!");
                } else {
                    Log.wtf(TAG, "doInBackground: Connection is null");

                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                Log.wtf(TAG, "Poeftoevoegen: scanbutton zichtbaar maken" );

                if (isBestuur == 1) {
                    Scanbutton.setVisibility(View.VISIBLE);
                }
            }
        };

        mAsyncTask2.execute("");



    }


    public void poefToevoegen(View view) {


        TextView naamText = findViewById(R.id.naamText);

        EditText bedragText = (EditText) findViewById(R.id.bedragInput);
        EditText redenText = (EditText) findViewById(R.id.redenInput);

        //final String gebruiker = mAuth.getCurrentUser().getEmail();
        final String hoeveelheid = bedragText.getText().toString();
        final String reden = redenText.getText().toString();
        String tijd = "";

        if (gebruiker != null && hoeveelheid != "" && reden != "") {
            final Poef mijnPoefPoef = new Poef(gebruiker, hoeveelheid, reden, tijd);
            tijd = mijnPoefPoef.getTijd();

            //to remote mysql database
            addData(mijnPoefPoef);
            //to local sqlite database
            AddData(gebruiker, hoeveelheid, reden, tijd);
        }


        progressBar.setVisibility(View.VISIBLE);


        final Intent intent = new Intent(this, toQr.class);




        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.rgb(0,100,0));
        textView.setTextSize(25);
        textView.setText("\n" +
                getString(R.string.bent_u_zeker) + hoeveelheid + getString(R.string.aan_poef_toevoegen));

        alert.setView(textView);

        final String finalTijd = tijd;


        alert.setPositiveButton(R.string.ja, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                intent.putExtra("Gebruiker", gebruiker);
                intent.putExtra("Hoeveelheid", hoeveelheid);
                intent.putExtra("Reden", reden);
                intent.putExtra("Tijd", finalTijd);
                startActivity(intent);

            }
        });

        alert.setNegativeButton(R.string.nee, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();



    }

    void addData(final Poef poef) {

        Log.wtf(TAG, "AddData: before");

        mAsyncTask = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                PoefDAO poefDAO = new PoefDAO();

                if (getConnectie() != null) {
                    //insert poef into database
                    poefDAO.insert(poef);

                    Log.wtf(TAG, "doInBackground: Succes!");
                } else {
                    Log.wtf(TAG, "doInBackground: Connection is null");

                }
                return null;
            }
        };

        mAsyncTask.execute("");
        Log.wtf(TAG, "AddData: after");
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


    public void ScanQR (View view) {

        setContentView(mScannerView);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Log.d(TAG,"Permission already granted");

            } else {
                requestPermission();
            }
        }

    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Log.d(TAG, "onRequestPermissionsResult: Permission Granted, Now you can access camera ");
                    }else {
                        Log.d(TAG, "onRequestPermissionsResult: Permission Denied, You cannot access and camera ");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel(getString(R.string.camera_permissie),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PoefToevoegen.this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.annuleer, null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }


    @Override
    public void handleResult(final Result rawResult) {


        final Intent intent = new Intent(this, fromQr.class);

        final String result = rawResult.getText();
        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.scan_result);
        builder.setPositiveButton(R.string.opnieuw, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               mScannerView.resumeCameraPreview(PoefToevoegen.this);
            }
        });
        builder.setNeutralButton(getString(R.string.toevoegen), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                intent.putExtra("Value", result);
                startActivity(intent);
            }
        });
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
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


}
