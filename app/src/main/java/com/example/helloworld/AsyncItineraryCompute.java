package com.example.helloworld;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Iterator;

public class AsyncItineraryCompute extends AsyncTask<String, Integer, JSONObject> {

    private AppCompatActivity myActivity;

    //Constructor
    public AsyncItineraryCompute (AppCompatActivity LoadingPageActivity) {
        myActivity = LoadingPageActivity;
    }


    @Override
    protected void onPreExecute() {
        //Prepare task and show waiting view
        myActivity.setContentView(R.layout.activity_loading_page);
        //progress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));


    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        publishProgress(1);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        URL url;
        HttpURLConnection urlConnection = null;
        String result = null;
        int error = 0;

        try{
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection(); // Open
            InputStream in = new BufferedInputStream(urlConnection.getInputStream()); // Stream
            publishProgress(2);
            result = readStream(in);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            error = 1;
        }
        catch (IOException e) {
            e.printStackTrace();
            error = 2;
        }

        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        publishProgress(4);
        JSONObject json = new JSONObject();
        try{
            switch (error){
                case 0 :
                    json = new JSONObject(result);
                    break;
                case 1 :
                    json.put("message",myActivity.getString(R.string.error_url_format));
                    break;
                case 2 :
                    json.put("message", String.format("%s\n%s", myActivity.getString(R.string.connexion_failed), strings[0]));
                default:
                    break;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        ProgressBar pb = myActivity.findViewById(R.id.progress_bar);
        pb.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(final JSONObject c) {
        if (c == null){
            Toast.makeText(myActivity, R.string.error404, Toast.LENGTH_SHORT).show();
        }else{
            try {
                Toast.makeText(myActivity, c.getString("message"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        //Reading JSON
        final ArrayList<double[]> coord = new ArrayList<>();
        try{
            JSONArray items = c.getJSONArray("items");
            for (int i = 0; i<items.length(); i++)
            {
                JSONObject point = items.getJSONObject(i);
                double longitude = point.getDouble("longitude");
                double latitude = point.getDouble("latitude");
                double[] p = {latitude,longitude};
                coord.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                int i = 1;
                for (double[] p:coord) {
                    intent.putExtra("point"+i,p);
                    i++;
                }
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
