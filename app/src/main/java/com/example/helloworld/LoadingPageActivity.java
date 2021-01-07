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
        double param1 = intent.getDoubleExtra("param1",0.0);
        double param2 = intent.getDoubleExtra("param2",0.0);
        double param3 = intent.getDoubleExtra("param3",0.0);
        double param4 = intent.getDoubleExtra("param4",0.0);

        //build url
        String url = String.format("http://ser-info-03.ec-nantes.fr:8080/itinerarytest/itinerary?pdaLat=%s&pdaLong=%s&pddLat=%s&pddLong=%s",
                param1, param2, param3, param4);
        //start of the async task
        AsyncItineraryCompute task = new AsyncItineraryCompute(LoadingPageActivity.this);
        task.execute(url);

    }
}