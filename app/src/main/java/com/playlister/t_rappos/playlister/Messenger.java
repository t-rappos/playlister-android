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
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * Created by Thomas Rappos (6336361) on 12/18/2017.
 */
public class Messenger {
    //private static HttpTransport httpTransport;
    private boolean valid = false;
    private static NetHttpTransport httpTransport;
    //private static HttpTransport httpTransport;
    private String apiUrl =  "http://192.168.0.11:8080/";

    boolean validateConnection(Context c){
        valid = sendGETRequest(c, "api/me") != null;
        if(valid){
            //if(!UserManager.hasDeviceId(c)){  //TODO: enabled this, also check if server hasnt been reset since last id was gathered
                loadDeviceId(c);
            //}
        }
        return valid;
    }

    public void sendTracks(Context c, TrackCollection tracks) {
        sendPOSTRequest(c, "tracks", MyJson.toJson(tracks));
        System.out.println("Sent tracks to server");
    }


    Messenger(){

        httpTransport = new NetHttpTransport();
        //httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    }

    String generateDeviceName(Context c){   //TODO: put a random gen in here in case users have
                                            // multiple of the same types of devices?
        return UserManager.getUsername(c) + " : " + android.os.Build.MODEL;
    }

    void loadDeviceId(Context c){
        //if(!UserManager.hasDeviceId()){   //TODO: enabled this, also check if server hasnt been reset since last id was gathered
        HttpResponse r = sendGETRequest(c, "device/"+generateDeviceName(c) + "/ANDROID");
        if(r != null){
            try {
                DeviceInfo di = MyJson.toDeviceInfo(r.parseAsString());
                UserManager.saveDeviceId(c, (int)di.id);
                System.out.println("Acquired device id = " + di.id + " for device name " + generateDeviceName(c));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //} else {
        //    System.out.println("Loaded device id = " + UserManager.getDeviceId());
        //}
    }

    HttpResponse sendPOSTRequest(Context context, String endpoint, String body){
        GenericUrl url2 = new GenericUrl(apiUrl + endpoint);
        BasicAuthentication ba = new BasicAuthentication(UserManager.getUsername(context), UserManager.getPassword(context));
        try{
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();//credential
            HttpRequest request = requestFactory.buildPostRequest(url2, ByteArrayContent.fromString("application/json", body));
            request.getHeaders().setContentType("application/json");
            ba.initialize(request);
            return request.execute();
        } catch(Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    HttpResponse sendGETRequest(Context context, String endpoint){
        GenericUrl url2 = new GenericUrl(apiUrl + endpoint);
        BasicAuthentication ba = new BasicAuthentication(UserManager.getUsername(context), UserManager.getPassword(context));
        try{
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();//credential
            HttpRequest request = requestFactory.buildGetRequest(url2);
            ba.initialize(request);
            return request.execute();
        } catch(Exception e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    /*
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

    }*/

}
