package com.example.e_voting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class PhoneVerification extends AppCompatActivity {
    private KeyStore keystore;
    private static final  String KEY_NAME="MARKFER";
    private Cipher cipher;
    private TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager)getSystemService(FINGERPRINT_SERVICE);

        if(!fingerprintManager.isHardwareDetected()){
            Toast.makeText(this, "Enable Fingerprint Authentication", Toast.LENGTH_SHORT).show();
        }
        else{
            if(!fingerprintManager.hasEnrolledFingerprints()){
                Toast.makeText(this,"Register FingerPrint in Settings",Toast.LENGTH_SHORT).show();
                }
            else{
                if(!keyguardManager.isKeyguardSecure()){
                    Toast.makeText(this,"Lock screen security not enabled in settings",Toast.LENGTH_SHORT).show();
                }else{
                    genKey();
                }
                if(cipherInit()){
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuthentication(fingerprintManager,cryptoObject);

                }
            }
        }

        //startActivity(new Intent(getApplicationContext(), MainActivity.class));



    }

    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance((KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
            try {
                keystore.load(null);
                SecretKey key = (SecretKey)keystore.getKey(KEY_NAME,null);
                cipher.init(Cipher.ENCRYPT_MODE,key);
                return true;
            } catch (CertificateException certificateException) {

                certificateException.printStackTrace();
                return false;

            } catch (IOException ioException) {

                ioException.printStackTrace();
                return false;

            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {

                noSuchAlgorithmException.printStackTrace();
                return false;

            } catch (UnrecoverableKeyException unrecoverableKeyException) {

                unrecoverableKeyException.printStackTrace();
                return false;

            } catch (KeyStoreException keyStoreException) {

                keyStoreException.printStackTrace();
                return false;

            } catch (InvalidKeyException invalidKeyException) {

                invalidKeyException.printStackTrace();
                return false;
            }


    }

    private void genKey() {
        try {
            keystore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator = null;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            keystore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());
            keyGenerator.generateKey();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidAlgorithmParameterException e){
            e.printStackTrace();

        }

    }

}