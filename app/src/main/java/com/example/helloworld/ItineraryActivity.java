package com.example.helloworld;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.HEREWeGoTileSource;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource;
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
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

import static java.lang.Double.parseDouble;

public class ItineraryActivity extends AppCompatActivity  {

    private MapView map = null;
    private ArrayList<double[]> response = new ArrayList<>();
    private IMapController mapController = null;
    private GeoPoint startPoint;
    private GeoPoint endPoint;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        // inflater used to display different views
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //Itinerary display
        map = findViewById(R.id.map);
        //final MapBoxTileSource tileSource = new MapBoxTileSource();
        //tileSource.retrieveAccessToken(this);
        //tileSource.retrieveMapBoxMapId(this);
        //map.setTileSource(tileSource);
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

        ArrayList<double[]> points = new ArrayList<>();
        points.add(new double[]{47.205461,-1.559122});
        points.add(new double[]{47.205559,-1.558233});
        points.add(new double[]{47.206165,-1.558373});
        points.add(new double[]{47.20622,-1.557744});

        ArrayList<double[]> points1 = new ArrayList<>();
        points1.add(new double[]{47.205461,-1.559122});
        points1.add(new double[]{47.206058,-1.55925});
        points1.add(new double[]{47.20622,-1.557744});

        ArrayList<int[]> stepTime = new ArrayList<>();
        stepTime.add(new int[]{84});
        stepTime.add(new int[]{62});
        stepTime.add(new int[]{73});

        ArrayList<int[]> stepTime1 = new ArrayList<>();
        stepTime1.add(new int[]{100});
        stepTime1.add(new int[]{62});

        ArrayList<int[]> stepDistance = new ArrayList<>();
        stepDistance.add(new int[]{92});
        stepDistance.add(new int[]{65});
        stepDistance.add(new int[]{85});

        ArrayList<int[]> stepDistance1 = new ArrayList<>();
        stepDistance1.add(new int[]{150});
        stepDistance1.add(new int[]{65});

        Itinerary iti = new Itinerary("piéton",0.5,5, stepTime,stepDistance,points);
        Itinerary iti1 = new Itinerary("voiture",0.7,2, stepTime1,stepDistance1,points1);

        ArrayList<Itinerary> itineraries = new ArrayList<>();
        itineraries.add(iti);
        itineraries.add(iti1);

