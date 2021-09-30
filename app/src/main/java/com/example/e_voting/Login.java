package com.example.e_voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText voterEmail, voterPassword;
    Button userLoginBtn;
    TextView userCreateBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        voterEmail = findViewById(R.id.email);
        voterPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        userLoginBtn = findViewById(R.id.buttonlogin);
        userCreateBtn = findViewById(R.id.registerCreate2);


        userLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = voterEmail.getText().toString().trim();
                String password = voterPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    voterEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    voterPassword.setError("Password is Required");
                    return;

                }

                if(password.length() < 9 ){
                    voterPassword.setError("Password must contian atleast 10 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(Login.this, "Login in Successfull!.",Toast.LENGTH_SHORT).show();
                           checkAccesslvl();
                           //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                       }else {
                           Toast.makeText(Login.this, "Error please enter correct details" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           progressBar.setVisibility(View.GONE);
                       }
                    }
                });

            }
        });

        userCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

    }
    private void checkAccesslvl(){
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String uid = currentFirebaseUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference df = db.collection("users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","onSuccess"+documentSnapshot.getData());

                if(documentSnapshot.getString("isAdmin").equals("0")){
                    startActivity(new Intent(getApplicationContext(),PhoneVerification.class));

                }

                if(documentSnapshot.getString("isAdmin").equals("1")){
                    startActivity(new Intent(getApplicationContext(),Admin.class));
                }

            }
        });


    }
}