package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class LoadingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        Intent intent = getIntent();
        //Get parameters corresponding to addresses from the main activity
        String param1 = intent.getStringExtra("param1");
        String param2 = intent.getStringExtra("param2");
        String param3 = intent.getStringExtra("param3");
        String param4 = intent.getStringExtra("param4");


        //build url
        String url = "http://ser-info-03.ec-nantes.fr:8080/itinerarytest/itinerary?pdaLat="+param1+
                "&pdaLong="+param2+"&pddLat="+param3+"&pddLong"+param4;
        ////url to test hello web service
        //String url = "http://ser-info-03.ec-nantes.fr:8080/hello?name=mint"
        //String url = "https://google.com/";
        //start of the async task
        AsyncItineraryCompute task = new AsyncItineraryCompute(LoadingPageActivity.this);
        task.execute(url);

    }
}