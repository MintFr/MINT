package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LoadingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        //Intent intent = getIntent();
        //Get parameters corresponding to addresses from the main activity
//        String param1 = intent.getStringExtra(getString(R.string.param1));
//        String param2 = intent.getStringExtra(getString(R.string.param2));

        //build url
        //String url = "http://ser-info-03.ec-nantes.fr:8080/mintitinerary?address1="+param1+"&adress2="+param2;
        ////url to test hello web service
        //String url = "http://ser-info-03.ec-nantes.fr:8080/hello?name=mint"
        String url = "https://google.com/";
        //start of the async task
        AsyncItineraryCompute task = new AsyncItineraryCompute(LoadingPageActivity.this);
        task.execute(url);

    }
}