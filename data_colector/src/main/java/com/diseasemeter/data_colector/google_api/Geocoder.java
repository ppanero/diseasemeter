package com.diseasemeter.data_colector.google_api;

import com.diseasemeter.data_colector.common.MACRO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Light on 16/04/16.
 */
public class Geocoder {

    private static final String API_KEY = "&key=YOUR_API_KEY";
    private static final String HTTP = "https://maps.googleapis.com/maps/api/geocode/json?address=";

    public static Double[] geocode(String place) {

        String stringUrl = HTTP.concat(place.replace(MACRO.SPACE, MACRO.PLUS)).concat(API_KEY);
        try {

            URL url = new URL(stringUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            String response = "";
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                response = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            try {
                if (!response.equals("")) {
                    JSONObject jsonDiseases = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray results = (JSONArray) jsonDiseases.get("results");
                    JSONObject geometry = (JSONObject) ((JSONObject)results.get(0)).get("geometry");
                    JSONObject location = geometry.getJSONObject("location");

                    return new Double[]{ location.getDouble("lng"), location.getDouble("lat")};
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
