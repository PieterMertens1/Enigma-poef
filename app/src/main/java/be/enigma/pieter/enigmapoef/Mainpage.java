package be.enigma.pieter.enigmapoef;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class Mainpage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }


    public void poefToevoegen(View view) {
        Intent intent = new Intent(this, PoefToevoegen.class);
        startActivity(intent);
    }

    public void poefBekijken(View view) {
        Intent intent = new Intent(this, PoefBekijken.class);
        startActivity(intent);
    }

    public void poefBetalen(View view) {


        //Write your code
        Log.wtf("test", "onOptionsItemSelected: betalen " );


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.rgb(0,100,0));
        textView.setTextSize(25);
        textView.setText("\n Om te betalen moet u de Bancontact app geïnstalleerd hebben");

        alert.setView(textView);

        alert.setPositiveButton("Ik heb de Bancontact app", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("mobi.inthepocket.bcmc.bancontact");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }

            }
        });

        alert.setNegativeButton("Ik betaal cash", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();


    }



    public void Test(View view) {

        Intent intent = new Intent(this, Request.class);
        startActivity(intent);
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
