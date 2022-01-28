package com.example.mint.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyHttpUtils {

    public static String getDataHttpUriConnection(String uri){
        try {
            URL url = new URL("http://51.77.201.227:100/pickdate/noemie/12_25");
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            String donneesPollen = inputStreamToString(urlConnection.getInputStream());
            return donneesPollen;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String inputStreamToString(InputStream stream)  {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = "";
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
