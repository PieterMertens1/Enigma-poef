package be.enigma.pieter.enigmapoef;

import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PoefBekijken extends AppCompatActivity {

    private static final String TAG = "PoefBekijken =>";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poef_bekijken);

        if (GoogleSignIn.getLastSignedInAccount(this) != null){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            displayName(account.getDisplayName());
        }
        else if (FirebaseAuth.getInstance() != null) {
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            displayName(mAuth.getCurrentUser().getEmail());
        }


        final DatabaseReference demoRef = mDatabase.child("Poef").child("piejiem@gmail,com");
        Button fetch = findViewById(R.id.btnFetch);

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                demoRef.child(mAuth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        TextView textView = findViewById(R.id.poefText);

                        if (dataSnapshot.getValue(String.class) != null)
                        {
                            String value = dataSnapshot.getValue(String.class);
                            textView.setText(value);
                        }
                        else {
                            textView.setText("aiaiai" );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        });







    }

    private void displayName(String displayName) {
        TextView textview = (TextView) findViewById(R.id.eigenaarText);

        if (displayName != null)
        {
            textview.setText("Poef van: " + displayName);
        }
        else {
            textview.setText("Poef van: aiaiai" );
        }
    }


    public void listen() {

       String user = mAuth.getCurrentUser().getEmail();
       user = encodeUserEmail(user);


       mDatabase.addValueEventListener(new ValueEventListener() {

           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

               Collection<Object> values = td.values();

               Log.d(TAG, "onDataChange: " + values.toString());

               for (Object value : values) {
                   Log.d(TAG, "onDataChange: " + value.toString());
               }


               //for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                   /*Poef post = postSnapshot.getValue(Poef.class);
                   Log.e("Get Data", post.getGebruiker());
                   Log.d(TAG, "onDataChange: " + post.toString());*/
               //}

           /*@Override
           public void onDataChange(DataSnapshot snapshot) {

               Log.e("Count " ,""+snapshot.getChildrenCount());
               for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            Poef post = postSnapshot.getValue(Poef.class);
                   Log.e("Get Data", post.getGebruiker());
               }
           }*/

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }


           //mDatabase.addChildEventListener((ChildEventListener) myQuery);


           //String value = mDatabase.child("Poef").child(user).getKey();

           //TextView textview = (TextView) findViewById(R.id.lijstText);
           //textview.setText(value);
       });
   }



    //mail encoderen omdat "." niet toegestaan is in firebase
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }



}
