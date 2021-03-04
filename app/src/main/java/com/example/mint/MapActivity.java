package com.example.mint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorLong;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static android.graphics.Color.argb;
import static android.graphics.Color.rgb;

/**
 * MapActivity handles the Maps page of the app, letting the user consult various maps of Nantes
 */
public class MapActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    /**
     * MAP
     */
    private MapView map = null;
    private ArrayList<double[]> response = new ArrayList<>();
    private IMapController mapController = null;
    private GeoPoint defaultPoint;
    private TanMap[] lines;

    /**
     * BUTTONS
     */
    private FloatingActionButton zoomInButton;
    private FloatingActionButton zoomOutButton;
    private FloatingActionButton locateButton;
    private MonochromaticPaintList plBorder;
    private MonochromaticPaintList plInside;
    private Paint paintBorder;
    private Paint paintInside;
    private CompassOverlay mCompassOverlay;
    private ScaleBarOverlay mScaleBarOverlay;


    //private static final String TAG = "MapActivity"; //--> for debugging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_map);

        ////////////////////////
        ///// MAP Download /////
        ////////////////////////


        try {
            /*
            URL url = new URL("http://ser-info-03.ec-nantes.fr:8080/itineraryBIS/map");
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();//open
            InputStream in = new BufferedInputStream(urlCon.getInputStream());
            String response = readStream(in);
             */
            InputStream inputStream = this.getResources().openRawResource(R.raw.maptan);
            String jsonString = readStream(inputStream);
            lines = readJSON(jsonString);



        }
        /*
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

*/
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

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
/*
        zoomInButton.setTag(13);
        zoomOutButton.setTag(12);
        locateButton.setTag(11);*/


        /////////////////////////
        ///// MAP DISPLAY //////
        ////////////////////////

        map = findViewById(R.id.map);
        //final MapBoxTileSource tileSource = new MapBoxTileSource();
        //tileSource.retrieveAccessToken(this);
        //tileSource.retrieveMapBoxMapId(this);
        //map.setTileSource(tileSource);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);
        defaultPoint = new GeoPoint(47.21, -1.55);
        mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(defaultPoint);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);


        mCompassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
        this.mCompassOverlay.enableCompass();
        map.getOverlays().add(this.mCompassOverlay);

        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
