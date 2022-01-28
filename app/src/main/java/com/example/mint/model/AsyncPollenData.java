package com.example.mint.model;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.mint.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AsyncPollenData extends AsyncTask<String , String , String> {
    TextView textView;

    @Override
    protected void onPreExecute() {
        textView.append("Get data ...\n\n");
        textView = (TextView) textView.findViewById(R.id.pollen_alert_text);
    }

    @Override
    protected String doInBackground(String... params) {
        return MyHttpUtils.getDataHttpUriConnection(params[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        textView.append(result + "\n");
    }
}
