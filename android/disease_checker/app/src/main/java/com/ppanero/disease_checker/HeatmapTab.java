package com.ppanero.disease_checker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Light on 11/03/16.
 */

public class HeatmapTab extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.heatmap_tab, container, false);

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng HAMBURG = new LatLng(53.558, 9.927);
        LatLng KIEL = new LatLng(53.551, 9.993);
        LatLng SYDNEY = new LatLng(-34, 151);
        // Add a marker in Sydney, Australia, and move the camera.
        Marker sydney = mMap.addMarker(new MarkerOptions().position(SYDNEY)
                .title("Marker in Sydney"));

        Marker hamburg = mMap.addMarker(new MarkerOptions().position(HAMBURG)
                .title("Hamburg"));
        Marker kiel = mMap.addMarker(new MarkerOptions().position(KIEL)
                .title("Kiel"));
        addHeatMap();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.394640, 2.172455), 10));
    }

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
    }
}
