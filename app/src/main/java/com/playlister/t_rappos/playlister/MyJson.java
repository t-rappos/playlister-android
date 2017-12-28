package com.playlister.t_rappos.playlister;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Tom-2015 on 12/22/2017.
 */

public class MyJson {
    public static String toJson(Object o){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(o);
    }

    static Object toObject(String s, Class c){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(s, c);
    }

    public static ArrayList<String> toStringList(String s){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        ArrayList<String> temp = new ArrayList<String>();
        return gson.fromJson(s, temp.getClass());
    }
}
