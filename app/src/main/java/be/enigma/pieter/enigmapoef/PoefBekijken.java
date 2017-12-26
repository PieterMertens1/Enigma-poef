package be.enigma.pieter.enigmapoef;

import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import be.enigma.pieter.enigmapoef.models.Poef;

public class PoefBekijken extends AppCompatActivity {

    private static final String TAG = "PoefBekijken =>";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

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



    }

    @Override
    public void onStart() {

        super.onStart();

        // -------------------- code voor uitlezen database

        /*final DatabaseReference demoRef = mDatabase.child("users");
        Button fetch = findViewById(R.id.PoefButton);

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                demoRef.child(mAuth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {

                    TextView textView = findViewById(R.id.PoefText);

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Poef value = dataSnapshot.getValue(Poef.class);

                        if (value != null) {
                            textView.setText("gelukt");
                        } else {
                            textView.setText("aiaiai");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        });*/
        //------------------------------------------------------------------------

    }

    public void GetData(View view) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("data/users");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Poef post = dataSnapshot.getValue(Poef.class);
                System.out.println(post.getReden());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }




    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }



}
