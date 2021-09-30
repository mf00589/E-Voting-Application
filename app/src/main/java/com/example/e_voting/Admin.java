package com.example.e_voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Admin extends AppCompatActivity {
    Button logout,addUser,vote_count,openVoting,closeVoting;
    private FirebaseFirestore db;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    public static final String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        logout = findViewById(R.id.button4);
        addUser = findViewById(R.id.addUser);
        vote_count = findViewById(R.id.vote_count);
        openVoting = findViewById(R.id.openVoting);
        closeVoting = findViewById(R.id.closeVoting);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddUser.class));
            }
        });

        vote_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),VoteCounting.class));

            }
        });

        openVoting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = "eHl7csSHK1BTQCdV0deR";
                DocumentReference df = db.collection("votingPeriods").document(uid);
                df.update("isVotingOpen","1").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Admin.this, "Updated.",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);

                    }
                });




            }
        });

        closeVoting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVotingOpen();
            }
        });









    }
    private void isVotingOpen(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = "eHl7csSHK1BTQCdV0deR";
        DocumentReference df = db.collection("votingPeriods").document(uid);

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","onSuccess"+documentSnapshot.getData());

                if(documentSnapshot.getString("isVotingOpen").equals("0")){
                    Toast.makeText(Admin.this, "Voting is Not Opened",Toast.LENGTH_SHORT).show();


                }

                if(documentSnapshot.getString("isVotingOpen").equals("1")){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String uid = "eHl7csSHK1BTQCdV0deR";
                    DocumentReference df = db.collection("votingPeriods").document(uid);
                    df.update("isVotingOpen","0").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Admin.this, "Updated.",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);

                        }
                    });



                    startActivity(new Intent(getApplicationContext(),Admin.class));
                }

            }
        });
    }
}