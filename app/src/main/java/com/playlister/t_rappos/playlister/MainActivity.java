package com.playlister.t_rappos.playlister;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import PlaylisterMain2.Messenger;


public class MainActivity extends AppCompatActivity {

    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userManager = new UserManager(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView tvUsername = (TextView) findViewById(R.id.textUsername);
        TextView tvEmail = (TextView) findViewById(R.id.textEmail);
        TextView tvPassword = (TextView) findViewById(R.id.textPassword);

        tvUsername.setText(userManager.getUsername());
        tvEmail.setText(userManager.getEmail());
        tvPassword.setText(userManager.getPassword());

        ScanAndSend s = new ScanAndSend(this);
        s.execute();
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class ScanAndSend extends AsyncTask<Void, Void, Boolean> {

        private Context context = null;

        ScanAndSend(Context c) {
            context = c;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            System.out.println("Scanning for tracks");
            Messenger m = new Messenger(userManager,getString(R.string.api_url));
            TrackStore store = TrackScanner.scan(context, userManager);
            m.sendTracks(store.toAdd, store.toRemove);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            System.out.println("Sent music tracks successfully");
        }

        @Override
        protected void onCancelled() {
            System.out.println("Cancelled scanning");
        }
    }

}
