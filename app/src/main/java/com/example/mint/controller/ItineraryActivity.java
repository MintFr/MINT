package com.example.mint.controller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.example.mint.R;
import com.example.mint.model.Itinerary;
import com.example.mint.model.PreferencesAddresses;
import com.example.mint.model.PreferencesPollution;
import com.example.mint.model.PreferencesSensibility;
import com.example.mint.model.PreferencesSize;
import com.example.mint.model.Step;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.PaintList;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for the itinerary page, on which the user can see the various itineraries calculated for them
 */
public class ItineraryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = ItineraryActivity.class.getSimpleName();

    /**
     * GEOPOINT POSITIONS
     */
    GeoPoint startPosition;
    GeoPoint endPosition;
    List<GeoPoint> markers;
    GeoPoint stepPosition;
    /**
     * ITINERARY
     */
    ArrayList<Itinerary> itineraries;
    /**
     * INFLATER : brings up necessary views
     */
    LayoutInflater inflater;
    /**
     * MAP
     */
    private MapView map = null;
    private IMapController mapController = null;

    /**
     * LAYOUT AND MENU
     */
    private BottomSheetBehavior sheetBehaviorRecap;
    private RelativeLayout recapLayout;
    private FloatingActionButton recapButton;
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
    private int threshold;

    //All attributes needed for displaying itineraries
    private ArrayList<Polyline> lines;
    private View infoWindowView;
    private TextView timeInfo;
    private ImageView transportationInfo;
    private ImageView pollutionInfo;

    private int itineraryToDisplay;

    /**
     * On create of this activity, display itineraries and the recap of all the itineraries
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  charger le bouton puis activer ou pas
        String sizePolice = PreferencesSize.getSize("police", ItineraryActivity.this);
        if (sizePolice.equals("big")) {
            setContentView(R.layout.activity_itinerary_big);
        } else {
            setContentView(R.layout.activity_itinerary);
        }

        // inflater used to display different views
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // get the sensibility from preferences
        /**
         * POLLUTION DATA
         */
        String sensibility = PreferencesSensibility.getSensibility("Sensibility", this);
        //set the threshold for the display color of the itineraries
        // TODO change values for threshold once you have the data from captation
        switch (sensibility) {
            case "Très élevée":
                threshold = 5;
                break;
            case "Élevée":
                threshold = 15;
                break;
            case "Modérée":
                threshold = 35;
                break;
            case "Faible":
                threshold = 55;
                break;
            case "Pas de sensibilité":
                threshold = 80;
                break;
            case "--":
                // TODO : Change it by default, just for good colors
                threshold = 33;
                break;
        }

        /////////////////////////
        //// BOTTOM SHEETS /////
        ////////////////////////

        final CoordinatorLayout coordinator = findViewById(R.id.coordinator);

        // i don't really know what this does but it fixed some bugs so don't touch
        ViewCompat.postOnAnimation(coordinator, () -> ViewCompat.postInvalidateOnAnimation(coordinator));

        // LAYOUTS

        // get the bottom sheets and their behaviors

        recapLayout = findViewById(R.id.itinerary_recap_layout);
        sheetBehaviorRecap = BottomSheetBehavior.from(recapLayout);

        recapButton = findViewById(R.id.recap_fab);
        recapButton.setOnClickListener(this);
        recapButton.setTag(10);

        /////////////////////////
        ///// MAP CONTROL //////
        ////////////////////////

        // get the buttons for map control
        /**
         * BUTTONS
         */
        FloatingActionButton zoomInButton = findViewById(R.id.zoom_in);
        FloatingActionButton zoomOutButton = findViewById(R.id.zoom_out);
        FloatingActionButton locateButton = findViewById(R.id.locate);

        // attribute the onClickListener and set Tags
        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);
        locateButton.setOnClickListener(this);

        zoomInButton.setTag(13);
        zoomOutButton.setTag(12);
        locateButton.setTag(11);

        ///////////////////////
        ///// MAP DISPLAY /////
        ///////////////////////

        //Map display
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);
        GeoPoint defaultPoint = new GeoPoint(47.21, -1.55);
        mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(defaultPoint);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        ////////////////////////
        ///// ITINERARIES //////
        ////////////////////////

        //Get itineraries from the Async task
        Intent intent = getIntent();
        itineraries = new ArrayList<>();
        itineraries = (ArrayList<Itinerary>) intent.getSerializableExtra("itineraries");


        // for the white border
        /**
         * STYLE
         */
        Paint paintBorder = new Paint();
        paintBorder.setStrokeWidth(30);
        paintBorder.setStyle(Paint.Style.FILL_AND_STROKE);
        paintBorder.setColor(Color.WHITE);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setStrokeJoin(Paint.Join.ROUND);
        paintBorder.setAntiAlias(true);

        // white border when line is selected
        Paint paintBorderSelected = new Paint(paintBorder);
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
        paintInsideM.setColor(getResources().getColor(R.color.colorYellow)); // <-- THIS IS WHERE YOU SET THE COLOR FOR A MEDIUM ITINERARY

        // inside the white border when the line is selected MEDIUM
        paintInsideSelectedM = new Paint(paintInsideM);
        paintInsideSelectedM.setStrokeWidth(20);

        // inside the white border BAD
        paintInsideB = new Paint(paintInsideG);
        paintInsideB.setColor(getResources().getColor(R.color.colorOrange)); // <-- THIS IS WHERE YOU SET THE COLOR FOR A BAD ITINERARY

        // inside the white border when the line is selected BAD
        paintInsideSelectedB = new Paint(paintInsideB);
        paintInsideSelectedB.setStrokeWidth(20);

        // paintLists are useful for having several colors inside the polyline,
        // we will store the paints we created in them, that way we can change their appearance according to the pollution
        plInside = new MonochromaticPaintList(paintInsideB);
        plInsideSelected = new MonochromaticPaintList(paintInsideSelectedB);
        plBorder = new MonochromaticPaintList(paintBorder);
        plBorderSelected = new MonochromaticPaintList(paintBorderSelected);

        // behaviour when you click outside the line on the map
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // behaviour when you click anywhere on the map : we want to reset everything back to normal
                InfoWindow.closeAllInfoWindowsOn(map);
                for (int i = 1; i < itineraries.size(); i++) { // we go through all the polylines that are displayed
                    resetPolylineAppearance(lines.get(i));
                }
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        // add an overlay that will detect the taps on the map
        MapEventsOverlay mOverlay = new MapEventsOverlay(mReceive);
        map.getOverlays().add(0, mOverlay);


        // start and end markers (we only need to draw them once)
        Marker startMarker = new Marker(map);
        startPosition = new GeoPoint(itineraries.get(0).getPoints().get(0)[0], itineraries.get(0).getPoints().get(0)[1]);
        startMarker.setPosition(startPosition);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setFlat(true);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));

        int indexEnd = itineraries.get(0).getPointSize() - 1;
        Marker endMarker = new Marker(map);
        endPosition = new GeoPoint(itineraries.get(0).getPoints().get(indexEnd)[0], itineraries.get(0).getPoints().get(indexEnd)[1]);
        endMarker.setPosition(endPosition);
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        endMarker.setIcon(getResources().getDrawable(R.drawable.ic_end_marker));

        // center the map on the itineraries
        markers = new ArrayList<>();
        markers.add(startPosition);
        markers.add(endPosition);
        final BoundingBox bounds = BoundingBox.fromGeoPointsSafe(markers);
        map.post(new Runnable() {
            @Override
            public void run() {
                map.zoomToBoundingBox(bounds, true, 120);
            }
        });

        // step marker if there is one (we only need to draw it once)
        // It's if there's a step specified in the itinerary call not the steps that constitutes the itinerary
        boolean hasStep = itineraries.get(0).isHasStep();
        if (hasStep) {
            stepPosition = new GeoPoint(itineraries.get(0).getStep().getLatitude(), itineraries.get(0).getStep().getLongitude());
            Marker stepMarker = new Marker(map);
            stepMarker.setPosition(stepPosition);
            stepMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            stepMarker.setFlat(true);
            stepMarker.setIcon(getResources().getDrawable(R.drawable.ic_step_marker));

        }

        //Bottom Menu
        Menu menu;
        if (sizePolice.equals("big")) {
            NavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setNavigationItemSelectedListener(new MenuSwitcherActivity(this));
            bottomNav.setItemIconTintList(null);
            menu = bottomNav.getMenu();
        } else {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setOnNavigationItemSelectedListener(new MenuSwitcherActivity(this));
            bottomNav.setItemIconTintList(null);
            menu = bottomNav.getMenu();
        }

        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        //Creations of the itineraries
        lines = new ArrayList<>();

        // display recap
        displayRecap(itineraries);

        // display each itinerary we just got from the Async task
        for (int j = 0; j < itineraries.size(); j++) {
            Polyline line = new Polyline(map);
            lines.add(j, line);
            displayItinerary(itineraries.get(j), j);
            Log.d(LOG_TAG, "Id line OnCreate : '" + line.getId() + "'");
        }

        // Draw start and end point above itineraries
        map.getOverlays().add(startMarker);
        map.getOverlays().add(endMarker);

        map.invalidate(); // this refreshes the display
    }

    /**
     * On Start method, which highlights an itinerary when clicked.
     */
    public void onStart() {
        super.onStart();

        for (int i = 0; i < itineraries.size(); i++) {
            // on click behaviour of line (highlight it, show details, show infowindow)
            int finalI = i;
            lines.get(i).setOnClickListener(new Polyline.OnClickListener() {
                @Override
                public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                    ItineraryActivity.this.highlightItinerary(polyline, mapView, eventPos, itineraries.get(finalI), finalI, itineraries.size()); // function that highlights an itinerary
                    return true;
                }
            });
        }
    }

    /**
     * Overrides method (when the activity has detected the user's press of the back key) to return to MainActivity
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * DISPLAY ITINERARY
     * Display one itinerary on the map
     * Is called on each itinerary
     *
     * @param itinerary Itinerary :  Current itinerary to display
     * @param i         int : index of the  current itineray
     */
    private void displayItinerary(final Itinerary itinerary, int i) {
        Log.d(LOG_TAG, "displayItinerary is called");

        infoWindowView = inflater.inflate(R.layout.itinerary_infowindow, null);
        timeInfo = infoWindowView.findViewById(R.id.time_info);
        transportationInfo = infoWindowView.findViewById(R.id.transportation);
        pollutionInfo = infoWindowView.findViewById(R.id.pollution_icon);

        // first we create a list of geopoints for the geometry of the polyline
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int j = 0; j < itinerary.getPointSize(); j++) {
            geoPoints.add(new GeoPoint(itinerary.getPoints().get(j)[0], itinerary.getPoints().get(j)[1]));
        }

        // then we attribute it to the polyline
        lines.get(i).setPoints(geoPoints);

        // then we handle the color :
        setColorForPolyline(itinerary);
        lines.get(i).getOutlinePaintLists().add(plBorder);
        lines.get(i).getOutlinePaintLists().add(plInside);

        // this is to be able to identify the line later on
        lines.get(i).setId(String.valueOf(i));
        Log.d(LOG_TAG, "Id line displayIti : '" + lines.get(i).getId() + "'");

        // set values for time, transportation and pollution
        //time
        int t = Double.valueOf(itinerary.getDuration()).intValue();
        String s = convertIntToHour(t);
        timeInfo.setText(s);

        //transportation
        switch (itinerary.getType()) {
            case "Piéton":
                transportationInfo.setImageResource(R.drawable.ic_walk_activated);
                break;
            case "Voiture":
                transportationInfo.setImageResource(R.drawable.ic_car_activated);
                break;
            case "Transport en commun":
                transportationInfo.setImageResource(R.drawable.ic_tram_activated);
                break;
            case "Vélo":
                transportationInfo.setImageResource(R.drawable.ic_bike_activated);
                break;
        }

        //pollution
        if ((itinerary.getPollution() >= 0) && (itinerary.getPollution() < 33)) {
            pollutionInfo.setImageResource(R.drawable.ic_pollution_good);
        } else if ((itinerary.getPollution() >= 33) && (itinerary.getPollution() < 66)) {
            pollutionInfo.setImageResource(R.drawable.ic_pollution_medium);
        } else if ((itinerary.getPollution() >= 66) && (itinerary.getPollution() <= 1000)) {
            pollutionInfo.setImageResource(R.drawable.ic_pollution_bad);
        }

        // add infoWindow to the polyline
        final InfoWindow infoWindow = new InfoWindow(infoWindowView, map) {
            @Override
            public void onOpen(Object item) {
            }

            @Override
            public void onClose() {
            }
        };
        lines.get(i).setInfoWindow(infoWindow);

        // add line
        map.getOverlays().add(lines.get(i));
    }


    /**
     * OnClick method in the bottom sheet. When clicked, displays the considered itinerary,
     * and changes its color.
     *
     * @param v : Button
     */
    public void displayDetailButton(View v) {

        LinearLayout detail = v.findViewById(R.id.itinerary_example);
        TextView exposition = v.findViewById(R.id.exposition_value);
        TextView time = v.findViewById(R.id.recap_time);
        TextView distance = v.findViewById(R.id.distance);
        ImageButton save = v.findViewById(R.id.save);
        TextView timeStart = v.findViewById(R.id.timeStart);
        TextView timeEnd = v.findViewById(R.id.timeEnd);

        //Highlighting the selected itinerary
        if (!v.isActivated()) {
            //Modification of the colors
            v.setActivated(true);
            save.setActivated(true);
            v.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            exposition.setTextColor(ResourcesCompat.getColor(getResources(), R.color.White_Main, null));
            time.setTextColor(ResourcesCompat.getColor(getResources(), R.color.White_Main, null));
            distance.setTextColor(ResourcesCompat.getColor(getResources(), R.color.White_Main, null));
            timeStart.setTextColor(ResourcesCompat.getColor(getResources(), R.color.White_Main, null));
            timeEnd.setTextColor(ResourcesCompat.getColor(getResources(), R.color.White_Main, null));

            //Changing the Visibility
            detail.setVisibility(View.VISIBLE);//Keys for Visible Invisible & Gone are respectively 0,4 & 8
        } else {
            //Modification of the colors
            v.setActivated(false);
            save.setActivated(false);
            v.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.cardview_light_background, null));
            exposition.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorDarkGrey, null));
            time.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorDarkGrey, null));
            distance.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorDarkGrey, null));
            timeStart.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorDarkGrey, null));
            timeEnd.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorDarkGrey, null));

            //Changing the Visibility
            detail.setVisibility(View.GONE);//Keys for Visible Invisible & Gone are respectively 0,4 & 8
        }
    }

    /**
     * This methods displays one single itinerary in the recap_list_item view. It is used
     * to display itinerary in the popUp window displayItineraryPopUp,
     * but also to display all itineraries in the bottom Sheet.
     *
     * @param listItem  : view to display the itinerary in
     * @param recapList : linear layout containing at the end the list of all itineraries (used for the bottom sheet)
     * @param i         : index of the itinerary to display (used for the popup display)
     * @param itinerary : itinerary to display
     */
    public void displayRecapI(View listItem, LinearLayout recapList, int i, Itinerary itinerary) {
        // get list item view and the views inside it
        ImageView transportationIcon = listItem.findViewById(R.id.transportation_icon);
        TextView time = listItem.findViewById(R.id.recap_time);
        TextView distance = listItem.findViewById(R.id.distance);
        ImageButton save = listItem.findViewById(R.id.save);
        TextView timeStart = listItem.findViewById(R.id.timeStart);
        TextView timeEnd = listItem.findViewById(R.id.timeEnd);
        TextView exposition = listItem.findViewById(R.id.exposition_value);

        // set time
        String timeStr = convertIntToHour((int) itinerary.getDuration());
        time.setText(timeStr);

        // set distance
        String distStr = String.format("%.1f" + " km", itinerary.getDistance() / 1000);
        distance.setText(distStr);

        // set exposition
        exposition.setText(String.format("%s", itinerary.getPollution()));
        if (itinerary.isHourStart()) {
            //set time Start
            timeStart.setText(itinerary.getTimeOption());
            int duration = (int) itinerary.getDuration();
            int minutes = duration / 60;
            int hours = minutes / 60;
            minutes = minutes - hours * 60;

            int hourStart = 10 * Integer.parseInt(String.valueOf(itinerary.getTimeOption().charAt(0)))
                    + Integer.parseInt(String.valueOf(itinerary.getTimeOption().charAt(1)));
            int minutesStart = 10 * Integer.parseInt(String.valueOf(itinerary.getTimeOption().charAt(3)))
                    + Integer.parseInt(String.valueOf(itinerary.getTimeOption().charAt(4)));
            int resHour = hourStart + hours;


            int resMin = minutesStart + minutes;
            if (resMin >= 60) {
                resHour += 1;
                resMin -= 60;
            }
            if (resHour < 10 && resMin < 10) {
                timeEnd.setText((String.format("0%s:0%s", resHour, resMin)));
            } else if (resHour < 10) {
                timeEnd.setText((String.format("0%s:%s", resHour, resMin)));
            } else if (resMin < 10) {
                timeEnd.setText((String.format("%s:0%s", resHour, resMin)));
            } else {
                timeEnd.setText((String.format("%s:%s", resHour, resMin)));
            }
        } else {
            timeEnd.setText(itinerary.getTimeOption());
            int duration = (int) itinerary.getDuration();
            int minutes = duration / 60;
            int hours = minutes / 60;
            minutes = minutes - hours * 60;

            int hourEnd = 10 * Integer.parseInt(String.valueOf(itinerary.getTimeOption().charAt(0)))
                    + Integer.parseInt(String.valueOf(itinerary.getTimeOption().charAt(1)));
            int minutesEnd = 10 * Integer.parseInt(String.valueOf(itinerary.getTimeOption().charAt(3)))
                    + Integer.parseInt(String.valueOf(itinerary.getTimeOption().charAt(4)));
            int resHour = hourEnd - hours;


            int resMin = minutesEnd - minutes;
            if (resMin < 0) {
                resHour -= 1;
                resMin = 60 + (minutesEnd - minutes);
            }
            if (resHour < 10 && resMin < 10) {
                timeStart.setText((String.format("0%s:0%s", resHour, resMin)));
            } else if (resHour < 10) {
                timeStart.setText((String.format("0%s:%s", resHour, resMin)));
            } else if (resMin < 10) {
                timeStart.setText((String.format("%s:0%s", resHour, resMin)));
            } else {
                timeStart.setText((String.format("%s:%s", resHour, resMin)));

            }


        }

        // set transportation
        switch (itinerary.getType()) {
            case "Voiture":
                transportationIcon.setImageResource(R.drawable.ic_car_activated);
                break;
            case "Vélo":
                transportationIcon.setImageResource(R.drawable.ic_bike_activated);
                break;
            case "Piéton":
                transportationIcon.setImageResource(R.drawable.ic_walk_activated);
                break;
            case "Transport en commun":
                transportationIcon.setImageResource(R.drawable.ic_tram_activated);
                break;
        }


        int height = getResources().getDimensionPixelSize(R.dimen.list_recap_height);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        listItem.setLayoutParams(params);

        // add the view to the layout
        // save pollution button
        save.setTag(100 + i); // we add 100 because otherwise we will override the tag for "listItem"
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag() - 100; // this lets us find the corresponding itinerary for which we want to save the pollution data
                // we inform the user that he just saved this pollution data to his profile :
                Toast.makeText(ItineraryActivity.this,
                        "L'exposition associée à ce trajet a bien été ajoutée à votre profil",
                        Toast.LENGTH_SHORT).show();
                // then we save the value of the pollution to Preferences to be able to retrieve it in the profile
                PreferencesPollution.setLastPollution(
                        (int) itinerary.getPollution(), ItineraryActivity.this);
            }
        });

        listItem.setTag(i);
        Button followItineraryButton = listItem.findViewById(R.id.followItineraryButton);
        followItineraryButton.setTag(i);


        View itinerary_detail = listItem.findViewById(R.id.itinerary_example);
        ArrayList<Step> STEPS = itinerary.getDetail();

        //start and end
        TextView viewPoint1 = listItem.findViewById(R.id.start_point);
        TextView viewPoint2 = listItem.findViewById(R.id.end_point);

        // get start and end addresses
        String start = getString(R.string.itinerary_point1) + " : " +
                (PreferencesAddresses.getAddress("startAddress", ItineraryActivity.this));
        String end = getString(R.string.itinerary_point2) + " : " +
                (PreferencesAddresses.getAddress("endAddress", ItineraryActivity.this));

        if (itinerary.getPointSize() > 0) {
            // start and end
            viewPoint1.setText(start);
            viewPoint2.setText(end);

            // time
            time.setText(timeStr);

            //between start and end
            if (itinerary.getPointSize() > 2) {

                // first we want to clear all previous steps that might already be displayed in itinerary detail
                //it's a container for the views for each step that will be created with itinerary_step_layout
                LinearLayout stepsLayout = listItem.findViewById(R.id.steps_linear_layout);
                for (int k = 1; k <= STEPS.size(); k++) {
                    // k is going to be the index at which we add the stepView
                    final View stepView = inflater.inflate(R.layout.itinerary_step_layout, null); // get the view from layout
                    TextView stepTimeMin = stepView.findViewById(R.id.address); // get the different textViews from the base view
                    TextView stepDist = stepView.findViewById(R.id.step_distance);
                    String streetName = STEPS.get(k - 1).getAddress();
                    int dist = STEPS.get(k - 1).getDistance();
                    stepTimeMin.setText(streetName);
                    stepDist.setText(String.format("%d", dist));
                    // add the textView to the linearlayout which contains the steps
                    stepsLayout.addView(stepView, k + 1);
                }
            }
        } else {
            viewPoint1.setText("error");
            viewPoint2.setText("error");
        }
        itinerary_detail.setVisibility(View.GONE);

    }

    /**
     * DISPLAY RECAP
     * Shows every itinerary proposed by the search at the bottom of the page
     *
     * @param list ArrayList<Itinerary> The list of all the itineraries
     */
    private void displayRecap(final ArrayList<Itinerary> list) {

        // ADD DIFFERENT ITINERARIES
        LinearLayout recapList = findViewById(R.id.recap_list);
        recapList.removeAllViews(); // remove the last views that were displayed
        for (int i = 0; i < list.size(); i++) {
            View listItem = inflater.inflate(R.layout.recap_list_item, null);
            displayRecapI(listItem, recapList, i, list.get(i));

            recapList.addView(listItem, i);
        }

        // this attaches the control buttons to the new bottom sheet (in this case recap)
        changeAnchor(recapButton, R.id.itinerary_recap_layout);

        // reassign the original peekheight so we can get the top of the view
        sheetBehaviorRecap.setPeekHeight((int) getResources().getDimension(R.dimen.peek_height));
        // we have to do it this way because of a bug from the google bottom sheet behavior
        recapLayout.post(new Runnable() {
            @Override
            public void run() {
                sheetBehaviorRecap.setState(
                        sheetBehaviorRecap.getState() == BottomSheetBehavior.STATE_COLLAPSED ?
                                BottomSheetBehavior.STATE_EXPANDED :
                                BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    /**
     * This method makes the selected itinerary stand out
     *
     * @param polyline  : the polyline of the itinerary that will be highlighted
     * @param mapView   : the background map
     * @param eventPos  : the geoPoint at which you click on the itinerary
     * @param itinerary : the itinerary that will be highlighted
     * @param ind       : index of the itinerary to highlight
     * @param size      : the size of the itinerary ArrayList
     */
    private void highlightItinerary(Polyline polyline, MapView mapView, GeoPoint eventPos, Itinerary itinerary, int ind, int size) {

        this.itineraryToDisplay = ind;
        // reset all other lines to original appearance
        for (int i = 0; i < size; i++) {
            resetPolylineAppearance(lines.get(i));
            lines.get(i).closeInfoWindow();
        }

        // show infoWindow and details
        polyline.showInfoWindow();
        polyline.setInfoWindowLocation(eventPos);

        // highlight the polyline
        polyline.getOutlinePaintLists().clear(); // reset polyline appearance
        setColorForPolyline(itinerary); //this will set the color for plInsideSelected
        polyline.getOutlinePaintLists().add(plBorderSelected);
        polyline.getOutlinePaintLists().add(plInsideSelected);

        // we remove it from the list of overlays and then add it again on top of all the other lines so it's in front
        mapView.getOverlays().remove(polyline);
        mapView.getOverlays().add(size, polyline);
        map.invalidate();
    }

    /**
     * Convert a time in seconds to hours
     *
     * @param seconds int
     * @return String
     */
    private String convertIntToHour(int seconds) {
        int minutes = seconds / 60;
        int hours = minutes / 60;
        minutes = minutes - hours * 60;
        String res = String.format("%s h %s min", hours, minutes);
        return res;
    }

    /**
     * Reset the appearance of a polyline that was highlighted
     *
     * @param polyline Polyline
     */
    private void resetPolylineAppearance(Polyline polyline) {
        // clear all previous paints
        polyline.getOutlinePaintLists().clear();
        // find the itinerary we are referring to, in order to then find the color it has to be
        int i = Integer.parseInt(polyline.getId()); // this is the index of the itinerary inside itineraries
        Itinerary itinerary = itineraries.get(i);
        setColorForPolyline(itinerary);
        // add the default paint style
        polyline.getOutlinePaintLists().add(plBorder);
        polyline.getOutlinePaintLists().add(plInside);
    }

    // Methods to center map on points

    public void onClickP1(View view) {
        mapController.setCenter(startPosition);
    }

    public void onClickP2(View view) {
        mapController.setCenter(endPosition);
    }

    /**
     * Method to control map
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        int i = (int) v.getTag();
        switch (i) {
            case 10: // recap button
                // reset everything back to normal and display recap
                InfoWindow.closeAllInfoWindowsOn(map);
                for (int j = 1; j < itineraries.size(); j++) { // we go through all the PolyLines that are displayed
                    resetPolylineAppearance(lines.get(j));
                }
                displayRecap(itineraries);
                break;
            case 11: // center on lines
                BoundingBox bounds = BoundingBox.fromGeoPointsSafe(markers);
                map.zoomToBoundingBox(bounds, true, 120);
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
     *
     * @param fab : the bottom-most button
     * @param id  : the id of the view which we want to attach the buttons to
     */
    public void changeAnchor(FloatingActionButton fab, int id) {
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(id);
        fab.setLayoutParams(p);
    }

    /**
     * this method decides which color the itinerary line will be, according to the threshold
     *
     * @param itinerary Itinerary
     */
    public void setColorForPolyline(Itinerary itinerary) {

        if (itinerary.getPollution() <= threshold) {
            plInside = new MonochromaticPaintList(paintInsideG);
            plInsideSelected = new MonochromaticPaintList(paintInsideSelectedG);
        } else if (itinerary.getPollution() <= threshold + 20) {
            plInside = new MonochromaticPaintList(paintInsideM);
            plInsideSelected = new MonochromaticPaintList(paintInsideSelectedM);
        } else if (itinerary.getPollution() > threshold + 20) {
            plInside = new MonochromaticPaintList(paintInsideB);
            plInsideSelected = new MonochromaticPaintList(paintInsideSelectedB);
        }
    }

    /**
     * onClick method who changes the activity to realTimeItinerary activity
     *
     * @param view : Start itinerary button
     */
    public void toRealTimeItinerary(View view) {
        Intent intent = new Intent(this, RealTimeItineraryActivity.class);
        intent.putExtra("itinerary", itineraries.get((int) view.getTag()));
        startActivity(intent);
    }


    /**
     * This methods is called when clicked on the pop-up shown on a higlighted itinerary.
     * It shows a popup with the details of the highlighted itinerary.
     *
     * @param view : Button in itinerary_infowindow layout
     */
    public void displayItineraryPopUp(View view) {
        // Popup handled by AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        // Choose the good itinerary to display (the highlighted one)
        Itinerary itinerary = itineraries.get(this.itineraryToDisplay);
        LinearLayout recapList = findViewById(R.id.recap_list);
        recapList.removeAllViews(); // remove the last views that were displayed

        // get list item view and the views inside it
        View listItem = inflater.inflate(R.layout.recap_list_item_map, null);
        // Preparing the view to be displayed
        displayRecapI(listItem, recapList, this.itineraryToDisplay, itinerary);
        View itinerary_detail = listItem.findViewById(R.id.itinerary_example);
        itinerary_detail.setVisibility(View.VISIBLE);

        dialogBuilder.setView(listItem)
                .setNegativeButton("FERMER", null)
                .create()
                .show();
    }

}