package com.example.helloworld;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class AsyncItineraryCompute extends AsyncTask<String, Integer, String> {

    private AppCompatActivity myActivity;

    //Constructor
    public AsyncItineraryCompute (AppCompatActivity LoadingPageActivity) {
        myActivity = LoadingPageActivity;
    }

    //Prepare task and show waiting view
    @Override
    protected void onPreExecute() {
        myActivity.setContentView(R.layout.activity_loading_page);
        //progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));


    }

    @Override
    protected String doInBackground(String... strings) {
        publishProgress(1);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        URL url;
        HttpURLConnection urlConnection = null;
        String result = null;

        try{
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection(); // Open
            InputStream in = new BufferedInputStream(urlConnection.getInputStream()); // Stream
            publishProgress(2);
            result = readStream(in); // Read stream
        }
        catch (MalformedURLException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        publishProgress(4);
//        JSONObject json = null;
//        try{
//            json = new JSONObject(result);
//        }catch (JSONException e) {
//            e.printStackTrace();
//        }

        return result;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        ProgressBar pb = myActivity.findViewById(R.id.progress_bar);
        pb.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(final String c) {
        String result = c;

        //à utiliser pour lire les JSON, penser à modifier le paramètre d'entrée
//        try{
//            JSONArray items = json.getJSONArray("items");
//            for (int i = 0; i<items.length(); i++)
//            {
//                //get coord of points from JSON
//                Iterator<String> it = json.keys();
//
//                //JSONObject flickr_entry = items.getJSONObject(i);
//                //String urlmedia = flickr_entry.getJSONObject("media").getString("m");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        TextView text = myActivity.findViewById(R.id.text);
        text.setText(R.string.itinerary_end_message); // Updates the textview
        ProgressBar pb = myActivity.findViewById(R.id.progress_bar);
        pb.setProgress(5);


        int ITINERARY_END_SCREEN_TIMEOUT = 1000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Send to the next Activity
                Intent intent = new Intent(myActivity.getApplicationContext(), ItineraryActivity.class);
                intent.putExtra("text",c);
                myActivity.startActivity(intent);
                myActivity.finish();

            }
        }, ITINERARY_END_SCREEN_TIMEOUT);

    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }        is.close();
        return sb.toString();
    }
}
