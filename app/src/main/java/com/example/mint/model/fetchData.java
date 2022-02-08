package com.example.mint.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mint.R;
import com.example.mint.controller.MainActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class fetchData extends AsyncTask<String, String, String> {
    TextView textView;
    String donneesPollen, result, textPopup, urlData, today, todayData;
    Integer max;
    Calendar calendar;
    Arrays l;
        @Override
        protected void onPreExecute() {
            textView.append("Le risque allergique prévisionnel (RAEP) pour chaque pollen est de :" + "\n" + "\n");
        }

    @Override
    protected void onPostExecute(String result) {
        textView.append(result + "\n");
    }

    public fetchData(TextView textView) {
        this.textView = textView;
    }

        @Override
        protected String doInBackground(String... params) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat today = new SimpleDateFormat("MM_dd");
            String todayData = today.format(calendar.getTime());
            urlData = "http://51.77.201.227:100/pickdate/noemie/" + todayData;
            donneesPollen = MyHttpUtils.getDataHttpUriConnection(urlData);
            try {
                JSONArray list = new JSONArray(donneesPollen);
                JSONObject dic = new JSONObject(String.valueOf(list.get(0)));
                String[] pollenList = new String[5];
                for (int i = 0; i < 5; i++) {
                    pollenList[i] = "";
                }
                for (int i = 3; i < dic.length() - 1; i++) {
                    if ((dic.getInt(dic.names().getString(i)) == 0)) {
                        pollenList[0] += dic.names().getString(i) + " ";
                        Log.v("TAG", "msg" + pollenList[0]) ;
                    }
                    if ((dic.getInt(dic.names().getString(i)) == 1)) {
                        pollenList[1] += dic.names().getString(i) + " " ;
                    }
                    if ((dic.getInt(dic.names().getString(i)) == 2)) {
                        pollenList[2] += dic.names().getString(i) + " " ;
                    }
                    if ((dic.getInt(dic.names().getString(i)) == 3)) {
                        pollenList[3] += dic.names().getString(i) + " " ;
                    }
                    if ((dic.getInt(dic.names().getString(i)) == 4)) {
                        pollenList[4] += dic.names().getString(i) + " " ;
                }
                textPopup = new String();
                max = 0;
                for (int j = 0; j < 5; j++) {
                    if (pollenList[j] != "") {
                        textPopup += "- " + pollenList[j] + ": " + j + "\n" +"\n" ;
                        }
                }
                textPopup = textPopup + "Les données sont fournies par le RNSA et le RAEP est calculé grâce au risque allergique théorique, à un index clinique provenant des bulletins de médecins, d\'un index phénologique appuyé sur des observations réalisées sur la croissance et la floraison des espèces et enfin sur un index météorologique.";

            }
            max = dic.getInt("Total");
            MainActivity.maxPollen = 1;

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return textPopup;
        }
    }

