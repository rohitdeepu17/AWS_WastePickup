package com.example.rohitsingla.scrapman;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by rohitsingla on 08/09/15.
 */
public class HandleSharedPrefs {
    public static void saveUsernameSharedPref(Context context, String keyUsername, String username, String keyPassword, String passwd){
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor mEditor = mSharedPref.edit();
        mEditor.putString(keyUsername, username);
        mEditor.putString(keyPassword, passwd);
        mEditor.commit();
    }

    public static String getSharedPrefValue(Context context, String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString(key, "");
        return value;
    }

}
