package com.example.mint.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;

/**
 * Activity for the splash screen at the launch of the app. Duration of this activity is arbitrary,
 * and is 1 second (1.000 ms)
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Context context = this.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("isStarting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isStarting", true);
        editor.apply();

        //redirect to MainActivity after 1 seconds

        //handler post delayed
        int SPLASH_SCREEN_TIMEOUT = 1000; //duration = 1s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //start a page
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);

    }
}