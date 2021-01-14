package com.example.mint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.HEREWeGoTileSource;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Activity for the itinerary page, on which the user can see the various itineraries calculated for them
 */
public class ItineraryActivity extends AppCompatActivity  {

    private MapView map = null;
    private ArrayList<double[]> response = new ArrayList<>();
    private IMapController mapController = null;
    private GeoPoint startPoint;
    private GeoPoint endPoint;

    Paint paintBorder;
    Paint paintInside;
    Paint paintBorderSelected;
    Paint paintInsideSelected;

    LayoutInflater inflater;

    View recapView; // this is so we can toggle its visibility on and off

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        // inflater used to display different views
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //Map display
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

        //Get itineraries from the Async task
        Intent intent = getIntent();
        ArrayList<Itinerary> itineraries = new ArrayList<>();
        itineraries = (ArrayList<Itinerary>) intent.getSerializableExtra("itineraries");

        //Paintlists for the effect on the polyline
        paintBorder = new Paint(); // for the white border
        paintBorder.setStrokeWidth(30);
        paintBorder.setStyle(Paint.Style.FILL_AND_STROKE);
        paintBorder.setColor(Color.WHITE);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setStrokeJoin(Paint.Join.ROUND);
        paintBorder.setShadowLayer(15,0,10,getResources().getColor(R.color.colorTransparentBlack));
        paintBorder.setAntiAlias(true);

        paintBorderSelected = new Paint(paintBorder); // white border when line is selected
        paintBorderSelected.setStrokeWidth(50);

        paintInside = new Paint(); // inside the white border
        paintInside.setStrokeWidth(10);
        paintInside.setStyle(Paint.Style.FILL);
        paintInside.setColor(getResources().getColor(R.color.colorLightGreen));
        paintInside.setStrokeCap(Paint.Cap.ROUND);
        paintInside.setStrokeJoin(Paint.Join.ROUND);
        paintInside.setAntiAlias(true);

        paintInsideSelected = new Paint(paintInside); // inside the white border when the line is selected
        paintInsideSelected.setStrokeWidth(20);
        paintInsideSelected.setColor(getResources().getColor(R.color.colorAccent));

        // display each itinerary we just got from the Async task
        for (int j=0;j<itineraries.size()-1;j++){
            displayItinerary(itineraries.get(j), itineraries,j);
        }

        // display recap
        displayRecap(itineraries);

        // behaviour when you click outside the line
        final ArrayList<Itinerary> finalItineraries = itineraries;
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // behaviour when you click anywhere on the map : we want to reset everything back to normal
                InfoWindow.closeAllInfoWindowsOn(map);
                for (int i = 1; i<finalItineraries.size(); i++){ // we go through all the polylines that are displayed
                    Polyline selectedLine = (Polyline) map.getOverlays().get(i);
                    resetPolylineAppearance(selectedLine);
                }
                // remove details in the bottom window and display recap instead
                displayRecap(finalItineraries);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        // add an overlay that will detect the taps on the map
        MapEventsOverlay mOverlay = new MapEventsOverlay(mReceive);
        map.getOverlays().add(0,mOverlay);

        // start and end markers (we only need to draw them once)
        Marker startMarker = new Marker(map);
        GeoPoint startPosition = new GeoPoint(itineraries.get(0).getPoints().get(0)[0],itineraries.get(0).getPoints().get(0)[1]);
        startMarker.setPosition(startPosition);
        startMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
        startMarker.setFlat(true);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        map.getOverlays().add(startMarker);

        int indexEnd = itineraries.get(0).getPointSize()-1;
        Marker endMarker = new Marker(map);
        GeoPoint endPosition = new GeoPoint(itineraries.get(0).getPoints().get(indexEnd)[0],itineraries.get(0).getPoints().get(indexEnd)[1]);
        endMarker.setPosition(endPosition);
        endMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
        endMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        map.getOverlays().add(endMarker);

        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    //DISPLAY ITINERARY
    private void displayItinerary(final Itinerary itinerary, final ArrayList<Itinerary> list,int i){
        // polyline for itinerary
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int j = 0; j<itinerary.getPointSize();j++){
            geoPoints.add(new GeoPoint(itinerary.getPoints().get(j)[0],itinerary.getPoints().get(j)[1]));
        }

