package com.playlister.t_rappos.playlister;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Tom-2015 on 12/22/2017.
 */

class TrackCollection{
    public long deviceId;
    public ArrayList<Track> tracks = new ArrayList<Track>();
    public TrackCollection(){
    }
    public void addTrack(Track t){
        tracks.add(t);
    }
    public void addTrackCollection(TrackCollection tc){
        tracks.addAll(tc.tracks);
    }
};


public class TrackScanner {

    //https://stackoverflow.com/questions/25298691/how-to-check-the-file-type-in-java
    public static String getFileExtension(String fullName) {
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public static boolean isFileValid(File f){
        boolean isFile = f.isFile();
        String ext = getFileExtension(f.getName());
        boolean isMp3 =  (ext.compareTo("mp3")==0 )|| (ext.compareTo("MP3") ==0);
        //TODO: filetype comparison //FilenameUtils.getExtension(f.getName())
                //.compareToIgnoreCase("mp3") == 0;
        return isFile && isMp3;
    }

    static TrackCollection scanRecursive(String d){
        System.out.println("scanning :" + d);
        File[] files = new File(d).listFiles();
        TrackCollection col = new TrackCollection();
        if (files != null && files.length > 0){
            for (File f:files) {
                if(isFileValid(f)){
                    Track t = new Track(f);
                    col.addTrack(t);
                } else if(f.isDirectory()){
                    col.addTrackCollection(scanRecursive(f.getAbsolutePath()));
                }
            }
        }
        return col;
    }

    public static TrackStore scan(Context c){
        File storageDir = new File("/mnt/");
        if(storageDir.isDirectory()){
            String[] dirList = storageDir.list();
            for(String s : dirList){
                System.out.println(s);
            }
        }

        long startTime = System.nanoTime();
        TrackCollection col = scanRecursive(Environment.getExternalStorageDirectory().toString());
        col.addTrackCollection(scanRecursive("/mnt"));
        long dt = System.nanoTime() - startTime;
        System.out.println("Completed in " + (float)dt/1000000f + " ms");

        TrackStore trackStore = new TrackStore();
        trackStore.checkInTracks(c, col);
        return trackStore;
    }
}
