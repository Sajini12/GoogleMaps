package com.suyati.mapstrackingcurrentlocationfinal.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.suyati.mapstrackingcurrentlocationfinal.constants.SharedPrefConstants;

/**
 * Created by suyati on 2/15/17.
 */

public class SharedPreferenceUtils {

    public static void setSharedPrefBoolean(String Constant,boolean value, Context mContext){
        SharedPreferences preferences = mContext.getSharedPreferences(SharedPrefConstants.SHARPREF_FILENAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant,value);
        editor.commit();
    }

    public static boolean getSharedPrefBoolean(String Constant, Context mContext){
        SharedPreferences preferences = mContext.getSharedPreferences(SharedPrefConstants.SHARPREF_FILENAME,Context.MODE_PRIVATE);
        return preferences.getBoolean(Constant,false);
    }
}
