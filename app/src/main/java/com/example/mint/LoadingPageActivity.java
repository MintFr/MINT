package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

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
        double param1 = intent.getDoubleExtra("param1", 0.0);
        double param2 = intent.getDoubleExtra("param2", 0.0);
        double param3 = intent.getDoubleExtra("param3", 0.0);
        double param4 = intent.getDoubleExtra("param4", 0.0);
        boolean param5 = intent.getBooleanExtra("param5", false);

        // options' management
        int[] options = Preferences.getOptionTransportation(this);
        boolean noOptions = true;
        for (int i : options) {
            if (i != 0) noOptions = false;
        }
        if (noOptions) {
            options = new int[]{1, 1, 1, 1}; //default case, all transports
        }

        // verification : is there a stepPoint?
        if (param5) {
            // Yes, there is a stepPoint
            double param6 = intent.getDoubleExtra("param6", 0.0);
            double param7 = intent.getDoubleExtra("param7", 0.0);

            if (param1 == 0.0 && param2 == 0.0 && param3 == 0.0 && param4 == 0.0 && param6 == 0.0 && param7 == 0.0) {
                param1 = 47.2484039066116;
                param2 = -1.549636963829987;
                param3 = 47.212191574506164;
                param4 = -1.5535549386503666;
                param6 = 47.212191574506164;
                param7 = -1.5535549386503666;
            }

            // build the two URLs

            String url = String.format("http://ser-info-03.ec-nantes.fr:8080/itineraryBIS/" +
                            "itinerary6?start=%s,%s&end=%s,%s&hasStep=%s&step=%s,%s&transportation=%s,%s,%s,%s&hourStart=%s&time=%s",
                    param1, param2, param3, param4, param5, param6, param7, options[0], options[1], options[2], options[3], start, time);

            System.out.println(url);
            //System.out.println(url2);
            AsyncItineraryCompute task = new AsyncItineraryCompute(LoadingPageActivity.this);
            task.execute(url);
            //task.execute(url2);

        } else {
            // No, there is no stepPoint
            if (param1 == 0.0 && param2 == 0.0 && param3 == 0.0 && param4 == 0.0) {
                param1 = 47.2484039066116;
                param2 = -1.549636963829987;
                param3 = 47.212191574506164;
                param4 = -1.5535549386503666;
            }
            /*String url = String.format("http://ser-info-03.ec-nantes.fr:8080/itinerary/" +
                            "itinerary4?pdaLat=%s&pdaLong=%s&pddLat=%s&pddLong=%s&transportation=%s,%s,%s,%s",
                    param1, param2, param3, param4, options[0], options[1], options[2], options[3]);*/


            String url = String.format("http://ser-info-03.ec-nantes.fr:8080/itineraryBIS/" +
                            "itinerary6?start=%s,%s&end=%s,%s&hasStep=%s&transportation=%s,%s,%s,%s&hourStart=%s&time=%s",
                    param1, param2, param3, param4, param5, options[0], options[1], options[2], options[3], start, time);

            // activity launch
            System.out.println(url);
            AsyncItineraryCompute task = new AsyncItineraryCompute(LoadingPageActivity.this);
            task.execute(url);
        }
    }
}
