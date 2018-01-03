package be.enigma.pieter.enigmapoef;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

import be.enigma.pieter.enigmapoef.database.DatabaseHelper;
import be.enigma.pieter.enigmapoef.database.PoefDAO;
import be.enigma.pieter.enigmapoef.models.Poef;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import static android.Manifest.permission.CAMERA;
import static be.enigma.pieter.enigmapoef.database.BaseDAO.getConnectie;

public class PoefToevoegen extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final String TAG = "PoefToevoegen => ";
    private FirebaseAuth mAuth;

    private static AsyncTask<String, String, String> mAsyncTask;

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;

    private DatabaseHelper mDatabaseHelper;

    String gebruiker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poef_toevoegen);

        TextView textview = (TextView) findViewById(R.id.naamText);


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


    }


    public void poefToevoegen(View view) {


        TextView naamText = findViewById(R.id.naamText);

        EditText bedragText = (EditText) findViewById(R.id.bedragInput);
        EditText redenText = (EditText) findViewById(R.id.redenInput);

        //final String gebruiker = mAuth.getCurrentUser().getEmail();
        final String hoeveelheid = bedragText.getText().toString();
        final String reden = redenText.getText().toString();
        String tijd = "";

        final Poef mijnPoefPoef = new Poef(gebruiker, hoeveelheid, reden, tijd);
        tijd = mijnPoefPoef.getTijd();

        //to remote mysql database
        addData(mijnPoefPoef);
        //to local sqlite database
        AddData(gebruiker, hoeveelheid, reden, tijd);

        final Intent intent = new Intent(this, toQr.class);




        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.rgb(0,100,0));
        textView.setTextSize(25);
        textView.setText("Bent u zeker dat u: €" + hoeveelheid + " aan uw poef wilt toevoegen?");

        alert.setView(textView);

        final String finalTijd = tijd;


        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                intent.putExtra("Gebruiker", gebruiker);
                intent.putExtra("Hoeveelheid", hoeveelheid);
                intent.putExtra("Reden", reden);
                intent.putExtra("Tijd", finalTijd);
                startActivity(intent);

            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    private void addData(final Poef poef) {

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
                                showMessageOKCancel("You need to allow access to both the permissions",
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
        new android.support.v7.app.AlertDialog.Builder(PoefToevoegen.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
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

        TextView resultText = findViewById(R.id.ResultText);

        final String result = rawResult.getText();
        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("Opnieuw", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               mScannerView.resumeCameraPreview(PoefToevoegen.this);
            }
        });
        builder.setNeutralButton("Toevoegen", new DialogInterface.OnClickListener() {
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



    //mail encoderen omdat "." niet toegestaan is in firebase
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }


}
