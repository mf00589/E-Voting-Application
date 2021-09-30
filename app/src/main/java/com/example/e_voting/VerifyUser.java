package com.example.e_voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VerifyUser extends AppCompatActivity {
    Button verifyuser,checkVote,home,delete;
    TextView code;
    public static final String TAG = "TAG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user);

        verifyuser = findViewById(R.id.verbutton);
        delete = findViewById(R.id.deleteacc);
        code = findViewById(R.id.vercode);
        home = findViewById(R.id.Home);
        checkVote = findViewById(R.id.CheckVote);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(VerifyUser.this)
                        .setTitle("DELETE ACCOUNT")
                        .setMessage("Are you sure you want to delete account?")
                        .setPositiveButton("Ok",null)
                        .setNegativeButton("Cancel",null)
                        .show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User account deleted.");
                                        }
                                    }
                                });

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("users").document(user.getUid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), Login.class));

                    }
                });
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        checkVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = "eHl7csSHK1BTQCdV0deR";
                DocumentReference df = db.collection("votingPeriods").document(uid);

                df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d("TAG","onSuccess"+documentSnapshot.getData());

                        if(documentSnapshot.getString("isVotingOpen").equals("0")){
                            Toast.makeText(VerifyUser.this, "Voting closed.",Toast.LENGTH_SHORT).show();
                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                            String uid = currentFirebaseUser.getUid();
                            DocumentReference docRef = db.collection("users").document(uid);

                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            String value = document.getString("publickey");
                                            Toast.makeText(VerifyUser.this, "publickey retrieved",Toast.LENGTH_SHORT).show();
                                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                            String uid = currentFirebaseUser.getUid();
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("voters")
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                           if(document.get("pubkey").equals(value)){
                                                               Toast.makeText(VerifyUser.this, "Vote is Counted.",Toast.LENGTH_SHORT).show();
                                                           }
                                                        }


                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }

                                                }
                                            });

                                        }else{
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }

                                    }else{
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }

                                }
                            });


                        }else{
                            Toast.makeText(VerifyUser.this, "Voting Period Not Finished",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });


        verifyuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String votercode = code.getText().toString().trim();
                db.collection("codes")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                ArrayList<String> arrayListOfFruit = new ArrayList<String>();
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        if(document.get("code").equals(votercode)){
                                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                            String uid = currentFirebaseUser.getUid();

                                            FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                                            DocumentReference washingtonRef = db.collection("users").document(uid);

                                            washingtonRef
                                                    .update("isVerified", "1")
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(VerifyUser.this, "Voter Account Updated.",Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);
                                                        }
                                                    });
                                        }

                                    }


                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }

                            }
                        });
            }
        });
    }
}