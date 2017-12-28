package com.playlister.t_rappos.playlister; /**
 * Created by Tom-2015 on 12/19/2017.
 */

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;


import java.util.prefs.Preferences;

/**
 * Created by Thomas Rappos (6336361) on 12/18/2017.
 */
public class UserManager {

    static void saveCredentials(Context context, String username, String password){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    static void saveEmail(Context context, String email){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("email", email);
        editor.commit();
    }

    static String getUsername(Context context){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        String username = settings.getString("username", "");
        return username;
    }

    static String getPassword(Context context){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        String password = settings.getString("password", "");
        return password;
    }

    static String getEmail(Context context){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        return settings.getString("email", "");
    }

    //return -1 if no id was found
    static int getDeviceId(Context context){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        return settings.getInt("deviceId", -1);
    }

    static boolean hasDeviceId(Context context){
        return (getDeviceId(context) != -1);
    }

    static void saveDeviceId(Context context, int deviceId){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("deviceId", deviceId);
        editor.commit();
    }

    static long getServerDBResetId(Context context){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        return settings.getLong("dbResetId", 0);
    }

    static void saveServerDBResetId(Context context, long id){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("dbResetId", id);
        editor.commit();
    }


}
