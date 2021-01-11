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


        int[] options = Preferences.getOptionTransportation(this);

        //build url
        String url = "http://ser-info-03.ec-nantes.fr:8080/itinerary/itinerary4?pdaLat=47.21213&pdaLong=-1.55479&pddLat=47.24811&pddLong=-1.54978&transportation=1,1,1";
        //start of the async task
        AsyncItineraryCompute task = new AsyncItineraryCompute(LoadingPageActivity.this);
        task.execute(url);

    }
}