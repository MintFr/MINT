package com.example.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Double.parseDouble;

public class ItineraryActivity extends AppCompatActivity  {

    private MapView map = null;
    private final ArrayList<double[]> response = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        //Itinerary display
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);
        GeoPoint defaultPoint = new GeoPoint(47.21, -1.55);
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(defaultPoint);
        map.setBuiltInZoomControls(true);


        //Points construction
        Intent intent = getIntent();
        ArrayList<double[]> response = new ArrayList<>();
        boolean bool = true;    //(intent != null);
        int i = 0;
        while (bool){
            double[] point = intent.getDoubleArrayExtra(String.valueOf(i));
            i++;
            if (point != null) {
                response.add(point);
            }else{
                bool = false;
            }
        }

        //Points on map
        ArrayList<OverlayItem> items = new ArrayList<>();
        for (int j = 0; j < items.size();j++){
            items.add(new OverlayItem("point "+j, "",
                    new GeoPoint(response.get(j)[0],response.get(j)[1]))); // Lat/Lon decimal degrees
        }

        //Overlay of points
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, this);
        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay);


        //display points with coordinates in array, under the map
        TextView viewPoint1 = (TextView) findViewById(R.id.point1);
        TextView viewPoint2 = (TextView) findViewById(R.id.point2);

        if (response.size() > 0){
            viewPoint1.setText(String.format("%s : %s,%s", getString(R.string.itinerary_point1), response.get(0)[0], response.get(0)[1]));
            viewPoint2.setText(String.format("%s : %s,%s", getString(R.string.itinerary_point2), response.get(1)[0], response.get(1)[1]));
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
        IMapController mapController = map.getController();
        GeoPoint startPoint;
        if (response.size() != 0) {
            startPoint = new GeoPoint(response.get(0)[0], response.get(0)[1]);
        }else{
            startPoint = new GeoPoint(47.21, -1.55);
        }
        mapController.setCenter(startPoint);


    }

    public void onClickP2(View view) {
        IMapController mapController = map.getController();
        GeoPoint endPoint;
        if (response.size() != 0) {
            endPoint = new GeoPoint(response.get(response.size()-1)[0], response.get(response.size()-1)[1]);
        }else{
            endPoint = new GeoPoint(47.21, -1.55);
        }
        mapController.setCenter(endPoint);
    }
}