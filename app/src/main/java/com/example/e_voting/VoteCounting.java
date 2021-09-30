package com.example.e_voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class VoteCounting extends AppCompatActivity {

    AnyChartView anyChartView;
    private FirebaseFirestore db;
    FirebaseFirestore fStore;
    public static final String TAG = "TAG";
    int count = 0;

    String[] parties = {"Party1","Party2","Party3","Party4"};
    int[] votes = {2,4,6,8};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_counting);

        anyChartView = findViewById(R.id.any_chart_view);
        getresults();


    }

    private void setuppiechart() {

        //votes[3] = count;

        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();


        for (int i=0; i< parties.length; i++){
            dataEntries.add(new ValueDataEntry(parties[i],votes[i]));
        }

        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }


    private void getresults() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String uid = currentFirebaseUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection("voters")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> party1 = new ArrayList<String>();
                        ArrayList<String> party2 = new ArrayList<String>();
                        ArrayList<String> party3 = new ArrayList<String>();
                        ArrayList<String> party4 = new ArrayList<String>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                               // arrayListOfFruit.add(document.get("pubkey").toString());
                                //Toast.makeText(VoteCounting.this, document.get("pubkey").toString(),Toast.LENGTH_SHORT).show();
                                verifyVote(document.get("pubkey").toString(),document.get("Votersign").toString(),document.get("party").toString(),party1,party2,party3,party4);
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        //count = arrayListOfFruit.size();
                        Toast.makeText(VoteCounting.this,""+party1.size(),Toast.LENGTH_SHORT).show();
                        votes[0] = party1.size();
                        votes[1] = party2.size();
                        votes[2] = party3.size();
                        votes[3] = party4.size();
                        setuppiechart();
                    }
                });
        Toast.makeText(VoteCounting.this, ""+count, Toast.LENGTH_SHORT).show();
    }

    private void verifyVote(String pubkey, String voterSign, String party,ArrayList<String> party1,ArrayList<String> party2, ArrayList<String> party3, ArrayList<String> party4){
        int[] countedvotes = {};

        if(party.equals("Party1")){
            try{
                boolean isCorrect = verify("Party1", voterSign, stringToPublicKey(pubkey));
                if(isCorrect){
                    party1.add(party);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if(party.equals("Party2")){
            try{
                boolean isCorrect = verify("Party2", voterSign, stringToPublicKey(pubkey));
                if(isCorrect){
                    party2.add(party);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if(party.equals("Party3")){
            try{
                boolean isCorrect = verify("Party3", voterSign, stringToPublicKey(pubkey));
                if(isCorrect){
                    party3.add(party);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if(party.equals("Party4")){
            try{
                boolean isCorrect = verify("Party4", voterSign, stringToPublicKey(pubkey));
                if(isCorrect){
                    party4.add(party);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes("UTF-8"));

        byte[] signatureBytes = Base64.decode(signature, Base64.DEFAULT);

        return publicSignature.verify(signatureBytes);
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
}