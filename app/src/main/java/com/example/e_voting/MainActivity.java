package com.example.e_voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class MainActivity extends AppCompatActivity {

    TextView showKeys;
    Button genkey;
    Button keyMatch;
    Button retrievekeys;
    Button logout;
    Button signoff;
    Button verify;

    String privKey = "";
    String pubKey = "";
    String pubKey2 = "";
    String newPubkey  = "";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    public static final String TAG = "TAG";
    String userID;
    private FirebaseFirestore db;






    private static int flags = Base64.NO_WRAP | Base64.URL_SAFE ;
    private int KEY_LENGTH = 2048;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //showKeys = findViewById(R.id.publicKey);
        genkey = findViewById(R.id.genkey);
        keyMatch = findViewById(R.id.keyMatch);
        retrievekeys = findViewById(R.id.retrievekeys);
        signoff = findViewById(R.id.signoff);
        verify = findViewById(R.id.verify);

        logout =  findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), Voting.class));
                checkverifction();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),VerifyUser.class));
            }
        });

        signoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

         //KeyPair rsaKeyPair = createKeyPair();
        //rsaKeyPair.getPrivate();
        //rsaKeyPair.getPublic();
        //showKeys.setText(rsaKeyPair.getPublic().toString());
        genkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyPair rsaKeyPair = createKeyPair();
                if(rsaKeyPair.getPublic()!=null||rsaKeyPair.getPrivate()!=null){
                    Toast.makeText(MainActivity.this, "Key Generation Successfull!",Toast.LENGTH_SHORT).show();
                }
                pubKey = publicKeyToString(rsaKeyPair.getPublic());
                privKey = privateKeyToString(rsaKeyPair.getPrivate());
                storage(privKey);

            }
        });

        retrievekeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginWithFingerPrint();

            }
        });

        keyMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //KeyPair pair = createKeyPair();
                //pubKey = publicKeyToString(pair.getPublic());
                //privKey = privateKeyToString(pair.getPrivate());
                try {
                    String signature = sign("foobar", loadPrivateKey(privKey));
                    boolean isCorrect = verify("foobar", signature, stringToPublicKey(pubKey));

                    if(isCorrect){
                        Toast.makeText(MainActivity.this, "Signature Matches!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "No Match",Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                                    ///Add public key to a document
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                String uid = currentFirebaseUser.getUid();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference washingtonRef = db.collection("users").document(uid);

                // Set the "isCapital" field of the city 'DC'
                washingtonRef
                        .update("publickey", pubKey)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Voter Account Updated.",Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });

                DocumentReference docRef = db.collection("users").document(uid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            ArrayList<String> arrayListOfFruit = new ArrayList<String>();
                            if (document.exists()) {
                                String value = document.getString("publickey");

                                Log.d(TAG, "DocumentSnapshot data: " + value);
                                pubKey2 = value;
                                Toast.makeText(MainActivity.this, value,Toast.LENGTH_SHORT).show();

                                try {
                                    String signature = sign("foobar", loadPrivateKey(privKey));
                                    boolean isCorrect = verify("foobar", signature, stringToPublicKey(pubKey2));

                                    if(isCorrect){
                                        Toast.makeText(MainActivity.this, "Signature Matches2!",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "No Match",Toast.LENGTH_SHORT).show();

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                               // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });



                // FirebaseFirestore db = FirebaseFirestore.getInstance();
               // Map<String,Object> userInfo = new HashMap<>();
               // userInfo.put("PublicKey",pubKey);
               // db.collection("users").document(userID).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
               //     @Override
                //    public void onSuccess(Void aVoid) {
                 //       Log.d(TAG, "DocumentSnapshot successfully written!");
                  //  }
               // }).addOnFailureListener(new OnFailureListener() {
                 //   @Override
                 //   public void onFailure(@NonNull Exception e) {
                   //     Log.w(TAG, "Error writing document", e);
                 //   }
               // });


            }
        });



        
    }

    public static String publicKeyToString(PublicKey publ) {
        String publicKeyString = Base64.encodeToString(publ.getEncoded(), 2);
        return publicKeyString;
    }

    public static String privateKeyToString(PrivateKey priv){
        String privateKeyString = Base64.encodeToString(priv.getEncoded(), 2);
        return privateKeyString;

    }
    public static PublicKey stringToPublicKey(String publStr)  {

        PublicKey publicKey = null;
        try {
            byte[] data = Base64.decode(publStr, Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            publicKey = fact.generatePublic(spec);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return publicKey;
    }
    public static PrivateKey loadPrivateKey(String privStr) {
        PrivateKey privateKey = null;
        try {
            byte[] data = Base64.decode(privStr, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            privateKey = fact.generatePrivate(keySpec);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }



    private void storage(String privKey){

        String storekey = privKey;
        SecretKey secretKey = createKey();
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance((KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] encryptionIv = cipher.getIV();
        try {
            byte[] passwordBytes = storekey.getBytes("UTF-8");
            byte[] encryptedPasswordBytes = cipher.doFinal(passwordBytes);
            String encryptedPassword = Base64.encodeToString(encryptedPasswordBytes,Base64.DEFAULT);

            KeyHelper.storeString(this, "password",encryptedPassword);
            KeyHelper.storeString(this, "encryptionIV", Base64.encodeToString(encryptionIv, Base64.DEFAULT));
            Log.d(TAG, "Password Stored "+encryptedPassword);

            Toast.makeText(MainActivity.this, "KeyGenerated and Stored.",Toast.LENGTH_SHORT).show();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }



    private void LoginWithFingerPrint(){
        try{
            String base64EncryptedPassword = KeyHelper.getStringFromsp(this,"password");
            String base64EncryptionIv = KeyHelper.getStringFromsp(this,"encryptionIV");


            byte[] encryptedPassword = Base64.decode(base64EncryptedPassword, Base64.DEFAULT);
            byte[] encryptionIv = Base64.decode(base64EncryptionIv, Base64.DEFAULT);


            KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);
            SecretKey secretKey = (SecretKey) keystore.getKey("key", null);

           Cipher cipher = Cipher.getInstance((KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7));

           cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(encryptionIv));
           byte[] passwordBytes = cipher.doFinal(encryptedPassword);
           String password = new String(passwordBytes, "UTF-8");

           Toast.makeText(this,"Password is :" + password,Toast.LENGTH_LONG).show();
           privKey = password;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SecretKey createKey(){
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder("key", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                 .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                 .setUserAuthenticationRequired(false)
                 .setUserAuthenticationValidityDurationSeconds(5)
                 .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                 .build());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

//----------------------------------------------------RSA Generation------------------------
    private KeyPair createKeyPair() {
        KeyPair keyPair = null;

        try{
            KeyPairGenerator keygeneration = KeyPairGenerator.getInstance("RSA");
            keygeneration.initialize(KEY_LENGTH);
            keyPair = keygeneration.generateKeyPair();


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return keyPair;
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    public static String getPrivateKeyBase64Str(KeyPair keyPair){
        if (keyPair == null) return null;
        return getBase64StrFromByte(keyPair.getPrivate().getEncoded());
    }

    public static String getPublicKeyBase64Str(KeyPair keyPair){
        if (keyPair == null) return null;
        return getBase64StrFromByte(keyPair.getPublic().getEncoded());
    }

    public static String getBase64StrFromByte(byte[] key){
        if (key == null || key.length == 0) return null;
        return new String(Base64.encode(key,flags));
    }
    //--------------------------------New Code
    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes("UTF-8"));

        byte[] signature = privateSignature.sign();

        return  Base64.encodeToString(signature,Base64.DEFAULT);
    }

    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes("UTF-8"));

        byte[] signatureBytes = Base64.decode(signature, Base64.DEFAULT);

        return publicSignature.verify(signatureBytes);
    }

    private void checkverifction(){
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String uid = currentFirebaseUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference df = db.collection("users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","onSuccess"+documentSnapshot.getData());

                if(documentSnapshot.getString("isVerified").equals("1") && documentSnapshot.getString("hasvoted").equals("0")){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String uid = "eHl7csSHK1BTQCdV0deR";
                    DocumentReference df2 = db.collection("votingPeriods").document(uid);
                    df2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d("TAG","onSuccess"+documentSnapshot.getData());
                            if(documentSnapshot.getString("isVotingOpen").equals("1")){
                                startActivity(new Intent(getApplicationContext(),Voting.class));
                                Toast.makeText(MainActivity.this, "Going to voting",Toast.LENGTH_SHORT).show();

                            }
                            if(documentSnapshot.getString("isVotingOpen").equals("0")){
                                Toast.makeText(MainActivity.this, "Voting is Not Yet opened",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }

                if(documentSnapshot.getString("isVerified").equals("0")){
                    Toast.makeText(MainActivity.this,"Verify your Account",Toast.LENGTH_LONG).show();
                }

                //else{
                  //  Toast.makeText(MainActivity.this,"You Have Voted",Toast.LENGTH_LONG).show();
                //}
                if(documentSnapshot.getString("hasvoted").equals("1")){
                    Toast.makeText(MainActivity.this,"You Have Voted",Toast.LENGTH_LONG).show();
                }

            }
        });
    }








}