        for (int j=0; j<itineraries.size();j++){
            displayItinerary(itineraries.get(j),itineraries);
        }

        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    //DISPLAY ITINERARY
    public void displayItinerary(final Itinerary itinerary,ArrayList<Itinerary> list){
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
        paintInside.setColor(getResources().getColor(R.color.colorLightGreen));
        paintInside.setStrokeCap(Paint.Cap.ROUND);
        paintInside.setStrokeJoin(Paint.Join.ROUND);
        paintInside.setAntiAlias(true);

        Polyline line = new Polyline(map);
        line.setPoints(geoPoints);
        line.getOutlinePaintLists().add(new MonochromaticPaintList(paintBorder));
        line.getOutlinePaintLists().add(new MonochromaticPaintList(paintInside));

        // SETUP INFO WINDOW
        final View infoWindowView = inflater.inflate(R.layout.itinerary_infowindow,null);
        // find all the corresponding views
        TextView timeInfo = infoWindowView.findViewById(R.id.time_info);
        ImageView transportationInfo = infoWindowView.findViewById(R.id.transportation);
        final ImageView pollutionInfo = infoWindowView.findViewById(R.id.pollution_icon);
        // set values for time, transportation and pollution
        //time
        int t = Double.valueOf(itinerary.getTime()).intValue();
        String s = Integer.toString(t);
        timeInfo.setText(s);
        //transportation
        switch (itinerary.getType()){
            case "piéton" :
                transportationInfo.setImageResource(R.drawable.ic_walk_activated);
                break;
            case "voiture" :
                transportationInfo.setImageResource(R.drawable.ic_car_activated);
                break;
            case "transport en commun" :
                transportationInfo.setImageResource(R.drawable.ic_tram_activated);
                break;
            case "vélo" :
                transportationInfo.setImageResource(R.drawable.ic_bike_activated);
                break;
        }
        //pollution
        if((itinerary.getPollution()>=0)&&(itinerary.getPollution()<0.33)){
            pollutionInfo.setImageResource(R.drawable.ic_pollution_good);
        }
        else if((itinerary.getPollution()>=0.33)&&(itinerary.getPollution()<0.66)){
            pollutionInfo.setImageResource(R.drawable.ic_pollution_medium);
        }
        else if((itinerary.getPollution()>=0.66)&&(itinerary.getPollution()<=1)){
            pollutionInfo.setImageResource(R.drawable.ic_pollution_bad);
        }
        final InfoWindow infoWindow = new InfoWindow(infoWindowView,map) {
            @Override
            public void onOpen(Object item) {
            }

            @Override
            public void onClose() {
            }
        };

        // add infowindow to the polyline
        line.setInfoWindow(infoWindow);
        line.showInfoWindow(); // we want the infowindow to already be showing without having to click

        RelativeLayout layout = infoWindowView.findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoWindow.close();
            }
        });

        // add line to map
        map.getOverlayManager().add(line);

        // on click behaviour of line
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                displayDetails(itinerary);
                return true;
            }
        });

        // start and end markers
        Marker startMarker = new Marker(map);
        GeoPoint startPosition = geoPoints.get(0);
        startMarker.setPosition(startPosition);
        startMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
        startMarker.setFlat(true);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        map.getOverlays().add(startMarker);

        Marker endMarker = new Marker(map);
        GeoPoint endPosition = geoPoints.get(itinerary.getPointSize()-1);
        endMarker.setPosition(endPosition);
        endMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
        endMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        map.getOverlays().add(endMarker);
    }

    //DISPLAY DETAILS UNDER MAP
    public void displayDetails(Itinerary itinerary){
        //start and end
        TextView viewPoint1 = (TextView) findViewById(R.id.start_point);
        TextView viewPoint2 = (TextView) findViewById(R.id.end_point);

        //time and pollution
        TextView time = findViewById(R.id.time);
        TextView pollution = findViewById(R.id.pollution);

        // get start and end addresses
        String start = getString(R.string.itinerary_point1)+" : "+(Preferences.getAddress("startAddress",ItineraryActivity.this));
        String end = getString(R.string.itinerary_point2)+" : "+(Preferences.getAddress("endAddress",ItineraryActivity.this));

        if (itinerary.getPointSize() > 0){
            // start and end
            viewPoint1.setText(start);
            viewPoint2.setText(end);
            // time
            int timeInt = (int) itinerary.getTime();
            String timeStr = timeInt +" min";
            time.setText(timeStr);
            //pollution
            String str = "3";
            str = str.replaceAll("3", "³"); // set the 3 to superscript
            String polStr = itinerary.getPollution()+"µg/m"+str;
            pollution.setText(polStr);
            if (itinerary.getPointSize()>2){
                // first we want to clear all previous steps that might already be displayed
                LinearLayout stepsLayout = findViewById(R.id.steps_linear_layout);
                int index = stepsLayout.indexOfChild(viewPoint2); // this is the number of steps from the previously displayed itinerary
                if (index>2) { // <=> if there is already something displayed in the stepsLayout
                    stepsLayout.removeViews(2, index - 2);
                }
                for (int k=1;k<itinerary.getPointSize();k++){
                    // k is going to be the index at which we add the stepView
                    final View stepView = inflater.inflate(R.layout.itinerary_step_layout,null); // get the view from layout
                    TextView stepTimeMin = stepView.findViewById(R.id.step_time_min); // get the different textViews from the base view
                    TextView stepTimeSec = stepView.findViewById(R.id.step_time_sec);
                    TextView stepDist = stepView.findViewById(R.id.step_distance);
                    int timeMin = (itinerary.getStepTime().get(k-1)[0] % 3600)/60; // amount of minutes it takes to travel this step
                    int timeSec = (itinerary.getStepTime().get(k-1)[0] % 60 ); // remaining seconds
                    stepTimeMin.setText(String.format("%02d",timeMin));
                    stepTimeSec.setText(String.format("%02d",timeSec));
                    stepDist.setText(String.format("%d",itinerary.getStepDistance().get(k-1)[0]));
                    // add the textView to the linearlayout which contains the steps
                    stepsLayout.addView(stepView,k+1);
                }
            }
        } else {
            viewPoint1.setText("error");
            viewPoint2.setText("error");
        }
    }

    public void onClickP1(View view) {
        mapController.setCenter(startPoint);
    }

    public void onClickP2(View view) {
        mapController.setCenter(endPoint);
    }

}