        final Polyline line = new Polyline(map);
        line.setPoints(geoPoints);
        line.getOutlinePaintLists().add(new MonochromaticPaintList(paintBorder));
        line.getOutlinePaintLists().add(new MonochromaticPaintList(paintInside));

        // this is to be able to identify the line later on
        line.setId(String.valueOf(i));

        // SETUP INFO WINDOW
        final View infoWindowView = inflater.inflate(R.layout.itinerary_infowindow,null);
        // find all the corresponding views in the infowindow
        TextView timeInfo = infoWindowView.findViewById(R.id.time_info);
        ImageView transportationInfo = infoWindowView.findViewById(R.id.transportation);
        final ImageView pollutionInfo = infoWindowView.findViewById(R.id.pollution_icon);
        // set values for time, transportation and pollution
        //time
        int t = Double.valueOf(itinerary.getTime()).intValue();
        String s = convertIntToHour(t);
        System.out.println(s);
        //String s = Integer.toString(t);
        timeInfo.setText(s);
        //transportation
        switch (itinerary.getType()){
            case "Piéton" :
                transportationInfo.setImageResource(R.drawable.ic_walk_activated);
                break;
            case "Voiture" :
                transportationInfo.setImageResource(R.drawable.ic_car_activated);
                break;
            case "Transport en commun" :
                transportationInfo.setImageResource(R.drawable.ic_tram_activated);
                break;
            case "Vélo" :
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

        // show details once you click on the infowindow
        RelativeLayout layout = infoWindowView.findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDetails(itinerary);
            }
        });

        // add line to map
        map.getOverlays().add(line);
        System.out.println("index de la ligne : "+map.getOverlayManager().indexOf(line));

        // on click behaviour of line (highlight it, show details, show infowindow)
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                hightlightItinerary(polyline,mapView,eventPos,itinerary,list.size()); // function that highlights an itinerary
                return true;
            }
        });
    }

    //DISPLAY DETAILS UNDER MAP
    private void displayDetails(Itinerary itinerary){
        // hide recap
        recapView.setVisibility(View.GONE);
        // show details layout
        LinearLayout detailLayout = findViewById(R.id.itinerary_detail_layout); // get a reference to the detail layout
        detailLayout.setVisibility(View.VISIBLE); // set visibility to visible in case it was gone
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
            String timeStr = convertIntToHour( (int) itinerary.getTime());
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

    //DISPLAY RECAP
    private void displayRecap(final ArrayList<Itinerary> list){
        // REMOVE DETAILS
        ConstraintLayout activityLayout = findViewById(R.id.activity_itinerary_layout); // get a reference to the activity layout
        LinearLayout detailLayout = findViewById(R.id.itinerary_detail_layout); // get a reference to the detail layout
        detailLayout.setVisibility(View.GONE); // remove the details
        // ADD RECAP LAYOUT
        recapView = inflater.inflate(R.layout.itinerary_recap,null); // get the recap view and inflate it
        recapView.setId(ViewCompat.generateViewId()); // set an id for it so we can use constraintSet
        activityLayout.addView(recapView); // add view to the general layout
        ConstraintSet set = new ConstraintSet();
        set.clone(activityLayout);
        set.connect(recapView.getId(),ConstraintSet.BOTTOM,R.id.bottom_navigation,ConstraintSet.TOP);
        set.applyTo(activityLayout); // add constraints to the recapView
        // ADD DIFFERENT ITINERARIES
        LinearLayout recapList = recapView.findViewById(R.id.recap_list);
        for (int i=0;i<list.size()-1;i++){
            // get list item view and the views inside it
            View listItem = inflater.inflate(R.layout.recap_list_item,null);
            ImageView transportationIcon = listItem.findViewById(R.id.transportation_icon);
            TextView time = listItem.findViewById(R.id.recap_time);
            TextView exposition = listItem.findViewById(R.id.exposition_value);
            // set time
            String timeStr = convertIntToHour((int)list.get(i).getTime());
            time.setText(timeStr);
            // set exposition
            exposition.setText(String.format("%s",list.get(i).getPollution()));
            // set transportation
            switch (list.get(i).getType()){
                case "Voiture" :
                    transportationIcon.setImageResource(R.drawable.ic_car_activated);
                    break;
                case "Vélo" :
                    transportationIcon.setImageResource(R.drawable.ic_bike_activated);
                    break;
                case "Piéton" :
                    transportationIcon.setImageResource(R.drawable.ic_walk_activated);
                    break;
                case "Transport en commun" :
                    transportationIcon.setImageResource(R.drawable.ic_tram_activated);
                    break;
            }
            int height = getResources().getDimensionPixelSize(R.dimen.list_item_height);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    height);
            listItem.setLayoutParams(params);
            recapList.addView(listItem,i); // add the view to the layout
            // highlight itinerary when you click on an itinerary
            listItem.setTag(i);
            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (int) v.getTag();
                    Polyline line = findPolylineFromId(String.valueOf(i));
                    GeoPoint pos = line.getInfoWindowLocation();
                    hightlightItinerary(line,map,pos,list.get(i),list.size());
                }
            });
        }
    }

    private void hightlightItinerary(Polyline polyline, MapView mapView, GeoPoint eventPos,Itinerary itinerary,int size) {
        // show infowindow and details
        polyline.showInfoWindow();
        polyline.setInfoWindowLocation(eventPos);
        displayDetails(itinerary);

        // highlight the polyline
        polyline.getOutlinePaintLists().clear(); // reset polyline appearance
        polyline.getOutlinePaintLists().add(new MonochromaticPaintList(paintBorderSelected));
        polyline.getOutlinePaintLists().add(new MonochromaticPaintList(paintInsideSelected));

        // we remove it from the list of overlays and then add it again on top of all the other lines so it's in front
        mapView.getOverlays().remove(polyline);
        mapView.getOverlays().add(size-1,polyline);

        // reset all other lines to original appearance
        for (int i=1;i<size;i++){ // we know that the polylines have indexes ranging from 1 to list.size()-1 because of the order in which we drew them
            Polyline selectedLine = (Polyline) map.getOverlays().get(i);
            if (selectedLine!=polyline){
                resetPolylineAppearance(selectedLine);
                selectedLine.closeInfoWindow();
            }
        }
    }

    private String convertIntToHour(int seconds) {
        int minutes = seconds / (int) 60;
        int hours = minutes /(int) 60 ;
        String res = String.format("%s h %s min",hours, minutes);
        return res;
    }

    private void resetPolylineAppearance(Polyline polyline){
        polyline.getOutlinePaintLists().clear();
        // add the default paint style
        polyline.getOutlinePaintLists().add(new MonochromaticPaintList(paintBorder));
        polyline.getOutlinePaintLists().add(new MonochromaticPaintList(paintInside));
    }

    private Polyline findPolylineFromId(String id){
        // this function is used to find a polyline from its id which was user-selected (in our case, the id is its rank in the itinirary list)
        // to do this we go through all the polylines until we find the one whose id matches the requested id
        int i =1;
        Polyline line = (Polyline) map.getOverlays().get(i);
        while (!line.getId().equals(id)){
            line = (Polyline) map.getOverlays().get(i);
            i++;
        }
        return line;
    }

    ///////////////////////////////////////////////////////////////////////////
    //Methods to center map on points, useless for the app, but useful to debug
    public void onClickP1(View view) {
        mapController.setCenter(startPoint);
    }

    public void onClickP2(View view) {
        mapController.setCenter(endPoint);
    }
}