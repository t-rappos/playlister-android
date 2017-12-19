package com.playlister.t_rappos.playlister;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        tvUsername.setText(UserManager.getUsername(this));
        tvEmail.setText(UserManager.getEmail(this));
        tvPassword.setText(UserManager.getPassword(this));
    }

}