//play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(this.mScaleBarOverlay);


        /*
        paintBorder = new Paint();
        paintBorder.setStrokeWidth(30);
        paintBorder.setStyle(Paint.Style.FILL_AND_STROKE);
        paintBorder.setColor(Color.WHITE);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setStrokeJoin(Paint.Join.ROUND);
        paintBorder.setShadowLayer(15, 0, 10, getResources().getColor(R.color.colorTransparentBlack));
        paintBorder.setAntiAlias(true);


        // paintlists are useful for having several colors inside the polyline,
        // we will store the paints we created in them, that way we can change their appearance according to the pollution
        plInside = new MonochromaticPaintList(paintBorder);
        plBorder = new MonochromaticPaintList(paintBorder);*/


        /////////////////////////////////////////////////////////
        // SPINNER                                             //
        /////////////////////////////////////////////////////////
        Spinner spinner = (Spinner) findViewById(R.id.map_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.map_array, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //Change map when an item is selected
        spinner.setOnItemSelectedListener(this);
        /////////////////////////////////////////////////////////
        //SPINNER END                                          //
        /////////////////////////////////////////////////////////


        /////////////////////////////////////////////////////////
        // BOTTOM MENU //
        /////////////////////////////////////////////////////////
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        /////////////////////////////////////////////////////////
        // BOTTOM MENU END //
        /////////////////////////////////////////////////////////
    }


    /////////////////////////////////////////////////////////
    // BACK BUTTON //
    /////////////////////////////////////////////////////////


    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    /**
     * Overrides onBackPressed method so we can navigate to the previous activity when the phone's back button is pressed
     */
    @Override
    public void onBackPressed() {

        String targetActivity = "No target activity yet";
        // Get previous intent with information of previous activity
        Intent intent = getIntent();
        targetActivity = intent.getStringExtra("previousActivity");

        // Creates a new intent to go back to that previous activity
        // Tries to get the class from the name that was passed through the previous intent
        Intent newIntent = null;
        try {
            newIntent = new Intent(this, Class.forName(targetActivity));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        intent.putExtra("previousActivity", this.getClass());

        this.startActivity(newIntent);

        //---------TRANSITIONS-----------
        //For Left-To-Right transitions
        if (targetActivity == "ProfileActivity") {

            //override the transition and finish the current activity
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //this.finish();
        }

        //For Right-To-Left transitions
        if (targetActivity.equals("com.example.mint.MainActivity")) {

            //override the transition and finish the current activity
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            this.finish();
        }
    }
    /////////////////////////////////////////////////////////
    // BACK BUTTON END //
    /////////////////////////////////////////////////////////



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        map.getOverlays().clear();



        switch (item) {
            case "Routes":
                System.out.println("Routes");
                mCompassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
                this.mCompassOverlay.enableCompass();
                map.getOverlays().add(this.mCompassOverlay);
                break;
            case "Transports en commun":
                try {
                    displayTanMap(lines);
                    System.out.println("Transports en commun");
                    map.invalidate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
                /*
            case "Pollution en direct":
                break;
            case "Pollens en direct":
                break;

                 */
            default:
                System.out.println("Default");
                break;
        }
        System.out.println("///////////////////////////////////////////////////////////////////");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

    }


    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    private TanMap[] readJSON(String response) throws JSONException {
        JSONArray json = new JSONArray(response);

        System.out.println(json.getJSONObject(0));
        System.out.println("json length : "+json.length());
        TanMap[] lines = new TanMap[json.length()];
        for (int i = 0; i < json.length(); i++) {
            lines[i] = new TanMap(json.getJSONObject(i));
        }
        return lines;
    }

    /**
     * Display the map
     *
     * @param lines
     * @throws IOException
     */
    private void displayTanMap(TanMap[] lines) throws IOException {
        for (TanMap busLine : lines) {
            displayBusLine(busLine);
        }
    }

    /**
     * Display a busline
     *
     * @param busline
     * @throws IOException
     */
    private void displayBusLine(TanMap busline) throws IOException {
        int n = busline.getCoordinates().size();
        int color = busline.getColor();
        for (int i = 0; i < n; i++) {
            displayRoute(busline.getCoordinates().get(i), i, color);
        }

    }

    /**
     * Display a single route
     *
     * @param route
     * @throws IOException
     */
    private void displayRoute(final ArrayList<double[]> route, int i, int color) throws IOException {
        // polyline for itinerary
        // first we create a list of geopoints for the geometry of the polyline
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int j = 0; j < route.size(); j++) {
            geoPoints.add(new GeoPoint(route.get(j)[1], route.get(j)[0]));
        }
        // then we attribute it to the new polyline
        final Polyline line = new Polyline(map);
        line.setPoints(geoPoints);



        // then we handle the color :
        int A =  0xff;
        int R = (color >> 16) & 0xff;
        int G = (color >>  8) & 0xff;
        int B = (color      ) & 0xff;

        line.getOutlinePaint().setColor(argb(A,R,G,B));

        // this is to be able to identify the line later on
        line.setId(String.valueOf(i));


/*
        // SETUP INFO WINDOW
        final View infoWindowView = inflater.inflate(R.layout.itinerary_infowindow, null);

        // find all the corresponding views in the infowindow
        TextView timeInfo = infoWindowView.findViewById(R.id.time_info);
        ImageView transportationInfo = infoWindowView.findViewById(R.id.transportation);
        final ImageView pollutionInfo = infoWindowView.findViewById(R.id.pollution_icon);

        // add infowindow to the polyline
        line.setInfoWindow(infoWindow);

        // show details once you click on the infowindow
        RelativeLayout layout = infoWindowView.findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDetails(route);
            }
        });



*/

        // add line to map
        map.getOverlays().add(line);
        map.invalidate(); // this is to refresh the display

        // on click behaviour of line (highlight it, show details, show infowindow)
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                //highlightItinerary(polyline, mapView, eventPos, route, list.size()); // function that highlights an itinerary
                return true;
            }
        });
    }
        /*
        public void setColorForPolyline(ArrayList<double[]> route){

            if (route.getPollution()<=threshold){
                plInside = new MonochromaticPaintList(paintInsideG);
                plInsideSelected = new MonochromaticPaintList(paintInsideSelectedG);
            }
            else if (route.getPollution()<=threshold+20){
                plInside = new MonochromaticPaintList(paintInsideM);
                plInsideSelected = new MonochromaticPaintList(paintInsideSelectedM);
            }
            else if (route.getPollution()>threshold+20){
                plInside = new MonochromaticPaintList(paintInsideB);
                plInsideSelected = new MonochromaticPaintList(paintInsideSelectedB);
            }
        }

         */







}