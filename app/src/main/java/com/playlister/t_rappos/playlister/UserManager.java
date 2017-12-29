package com.playlister.t_rappos.playlister; /**
 * Created by Tom-2015 on 12/19/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import PlaylisterMain2.IUserManager;


import java.util.prefs.Preferences;

/**
 * Created by Thomas Rappos (6336361) on 12/18/2017.
 */
public class UserManager implements IUserManager {
    private Context context;

    public UserManager(Context c){
        context = c;
    }

    public void saveCredentials(String username, String password){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public  void saveEmail(String email){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("email", email);
        editor.commit();
    }

    public String getUsername(){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        String username = settings.getString("username", "");
        return username;
    }

    public String getPassword(){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        String password = settings.getString("password", "");
        return password;
    }

    public String getEmail(){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        return settings.getString("email", "");
    }

    //return -1 if no id was found
    public int getDeviceId(){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        return settings.getInt("deviceId", -1);
    }

    public boolean hasDeviceId(){
        return (getDeviceId() != -1);
    }

    public void saveDeviceId(int deviceId){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("deviceId", deviceId);
        editor.commit();
    }

    public long getServerDBResetId(){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        return settings.getLong("dbResetId", 0);
    }

    public void saveServerDBResetId(long id){
        SharedPreferences settings = context.getSharedPreferences("USER",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("dbResetId", id);
        editor.commit();
    }


}
