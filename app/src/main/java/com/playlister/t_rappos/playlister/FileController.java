package com.playlister.t_rappos.playlister;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom-2015 on 1/31/2018.
 */

class FileController {

    static void fileStatus(File f){
        boolean exists = f.exists();
        boolean isFile = f.isFile();
        boolean isDir = f.isDirectory();
        boolean canbdWrite = f.canWrite();
        boolean canRead = f.canRead();
        String absPath = f.getAbsolutePath();
        String[] children = f.list();
        int bp = 0;
    }

    static void validateDeleted(String path, Context context){

    }

    //https://www.programcreek.com/java-api-examples/index.php?source_dir=AmazeFileManager-master/src/main/java/com/amaze/filemanager/utils/FileUtil.java#
    public static Uri getUriFromFile(final String path,Context context) {
        ContentResolver resolver = context.getContentResolver();

        Cursor filecursor = resolver.query(MediaStore.Files.getContentUri("external"),
                new String[] { BaseColumns._ID }, MediaStore.MediaColumns.DATA + " = ?",
                new String[] { path }, MediaStore.MediaColumns.DATE_ADDED + " desc");
        filecursor.moveToFirst();

        if (filecursor.isAfterLast()) {
            filecursor.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, path);
            return resolver.insert(MediaStore.Files.getContentUri("external"), values);
        }
        else {
            int imageId = filecursor.getInt(filecursor.getColumnIndex(BaseColumns._ID));
            Uri uri = MediaStore.Files.getContentUri("external").buildUpon().appendPath(
                    Integer.toString(imageId)).build();
            filecursor.close();
            return uri;
        }
    }

    public static void deleteFiles3(ArrayList<String> paths, Context context){

        for(String p : paths){
            System.out.println("Deleting : " +p + " -> " + deleteFile(new File(p), context));
        }
    }


    /**
     * Delete all files in a folder.
     *
     * @param folder
     *            the folder
     * @return true if successful.
     */
    public static final boolean deleteFilesInFolder(final File folder,Context context) {
        boolean totalSuccess = true;
        if(folder==null)
            return false;
        if (folder.isDirectory()) {
            for (File child : folder.listFiles()) {
                deleteFilesInFolder(child, context);
            }

            if (!folder.delete())
                totalSuccess = false;
        } else {

            if (!folder.delete())
                totalSuccess = false;
        }
        return totalSuccess;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] getExtSdCardPaths(Context context) {
        List<String> paths = new ArrayList<String>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("AmazeFileUtils", "Unexpected external file dir: " + file.getAbsolutePath());
                }
                else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    }
                    catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        if(paths.isEmpty())paths.add("/storage/sdcard1");
        return paths.toArray(new String[0]);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getExtSdCardFolder(final File file,Context context) {
        String[] extSdPaths = getExtSdCardPaths(context);
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        }
        catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Determine if a file is on external sd card. (Kitkat or higher.)
     *
     * @param file
     *            The file.
     * @return true if on external sd card.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isOnExtSdCard(final File file,Context c) {
        return getExtSdCardFolder(file,c) != null;
    }

    public static DocumentFile getDocumentFile(final File file, final boolean isDirectory,Context context) {
        String baseFolder = getExtSdCardFolder(file,context);
        boolean originalDirectory=false;
        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            if(!baseFolder.equals(fullPath))
                relativePath = fullPath.substring(baseFolder.length() + 1);
            else originalDirectory=true;
        }
        catch (IOException e) {
            return null;
        }
        catch (Exception f){
            originalDirectory=true;
            //continue
        }
        String as= PreferenceManager.getDefaultSharedPreferences(context).getString("URI",null);

        Uri treeUri =null;
        if(as!=null)treeUri=Uri.parse(as);
        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(context, treeUri);
        if(originalDirectory)return document;
        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                }
                else {
                    nextDocument = document.createFile("image", parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    public static final boolean deleteFile(@NonNull final File file, Context context) {
        // First try the normal deletion.
// First try the normal deletion.
        if(file==null) return true;
        boolean fileDelete = deleteFilesInFolder(file, context);
        if (file.delete() || fileDelete)
            return true;

        // Try with Storage Access Framework.
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP && isOnExtSdCard(file, context)) {

            DocumentFile document = getDocumentFile(file, false,context);
            return document.delete();
        }


        // Try the Kitkat workaround.
        if (Build.VERSION.SDK_INT==Build.VERSION_CODES.KITKAT) {
            ContentResolver resolver = context.getContentResolver();

            try {
                Uri uri = getUriFromFile(file.getAbsolutePath(),context);
                resolver.delete(uri, null, null);
                return !file.exists();
            }
            catch (Exception e) {
                System.out.println("AmazeFileUtils: " + "Error when deleting file " + file.getAbsolutePath() +  e);
                e.printStackTrace();
                return false;
            }
        }

        return !file.exists();
    }

static void findAndDelete(String path, Context context){
        /*
    String[] projection = { MediaStore.Audio.Media._ID };
    String selection = MediaStore.Audio.Media.DATA + " = ?";
    String[] selectionArgs = new String[] { "33 - Jurgen Vries - The Theme (Radio Edit).mp3" };

    // Query for the ID of the media matching the file path
    Uri queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    ContentResolver contentResolver = context.getContentResolver();
    Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);*/

    ContentResolver cr = context.getContentResolver();

    Uri uri = MediaStore.Files.getContentUri("external");

    String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Audio.Media._ID};
    String sortOrder = null;
    String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
    String selection = MediaStore.MediaColumns.DISPLAY_NAME+ "=?";

    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
    String[] selectionArgsMp3 = new String[]{ path };
    Cursor c = cr.query(uri, projection, selection, selectionArgsMp3 , sortOrder);


    if (c.moveToFirst()) {
        // We found the ID. Deleting the item via the content provider will also remove the file
        long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

        final int delete = cr.delete(deleteUri, null, null);
        if(delete == 1){
            System.out.println("findAndDelete was able to delete" + c.getString(0));
        } else {
            System.out.println("findAndDelete failed to delete2" + c.getString(0));
        }

    } else {
        // File not found in media store DB
        System.out.println("findAndDelete failed to delete" + c.getString(0));
    }
    //cr.
    //c.close();
}

    static void deleteFiles2(ArrayList<String> filenames, Context context){

        //Uri duri = Uri.parse("file://mnt/extSdCard/music/VA - I Love Trance - Ministry of Sound/33 - Jurgen Vries - The Theme (Radio Edit).mp3");
        //context.getContentResolver().delete(duri, null, null);


        for(String f : filenames){
            findAndDelete(f, context);
        }
    }

    static void deleteFiles(ArrayList<String> paths, Context context){
        findAndDelete("33 - Jurgen Vries - The Theme (Radio Edit).mp3", context);

        File myFile = new File(new File("/mnt/extSdCard/music/VA - I Love Trance - Ministry of Sound").getAbsoluteFile(), "33 - Jurgen Vries - The Theme (Radio Edit).mp3");
        try {
            boolean bb = myFile.delete();
            FileOutputStream fop = new FileOutputStream(myFile,false);
            //fop.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File f1 = new File("/mnt/extSdCard/music/VA - I Love Trance - Ministry of Sound/33 - Jurgen Vries - The Theme (Radio Edit).mp3");

        try {
            boolean bb = f1.delete();
            FileOutputStream fop = new FileOutputStream(f1,false);
            //fop.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File f3 = new File("mnt/extSdCard/music/VA - I Love Trance - Ministry of Sound/33 - Jurgen Vries - The Theme (Radio Edit).mp3");

        fileStatus(f1);
        fileStatus(f3);

        fileStatus(new File("/mnt/"));
        fileStatus(new File("/mnt/extSdCard"));
        fileStatus(new File("/mnt/extSdCard/music/"));
        fileStatus(new File("/mnt/extSdCard/music/VA - I Love Trance - Ministry of Sound"));
        fileStatus(new File("/mnt/extSdCard/music/VA - I Love Trance - Ministry of Sound/33 - Jurgen Vries - The Theme (Radio Edit).mp3"));



        System.out.println("ROOT :" +Environment.getRootDirectory().toString()+", ext: "+Environment.getExternalStorageDirectory() + " music :" +
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));



        try {
            Boolean b1 = f1.getAbsoluteFile().delete();
            Boolean b3 = f3.getAbsoluteFile().delete();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        for(String path : paths){
            Boolean deleted = false;
            File f = new File(path);
            fileStatus(f);
            if(f.isFile()){
                try{
                    boolean result = f.delete();

                    if(!result){
                        System.out.println("Couldn't delete file: " + path + ", can read: " + f.canRead() + ", can write: "+f.canWrite());
                    } else {
                        System.out.println("Deleted " + path);
                        deleted = true;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                System.out.println("Couldn't find file: " + path + ", can read: " + f.canRead() + ", can write: "+f.canWrite());

            }
            /*
            if(!deleted){
                Boolean b = context.deleteFile(path);
                if(b){
                    System.out.println("Deleted2 " + path);
                } else {
                    System.out.println("Couldn't delete file2: " + path);
                }
            }
            */
        }
    }

    static void copyFilesToFolder(ArrayList<String> paths, File folder){
        //TODO: implement this
    }

    static void makeM3UPlaylist(ArrayList<String> paths, String playlistName, Context context) throws IOException {
        String validatedName = playlistName.replaceAll("[^-_.A-Za-z0-9]", "_" );
        String defaultSavePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/" + validatedName + ".m3u";
        //String destPath = destFolder.getPath() + "/" + playlistName + ".m3u";
        FileOutputStream fos = new FileOutputStream(new File(defaultSavePath));

                //context.openFileOutput(defaultSavePath,Context.MODE_WORLD_READABLE);
        String output = "";
        for(String path : paths){
            output += path + System.lineSeparator();
        }
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(output);
        oos.close();
        fos.close();
    }
}
