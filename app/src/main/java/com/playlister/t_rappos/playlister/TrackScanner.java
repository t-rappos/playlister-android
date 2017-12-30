package com.playlister.t_rappos.playlister;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import PlaylisterMain2.TrackCollection;
import PlaylisterMain2.Track;
import PlaylisterMain2.FolderScanner;
/**
 * Created by Tom-2015 on 12/22/2017.
 */


public class TrackScanner {
    static int maxFolderCount = 0;
    static int currentFolderCount = 0;


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
        return isFile && isMp3;
    }

    static TrackCollection scanRecursive(String d, MainActivity.ScanAndSend task){
        File[] files = new File(d).listFiles();
        TrackCollection col = new TrackCollection();
        FilePropertyReader reader = new FilePropertyReader();
        if (files != null && files.length > 0){
            task.doProgress(++currentFolderCount);
            System.out.println("Scanning: " + currentFolderCount + " / " + maxFolderCount);
            for (File f:files) {
                if(isFileValid(f)){
                    Track t = new Track(f,reader);
                    col.addTrack(t);
                } else if(f.isDirectory()){

                    col.addTrackCollection(scanRecursive(f.getAbsolutePath(),task));
                }
            }
        }
        return col;
    }

    public static TrackStore scan(Context c, UserManager userManager, MainActivity.ScanAndSend task){
        File storageDir = new File("/mnt/");
        maxFolderCount = 0;
        if(storageDir.isDirectory()){
            maxFolderCount += FolderScanner.countFolders(storageDir);
            String[] dirList = storageDir.list();
            for(String s : dirList){
                System.out.println(s);
            }
        }

        int currentFolderCount = 0;
        File esd = Environment.getExternalStorageDirectory();
        if (esd.isDirectory()) {
            maxFolderCount += FolderScanner.countFolders(esd);
        }

        task.setProgressMax(maxFolderCount);

        System.out.println("Scanning " + maxFolderCount + " folders");

        long startTime = System.nanoTime();
        TrackCollection col = scanRecursive(esd.getPath(),task);
        col.addTrackCollection(scanRecursive("/mnt",task));
        long dt = System.nanoTime() - startTime;
        System.out.println("Completed in " + (float)dt/1000000f + " ms");

        TrackStore trackStore = new TrackStore(c);
        trackStore.checkInTracks(col, userManager);
        return trackStore;
    }
}
