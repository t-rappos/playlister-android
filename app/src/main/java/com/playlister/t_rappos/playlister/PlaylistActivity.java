package com.playlister.t_rappos.playlister;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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

import java.util.ArrayList;

import PlaylisterMain2.Messenger;
import PlaylisterMain2.Playlist;
import PlaylisterMain2.Track;

public class PlaylistActivity extends AppCompatActivity {
    private int selectionId = 0;
    private ArrayList<String> playlistNames = new ArrayList<>();
    private ArrayList<Integer> playlistIds = new ArrayList<>();

    public class LoadPlaylists extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            //http://www.vogella.com/tutorials/AndroidListView/article.html#androidlists_overview

            ArrayList<Playlist> playlistArray = LoginActivity.gMessenger.loadPlaylists();
            if(playlistArray == null){
                return false;
            }
            for(Playlist p : playlistArray){
                if(p.name != null){
                    playlistNames.add(p.name);
                    playlistIds.add((int)(long)p.id);
                }
            }
            //https://stackoverflow.com/questions/38743402/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
            runOnUiThread(new Runnable() {
                public void run() {
                    final ListView listview = (ListView) findViewById(R.id.listVIewPlaylist);

                    final ArrayList<String> list = new ArrayList<String>();
                    for (String s : playlistNames) {
                        list.add(s);
                    }

                    final ArrayAdapter<String> adp= new ArrayAdapter<String>(PlaylistActivity.this,
                            android.R.layout.simple_list_item_1,
                            list);

                    listview.setAdapter(adp);

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {
                            selectionId = position;
                            //https://stackoverflow.com/questions/16189651/android-listview-selected-item-stay-highlighted
                            for (int j = 0; j < listview.getChildCount(); j++)
                                listview.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

                            view.setBackgroundColor(Color.LTGRAY);
                        }

                    });
                }
            });

            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: this is a hack, only need to create these managers once
        /*
        UserManager userManager = new UserManager(this);
        Messenger messenger = new Messenger(userManager);

        Boolean res = messenger.validateConnection(getString(R.string.local_api_url))
                || messenger.validateConnection(getString(R.string.remote_api_url));
                */
        //

        LoadPlaylists l = new LoadPlaylists();
        final AsyncTask<Void, Integer, Boolean> execute = l.execute();

        setContentView(R.layout.activity_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button export = (Button)findViewById(R.id.buttonPlaylistExport);
        Button copy = (Button)findViewById(R.id.buttonPlaylistCopy);
        Button delete = (Button)findViewById(R.id.buttonPlaylistDelete);

        export.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("PLAYLIST_NAME", playlistNames.get(selectionId));
                b.putInt("PLAYLIST_ID", playlistIds.get(selectionId));
                b.putString("INTERACTION_TYPE", "EXPORT");
                Intent intent = new Intent(PlaylistActivity.this, TrackActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        //TODO: refactor duplicate code
        copy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("PLAYLIST_NAME", playlistNames.get(selectionId));
                b.putInt("PLAYLIST_ID", playlistIds.get(selectionId));
                b.putString("INTERACTION_TYPE", "COPY");
                Intent intent = new Intent(PlaylistActivity.this, TrackActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("PLAYLIST_NAME", playlistNames.get(selectionId));
                b.putInt("PLAYLIST_ID", playlistIds.get(selectionId));
                b.putString("INTERACTION_TYPE", "DELETE");
                Intent intent = new Intent(PlaylistActivity.this, TrackActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

}
