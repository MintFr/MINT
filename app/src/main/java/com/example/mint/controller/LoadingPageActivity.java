package com.example.mint.controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;
import com.example.mint.model.AsyncItineraryCompute;
import com.example.mint.model.PreferencesTransport;

/**
 * Activity for the loading page, used when calculating an itinerary
 */
public class LoadingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        Intent intent = getIntent();

        //Get parameters corresponding to addresses from the main activity (by default we put centrale nantes and chu hotel dieu)
        boolean start = intent.getBooleanExtra("starting", true);
        String time = intent.getStringExtra("time");
        System.out.println(time);
        double latitudeStart = intent.getDoubleExtra("latitudeStart", 0.0);
        double longitudeStart = intent.getDoubleExtra("longitudeStart", 0.0);
        double latitudeEnd = intent.getDoubleExtra("latitudeEnd", 0.0);
        double longitudeEnd = intent.getDoubleExtra("longitudeEnd", 0.0);
        boolean stepInItinerary = intent.getBooleanExtra("stepInItinerary", false);

        // options' management
        int[] options = PreferencesTransport.getOptionTransportation(this);
        boolean noOptions = true;
        for (int i : options) {
            if (i != 0) noOptions = false;
        }
        if (noOptions) {
            options = new int[]{1, 1, 1, 1}; //default case, all transports
        }

        // verification : is there a stepPoint?
        if (stepInItinerary) {
            // Yes, there is a stepPoint
            double param6 = intent.getDoubleExtra("param6", 0.0);
            double param7 = intent.getDoubleExtra("param7", 0.0);

            if (latitudeStart == 0.0 && longitudeStart == 0.0 && latitudeEnd == 0.0 && longitudeEnd == 0.0 && param6 == 0.0 && param7 == 0.0) {
                latitudeStart = 47.2484039066116;
                longitudeStart = -1.549636963829987;
                latitudeEnd = 47.212191574506164;
                longitudeEnd = -1.5535549386503666;
                param6 = 47.212191574506164;
                param7 = -1.5535549386503666;
            }

            // build the URL for the request to the server

            String url = String.format("http://ser-info-03.ec-nantes.fr:8080/itinerary_pol/" +
                            "itinerary6?start=%s,%s&end=%s,%s&hasStep=%s&step=%s,%s&transportation=%s,%s,%s,%s&hourStart=%s&time=%s",
                    latitudeStart, longitudeStart, latitudeEnd, longitudeEnd, stepInItinerary, param6, param7, options[0], options[1], options[2], options[3], start, time);

            System.out.println(url);
            AsyncItineraryCompute task = new AsyncItineraryCompute(LoadingPageActivity.this);
            task.execute(url);

        } else {
            // No, there is no stepPoint
            if (latitudeStart == 0.0 && longitudeStart == 0.0 && latitudeEnd == 0.0 && longitudeEnd == 0.0) {
                latitudeStart = 47.2484039066116;
                longitudeStart = -1.549636963829987;
                latitudeEnd = 47.212191574506164;
                longitudeEnd = -1.5535549386503666;
            }

            String url = String.format("http://ser-info-03.ec-nantes.fr:8080/itinerary_pol/" +
                            "itinerary6?start=%s,%s&end=%s,%s&hasStep=%s&transportation=%s,%s,%s,%s&hourStart=%s&time=%s",
                    latitudeStart, longitudeStart, latitudeEnd, longitudeEnd, stepInItinerary, options[0], options[1], options[2], options[3], start, time);

            // activity launch
            System.out.println(url);
            AsyncItineraryCompute task = new AsyncItineraryCompute(LoadingPageActivity.this);
            task.execute(url);
        }
    }
}
