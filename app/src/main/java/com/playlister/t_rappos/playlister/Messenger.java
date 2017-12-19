package com.playlister.t_rappos.playlister;

import android.content.Context;
import android.util.Base64;
import org.json.*;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;

/**
 * Created by Thomas Rappos (6336361) on 12/18/2017.
 */
public class Messenger {
    //private static HttpTransport httpTransport;
    private boolean valid = false;

    public boolean validateConnection(Context c){
        valid = send(c);
        return valid;
    }

    public void sendTracks(){

    }

    public Messenger(){

    }

    public boolean send(Context context){

        HttpURLConnection c = null;
        try {

            URL url2 = new URL("http://192.168.0.11:8080/api/me");
            c = (HttpURLConnection) url2.openConnection();

            final String userpw = UserManager.getUsername(context) + ":" + UserManager.getPassword(context);
            final String basicAuth = "Basic " + Base64.encodeToString(userpw.getBytes(), Base64.NO_WRAP);
            c.setRequestProperty("Authorization",basicAuth);
            c.setUseCaches(false);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    c.getInputStream()));
            String inputLine = "";
            String data = "";
            while ((inputLine = in.readLine()) != null){
                data += inputLine;
                System.out.println(inputLine);
            }

            JSONObject jobj = new JSONObject(data);
            String email = jobj.getString("email");
            String username = jobj.getString("username");

            System.out.println("Email : " + email);
            System.out.println("Username : " + username);

            UserManager.saveEmail(context, email);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
