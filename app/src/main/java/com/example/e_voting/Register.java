package com.example.e_voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText voterName, voterEmail, voterPassword, voterPhone;
    Button vRegisterBtn;
    CheckBox checkBox,checkBox2;
    TextView vLoginBtn,textviewpriv;
    boolean isAdmin;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    boolean isFieldSet= true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        checkBox2 = (CheckBox)findViewById(R.id.privAgree);
        textviewpriv = findViewById(R.id.textView8);



        voterName = findViewById(R.id.name);
        voterEmail = findViewById(R.id.email);
        voterPassword = findViewById(R.id.password);
        voterPhone = findViewById(R.id.phone);
        vRegisterBtn = findViewById(R.id.register);
        vLoginBtn = findViewById(R.id.registerCreate2);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);


        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        textviewpriv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Privacypolicy.class));
            }
        });


        vRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isFieldEmpty(voterName);
                isFieldEmpty(voterEmail);
                isFieldEmpty(voterPhone);
                isFieldEmpty(voterPassword);

                String email = voterEmail.getText().toString().trim();
                String password = voterPassword.getText().toString().trim();
                String fullName = voterName.getText().toString();
                String phone = voterPhone.getText().toString();

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

                //register the User
                if(checkBox2.isChecked()) {
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = fAuth.getCurrentUser();
                                //Toast.makeText(Register.this, "Voter Account Created.",Toast.LENGTH_SHORT).show();
                                userID = fAuth.getCurrentUser().getUid();
                                if (isAdmin) {
                                    DocumentReference documentReference = fStore.collection("users").document(user.getUid());

                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("fullName", fullName);
                                    userInfo.put("email", email);
                                    userInfo.put("phone", phone);
                                    userInfo.put("isAdmin", "1");
                                    documentReference.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: user Profile is created for " + userID);

                                        }
                                    });
                                    startActivity(new Intent(getApplicationContext(), Admin.class));

                                } else {
                                    DocumentReference documentReference = fStore.collection("users").document(user.getUid());

                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("fullName", fullName);
                                    userInfo.put("email", email);
                                    userInfo.put("phone", phone);
                                    userInfo.put("publickey", "");
                                    userInfo.put("isAdmin", "0");
                                    userInfo.put("isVerified", "0");
                                    userInfo.put("hasvoted", "0");
                                    documentReference.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                            //Toast.makeText(Register.this, "this is a tesst to be created", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    Toast.makeText(Register.this, "going to the main activity", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Register.this , "entering main activity", Toast.LENGTH_SHORT).show();




                                }


                            } else {

                                Toast.makeText(Register.this, "Error Account cannot be created." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }else{
                    Toast.makeText(Register.this, "Please agree to privacy policy",Toast.LENGTH_SHORT).show();
                }

            }
        });

        vLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }

    public boolean isFieldEmpty(EditText textfield){
        if(textfield.getText().toString().isEmpty()){
            textfield.setError("Empty Field");
            isFieldSet = false;
        }else{
            isFieldSet = true;
        }
        return isFieldSet;
    }

    public void itemClicked(View v) {
        if (((CheckBox) v).isChecked()) {
            isAdmin = true;
            Toast.makeText(Register.this,
                    "Checked", Toast.LENGTH_LONG).show();
        }
    }

    public void privacyClicked(View v){

    }
    private void testmethod(){
        int var  = 0;
        for(int i=0; i<6; i++){
            if(i%2== 0 ){
                Log.d("this is my message", "this is a test message");

            }
            else if (i%2 == 1 ){
                Log.d("testMessage2", "this is a test message 2");
                FirebaseUser auth = fAuth.getCurrentUser();

                DocumentReference df = fStore.collection("users").document();
                fAuth.getAccessToken().addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        Log.d("test message 2", "this is a test message 2 ");

                    }
                });


            }        }
    }
}