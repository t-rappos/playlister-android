package com.playlister.t_rappos.playlister;

import android.app.ProgressDialog;
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
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userManager = new UserManager(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Scanning for music");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);
        progressDialog.show();

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
    public class ScanAndSend extends AsyncTask<Void, Integer, Boolean> {

        private Context context = null;
        private int max = 0;

        ScanAndSend(Context c) {
            context = c;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            System.out.println("Scanning for tracks");
            Messenger m = new Messenger(userManager,getString(R.string.api_url));
            TrackStore store = TrackScanner.scan(context, userManager,this);
            m.sendTracks(store.toAdd, store.toRemove);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            System.out.println("Sent music tracks successfully");
            progressDialog.hide();
        }

        @Override
        protected void onCancelled() {
            System.out.println("Cancelled scanning");
        }


        public void setProgressMax(Integer i){
            max = i;
            progressDialog.setMax(i);
        }

        //public wrapper hack...
        public void doProgress(Integer i){
            this.publishProgress(i);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            System.out.println("onProgressUpdate update " + progress[0]);
            progressDialog.setProgress(progress[0]);
        }
    }

}
