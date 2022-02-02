package com.example.mint.controller;

import static java.lang.String.valueOf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.example.mint.R;
import com.example.mint.model.Coordinates;
import com.example.mint.model.Itinerary;
import com.example.mint.model.PreferencesAddresses;
import com.example.mint.model.PreferencesPollution;
import com.example.mint.model.PreferencesSensibility;
import com.example.mint.model.Step;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.PaintList;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RealTimeItineraryActivity extends AppCompatActivity implements LocationListener {
    private static final String LOG_TAG = RealTimeItineraryActivity.class.getSimpleName();

    /**
     * GEOPOINT POSITIONS
     */
    GeoPoint startPosition;
    GeoPoint endPosition;
    List<GeoPoint> markers;
    GeoPoint stepPosition;

    int nbActualStep;
    /**
     * Temporary point for location changes
     */
    private GeoPoint pointTempo;
    /**
     * MAP
     */
    private MapView map = null;
    private IMapController mapController = null;
    /**
     * GEOLOC
     */
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private final int POSITION_PERMISSION_CODE = 1;
    boolean GpsStatus = false; //true if the user's location is activated on the phone
    LocationManager locationManager;
    Location locationUser;
    private Marker positionMarker;
    private Itinerary itinerary = new Itinerary();
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
    /**
     * INFLATER : brings up necessary views
     */
    LayoutInflater inflater;

    private ArrayList<Itinerary> itineraries;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_itinerary);

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
        Log.d(LOG_TAG, (" Save State itineraries from OnCreate Realtimeiti is null ? : '" + String.valueOf(itineraries == null)  + "'"));

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
                    //TODO : fix this
                    // if the return is null we show a toast to the user
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
        }
        map.invalidate();
        Log.d(LOG_TAG, "onStart: finished ");

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void zoomIn(View view){
        map.getController().zoomIn();
    }
    public void zoomOut(View view){
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


    //DISPLAY ITINERARY
    private void displayItinerary(final Itinerary itinerary) {
        // polyline for itinerary
        // first we create a list of geopoints for the geometry of the polyline
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int j = 0; j < itinerary.getPointSize(); j++) {
            geoPoints.add(new GeoPoint(itinerary.getPoints().get(j)[0], itinerary.getPoints().get(j)[1]));
        }

        // then we attribute it to the new polyline
        final Polyline line = new Polyline(map);
        line.setPoints(geoPoints);

        // then we handle the color :
        setColorForPolyline(itinerary);

        line.getOutlinePaintLists().add(plBorder);
        line.getOutlinePaintLists().add(plInside);

        // this is to be able to identify the line later on
        line.setId(valueOf(0));

        //Display the first step
        displayStep(1);



        // SETUP INFO WINDOW
//        final View infoWindowView = inflater.inflate(R.layout.itinerary_infowindow, null);


        // find all the corresponding views in the infowindow
      /*  TextView timeInfo = infoWindowView.findViewById(R.id.time_info);
        ImageView transportationInfo = infoWindowView.findViewById(R.id.transportation);
        final ImageView pollutionInfo = infoWindowView.findViewById(R.id.pollution_icon);

        // set values for time, transportation and pollution

        //time
        int t = Double.valueOf(itinerary.getDuration()).intValue();
        String s = convertIntToHour(t);
        System.out.println(s);
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
        } else if ((itinerary.getPollution() >= 66) && (itinerary.getPollution() <= 100)) {
            pollutionInfo.setImageResource(R.drawable.ic_pollution_bad);
        }
        final InfoWindow infoWindow = new InfoWindow(infoWindowView, map) {
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
        RelativeLayout layout = infoWindowView.findViewById(R.id.layout);*/

        // add line
        map.getOverlays().add(line);

    }

    public void nextStep(View view){
        nextStep();
    }

    public void nextStep(){
        nbActualStep += 1;
        displayStep(nbActualStep);
    }

    public int distanceToStep(int n){

        //calculate the distance to the current step
        double[] point = itinerary.getPoints().get(n);
        Location targetLocation = new Location("");//provider name is unnecessary
        targetLocation.setLatitude(point[0]);
        targetLocation.setLongitude(point[1]);
        return((int)targetLocation.distanceTo(locationUser));
    }

    public void updateDist(){
        //calculate the distance to the current step
        int dist = distanceToStep(nbActualStep);

        //display the distance to the current step
        TextView currentStepDist = findViewById(R.id.step_distance);
        currentStepDist.setText(Integer.toString(dist));
    }

    /**
     * Display the step on top of the layout
     *
     * @param n : number of the step to display
     */
    private void displayStep(int n){

        //Saving Steps
        ArrayList<Step> STEPS = itinerary.getDetail();
        int len = STEPS.size();

        //display the name of the current step
        TextView currentStepName = findViewById(R.id.address);
        currentStepName.setText(STEPS.get(n).getAddress());

        //display the distance to the current step
        updateDist();

        //Displaying or not the next step
        if (n<len-1){
            //display the name of the next step
            TextView nextStepName = findViewById(R.id.address_2);
            nextStepName.setText(STEPS.get(n+1).getAddress());

            //display the distance to the next step
            TextView nextStepDist = findViewById(R.id.step_distance_2);
            nextStepDist.setText(Integer.toString(STEPS.get(n+1).getDistance()));
        }
        else{
            //Hide the next step (for there is none)
            View nextStepLayout = findViewById(R.id.itinerary_real_time_step_layout_second);
            nextStepLayout.setVisibility(View.INVISIBLE);
        }
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


    //Centers the map on the user's position when the button myPosition is clicked
    public void positionCentering(View view){
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        assert locationManager != null; //check if there the app is allowed to access location
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if the GPS is enabled

        // If the permission to access to the user's location is already given, we use it
        if (ContextCompat.checkSelfPermission(RealTimeItineraryActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // We also need the phone's GPS to be activated. We check this here.
            if (GpsStatus) {
                // if there's already a marker on the map it is deleted
                for(int i=0;i<map.getOverlays().size();i++){
                    Overlay overlay=map.getOverlays().get(i);
                    if(overlay instanceof Marker){
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
            for(int i=0;i<map.getOverlays().size();i++){
                Overlay overlay=map.getOverlays().get(i);
                if(overlay instanceof Marker){
                    map.getOverlays().remove(overlay);
                }

            }
            System.out.println(map);

            //printing a new position marker on the map
            if (map != null) {
                Marker positionMarker = new Marker(map);
                positionMarker.setPosition(pointTempo);
                positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                positionMarker.setFlat(true);
                positionMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
                map.getOverlays().add(positionMarker);
            }

            int dist = distanceToStep(nbActualStep);

            updateDist();

            if (dist < 5){
                nextStep();
            }
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

    public void toMain(View view){
        Intent intent = new Intent ( this, MainActivity.class);
        intent.putExtra("itineraries",itineraries);
        Log.d(LOG_TAG, (" Save State itineraries from main is null ? : '" + String.valueOf(itineraries == null)  + "'"));
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
}