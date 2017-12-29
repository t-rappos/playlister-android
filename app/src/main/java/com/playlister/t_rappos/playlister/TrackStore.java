package com.playlister.t_rappos.playlister;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import PlaylisterMain2.TrackCollection;
import PlaylisterMain2.Track;
import PlaylisterMain2.ATrackStore;
import java.util.HashSet;

/**
 * Created by Tom-2015 on 12/28/2017.
 */


class TrackStore extends PlaylisterMain2.ATrackStore{

    private Context context;

    TrackStore(Context c){
        context = c;
    }

    public HashSet<Track> loadStore(){
        //load old tracks
        try{
            FileInputStream fis = context.openFileInput("tracks.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (HashSet<Track>)ois.readObject();
        } catch (IOException e){
            e.printStackTrace();
            return new HashSet<Track>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new HashSet<Track>();
    }

    //https://stackoverflow.com/questions/7673424/how-to-dump-a-hashset-into-a-file-in-java

    public void saveStore(HashSet<Track> tracks){
        //save new tracks
        try {
            FileOutputStream fos = context.openFileOutput("tracks.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tracks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: if there has been a server reset we'll need to resend all tracks.
    //right now this is linked to when a new deviceId is required, we can
    //assume that the server has reset
    public void invalidateStore(){
        try {
            FileOutputStream fos = context.openFileOutput("tracks.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(new HashSet<Track>());
            System.out.println("Invalidating track storage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}