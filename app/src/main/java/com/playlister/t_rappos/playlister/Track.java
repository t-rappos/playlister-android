package com.playlister.t_rappos.playlister;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by Tom-2015 on 12/22/2017.
 */

public class Track {
    public String filename = "";
    public String path = "";
    public String title = "";
    public String artist = "";
    public String album = "";
    public long filesize = 0;
    public String hash = "";

    //TODO: optimise this
    //https://www.mkyong.com/java/how-to-generate-a-file-checksum-value-in-java/
    private void generateHash(File f){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(f);
            byte[] dataBytes = new byte[1000];
            int nread = 0;

            int counter = 0;    //only reads the first 150*5 bytes
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
                if(counter++ > 5){
                    break;
                }
            };
            byte[] mdbytes = md.digest();
            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            this.hash = sb.toString();
            System.out.println(f.getName() + " : " + this.hash);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Track(File f){
        filename = f.getName();
        path = f.getParent();
        filesize = f.length();
        generateHash(f);
    }
}
