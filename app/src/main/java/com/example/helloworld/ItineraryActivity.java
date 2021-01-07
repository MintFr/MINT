package com.example.helloworld;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        ArrayList<double[]> points = new ArrayList<double[]>();
        points.add(new double[]{47.205461, -1.559122});
        points.add(new double[]{47.205559,-1.558233});
        points.add(new double[]{47.206165,-1.558373});
        points.add(new double[]{47.20622,-1.557744});

        Itinerary itinerary = new Itinerary("voiture",0.5f,52f,points);

        //Overlay of points

        // polyline for itinerary
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int j = 0; j<itinerary.getPointSize();j++){
            geoPoints.add(new GeoPoint(itinerary.getPoints().get(j)[0],itinerary.getPoints().get(j)[1]));
        }

        //Paintlists for the effect on the polyline
        final Paint paintBorder = new Paint();
        paintBorder.setStrokeWidth(30);
        paintBorder.setStyle(Paint.Style.FILL_AND_STROKE);
        paintBorder.setColor(Color.WHITE);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setStrokeJoin(Paint.Join.ROUND);
        paintBorder.setShadowLayer(15,0,10,getResources().getColor(R.color.colorTransparentBlack));
        paintBorder.setAntiAlias(true);

        final Paint paintInside = new Paint();
        paintInside.setStrokeWidth(10);
        paintInside.setStyle(Paint.Style.FILL);
        paintInside.setColor(getResources().getColor(R.color.colorAccent));
        paintInside.setStrokeCap(Paint.Cap.ROUND);
        paintInside.setStrokeJoin(Paint.Join.ROUND);
        paintInside.setAntiAlias(true);

        Polyline line = new Polyline(map);
        line.setPoints(geoPoints);
        line.getOutlinePaintLists().add(new MonochromaticPaintList(paintBorder));
        line.getOutlinePaintLists().add(new MonochromaticPaintList(paintInside));
        map.getOverlayManager().add(line);

        // start and end markers
        Marker startMarker = new Marker(map);
        GeoPoint startPosition = new GeoPoint(itinerary.getPoints().get(0)[0],itinerary.getPoints().get(0)[1]);
        startMarker.setPosition(startPosition);
        startMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
        startMarker.setFlat(true);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        map.getOverlays().add(startMarker);

        Marker endMarker = new Marker(map);
        GeoPoint endPosition = new GeoPoint(itinerary.getPoints().get(itinerary.getPointSize()-1)[0],itinerary.getPoints().get(itinerary.getPointSize()-1)[1]);
        endMarker.setPosition(endPosition);
        endMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
        endMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        map.getOverlays().add(endMarker);

        //display points with coordinates in array, under the map
        TextView viewPoint1 = (TextView) findViewById(R.id.start_point);
        TextView viewPoint2 = (TextView) findViewById(R.id.end_point);

        //display time and pollution
        TextView time = findViewById(R.id.time);
        TextView pollution = findViewById(R.id.pollution);

        // get start and end addresses
        String start = getString(R.string.itinerary_point1)+" : "+(Preferences.getAddress("startAddress",ItineraryActivity.this));
        String end = getString(R.string.itinerary_point2)+" : "+(Preferences.getAddress("endAddress",ItineraryActivity.this));

        if (response.size() > 0){
            viewPoint1.setText(start);
            viewPoint2.setText(end);
            int timeInt = (int) itinerary.getTime();
            String timeStr = timeInt +" min";
            time.setText(timeStr);
            String str = "3";
            str = str.replaceAll("3", "³");
            String polStr = itinerary.getPollution()+"µg/m"+str;
            pollution.setText(polStr);
        } else {
            viewPoint1.setText("error");
            viewPoint2.setText("error");

        }

        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        bottomNav.setItemIconTintList(null);
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