package com.example.mint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.HEREWeGoTileSource;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PaintList;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

/**
 * Activity for the itinerary page, on which the user can see the various itineraries calculated for them
 */
public class ItineraryActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * MAP
     */
    private MapView map = null;
    private ArrayList<double[]> response = new ArrayList<>();
    private IMapController mapController = null;
    private GeoPoint startPoint;
    private GeoPoint endPoint;

    /**
     * GEOPOINT POSITIONS
     */
    GeoPoint startPosition;
    GeoPoint endPosition;
    List<GeoPoint> markers;

    /**
     * LAYOUT AND MENU
     */
    private BottomSheetBehavior sheetBehaviorDetail;
    private BottomSheetBehavior sheetBehaviorRecap;
    private LinearLayout detailLayout;
    private RelativeLayout recapLayout;

    /**
     * BUTTONS
     */
    private FloatingActionButton zoomInButton;
    private FloatingActionButton zoomOutButton;
    private FloatingActionButton locateButton;
    private FloatingActionButton recapButton;

    /**
     * STYLE
     */
    private Paint paintBorder;
    private Paint paintInside;
    private Paint paintBorderSelected;

    private Paint paintInsideG; // color when the pollution is good
    private Paint paintInsideM; // color when the pollution is medium
    private Paint paintInsideB; // color when the pollution is bad

    private Paint paintInsideSelectedG; // color when the pollution of the selected itinerary is good
    private Paint paintInsideSelectedM; // color when the pollution of the selected itinerary is medium
    private Paint paintInsideSelectedB; // color when the pollution of the selected itinerary is bad

    private PaintList plInside;
    private PaintList plInsideSelected;
    private PaintList plBorder;
    private PaintList plBorderSelected;



    /**
     * ITINERARY
     */
    ArrayList<Itinerary> itineraries;

    /**
     * POLLUTION DATA
     */
    private String sensibility;
    private int threshold;

    /**
     * INFLATER : brings up necessary views
     */
    LayoutInflater inflater;

    /**
     * On create of this activity, display itineraries and the recap of all the itineraries
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        // inflater used to display different views
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // get the sensibility from preferences
        sensibility = Preferences.getSensibility("Sensibility",this);
        //set the threshold for the display color of the itineraries
        // TODO change values for threshold once you have the data from captation
        switch (sensibility){
            case "Très élevée" :
                threshold = 5;
                break;
            case "Élevée" :
                threshold = 15;
                break;
            case "Modérée" :
                threshold = 35;
                break;
            case "Faible" :
                threshold = 55;
                break;
            case "Pas de sensibilité" :
            case "--" :
                threshold = 75;
                break;
        }

        /////////////////////////
        //// BOTTOM SHEETS /////
        ////////////////////////

        final CoordinatorLayout coordinator = findViewById(R.id.coordinator);
        // i dont really know what this does but it fixed some bugs so dont touch
        ViewCompat.postOnAnimation(coordinator, new Runnable() {
            @Override
            public void run() {
                ViewCompat.postInvalidateOnAnimation(coordinator);
            }
        });

        // LAYOUTS

        // get the bottom sheets and their behaviors
        detailLayout = findViewById(R.id.itinerary_detail_layout);
        recapLayout = findViewById(R.id.itinerary_recap_layout);
        sheetBehaviorDetail = BottomSheetBehavior.from(detailLayout);
        sheetBehaviorRecap = BottomSheetBehavior.from(recapLayout);

        recapButton = findViewById(R.id.recap_fab);
        recapButton.setOnClickListener(this);
        recapButton.setTag(10);

        /////////////////////////
        ///// MAP CONTROL //////
        ////////////////////////

        // get the buttons for map control
        zoomInButton = findViewById(R.id.zoom_in);
        zoomOutButton = findViewById(R.id.zoom_out);
        locateButton = findViewById(R.id.locate);

        // attribute the onClickListener and set Tags
        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);
        locateButton.setOnClickListener(this);

        zoomInButton.setTag(13);
        zoomOutButton.setTag(12);
        locateButton.setTag(11);

        /////////////////////////
        ///// MAP DISPLAY //////
        ////////////////////////

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

        /////////////////////////
        ///// ITINERARIES //////
        ////////////////////////

        //Get itineraries from the Async task
        Intent intent = getIntent();
        itineraries = new ArrayList<>();
        itineraries = (ArrayList<Itinerary>) intent.getSerializableExtra("itineraries");

        // Paintlists for the effects on the polylines //

        // for the white border
        paintBorder = new Paint();
        paintBorder.setStrokeWidth(30);
        paintBorder.setStyle(Paint.Style.FILL_AND_STROKE);
        paintBorder.setColor(Color.WHITE);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setStrokeJoin(Paint.Join.ROUND);
        paintBorder.setShadowLayer(15,0,10,getResources().getColor(R.color.colorTransparentBlack));
        paintBorder.setAntiAlias(true);

        // white border when line is selected
        paintBorderSelected = new Paint(paintBorder);
        paintBorderSelected.setStrokeWidth(50);

        // inside the white border GOOD
        paintInsideG = new Paint();
        paintInsideG.setStrokeWidth(10);
        paintInsideG.setStyle(Paint.Style.FILL);
        paintInsideG.setColor(getResources().getColor(R.color.colorAccent)); // <-- THIS IS WHERE YOU SET THE COLOR FOR A PREFERED ITINERARY
        paintInsideG.setStrokeCap(Paint.Cap.ROUND);
        paintInsideG.setStrokeJoin(Paint.Join.ROUND);
        paintInsideG.setAntiAlias(true);

        // inside the white border when the line is selected GOOD
        paintInsideSelectedG = new Paint(paintInsideG);
        paintInsideSelectedG.setStrokeWidth(20);

        // inside the white border MEDIUM
        paintInsideM = new Paint(paintInsideG);
        paintInsideM.setColor(getResources().getColor(R.color.colorLightGreen)); // <-- THIS IS WHERE YOU SET THE COLOR FOR A MEDIUM ITINERARY

        // inside the white border when the line is selected MEDIUM
        paintInsideSelectedM = new Paint(paintInsideM);
        paintInsideSelectedM.setStrokeWidth(20);

        // inside the white border BAD
        paintInsideB = new Paint(paintInsideG);
        paintInsideB.setColor(getResources().getColor(R.color.colorLightGrey)); // <-- THIS IS WHERE YOU SET THE COLOR FOR A BAD ITINERARY

        // inside the white border when the line is selected BAD
        paintInsideSelectedB = new Paint(paintInsideB);
        paintInsideSelectedB.setStrokeWidth(20);

        // paintlists are useful for having several colors inside the polyline,
        // we will store the paints we created in them, that way we can change their appearance according to the pollution
        plInside = new MonochromaticPaintList(paintInsideB);
        plInsideSelected = new MonochromaticPaintList(paintInsideSelectedB);
        plBorder = new MonochromaticPaintList(paintBorder);
        plBorderSelected = new MonochromaticPaintList(paintBorderSelected);

        // display each itinerary we just got from the Async task
        for (int j=0;j<itineraries.size();j++){
            displayItinerary(itineraries.get(j), itineraries,j);
        }

        // display recap
        displayRecap(itineraries);

        // behaviour when you click outside the line on the map
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // behaviour when you click anywhere on the map : we want to reset everything back to normal
                InfoWindow.closeAllInfoWindowsOn(map);
                for (int i = 1; i<itineraries.size(); i++){ // we go through all the polylines that are displayed
                    Polyline selectedLine = (Polyline) map.getOverlays().get(i);
                    resetPolylineAppearance(selectedLine);
                }
                // if a line is selected, display recap, else, do nothing
//                boolean lineSelected = false;
//                int i=1;
//                while (!lineSelected&&i<finalItineraries.size()){
//                    Polyline l = (Polyline) map.getOverlays().get(i);
//                    if (l.getOutlinePaintLists().contains(paintBorderSelected)){
//                        lineSelected=true;
//                        displayRecap(finalItineraries);
//                    }
//                    i++;
//                }
                displayRecap(itineraries);
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
        startPosition = new GeoPoint(itineraries.get(0).getPoints().get(0)[0],itineraries.get(0).getPoints().get(0)[1]);
        startMarker.setPosition(startPosition);
        startMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER);
        startMarker.setFlat(true);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        map.getOverlays().add(startMarker);

        int indexEnd = itineraries.get(0).getPointSize()-1;
        Marker endMarker = new Marker(map);
        endPosition = new GeoPoint(itineraries.get(0).getPoints().get(indexEnd)[0],itineraries.get(0).getPoints().get(indexEnd)[1]);
        endMarker.setPosition(endPosition);
        endMarker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        endMarker.setIcon(getResources().getDrawable(R.drawable.ic_end_marker));
        map.getOverlays().add(endMarker);

        // center the map on the itineraries
        markers = new ArrayList<>();
        markers.add(startPosition);
        markers.add(endPosition);
        final BoundingBox bounds = BoundingBox.fromGeoPointsSafe(markers);
        map.post(new Runnable() {
            @Override
            public void run() {
                map.zoomToBoundingBox(bounds,true,120);
            }
        });

        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }
    /**
     * Overrides method (when the activity has detected the user's press of the back key) to return to MainActivity
     */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * DISPLAY ITINERARY
     * @param itinerary
     * @param list
     * @param i
     */


    //DISPLAY ITINERARY
    private void displayItinerary(final Itinerary itinerary, final ArrayList<Itinerary> list,int i){
        // polyline for itinerary
        // first we create a list of geopoints for the geometry of the polyline
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int j = 0; j<itinerary.getPointSize();j++){
            geoPoints.add(new GeoPoint(itinerary.getPoints().get(j)[0],itinerary.getPoints().get(j)[1]));
        }

        // then we attribute it to the new polyline
        final Polyline line = new Polyline(map);
        line.setPoints(geoPoints);

        // then we handle the color :
        setColorForPolyline(itinerary);

        line.getOutlinePaintLists().add(plBorder);
        line.getOutlinePaintLists().add(plInside);

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
        int t = Double.valueOf(itinerary.getDuration()).intValue();
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
        if((itinerary.getPollution()>=0)&&(itinerary.getPollution()<33)){
            pollutionInfo.setImageResource(R.drawable.ic_pollution_good);
        }
        else if((itinerary.getPollution()>=33)&&(itinerary.getPollution()<66)){
            pollutionInfo.setImageResource(R.drawable.ic_pollution_medium);
        }
        else if((itinerary.getPollution()>=66)&&(itinerary.getPollution()<=100)){
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
        map.invalidate(); // this is to refresh the display

        // on click behaviour of line (highlight it, show details, show infowindow)
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                highlightItinerary(polyline,mapView,eventPos,itinerary,list.size()); // function that highlights an itinerary
                return true;
            }
        });
    }

    /**
     * DISPLAY DETAILS UNDER MAP
     */
    private void displayDetails(Itinerary itinerary){
//        // hide recap
//        recapView.setVisibility(View.GONE);
//        // show details layout
//        LinearLayout detailLayout = findViewById(R.id.itinerary_detail_layout); // get a reference to the detail layout
//        detailLayout.setVisibility(View.VISIBLE); // set visibility to visible in case it was gone
        ArrayList<Step> STEPS = itinerary.getDetail();
        //System.out.println(STEPS);
        //start and end
        TextView viewPoint1 = findViewById(R.id.start_point);
        TextView viewPoint2 = findViewById(R.id.end_point);

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
            String timeStr = convertIntToHour( (int) itinerary.getDuration());
            time.setText(timeStr);

            //pollution
            String str = "3";
            str = str.replaceAll("3", "³"); // set the 3 to superscript
            String polStr = itinerary.getPollution()+"µg/m"+str;
            pollution.setText(polStr);

            //between start and end
            if (itinerary.getPointSize()>2){

                // first we want to clear all previous steps that might already be displayed
                LinearLayout stepsLayout = findViewById(R.id.steps_linear_layout);

                // this is the number of steps from the previously displayed itinerary
                int index = stepsLayout.indexOfChild(viewPoint2);

                if (index>2) { // <=> if there is already something displayed in the stepsLayout
                    stepsLayout.removeViews(2, index - 2);
                }
                System.out.println(STEPS.size());
                for (int k=1;k<=STEPS.size();k++){
                    // k is going to be the index at which we add the stepView
                    final View stepView = inflater.inflate(R.layout.itinerary_step_layout,null); // get the view from layout
                    TextView stepTimeMin = stepView.findViewById(R.id.address); // get the different textViews from the base view
                    //TextView stepTimeSec = stepView.findViewById(R.id.step_time_sec);
                    //TextView street = stepView.findViewById(R.id.street);
                    TextView stepDist = stepView.findViewById(R.id.step_distance);
                    String streetName = STEPS.get(k-1).getAddress();
                    int dist = STEPS.get(k-1).getDistance();
                    //int timeMin = (itinerary.getStepTime().get(k-1) % 3600)/60; // amount of minutes it takes to travel this step
                    //int timeSec = (itinerary.getStepTime().get(k-1) % 60 ); // remaining seconds
                    stepTimeMin.setText(streetName);
                    //stepTimeSec.setText(String.format("%02d",timeSec));
                    stepDist.setText(String.format("%d",dist));
                    //stepDist.setText(String.format("%d",itinerary.getStepDistance().get(k-1)));
                    // add the textView to the linearlayout which contains the steps
                    stepsLayout.addView(stepView,k+1);
                }
            }
        } else {
            viewPoint1.setText("error");
            viewPoint2.setText("error");
        }
        // so that the sheet can be hidden
        sheetBehaviorRecap.setPeekHeight(0);
        sheetBehaviorRecap.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // this attaches the map control buttons to the new bottom sheet (in this case details)
        changeAnchor(recapButton,R.id.itinerary_detail_layout);
        sheetBehaviorDetail.setPeekHeight((int)getResources().getDimension(R.dimen.peek_height));
        sheetBehaviorDetail.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
    }

    /**
     * DISPLAY RECAP
     * @param list
     */
    private void displayRecap(final ArrayList<Itinerary> list){

        // ADD DIFFERENT ITINERARIES
        LinearLayout recapList = findViewById(R.id.recap_list);
        recapList.removeAllViews(); // remove the last views that were displayed
        for (int i=0;i<list.size();i++){

            // get list item view and the views inside it
            View listItem = inflater.inflate(R.layout.recap_list_item,null);
            ImageView transportationIcon = listItem.findViewById(R.id.transportation_icon);
            TextView time = listItem.findViewById(R.id.recap_time);
            TextView exposition = listItem.findViewById(R.id.exposition_value);
            TextView distance = listItem.findViewById(R.id.distance);
            ImageButton save = listItem.findViewById(R.id.save);
            TextView timeStart = listItem.findViewById(R.id.timeStart);
            TextView timeEnd = listItem.findViewById(R.id.timeEnd);


            // set time
            String timeStr = convertIntToHour((int)list.get(i).getDuration());
            time.setText(timeStr);

            // set distance
            String distStr = String.format("%.1f"+" km",list.get(i).getDistance() / 1000);
            distance.setText(distStr);

            // set exposition
            exposition.setText(String.format("%s",list.get(i).getPollution()));
            if (list.get(i).isHourStart()){
                //set time Start
                timeStart.setText(list.get(i).getTimeOption());
                int duration = (int) list.get(i).getDuration();
                int minutes = duration / (int) 60;
                int hours = minutes /(int) 60 ;
                System.out.println("duration" + duration);
                System.out.println("hours" + hours);
                minutes = minutes - hours*60;
                System.out.println("minutes" +minutes);

                int hourStart = 10*Integer.parseInt(String.valueOf(list.get(i).getTimeOption().charAt(0)))+Integer.parseInt(String.valueOf(list.get(i).getTimeOption().charAt(1)));
                int minutesStart = 10*Integer.parseInt(String.valueOf(list.get(i).getTimeOption().charAt(3)))+Integer.parseInt(String.valueOf(list.get(i).getTimeOption().charAt(4)));
                int resHour = hourStart + hours;
                System.out.println("HourStart" +list.get(i).getTimeOption());

                System.out.println("HourStart" + 10*list.get(i).getTimeOption().charAt(0)+list.get(i).getTimeOption().charAt(1));

                int resMin = minutesStart+minutes;
                if (resMin>=60){
                    resHour+=1;
                    resMin-=60;
                }
                timeEnd.setText((String.format("%s:%s",resHour,resMin)));
            }
            else{
                timeEnd.setText(list.get(i).getTimeOption());
                int duration = (int) list.get(i).getDuration();
                int minutes = duration / (int) 60;
                int hours = minutes /(int) 60 ;
                System.out.println("duration" + duration);
                System.out.println("hours" + hours);
                minutes = minutes - hours*60;
                System.out.println("minutes" +minutes);

                int hourEnd = 10*Integer.parseInt(String.valueOf(list.get(i).getTimeOption().charAt(0)))+Integer.parseInt(String.valueOf(list.get(i).getTimeOption().charAt(1)));
                int minutesEnd = 10*Integer.parseInt(String.valueOf(list.get(i).getTimeOption().charAt(3)))+Integer.parseInt(String.valueOf(list.get(i).getTimeOption().charAt(4)));
                int resHour = hourEnd - hours;
                System.out.println("HourStart" +list.get(i).getTimeOption());

                System.out.println("HourStart" + 10*list.get(i).getTimeOption().charAt(0)+list.get(i).getTimeOption().charAt(1));

                int resMin = minutesEnd-minutes;
                if (resMin<0){
                    resHour-=1;
                    resMin=60-(minutesEnd-minutes);
                }
                timeStart.setText((String.format("%s:%s",resHour,resMin)));


            }
            //list.get(i).getTime

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

            // set the height and width of the list item
            int height = getResources().getDimensionPixelSize(R.dimen.list_recap_height);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    height);
            listItem.setLayoutParams(params);

            // add the view to the layout
            recapList.addView(listItem,i);

            // save pollution button
            save.setTag((int)(100+i)); // we add 100 because otherwise we will override the tag for "listItem"
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (int) v.getTag() - 100; // this lets us find the corresponding itinerary for which we want to save the pollution data
                    // we inform the user that he just saved this pollution data to his profile :
                    Toast.makeText(ItineraryActivity.this, "L'exposition associée à ce trajet a bien été ajoutée à votre profil", Toast.LENGTH_SHORT).show();
                    // then we save the value of the pollution to Preferences to be able to retrieve it in the profile
                    Preferences.setLastPollution((int)list.get(i).getPollution(),ItineraryActivity.this);
                }
            });

            // highlight itinerary when you click on an itinerary
            // this will used to find the corresponding itinerary
            listItem.setTag(i);
            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (int) v.getTag();
                    Polyline line = findPolylineFromId(String.valueOf(i));
                    GeoPoint pos = line.getInfoWindowLocation();
                    highlightItinerary(line,map,pos,list.get(i),list.size());
                }
            });
        }

        // so that the sheet can be hidden
        sheetBehaviorDetail.setPeekHeight(0);
        sheetBehaviorDetail.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // this attaches the control buttons to the new bottom sheet (in this case recap)
        changeAnchor(recapButton,R.id.itinerary_recap_layout);

        // reassign the original peekheight so we can get the top of the view
        sheetBehaviorRecap.setPeekHeight((int)getResources().getDimension(R.dimen.peek_height));
        // we have to do it this way because of a bug from the google bottom sheet behavior
        recapLayout.post(new Runnable() {
            @Override
            public void run() {
                sheetBehaviorRecap.setState(sheetBehaviorRecap.getState()==BottomSheetBehavior.STATE_COLLAPSED? BottomSheetBehavior.STATE_EXPANDED: BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    /**
     * This method makes the selected itinerary stand out
     * @param polyline : the polyline of the itinerary that will be highlighted
     * @param mapView : the background map
     * @param eventPos : the geoPoint at which you click on the itinerary
     * @param itinerary : the itinerary that will be highlighted
     * @param size : the size of the itinerary ArrayList
     */
    private void highlightItinerary(Polyline polyline, MapView mapView, GeoPoint eventPos,Itinerary itinerary,int size) {
        // show infowindow and details
        polyline.showInfoWindow();
        polyline.setInfoWindowLocation(eventPos);
        displayDetails(itinerary);

        // highlight the polyline
        polyline.getOutlinePaintLists().clear(); // reset polyline appearance
        setColorForPolyline(itinerary); // this will set the color for plInsideSelected
        polyline.getOutlinePaintLists().add(plBorderSelected);
        polyline.getOutlinePaintLists().add(plInsideSelected);

        // we remove it from the list of overlays and then add it again on top of all the other lines so it's in front
        mapView.getOverlays().remove(polyline);
        mapView.getOverlays().add(size-1,polyline);

        // reset all other lines to original appearance
        // we know that the polylines have indexes ranging from 1 to list.size()-1 because of the order in which we drew them
        for (int i=1;i<size;i++){
            Polyline selectedLine = (Polyline) map.getOverlays().get(i);
            if (selectedLine!=polyline){
                resetPolylineAppearance(selectedLine);
                selectedLine.closeInfoWindow();
            }
        }
    }

    /**
     * Convert a time in seconds to hours
     * @param seconds
     * @return String
     */
    private String convertIntToHour(int seconds) {
        int minutes = seconds / (int) 60;
        int hours = minutes /(int) 60 ;
        minutes = minutes - hours*60;
        String res = String.format("%s h %s min",hours, minutes);
        return res;
    }

    /**
     * Reset the appearance of a polyline that was highlighted
     * @param polyline
     */
    private void resetPolylineAppearance(Polyline polyline){
        // clear all previous paints
        polyline.getOutlinePaintLists().clear();
        // find the itinerary we are referring to, in order to then find the color it has to be
        int i = Integer.valueOf(polyline.getId()); // this is the index of the itinerary inside itineraries
        Itinerary itinerary = itineraries.get(i);
        setColorForPolyline(itinerary);
        // add the default paint style
        polyline.getOutlinePaintLists().add(plBorder);
        polyline.getOutlinePaintLists().add(plInside);
    }

    /**
     * this function is used to find a polyline from its id which was user-selected (in our case, the id is its rank in the itinerary list)
     * @param id
     * @return
     */
    private Polyline findPolylineFromId(String id){
        // to do this we go through all the polylines until we find the one whose id matches the requested id
        int i =1;
        Polyline line = (Polyline) map.getOverlays().get(i);
        while (!line.getId().equals(id)){
            line = (Polyline) map.getOverlays().get(i);
            i++;
        }
        return line;
    }

    //////////////////////////////////
    // Methods to center map on points

    public void onClickP1(View view) {
        System.out.println(startPosition);
        mapController.setCenter(startPosition);
    }

    public void onClickP2(View view) {
        mapController.setCenter(endPosition);
    }

/*    public ArrayList<Step> detailItinerary(Itinerary itinerary){
        List<Step> steps = new ArrayList<>();
        //System.out.println(itinerary.getPointSize());
        //System.out.println(itinerary.getStepDistance().size());

        for (int j = 0; j<itinerary.getStepDistance().size();j++){
            System.out.println(j);

            Address address = new Address();
            try {
                Geocoder geocoder = new Geocoder(ItineraryActivity.this, Locale.getDefault());
                List<android.location.Address> addresses = geocoder.getFromLocation(itinerary.getPoints().get(j+1)[0], itinerary.getPoints().get(j)[1],1);
                System.out.println(addresses);
                String a = addresses.get(0).getAddressLine(0);
                if (addresses.get(0).getThoroughfare() != null){
                    a = addresses.get(0).getThoroughfare();
                }
                else if(addresses.get(0).getFeatureName() != null){
                    a = addresses.get(0).getFeatureName();
                }
                System.out.println("a" + a);
                address.setCoordinates(itinerary.getPoints().get(j)[0], itinerary.getPoints().get(j)[1]);
                address.setLocationName(a);
                System.out.println(itinerary.getStepDistance().size());
                Step tempStep = new Step(address.getLocationName(), itinerary.getStepDistance().get(j));
                steps.add(tempStep);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<Step> newSteps = new ArrayList<>();
        String address = steps.get(0).getAddress();
        System.out.println(address);
        int distance = steps.get(0).getDistance();
        for (Step step:steps){
            System.out.println(step.getAddress());
            if (step.getAddress().equals(address)){
                distance += step.getDistance();
            }
            else{
                newSteps.add(new Steps(address, distance));
                address = step.getAddress();
                distance = step.getDistance();
            }
        }
        //System.out.println(newSteps);
        return newSteps;
    }*/

    ////////////////////////////////

    /**
     * Method to control map
     * @param v
     */
    @Override
    public void onClick(View v) {
        int i = (int) v.getTag();
        switch (i) {
            case 10: // recap button
                // reset everything back to normal and display recap
                InfoWindow.closeAllInfoWindowsOn(map);
                for (int j = 1; j<itineraries.size(); j++){ // we go through all the polylines that are displayed
                    Polyline selectedLine = (Polyline) map.getOverlays().get(j);
                    resetPolylineAppearance(selectedLine);
                }
                displayRecap(itineraries);
                break;
            case 11: // center on lines
                BoundingBox bounds = BoundingBox.fromGeoPointsSafe(markers);
                map.zoomToBoundingBox(bounds,true,120);
                break;
            case 12: // zoom out
                map.getController().zoomOut();
                break;
            case 13: // zoom in
                map.getController().zoomIn();
                break;
        }
    }

    /**
     * This method changes the anchor for the map control buttons when the bottom menu changes
     * @param fab : the bottom-most button
     * @param id : the id of the view which we want to attach the buttons to
     */
    public void changeAnchor(FloatingActionButton fab, int id){
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(id);
        fab.setLayoutParams(p);
    }

    /**
     * this method decides which color the itinerary line will be, according to the threshold
     * @param itinerary
     */
    public void setColorForPolyline(Itinerary itinerary){
        if (itinerary.getPollution()<=threshold){
            plInside = new MonochromaticPaintList(paintInsideG);
            plInsideSelected = new MonochromaticPaintList(paintInsideSelectedG);
        }
        else if (itinerary.getPollution()<=threshold+20){
            plInside = new MonochromaticPaintList(paintInsideM);
            plInsideSelected = new MonochromaticPaintList(paintInsideSelectedM);
        }
        else if (itinerary.getPollution()>threshold+20){
            plInside = new MonochromaticPaintList(paintInsideB);
            plInsideSelected = new MonochromaticPaintList(paintInsideSelectedB);
        }
    }
}