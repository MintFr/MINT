package com.example.mint.controller;

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
import org.osmdroid.views.overlay.PaintList;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.File;
import java.io.IOException;
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
        //Temporary Itinerary
        String resourceName = "src/main/res/raw/itinerary_test.json";
        File file = new File(resourceName);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonObj = new JSONObject(content);

            itinerary = new Itinerary(jsonObj);



        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Log.d("Json",itinerary.toString());


    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Save State Main OnStart");

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
        }
        Log.d(LOG_TAG, "onStart: finished ");

    }
    public void zoomIn(View view){
        map.getController().zoomIn();
    }
    public void zoomOut(View view){
        map.getController().zoomOut();
    }

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
                if (map.getOverlays().size() != 0) {
                    map.getOverlays().clear();
                    map.postInvalidate();
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
            if (map.getOverlays().size() != 0) {
                map.getOverlays().clear();
                map.postInvalidate();
            } else {
                // if there is no marker already we center the map on the new point
                mapController.setCenter(pointTempo);
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

}