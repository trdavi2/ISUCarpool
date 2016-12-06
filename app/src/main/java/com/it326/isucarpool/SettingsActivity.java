package com.it326.isucarpool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private static boolean chat = false;
    private static boolean ride = false;
    private static boolean vibe = false;

    public static boolean getChat(){
        return chat;
    }
    public static boolean getRide(){
        return ride;
    }
    public static boolean getVibe(){
        return vibe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Switch rideS = (Switch) findViewById(R.id.notify_ride);
        Switch chatS = (Switch) findViewById(R.id.notify_chat);
        Switch vibrate = (Switch) findViewById(R.id.vibrate);

        SharedPreferences settings = getSharedPreferences("myPref", 0);
        chat = settings.getBoolean("recieveChat", true);
        ride = settings.getBoolean("recieveRide", true);
        vibe = settings.getBoolean("vibrate", false);


    }
    @Override
    protected void onStop(){
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor = settings.edit();
        Switch rideS = (Switch) findViewById(R.id.notify_ride);
        Switch chatS = (Switch) findViewById(R.id.notify_chat);
        Switch vibrate = (Switch) findViewById(R.id.vibrate);
        editor.putBoolean("recieveChat", chatS.isChecked());
        editor.putBoolean("recieveRide", rideS.isChecked());
        editor.putBoolean("vibrate", vibrate.isChecked());

        // Commit the edits!
        editor.commit();
    }
}
