package com.example.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Double.parseDouble;

public class ItineraryActivity extends AppCompatActivity  {

    private MapView map = null;
    private ArrayList<double[]> response = new ArrayList<>();
    private IMapController mapController = null;
    private GeoPoint startPoint;
    private GeoPoint endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        //Itinerary display
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);
        GeoPoint defaultPoint = new GeoPoint(47.21, -1.55);
        mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(defaultPoint);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        //Points construction
        Intent intent = getIntent();
        ArrayList<double[]> response = new ArrayList<>();
        boolean bool = true;    //(intent != null);
        int i = 0;
        while (bool){
            double[] point = intent.getDoubleArrayExtra(String.format("point%d", i));
            i++;
            if (point != null) {
                response.add(point);
            }else{
                bool = false;
            }
        }

        Toast.makeText(this, String.format("%d", response.size()), Toast.LENGTH_SHORT).show();

        if (response.size() > 0) {
            startPoint = new GeoPoint(response.get(0)[0], response.get(0)[1]);
            endPoint = new GeoPoint(response.get(1)[0], response.get(1)[1]);
        }else{
            startPoint = new GeoPoint(47.21, -1.55);
            endPoint = new GeoPoint(47.21, -1.55);
            Toast.makeText(this, String.format("taille de response = %d", response.size()), Toast.LENGTH_SHORT).show();
        }


        //Points on map
        ArrayList<OverlayItem> items = new ArrayList<>();
        for (int j = 0; j < response.size();j++){
            items.add(new OverlayItem("point "+j, "",
                    new GeoPoint(response.get(j)[0],response.get(j)[1]))); // Lat/Lon decimal degrees
        }

        //Overlay of points
        Marker startMarker = new Marker(map);
        System.out.println(startPoint);
        //GeoPoint startPosition = new GeoPoint(response.get(0)[0],response.get(0)[1]);
        startMarker.setPosition(startPoint);

        Marker endMarker = new Marker(map);
        System.out.println(endPoint);
        //GeoPoint endPosition = new GeoPoint(response.get(response.size()-1)[0],response.get(response.size()-1)[1]);
        endMarker.setPosition(endPoint);

        map.getOverlays().add(startMarker);
        map.getOverlays().add(endMarker);

        //display points with coordinates in array, under the map
        TextView viewPoint1 = (TextView) findViewById(R.id.point1);
        TextView viewPoint2 = (TextView) findViewById(R.id.point2);

        // get start and end addresses
        String start = getString(R.string.itinerary_point1)+" : "+(Preferences.getAddress("startAddress",ItineraryActivity.this));
        String end = getString(R.string.itinerary_point2)+" : "+(Preferences.getAddress("endAddress",ItineraryActivity.this));

        if (response.size() > 0){
            viewPoint1.setText(start);
            viewPoint2.setText(end);
        } else {
            viewPoint1.setText("error");
            viewPoint2.setText("error");

        }

        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

    }

    public void onClickP1(View view) {
        mapController.setCenter(startPoint);
    }

    public void onClickP2(View view) {
        mapController.setCenter(endPoint);
    }
}