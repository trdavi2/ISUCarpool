package com.it326.isucarpool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
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
        setSettings();

    }

    public void setSettings(){
        final Switch rideS = (Switch) findViewById(R.id.notify_ride);
        final Switch chatS = (Switch) findViewById(R.id.notify_chat);

        SharedPreferences settings = getSharedPreferences("myPref", 0);
        chat = settings.getBoolean("recieveChat", true);
        chatS.setChecked(chat);
        ride = settings.getBoolean("recieveRide", false);
        rideS.setChecked(ride);

        chatS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences settings = getSharedPreferences("myPref", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("recieveChat", chatS.isChecked());
            }
        });
        rideS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences settings = getSharedPreferences("myPref", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("recieveRide", rideS.isChecked());
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences settings = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor = settings.edit();
        Switch rideS = (Switch) findViewById(R.id.notify_ride);
        Switch chatS = (Switch) findViewById(R.id.notify_chat);
        editor.putBoolean("recieveChat", chatS.isChecked());
        editor.putBoolean("recieveRide", rideS.isChecked());

        // Commit the edits!
        editor.commit();
    }
}
