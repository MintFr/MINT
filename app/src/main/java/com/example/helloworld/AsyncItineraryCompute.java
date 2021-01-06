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
import java.util.Arrays;
import java.util.Iterator;

public class AsyncItineraryCompute extends AsyncTask<String, Integer, JSONArray> {

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

    /**
     * @param strings
     * Background task to send a request to ECN server with parameters such as start point, end point,
     * transport.
     * And reception of a JSONArray like :
     *         [{"type":route,
     *            "exposition":0.60,
     *             "time":444.1,
     *             "points": [{"longitude":42.155,"latitude":55.244444},{...},{...},...]}
     *         ,{..}]
     *
     * But now, response is only : [{"longitude":42.155,"latitude":55.244444},{...},{...},...]
     *
     * @return JSONArray
     */

    @Override

    protected JSONArray doInBackground(String... strings) {
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
            publishProgress(2); //"marker" to display progress on splash screen
            result = readStream(in); //read text file
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



        JSONArray json = new JSONArray();
        try{
            JSONObject message = new JSONObject(); // to debug, maybe to delete for real version of the app
            switch (error){
                case 0 :
                    json = new JSONArray(result); //create JSONArray from text file JSON encoded
                    message.put("message",myActivity.getString(R.string.connexion_success));
                    break;
                case 1 :
                    message.put("message",myActivity.getString(R.string.error_url_format));
                    break;
                case 2 :
                    message.put("message", String.format("%s\n%s", myActivity.getString(R.string.connexion_failed), strings[0]));
                    break;
                default:
                    break;
            }
            json.put(json.length(),message);

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

    /**
     * Function to decode JSONArray and extract attributes
     * @param c
     */
    @Override
    protected void onPostExecute(final JSONArray c) {
        if (c == null){
            Toast.makeText(myActivity, R.string.error404, Toast.LENGTH_SHORT).show();
        }else{
            try {
                Toast.makeText(myActivity, c.getJSONObject(c.length()-1).getString("message"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



     ///////////////////////////////////////////////////////////////////////////////////////////////
     //Change this part to adapt to new format of response

        //Reading JSON
        final ArrayList<double[]> coord = new ArrayList<>();
        try{
            for (int i = 0; i<c.length()-1; i++)        //-1 to avoid the "message" element
            {
                JSONObject point = c.getJSONObject(i);
                double longitude = point.getDouble("longitude");
                double latitude = point.getDouble("latitude");
                double[] p = {latitude,longitude};
                coord.add(p);
                Toast.makeText(myActivity, Arrays.toString(p), Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

     ///////////////////////////////////////////////////////////////////////////////////////////////

        TextView text = myActivity.findViewById(R.id.text);
        text.setText(R.string.itinerary_end_message); // Updates the textview
        ProgressBar pb = myActivity.findViewById(R.id.progress_bar);
        pb.setProgress(5);


        int ITINERARY_END_SCREEN_TIMEOUT = 1000;

        //send to the itinerary activity, with delay to let user see the message "calcul terminé"
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Send to the next Activity
                Intent intent = new Intent(myActivity.getApplicationContext(), ItineraryActivity.class);
                int i = 0;
                for (double[] p:coord) {
                    intent.putExtra(String.format("point%d", i),p);
                    i++;
                }
                myActivity.startActivity(intent);
                myActivity.finish();
            }
        }, ITINERARY_END_SCREEN_TIMEOUT);

    }

    /**
    * Method to read server response, which is as text file, and put it in a String object.
     * @param is
     * @return String
    */
    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }        is.close();
        return sb.toString();
    }
}