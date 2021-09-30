package com.example.e_voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddUser extends AppCompatActivity {
    EditText email;
    Button add;
    FirebaseAuth fAuth;
    String userID;
    FirebaseFirestore fStore;
    public static final String TAG = "TAG";
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

         email = findViewById(R.id.useremail);
         add = findViewById(R.id.add);



         add.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //FirebaseFirestore db = FirebaseFirestore.getInstance();
                 long code = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
                 String strcode = Long.toString(code);
                 String voteremail = email.getText().toString().trim();
                 //FirebaseUser user = fAuth.getCurrentUser();
                 //userID = fAuth.getCurrentUser().getUid();
                 FirebaseFirestore db = FirebaseFirestore.getInstance();

                 Map<String, Object> user = new HashMap<>();
                 user.put("email", voteremail);
                 user.put("code", strcode);

                 db.collection("codes").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                     @Override
                     public void onSuccess(DocumentReference documentReference) {
                         Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                         startActivity(new Intent(getApplicationContext(),Admin.class));
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Log.w(TAG, "Error adding document", e);

                     }
                 });


             }
         });












    }
}