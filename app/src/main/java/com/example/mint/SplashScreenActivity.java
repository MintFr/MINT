package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * TODO Explain aim and use of class here
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //redirect to MainActivity after 1 seconds

        //handler post delayed
        int SPLASH_SCREEN_TIMEOUT = 1000;
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