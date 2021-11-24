package com.example.mint.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.controller.ActivityMenuSwitcher;
import com.example.mint.model.Pollution;
import com.example.mint.R;
import com.example.mint.model.TanMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PaintList;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.argb;
import static android.graphics.Color.green;
import static android.graphics.Color.rgb;

/**
 * MapActivity handles the Maps page of the app, letting the user consult various maps of Nantes
 * MapActivity is a inherited class from AppCompatActivity which is a base class of Andorid Studio
 */
public class MapActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    ////////////////////////
    ////////  MAP  /////////
    ////////////////////////

    private MapView map = null;
    private ArrayList<double[]> response = new ArrayList<>();
    private IMapController mapController = null;
    private GeoPoint defaultPoint;
    private TanMap[] lines;
    private Pollution[] pol_streets;

    ////////////////////////
    /////// BUTTONS  ///////
    ////////////////////////

    private FloatingActionButton zoomInButton;
    private FloatingActionButton zoomOutButton;
    private FloatingActionButton locateButton;
    private MonochromaticPaintList plBorder;
    private MonochromaticPaintList plInside;
    private Paint paintBorder;
    private Paint paintInside;
    private CompassOverlay mCompassOverlay;
    private ScaleBarOverlay mScaleBarOverlay;


    LayoutInflater inflaterMap;

    //private static final String TAG = "MapActivity"; //--> for debugging

    //private static final String TAG = "MapActivity"; //--> for debugging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_map);


        // inflater used to display different views
        inflaterMap = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

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
            InputStream inputStream1 = this.getResources().openRawResource(R.raw.data_top);
            String jsonString = readStream(inputStream);
            String jsonString1 = readStream(inputStream1);
            lines = readJSON(jsonString);
            pol_streets = readJSONPol(jsonString1);



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

        // attribute the onClickListener and set Tags
        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);

        zoomInButton.setTag(13);
        zoomOutButton.setTag(12);


        /////////////////////////
        ///// MAP DISPLAY //////
        ////////////////////////

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);
        defaultPoint = new GeoPoint(47.21, -1.55);
        mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(defaultPoint);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        paintBorder = new Paint();
        paintBorder.setStrokeWidth(30);
        paintBorder.setStyle(Paint.Style.FILL_AND_STROKE);
        paintBorder.setColor(Color.WHITE);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setStrokeJoin(Paint.Join.ROUND);
        paintBorder.setShadowLayer(15, 0, 10, getResources().getColor(R.color.colorTransparentBlack));
        paintBorder.setAntiAlias(true);






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
        if (targetActivity.equals("com.example.mint.controller.MainActivity")) {

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

                final DisplayMetrics dm = this.getResources().getDisplayMetrics();
                mScaleBarOverlay = new ScaleBarOverlay(map);
                mScaleBarOverlay.setCentred(true);
                //play around with these values to get the location on screen in the right place for your application
                mScaleBarOverlay.setScaleBarOffset((int) (dm.widthPixels * 0.76), (int) (dm.heightPixels*0.72));
                map.getOverlays().add(this.mScaleBarOverlay);

                map.invalidate(); // this is to refresh the display

                break;
            case "Transports en commun":
                try {
                    displayTanMap(lines);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case "Pollution":
                displayPollution(pol_streets);
                break;

                /*case "Pollens en direct":
                break;

                 */
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int i = (int) v.getTag();
        switch (i) {
            case 12: // zoom out
                map.getController().zoomOut();
                break;
            case 13: // zoom in
                map.getController().zoomIn();
                break;
            default :
                break;
        }
    }


    /**
     * Function to read a file.
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
     *
     * @param response String in JSON array format.
     * @return return a TanMap array, which contains all bus lines.
     * @throws JSONException
     */
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

    private Pollution[] readJSONPol(String response) throws JSONException {
        JSONArray json = new JSONArray(response);

        //System.out.println(json.getJSONObject(0));
        //System.out.println("json length : "+json.length());
        Pollution[] streets = new Pollution[json.length()];
        for (int i = 0; i < json.length(); i++) {
            streets[i] = new Pollution(json.getJSONObject(i));
            System.out.println(streets[i]);
        }
        return streets;
    }

    /**
     * Display the tan network
     *
     * @param lines
     * @throws IOException
     */
    private void displayTanMap(TanMap[] lines) throws IOException {
        for (TanMap busLine : lines) {
            displayBusLine(busLine);
        }
    }

    private void displayPollution(Pollution[] streets){
        for (Pollution street : streets){
            displayStreet(street);
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
        String name = busline.getShortName();
        String direction = busline.getFullName();
        for (int i = 0; i < n; i++) {
            displayRoute(busline.getCoordinates().get(i), i, color, name, direction);
        }

    }

    private void displayStreet(Pollution street){
        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint(street.getStart().getLatitude(), street.getStart().getLongitude()));
        geoPoints.add(new GeoPoint(street.getEnd().getLatitude(), street.getEnd().getLongitude()));
        // then we attribute it to the new polyline
        final Polyline line = new Polyline(map);
        line.setPoints(geoPoints);
        if (street.getPol()<5.2){
            line.getOutlinePaint().setColor(rgb(0,255,0));
        }
        else if(street.getPol()>5.2 && street.getPol()<5.6){
            line.getOutlinePaint().setColor(rgb(0,0,255));
        }
        else{
            line.getOutlinePaint().setColor(rgb(255,0,0));
        }
        map.getOverlays().add(line);

        map.invalidate();

    }

    /**
     * Display a single route
     *
     * @param route
     * @param name
     * @param direction
     * @throws IOException
     */
    private void displayRoute(final ArrayList<double[]> route, int i, int color, String name, String direction) throws IOException {
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
        int alpha =  0xff;
        int red = (color >> 16) & 0xff;
        int green = (color >>  8) & 0xff;
        int blue = (color      ) & 0xff;
        line.getOutlinePaint().setColor(argb(alpha,red,green,blue));

        // this is to be able to identify the line later on
        line.setId(String.valueOf(i));

        // SETUP INFO WINDOW
        final View infoWindowMapView = inflaterMap.inflate(R.layout.map_infowindow,null);

        RelativeLayout relativeLayout = (RelativeLayout) infoWindowMapView.findViewById(R.id.bus_line_figure_background);
        TextView busLineName =  relativeLayout.findViewById(R.id.bus_line_figure);
        TextView busLineDirection = infoWindowMapView.findViewById(R.id.bus_line_direction);

        relativeLayout.setBackgroundColor(argb(alpha,red,green,blue));
        if (red == 0xff && green == 0xff && blue == 0xff){
            busLineDirection.setTextColor(argb(alpha,0,0,0));
            busLineName.setTextColor(argb(alpha,0,0,0));

        }
        /*
        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        .setColorFilter(colorFilter);

         */

        busLineDirection.setText(direction);
        busLineName.setText(name);



        final InfoWindow infoWindow = new InfoWindow(infoWindowMapView,map) {
            @Override
            public void onOpen(Object item) {
            }

            @Override
            public void onClose() {
            }
        };

        // add infowindow to the polyline
        line.setInfoWindow(infoWindow);

        // add line to map
        map.getOverlays().add(line);

        map.invalidate(); // this is to refresh the display

        // on click behaviour of line (highlight it, show details, show infowindow)
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                highlightItinerary(polyline, mapView, eventPos); // function that highlights an itinerary
                return true;
            }
        });
    }



    private void highlightItinerary(Polyline polyline, MapView mapView, GeoPoint eventPos) {
        // show infowindow and details
        polyline.showInfoWindow();
        polyline.setInfoWindowLocation(eventPos);

        // highlight the polyline
        polyline.getOutlinePaint().clearShadowLayer();
        polyline.getOutlinePaintLists().clear(); // reset polyline appearance

        MonochromaticPaintList plInsideSelected = new MonochromaticPaintList(polyline.getOutlinePaint());
        MonochromaticPaintList plBorderSelected = new MonochromaticPaintList(paintBorder);


        polyline.getOutlinePaintLists().add(plBorderSelected);
        polyline.getOutlinePaintLists().add(plInsideSelected);

        // we remove it from the list of overlays and then add it again on top of all the other lines so it's in front
        mapView.getOverlays().remove(polyline);
        mapView.getOverlays().add(polyline);

        // reset all other lines to original appearance
        // which concerns only the last polyline in the overlay.
        int n = map.getOverlays().size();
        System.out.println(n);
        Polyline alreadySelectedPolyline = (Polyline) map.getOverlays().get(n-2);
        resetPolylineAppearance(alreadySelectedPolyline);
        alreadySelectedPolyline.closeInfoWindow();
        map.invalidate();
    }
    /////////////////////////////////////////////////////////
    // BACK BUTTON END //
    /////////////////////////////////////////////////////////


    /**
     * Reset the appearance of a polyline that was highlighted
     * @param polyline
     */
    private void resetPolylineAppearance (Polyline polyline){
        List<PaintList> pl = polyline.getOutlinePaintLists();
        if (pl.size() != 0) {
            @ColorInt int color = pl.get(1).getPaint().getColor();
            // clear previous paints
            polyline.getOutlinePaintLists().clear();
            polyline.getOutlinePaint().setColor(color);
        }
    }



}