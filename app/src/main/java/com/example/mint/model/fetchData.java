package com.example.mint.model;

import static com.example.mint.model.App.getRes;

import android.os.AsyncTask;
import android.widget.TextView;

import com.example.mint.R;
import com.example.mint.controller.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class fetchData extends AsyncTask<String, String, String> {
    TextView textView;
    String donneesPollen, textPopup, urlData, result;
    Integer max;
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

            //Get the link with today's date
            urlData = "http://51.77.201.227:100/pickdate/noemie/" + todayData;
            donneesPollen = MyHttpUtils.getDataHttpUriConnection(urlData);
            try {
                JSONArray list = new JSONArray(donneesPollen);
                JSONObject dic = new JSONObject(String.valueOf(list.get(0)));
                String[] pollenList = new String[5];
                for (int i = 0; i <= 4; i++) {
                    pollenList[i] = "";
                }

                //Get the names of each pollen for each sensibility
                for (int i = 3; i < dic.length() - 1; i++) {
                    if ((dic.getInt(dic.names().getString(i)) == 0)) {
                        pollenList[0] += dic.names().getString(i) + " ";
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
                    if ((dic.getInt(dic.names().getString(i)) >= 4)) {
                        pollenList[4] += dic.names().getString(i) + " " ;
                }

                // Avoid printing the ones that are empty
                textPopup = new String();
                max = 0;
                for (int j = 0; j < 5; j++) {
                    if (pollenList[j] != "") {
                        textPopup += "- " + pollenList[j] + ": " + j + " / 5 " + "\n" +"\n" ;
                        }
                }
                textPopup = textPopup + getRes().getString(R.string.message_fin_pollen_alert);
            }

            //Get max for the color or the Popup
            max = dic.getInt("Total");
            MainActivity.maxPollen = 1;

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return textPopup;
        }
    }

