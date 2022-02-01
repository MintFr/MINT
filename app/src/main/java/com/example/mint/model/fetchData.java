package com.example.mint.model;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mint.R;
import com.example.mint.controller.MainActivity;

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

public class fetchData extends AsyncTask<String, String, String> {
    TextView textView;

        @Override
        protected void onPreExecute() {
            textView.append("Get data ...\n\n");
        }

        @Override
        protected String doInBackground(String... params) {
            return MyHttpUtils.getDataHttpUriConnection("http://51.77.201.227:100/pickdate/noemie/12_25");
        }

        @Override
        protected void onPostExecute(String result) {
            textView.append(result + "\n");
        }

    public fetchData(TextView textView) {
            this.textView = textView;
    }
}