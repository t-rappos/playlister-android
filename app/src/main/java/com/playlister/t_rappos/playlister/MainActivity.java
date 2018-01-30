package com.playlister.t_rappos.playlister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

import PlaylisterMain2.Messenger;


public class MainActivity extends AppCompatActivity {

    UserManager userManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userManager = new UserManager(this);
        setContentView(R.layout.activity_main);


        TextView tvUsername = (TextView) findViewById(R.id.textUsername);
        TextView tvEmail = (TextView) findViewById(R.id.textEmail);
        TextView tvPassword = (TextView) findViewById(R.id.textPassword);

        Button logoutButton = (Button)findViewById(R.id.buttonLogout);
        Button scanButton = (Button)findViewById(R.id.buttonScan);
        Button playlistsButton = (Button) findViewById(R.id.buttonPlaylists);
        //CheckBox keepLoggedInCB = (CheckBox) findViewById(R.id.checkBoxStayLoggedIn);

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Scanning for music");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgress(0);
                progressDialog.show();
                ScanAndSend s = new ScanAndSend(MainActivity.this);
                s.execute();
            }
        });

        playlistsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userManager.saveCredentials("","");
                finish();
            }
        });

        tvUsername.setText(userManager.getUsername());
        tvEmail.setText(userManager.getEmail());
        tvPassword.setText(userManager.getPassword());

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
            Messenger m = new Messenger(userManager);
            //TODO: this re-validation isn't neccessary.
            Boolean valid = m.validateConnection(getString(R.string.local_api_url))
                    ||  m.validateConnection(getString(R.string.remote_api_url));
            if(!valid){
                return false;
            }
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
