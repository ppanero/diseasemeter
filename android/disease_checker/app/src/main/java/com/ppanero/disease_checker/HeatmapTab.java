package com.ppanero.disease_checker;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Light on 11/03/16.
 */

public class HeatmapTab extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private Button searchButton;
    private EditText diseaseText;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.heatmap_tab, container, false);


        progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar_heatmap);
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);


        diseaseText = (EditText)rootView.findViewById(R.id.disease_it);
        searchButton = (Button)rootView.findViewById(R.id.search_disease_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String param = diseaseText.getText().toString();
                new RetriveHeatmapDataTask().execute(param);
            }
        });

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addHeatMap();
    }

    /*private void addHeatMap(){
        // Create the gradient.
        int[] colors = { Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = { 0.2f, 1f};

        Gradient gradient = new Gradient(colors, startPoints);

        // Create a heat mMap tile provider, passing it the latlngs of the police stations.
        WeightedLatLng basePoint = new WeightedLatLng(new LatLng(75.485566, 167.895584), 0);
        Set<WeightedLatLng> baseData = new HashSet<>();
        baseData.add(basePoint);
        mProvider = new HeatmapTileProvider.Builder()
                .weightedData(baseData)
                .gradient(gradient)
                .build();

        // Add a tile overlay to the mMap, using the heat mMap tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        mProvider.setOpacity(0.7);
        mOverlay.clearTileCache();
    }*/
    private void addHeatMap() {
        List<WeightedLatLng> list = new ArrayList<WeightedLatLng>();

        list.add(new WeightedLatLng(new LatLng(41.394640, 2.172455), 1000));
        list.add(new WeightedLatLng(new LatLng(41.372307, 2.125695), 800));
        list.add(new WeightedLatLng(new LatLng(41.449830, 2.225583), 600));
        list.add(new WeightedLatLng(new LatLng(41.500640, 2.175555), 50));
        list.add(new WeightedLatLng(new LatLng(41.444640, 2.022455), 100));
        list.add(new WeightedLatLng(new LatLng(41.447440, 2.042455), 600));
        list.add(new WeightedLatLng(new LatLng(41.398230, 2.172365), 1000));
        list.add(new WeightedLatLng(new LatLng(41.379240, 2.042455), 60));
        list.add(new WeightedLatLng(new LatLng(41.408680, 2.032595), 100));
        list.add(new WeightedLatLng(new LatLng(41.392340, 2.012455), 90));
        list.add(new WeightedLatLng(new LatLng(41.401230, 2.135875), 10));

        // Create the gradient.
        int[] colors = { Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = { 0.2f, 1f};

        Gradient gradient = new Gradient(colors, startPoints);

        // Create a heat mMap tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
                .weightedData(list)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the mMap, using the heat mMap tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        mProvider.setOpacity(0.7);
        mOverlay.clearTileCache();

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.401230, 2.135875))
                .title("Ebola"));
    }




    public class RetriveHeatmapDataTask extends AsyncTask<String, Void, HeatmapItem>{

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            mMap.clear();
        }

        protected HeatmapItem doInBackground(String... params) {
            // Do some validation here
            try {
                String stringUrl = MACRO.API_IP.concat(MACRO.API_HEATMAP_ENTRYPOINT);
                for (String param : params){
                    boolean withParams = false;
                    if (!param.equals("")) {
                        if(!withParams) stringUrl = stringUrl.concat("?");
                        stringUrl = stringUrl.concat("disease=").concat(param);
                    }
                }
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
                        Set<MarkerOptions> markers = new HashSet<>();
                        List<WeightedLatLng> points = new ArrayList<>();

                        //Process heatpoints
                        JSONObject jsonDiseases = (JSONObject) new JSONTokener(response).nextValue();
                        JSONArray jsondlist = (JSONArray) jsonDiseases.get("heatPointList");
                        for (int i = 0; i < jsondlist.length(); ++i){
                            JSONObject obj = jsondlist.getJSONObject(i);
                            //First iteration to get the center
                            JSONArray coordinates = (JSONArray) ((JSONObject)obj.get("location")).get("coordinates");
                            points.add(new WeightedLatLng(new LatLng((Double)coordinates.get(0),(Double)coordinates.get(1)),
                                    obj.getInt("weight")));
                        }

                        //Process markers
                        jsondlist = (JSONArray) jsonDiseases.get("centerList");
                        for (int i = 0; i < jsondlist.length(); ++i){
                            JSONObject obj = jsondlist.getJSONObject(i);
                            //First iteration to get the center
                            String name = obj.getString("name");
                            JSONArray coordinates = (JSONArray) ((JSONObject)obj.get("location")).get("coordinates");
                            markers.add(new MarkerOptions()
                                    .position(new LatLng((Double)coordinates.get(0),(Double)coordinates.get(1)))
                                    .title(name));
                        }

                        //finished
                        return new HeatmapItem(markers, points);
                    }
                } catch (JSONException e) {
                    return null;
                }
            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
            return null;
        }

        protected void onPostExecute(HeatmapItem data) {
            progressBar.invalidate();
            progressBar.setVisibility(View.GONE);
            if(data != null) {
                Set<MarkerOptions> markers = data.getMarkers();
                for(MarkerOptions marker : markers) {
                    mMap.addMarker(marker);
                }
                mProvider.setWeightedData(data.getPoints());
                // Add a tile overlay to the mMap, using the heat mMap tile provider.
                mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                mProvider.setOpacity(0.7);
                mOverlay.clearTileCache();
            }
        }
    }
}
