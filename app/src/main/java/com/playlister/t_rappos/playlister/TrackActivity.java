package com.playlister.t_rappos.playlister;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import PlaylisterMain2.TrackPath;

import java.io.IOException;
import java.util.ArrayList;

public class TrackActivity extends AppCompatActivity {
    ArrayList<TrackPath> tracks;

    public class LoadTracks extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            Bundle b = getIntent().getExtras();
            if(b == null){
                return false;
            }
            int playlistId = b.getInt("PLAYLIST_ID");
            String playlistName = b.getString("PLAYLIST_NAME");

            tracks = LoginActivity.gMessenger.loadTrackPathsForPlaylist(playlistId);
            if(tracks == null){
                return false;
            }
            final ArrayList<String> trackFilenames = new ArrayList<>();
            for(TrackPath p : tracks){
                trackFilenames.add(p.filename);
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    final ListView listview = (ListView) findViewById(R.id.listViewTrack);

                    final ArrayAdapter<String> adp= new ArrayAdapter<String>(TrackActivity.this,
                            android.R.layout.simple_list_item_1,
                            trackFilenames);

                    listview.setAdapter(adp);
                }
            });

            return true;
        }
    }

    //https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
        System.out.println("Requested permissions");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (shouldAskPermissions()) {
            askPermissions();
        }

        setContentView(R.layout.activity_track);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button confirm = (Button)findViewById(R.id.buttonTrackConfirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = getIntent().getExtras();
                if(b == null){
                    return;
                }

                String type = b.getString("INTERACTION_TYPE");
                if(type == null){
                    return;
                }

                ArrayList<String> paths = new ArrayList<>();
                for(TrackPath t : tracks){
                    paths.add(t.path + "/" + t.filename);
                }

                ArrayList<String> filenames = new ArrayList<>();
                for(TrackPath t : tracks){
                    filenames.add(t.filename);
                }

                switch(type){
                    case "COPY":
                        System.out.println("NOT IMPLEMENTED!");
                        break;

                    case "DELETE":
                        FileController.deleteFiles3(paths, TrackActivity.this);
                        finish();
                        break;

                    case "EXPORT":
                        try {
                            FileController.makeM3UPlaylist(paths, b.getString("PLAYLIST_NAME"), TrackActivity.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Couldnt make M3U playlist");
                        }
                        break;
                    default:

                        break;
                }
            }
        });

        LoadTracks l = new LoadTracks();
        l.execute();
    }

}
