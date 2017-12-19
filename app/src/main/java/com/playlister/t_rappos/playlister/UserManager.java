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

    public static void saveCredentials(Context context, String username, String password){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public static void saveEmail(Context context, String email){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("email", email);
        editor.commit();
    }

    public static String getUsername(Context context){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        String username = settings.getString("username", "");
        return username;
    }

    public static String getPassword(Context context){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        String password = settings.getString("password", "");
        return password;
    }

    public static String getEmail(Context context){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        return settings.getString("email", "");
    }
}
