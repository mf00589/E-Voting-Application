package com.example.e_voting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

public class KeyHelper {

    static void storeString(Context context, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences("SP", Activity.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    static String getStringFromsp(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SP",Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);

    }

}
