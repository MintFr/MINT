package com.example.mint.controller;

import static java.lang.String.valueOf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mint.R;
import com.example.mint.model.Itinerary;
import com.example.mint.model.Step;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.PaintList;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RealTimeItineraryActivity extends AppCompatActivity implements LocationListener {
    private static final String LOG_TAG = RealTimeItineraryActivity.class.getSimpleName();
    /**
     * GEOLOC
     */
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private final int POSITION_PERMISSION_CODE = 1;
    /**
     * GEOPOINT POSITIONS
     */
    GeoPoint startPosition;
    GeoPoint endPosition;
    List<GeoPoint> markers;
    GeoPoint stepPosition;
    int nbActualStep;
    boolean GpsStatus = false; //true if the user's location is activated on the phone
    LocationManager locationManager;
    Location locationUser;
    /**
     * INFLATER : brings up necessary views
     */
    LayoutInflater inflater;
    /**
     * Temporary point for location changes
     */
    private GeoPoint pointTempo;
    /**
     * MAP
     */
    private MapView map = null;
    private IMapController mapController = null;
    private Marker positionMarker;
    private Itinerary itinerary = new Itinerary();
    private Button nextStepButton;
    private Button previousStepButton;
    /**
     * LAYOUT AND MENU
     */
    private BottomSheetBehavior sheetBehaviorDetail;
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
    private ImageView next_arrow;
    private ImageView current_arrow;
    private ArrayList<Itinerary> itineraries;
    private int nbPointsDone;

    private Polyline line;
    private Polyline lineHighlighted;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_itinerary);

        // Next and previous step button display
        previousStepButton = findViewById(R.id.previous_step_button);
        nextStepButton = findViewById(R.id.next_step_button);

        //Map display
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);
        GeoPoint defaultPoint = new GeoPoint(47.21, -1.55);
        mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(defaultPoint);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new MenuSwitcherActivity(this));
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        // Paintlists for the effects on the polylines //

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
        paintBorder.setShadowLayer(15, 0, 10, getResources().getColor(R.color.colorTransparentBlack));
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

        // paintlists are useful for having several colors inside the polyline,
        // we will store the paints we created in them, that way we can change their appearance according to the pollution
        plInside = new MonochromaticPaintList(paintInsideB);
        plInsideSelected = new MonochromaticPaintList(paintInsideSelectedB);
        plBorder = new MonochromaticPaintList(paintBorder);
        plBorderSelected = new MonochromaticPaintList(paintBorderSelected);


        //Getting the Itinerary from the intend clicked
        itinerary = (Itinerary) getIntent().getSerializableExtra("itinerary");
        //Getting the array of itineraries to handle the intent to main
        itineraries = (ArrayList<Itinerary>) getIntent().getSerializableExtra("itineraries");
        Log.d(LOG_TAG, (" Save State itineraries from OnCreate Realtimeiti is null ? : '" + String.valueOf(itineraries == null) + "'"));

        // Arrow for directions to take
        next_arrow = findViewById(R.id.next_arrow_image);
        current_arrow = findViewById(R.id.current_arrow_image);

        // Initialization for the following real time
        this.nbPointsDone = 0;
        this.nbActualStep = 0;


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Save State RealTImeItinerary OnStart");

        /////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////// Centers the map on lauch on the user's position ///////////
        /////////////////////////////////////////////////////////////////////////////////////////
        // We need this parameter to check if the phone's GPS is activated
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        assert locationManager != null; // check if there the app is allowed to access location
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if the GPS is enabled

        // If the permission to access to the user's location is already given, we use it
        if (ContextCompat.checkSelfPermission(RealTimeItineraryActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // We also need the phone's GPS to be activated. We check this here.
            if (GpsStatus) {

                getLocation();
                if (locationUser != null) {
                    //we put the marker on the map if the point returned is not null
                    Marker positionMarker = new Marker(map);
                    pointTempo = new GeoPoint(locationUser.getLatitude(), locationUser.getLongitude());
                    positionMarker.setPosition(pointTempo);
                    positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    positionMarker.setFlat(true);
                    positionMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
                    map.getOverlays().add(positionMarker);
                    mapController.setCenter(pointTempo);
                } else {
                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            "Nous n'avons pas réussi à vous localiser",
                            Toast.LENGTH_SHORT
                    );
                    toast.show();

                }
            }

            // If the phone's GPS is NOT activated, we ask the user to activate it
            else {
                showAlertMessageNoGps();
            }
            displayItinerary(itinerary);
            //Display the first step
            displayStep(nbActualStep);
            this.nbPointsDone += itinerary.getDetail().get(nbActualStep).getNbEdges();
            updateFirstDirection();
        }
        highlightCurrentStep();
        map.invalidate();
        Log.d(LOG_TAG, "onStart: finished ");
        testDirection();

        Button previousStepButton = findViewById(R.id.previous_step_button);
        previousStepButton.setVisibility(Button.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void zoomIn(View view) {
        map.getController().zoomIn();
    }

    public void zoomOut(View view) {
        map.getController().zoomOut();
    }

    /////////////////////////////////////////////////////////
    // Display Itinerary on the map //
    /////////////////////////////////////////////////////////

    /**
     * DISPLAY ITINERARY
     * Display one itinerary on the map
     * Is called on each itinerary
     *
     * @param itinerary Itinerary :  Current itinerary to display
     */
    private void displayItinerary(final Itinerary itinerary) {
        // polyline for itinerary
        // first we create a list of geopoints for the geometry of the polyline
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int j = 0; j < itinerary.getPointSize(); j++) {
            geoPoints.add(new GeoPoint(itinerary.getPoints().get(j)[0], itinerary.getPoints().get(j)[1]));
        }

        // then we attribute it to the new polyline
        this.line = new Polyline(map);
        line.setPoints(geoPoints);

        // then we handle the color :
        setColorForPolyline(itinerary);

        line.getOutlinePaintLists().add(plBorder);
        line.getOutlinePaintLists().add(plInside);

        // this is to be able to identify the line later on
        line.setId(valueOf(0));

        // add line
        map.getOverlays().add(line);

    }

    /**
     * Removes the previous superposed highlighted polyline and the new polyline that is superposed with the current step
     */
    private void highlightCurrentStep() {

        map.getOverlays().remove(lineHighlighted);

        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int j = nbPointsDone - itinerary.getDetail().get(nbActualStep).getNbEdges(); j <= nbPointsDone; j++) {
            geoPoints.add(new GeoPoint(itinerary.getPoints().get(j)[0], itinerary.getPoints().get(j)[1]));
        }

        lineHighlighted = new Polyline(map);
        lineHighlighted.setPoints(geoPoints);

        setColorForPolyline(itinerary);

        lineHighlighted.getOutlinePaintLists().add(plBorderSelected);
        lineHighlighted.getOutlinePaintLists().add(plInsideSelected);

        map.getOverlays().add(lineHighlighted);

        map.invalidate();


    }

    /**
     * Method that updates the direction to take to the first street.
     */
    private void updateFirstDirection() {
        ArrayList<double[]> pointsIti = itinerary.getPoints();
        Log.d(LOG_TAG, "TAGG : " + nbPointsDone);
        Log.d(LOG_TAG, "TAGG : " + pointsIti.get(0));
        Log.d(LOG_TAG, "TAGG : " + pointsIti.get(0)[1]);

        double v1x = pointsIti.get(this.nbPointsDone)[1] - pointsIti.get(0)[1];
        double v1y = pointsIti.get(this.nbPointsDone)[0] - pointsIti.get(0)[0];
        double v2x = pointsIti.get(nbPointsDone + itinerary.getDetail().get(1).getNbEdges())[1] - pointsIti.get(this.nbPointsDone)[1];
        double v2y = pointsIti.get(nbPointsDone + itinerary.getDetail().get(1).getNbEdges())[0] - pointsIti.get(this.nbPointsDone)[0];

        double theta = v1x * v2y - v2x * v1y;

        System.out.println(theta);
        Log.d(LOG_TAG, "TAGG : " + String.valueOf(v1x) + ", " + String.valueOf(v1y) + ", " + String.valueOf(v2x) + ", " + String.valueOf(v2y));
        Log.d(LOG_TAG, "TAGG : " + theta);

        if (theta > 0) {
            next_arrow.setImageResource(R.drawable.ic_baseline_arrow_back_24);
        } else {
            next_arrow.setImageResource(R.drawable.ic_baseline_arrow_forward_24);
        }

    }

    public void nextStep(View view) {
        nextStep();
    }

    public void nextStep() {
        nbActualStep += 1;
        displayStep(nbActualStep);
    }

    /**
     * Tell the user the direction to take in the next step. Calculate the determinant between
     * 2 vectors.
     */
    public void nextDirection() {
        ArrayList<double[]> pointsIti = itinerary.getPoints();
        double v1x = pointsIti.get(this.nbPointsDone)[1] - pointsIti.get(this.nbPointsDone - itinerary.getDetail().get(nbActualStep - 1).getNbEdges())[1];
        double v1y = pointsIti.get(this.nbPointsDone)[0] - pointsIti.get(this.nbPointsDone - itinerary.getDetail().get(nbActualStep - 1).getNbEdges())[0];

        double v2x = pointsIti.get(nbPointsDone + itinerary.getDetail().get(nbActualStep + 1).getNbEdges())[1] - pointsIti.get(this.nbPointsDone)[1];
        double v2y = pointsIti.get(nbPointsDone + itinerary.getDetail().get(nbActualStep + 1).getNbEdges())[0] - pointsIti.get(this.nbPointsDone)[0];

        double theta = v1x * v2y - v2x * v1y;
        if (theta > 0) {
            next_arrow.setImageResource(R.drawable.ic_baseline_arrow_back_24);
        } else {
            next_arrow.setImageResource(R.drawable.ic_baseline_arrow_forward_24);
        }

    }

    /**
     * Returns the distance between user location and the end of the current step.
     *
     * @param n
     * @return
     */
    public int distanceToStep(int n) {

        //calculate the distance to the current step
        double[] point = itinerary.getPoints().get(n);
        Location targetLocation = new Location("");//provider name is unnecessary
        targetLocation.setLatitude(point[0]);
        targetLocation.setLongitude(point[1]);
        return ((int) targetLocation.distanceTo(locationUser));
    }

    /**
     * Update the distance between the user and the end of the current step in the layout.
     */
    public void updateDist() {
        //calculate the distance to the current step
        int dist = distanceToStep(nbPointsDone);

        //display the distance to the current step
        TextView currentStepDist = findViewById(R.id.step_distance);
        currentStepDist.setText(Integer.toString(dist));
    }

    /**
     * Display the step on top of the layout
     *
     * @param n : number of the step to display
     */
    private void displayStep(int n) {

        if (n == 0) {
            previousStepButton.setVisibility(Button.INVISIBLE);
        } else {
            previousStepButton.setVisibility(Button.VISIBLE);
        }

        if (n == itinerary.getDetail().size() - 1) {
            nextStepButton.setVisibility(Button.INVISIBLE);
        } else {
            nextStepButton.setVisibility(Button.VISIBLE);
        }

        //Saving Steps
        ArrayList<Step> STEPS = itinerary.getDetail();
        int len = STEPS.size();

        //display the name of the current step
        TextView currentStepName = findViewById(R.id.address);
        String address = STEPS.get(n).getAddress();

        if (address.equals("")) {
            currentStepName.setText(
                    (n >= len - 2) ?
                            STEPS.get(len - 1).getAddress()
                            : "Continuez puis tournez sur " + STEPS.get(n + 1).getAddress()
            );
        } else {
            currentStepName.setText(address);
        }

        //display the distance to the current step
        updateDist();

        //Displaying or not the next step
        if (n < len - 1) {
            //display the name of the next step
            TextView nextStepName = findViewById(R.id.address_2);
            String nextAddress = STEPS.get(n + 1).getAddress();
            if (nextAddress.equals("")) {
                nextStepName.setText(
                        (n == len - 2) ?
                                STEPS.get(len - 1).getAddress()
                                : "Tournez sur " + STEPS.get(n + 2).getAddress()
                );
            } else {
                nextStepName.setText(nextAddress);
            }

            //display the distance to the next step
            TextView nextStepDist = findViewById(R.id.step_distance_2);
            nextStepDist.setText(Integer.toString(STEPS.get(n + 1).getDistance()));
        } else {
            //Hide the next step (for there is none)
            View nextStepLayout = findViewById(R.id.itinerary_real_time_step_layout_second);
            nextStepLayout.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * this method decides which color the itinerary line will be, according to the threshold
     *
     * @param itinerary Itinerary
     */
    public void setColorForPolyline(Itinerary itinerary) {
        System.out.println("pollution" + itinerary.getPollution());

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

    /////////////////////////////////////////////////////////
    // Display Itinerary on the map  END//
    /////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////
    // LOCATION //
    /////////////////////////////////////////////////////////

    /**
     * Centers the map on the user's position when the button myPosition is clicked
     * @param view standard onClick param for button
     */
    public void positionCentering(View view) {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        assert locationManager != null; //check if there the app is allowed to access location
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if the GPS is enabled

        // If the permission to access to the user's location is already given, we use it
        if (ContextCompat.checkSelfPermission(RealTimeItineraryActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // We also need the phone's GPS to be activated. We check this here.
            if (GpsStatus) {
                // if there's already a marker on the map it is deleted
                for (int i = 0; i < map.getOverlays().size(); i++) {
                    Overlay overlay = map.getOverlays().get(i);
                    if (overlay instanceof Marker) {
                        map.getOverlays().remove(overlay);
                    }
                }
                getLocation();
                //we put a new marker on the map where the user is
                if (locationUser != null) {
                    Marker positionMarker = new Marker(map);
                    pointTempo = new GeoPoint(locationUser.getLatitude(), locationUser.getLongitude());
                    positionMarker.setPosition(pointTempo);
                    positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    positionMarker.setFlat(true);
                    positionMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
                    map.getOverlays().add(positionMarker);
                    mapController.setCenter(pointTempo);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Nous n'avons pas réussi à vous localiser", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }

            // If the phone's GPS is NOT activated, we ask the user to activate it
            else {
                showAlertMessageNoGps();
            }
        }

        // If we don't have the permission, we ask the permission to use their location
        else {
            requestLocalisationPermission(); //line 447
        }
    }

    /**
     * Return user's position in coordinates
     */
    @SuppressLint("MissingPermission")
    private void getLocation() {
        //Access user's location
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, RealTimeItineraryActivity.this);
        locationUser = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    /**
     * Ask the user to turn on their location
     */
    private void showAlertMessageNoGps() {
        new AlertDialog.Builder(RealTimeItineraryActivity.this)
                .setTitle("Echec de la localisation")
                .setMessage("Votre localisation n'est pas activée. Voulez-vous l'activer ?")
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //access to phone's settings to activate GPS
                        ActivityCompat.requestPermissions(RealTimeItineraryActivity.this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION}, POSITION_PERMISSION_CODE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener() { //refuse to activate GPS
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    /**
     * Print user's position If we need to convert the
     * coordinates in an address, we need to do it here with a "geocoder"
     *
     * @param location
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void onLocationChanged(Location location) {
        // Check if the activity is destroyed, if true then no need to update locations
        if (!isDestroyed()) {
            //getting the new location ( I tried using location as in the argument but it doesn't work and this works
            getLocation();
            pointTempo = new GeoPoint(locationUser.getLatitude(), locationUser.getLongitude());
            //Deleting the previous marker
            for (int i = 0; i < map.getOverlays().size(); i++) {
                Overlay overlay = map.getOverlays().get(i);
                if (overlay instanceof Marker) {
                    map.getOverlays().remove(overlay);
                }

            }

            int dist = distanceToStep(nbPointsDone);

            updateDist();

            // We go to the next step if the distance to it is under 15 meters.
            if (dist < 10) {
                nextStep();
                this.nbPointsDone += itinerary.getDetail().get(nbActualStep).getNbEdges();
                highlightCurrentStep();
                if (nbActualStep < itinerary.getDetail().size() - 1) {
                    nextDirection();
                }
            }

            //printing a new position marker on the map
            if (map != null) {
                Marker positionMarker = new Marker(map);
                positionMarker.setPosition(pointTempo);
                positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                positionMarker.setFlat(true);
                positionMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
                map.getOverlays().add(positionMarker);
            }
        }
    }

    /**
     * Test method in log to verify the good direction arrows. Could be deleted.
     */
    private void testDirection() {
        int points = itinerary.getDetail().get(0).getNbEdges();
        ArrayList<double[]> pointsIti = itinerary.getPoints();
        for (int i = 1; i < itinerary.getDetail().size() - 1; i++) {

            double v1x = pointsIti.get(points)[1] - pointsIti.get(points - itinerary.getDetail().get(i - 1).getNbEdges())[1];
            double v1y = pointsIti.get(points)[0] - pointsIti.get(points - itinerary.getDetail().get(i - 1).getNbEdges())[0];

            double v2x = pointsIti.get(points + itinerary.getDetail().get(i + 1).getNbEdges())[1] - pointsIti.get(points)[1];
            double v2y = pointsIti.get(points + itinerary.getDetail().get(i + 1).getNbEdges())[0] - pointsIti.get(points)[0];

            points += itinerary.getDetail().get(i).getNbEdges();

            double theta3 = v1x * v2y - v2x * v1y;
            if (theta3 > 0) {
                Log.d(LOG_TAG, "TAGG : " + "gauche");
            } else {
                Log.d(LOG_TAG, "TAGG : " + "droite");
            }
            Log.d(LOG_TAG, "TAGG : " + itinerary.getDetail().get(i).getAddress());
        }
    }

    /**
     * Ask the permission to the user to use their location
     */
    private void requestLocalisationPermission() {
        // If the permission WAS DENIED PREVIOUSLY,
        // we open a dialog to ask for the permission to access to the user's location
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this) //create a dialog window to autorise access to location only if the user previously refused to grant location
                    .setTitle("Autorisation nécessaire")
                    .setMessage("Nous avons besoin de votre autorisation pour utiliser votre géolocalisation.")
                    .setPositiveButton("autoriser", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If the user click on this button, we ask her/him the permission to use her/his position
                            ActivityCompat.requestPermissions(RealTimeItineraryActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION}, POSITION_PERMISSION_CODE);
                            dialog.dismiss();
                            Log.d("test geo loc", "onClick: finished");
                        }
                    })
                    .setNegativeButton("annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            // If the permission was NOT denied previously, we ask for the permission to access to the user's position
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, POSITION_PERMISSION_CODE);
        }
    }

    /**
     * Return the answer of the location permission request in a "short toast window" at the bottom
     * of the screen and print the user's position if we have the permission
     *
     * @param requestCode
     * @param grantResults
     * @param permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == POSITION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Autorisation ACCORDÉE", Toast.LENGTH_SHORT).show();
                //if the result is positive we do what we want to do
                // If the permission to access to the user's location is  allowed AND if the GPS' phone is activated,
                // we use this location
                if (ContextCompat.checkSelfPermission(RealTimeItineraryActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && GpsStatus) {

                    if (GpsStatus) {

                        getLocation();
                        if (locationUser != null) {
                            //We put the marker on the map
                            //TODO: refactor this in a function
                            Marker positionMarker = new Marker(map);
                            pointTempo = new GeoPoint(locationUser.getLatitude(), locationUser.getLongitude());
                            positionMarker.setPosition(pointTempo);
                            positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                            positionMarker.setFlat(true);
                            positionMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
                            map.getOverlays().add(positionMarker);
                            mapController.setCenter(pointTempo);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Nous n'avons pas réussi à vous localiser", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                    // If the phone's GPS is NOT activated, we ask the user to activate it
                    else {
                        showAlertMessageNoGps();
                    }
                }
            } else {
                Toast.makeText(this, "Autorisation REFUSÉE", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /////////////////////////////////////////////////////////
    // LOCATION END //
    /////////////////////////////////////////////////////////

    /**
     * Function to read a file.
     *
     * @param is
     * @return
     * @throws IOException
     */
    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    /**
     * On click method to quit the itinerary and go back to Main Activity
     *
     * @param view
     */
    public void toMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("itineraries", itineraries);
        Log.d(LOG_TAG, (" Save State itineraries from main is null ? : '" + String.valueOf(itineraries == null) + "'"));
        startActivity(intent);
        finish();
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Save State RealTimeItinerary OnPause");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "Save State RealTimeItinerary OnRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Save State RealTimeItinerary OnResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "Save State RealTimeItinerary OnStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Save State RealTimeItinerary OnDestroy");
    }

    /**
     * On click method to display next step if needed.
     *
     * @param view
     */
    public void onClickNextStep(View view) {
        Log.d(LOG_TAG, "TAGGG : nbPoints next 1 " + nbPointsDone);
        Log.d(LOG_TAG, "TAGGG : nbAct next 1 " + nbActualStep);
        nextStep(view);
        this.nbPointsDone += itinerary.getDetail().get(nbActualStep).getNbEdges();
        highlightCurrentStep();

        if (nbActualStep < itinerary.getDetail().size() - 1) {
            nextDirection();
        }
        Log.d(LOG_TAG, "TAGGG : nbPoints next 2 " + nbPointsDone);
        Log.d(LOG_TAG, "TAGGG : nbAct next 2 " + nbActualStep);
    }

    /**
     * On click method to display previous step if needed.
     *
     * @param view
     */
    public void onClickPreviousStep(View view) {
        Log.d(LOG_TAG, "TAGGG : nbPoints prev 1 " + nbPointsDone);
        Log.d(LOG_TAG, "TAGGG : nbAct prev 1 " + nbActualStep);
        nbPointsDone -= itinerary.getDetail().get(nbActualStep).getNbEdges();
        nbActualStep -= 1;

        displayStep(nbActualStep);
        highlightCurrentStep();

        updateDist();
        Log.d(LOG_TAG, "TAGGG : nbPoints prev 2 : " + nbPointsDone);
        Log.d(LOG_TAG, "TAGGG : nbAct prev 2 : " + nbActualStep);

        if (nbActualStep > 0) {
            nextDirection();
        }
        //Hide the next step (for there is none)
        View nextStepLayout = findViewById(R.id.itinerary_real_time_step_layout_second);
        nextStepLayout.setVisibility(View.VISIBLE);
    }
}