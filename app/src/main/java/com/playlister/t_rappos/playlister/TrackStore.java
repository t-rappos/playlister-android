package com.playlister.t_rappos.playlister;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

/**
 * Created by Tom-2015 on 12/28/2017.
 */

public class TrackStore {
    TrackCollection toAdd = new TrackCollection();
    TrackCollection toRemove = new TrackCollection();

    private void setDeviceId(long deviceId){
        toAdd.deviceId = deviceId;
        toRemove.deviceId = deviceId;
    }

    private HashSet<Track> loadStore(Context c){
        //load old tracks
        try{
            FileInputStream fis = c.openFileInput("tracks.txt");
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
    private void saveStore(Context c, HashSet<Track> tracks){
        //save new tracks
        try {
            FileOutputStream fos = c.openFileOutput("tracks.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tracks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: if there has been a server reset we'll need to resend all tracks.
    //right now this is linked to when a new deviceId is required, we can
    //assume that the server has reset
    static void invalidateStore(Context c){
        try {
            FileOutputStream fos = c.openFileOutput("tracks.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(new HashSet<Track>());
            System.out.println("Invalidating track storage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void checkInTracks(Context c, TrackCollection col){
        HashSet<Track> tracks = new HashSet<Track>(col.tracks);


        HashSet<Track> oldTracks = loadStore(c);

        for(Track t : tracks){
            if(!oldTracks.contains(t)){
                toAdd.addTrack(t);
                System.out.println("adding track " + t.filename);
            }
        }
        for(Track ot : oldTracks){
            if (!tracks.contains(ot)) {
                toRemove.addTrack(ot);
                System.out.println("removing track " + ot.filename);
            }
        }
        setDeviceId(UserManager.getDeviceId(c));
        saveStore(c, tracks);
    }
}
