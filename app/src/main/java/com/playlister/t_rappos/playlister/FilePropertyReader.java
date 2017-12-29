package com.playlister.t_rappos.playlister;

/**
 * Created by Tom-2015 on 12/29/2017.
 */
import android.media.MediaMetadataRetriever;

import java.io.File;

import PlaylisterMain2.IFilePropertyReader;

public class FilePropertyReader implements  IFilePropertyReader{

    String artist = "";
    String album = "";
    String title = "";

    public String getArtist(){
        return artist;
    }
    public String getTitle(){
        return title;
    }
    public String getAlbum(){
        return album;
    }

    public void readFileProperties(File f) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try{
            mmr.setDataSource(f.getAbsolutePath());
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if(artist == null || artist.compareTo("")==0){
                artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
                if(artist == null || artist.compareTo("")==0){
                    artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
                }
            }
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
