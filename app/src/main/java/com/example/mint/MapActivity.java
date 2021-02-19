package com.example.mint;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

import androidx.appcompat.app.AppCompatActivity;

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
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;
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


        try{
            /*
            URL url = new URL("http://ser-info-03.ec-nantes.fr:8080/itineraryBIS/map");
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();//open
            InputStream in = new BufferedInputStream(urlCon.getInputStream());
            String response = readStream(in);
             */
            String response = "[{\"fullName\":\"Jonelière - Hotel de Région\",\"type\":\"Bus\",\"coordinates\":[[[-1.5407641,47.251644],[-1.5453092,47.2523],[-1.5453092,47.2523],[-1.5499276,47.252144],[-1.5499276,47.252144],[-1.5529888,47.252403],[-1.5529888,47.252403],[-1.5582004,47.253128],[-1.5582004,47.253128],[-1.5612289,47.252934],[-1.5612289,47.252934],[-1.5674773,47.251553],[-1.5674773,47.251553],[-1.5684878,47.249084],[-1.5684878,47.249084],[-1.5672812,47.247055],[-1.5672812,47.247055],[-1.5611475,47.24456],[-1.5611475,47.24456],[-1.5573748,47.243607],[-1.5573748,47.243607],[-1.557317,47.240997],[-1.557317,47.240997],[-1.5610446,47.23952],[-1.5610446,47.23952],[-1.5634209,47.235836],[-1.5634209,47.235836],[-1.5660075,47.233227],[-1.5660075,47.233227],[-1.5682966,47.23198],[-1.5682966,47.23198],[-1.5695375,47.229057],[-1.5695375,47.229057],[-1.5699732,47.22778],[-1.5699732,47.22778],[-1.5715309,47.223763],[-1.5715309,47.223763],[-1.5729296,47.221195],[-1.5729296,47.221195],[-1.5712224,47.21954],[-1.5712224,47.21954],[-1.5688429,47.21773],[-1.5688429,47.21773],[-1.5669311,47.216896],[-1.5669311,47.216896],[-1.564044,47.21537],[-1.564044,47.21537],[-1.5591782,47.215717],[-1.5591782,47.215717],[-1.5556451,47.214394],[-1.5556451,47.214394],[-1.5556016,47.210163],[-1.5556016,47.210163],[-1.554741,47.205597],[-1.554741,47.205597],[-1.551954,47.20362],[-1.551954,47.20362],[-1.5537878,47.201576],[-1.5537878,47.201576],[-1.5455362,47.200775],[-1.5455362,47.200775],[-1.5436108,47.20156],[-1.5436108,47.20156],[-1.5414934,47.203342],[-1.5414934,47.203342],[-1.5396347,47.203224],[-1.5396347,47.203224],[-1.5372032,47.202496],[-1.5372032,47.202496],[-1.5341172,47.203682],[-1.5341172,47.203682],[-1.5318415,47.20511],[-1.5318415,47.20511],[-1.5327755,47.207058],[-1.5327755,47.207058],[-1.5298145,47.20815],[-1.5298145,47.20815],[-1.5275255,47.209396],[-1.5275255,47.209396],[-1.5260082,47.210346]],[[-1.5261401,47.210342],[-1.5283037,47.20919],[-1.5283037,47.20919],[-1.5299463,47.208145],[-1.5299463,47.208145],[-1.5330392,47.20705],[-1.5330392,47.20705],[-1.5316902,47.20484],[-1.5316902,47.20484],[-1.5347633,47.20348],[-1.5347633,47.20348],[-1.5370779,47.20259],[-1.5370779,47.20259],[-1.5399114,47.203396],[-1.5399114,47.203396],[-1.5418824,47.20324],[-1.5418824,47.20324],[-1.5436239,47.20174],[-1.5436239,47.20174],[-1.5459512,47.20103],[-1.5459512,47.20103],[-1.5537944,47.201668],[-1.5537944,47.201668],[-1.5519867,47.20407],[-1.5519867,47.20407],[-1.5546092,47.205605],[-1.5546092,47.205605],[-1.555727,47.210068],[-1.555727,47.210068],[-1.5556713,47.214752],[-1.5556713,47.214752],[-1.5603584,47.215588],[-1.5603584,47.215588],[-1.5645977,47.215714],[-1.5645977,47.215714],[-1.5669311,47.216896],[-1.5669311,47.216896],[-1.5687242,47.217915],[-1.5687242,47.217915],[-1.5701077,47.22057],[-1.5701077,47.22057],[-1.5712671,47.223774],[-1.5712671,47.223774],[-1.5701911,47.227142],[-1.5701911,47.227142],[-1.5693132,47.229603],[-1.5693132,47.229603],[-1.5686398,47.231247],[-1.5686398,47.231247],[-1.5662582,47.23304],[-1.5662582,47.23304],[-1.5633218,47.23629],[-1.5633218,47.23629],[-1.5596328,47.24011],[-1.5596328,47.24011],[-1.5563008,47.241573],[-1.5563008,47.241573],[-1.5572494,47.243702],[-1.5572494,47.243702],[-1.5619718,47.244984],[-1.5619718,47.244984],[-1.567723,47.24767],[-1.567723,47.24767],[-1.5688173,47.249977],[-1.5688173,47.249977],[-1.5660324,47.25169],[-1.5660324,47.25169],[-1.5609715,47.253033],[-1.5609715,47.253033],[-1.5568612,47.252903],[-1.5568612,47.252903],[-1.5528502,47.252316],[-1.5528502,47.252316],[-1.5483638,47.25247],[-1.5483638,47.25247],[-1.5451707,47.252216],[-1.5451707,47.252216],[-1.5407641,47.251644]]],\"color\":\"009534\",\"shortName\":\"26\"}]";
            lines = readJSON(response);
            System.out.println(lines.length);

        }
        /*
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        */catch (JSONException e) {

            e.printStackTrace();
        }

        /////////////////////////
        ///// MAP CONTROL //////
        ////////////////////////

/*        // get the buttons for map control
        zoomInButton = findViewById(R.id.zoom_in);
        zoomOutButton = findViewById(R.id.zoom_out);
        locateButton = findViewById(R.id.locate);

        // attribute the onClickListener and set Tags
        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);
        locateButton.setOnClickListener(this);

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
        System.out.println("///////////////////////////////////////////////////////////////////");
        System.out.println("SELECTED");


        switch (item) {
            case "Routes":
                System.out.println("Routes");
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


    private String readStream (InputStream is) throws IOException {
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
        System.out.println(json.length());
        TanMap[] lines = new TanMap[json.length()];
        for (int i=0;i<json.length();i++){
            lines[i]= new TanMap(json.getJSONObject(i));
        }
        return lines;
    }

    /**
     * Display the map
     * @param lines
     * @throws IOException
     */
    private void displayTanMap(TanMap[] lines) throws IOException {
        for( TanMap busLine : lines){
            displayBusLine(busLine);
        }
    }

    /**
     * Display a busline
     * @param busline
     * @throws IOException
     */
    private void displayBusLine(TanMap busline) throws IOException {
        int n = busline.getCoordinates().size();
        for (int i = 0; i<n; i++){
            displayRoute(busline.getCoordinates().get(i));
        }

    }

    /**
     * Display a single route
     * @param route
     * @throws IOException
     */
    private void displayRoute(final ArrayList<double[]> route) throws IOException {
        // polyline for itinerary
        // first we create a list of geopoints for the geometry of the polyline
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (int j = 0; j<route.size(); j++){
            geoPoints.add(new GeoPoint(route.get(j)[0],route.get(j)[1]));
        }
        System.out.println(geoPoints.size());
        System.out.println(geoPoints.get(0));
        // then we attribute it to the new polyline
        final Polyline line = new Polyline(map);
        line.setPoints(geoPoints);
        System.out.println("///////////////////////////////////////////////////////////////////");
        System.out.println(line.getDistance());
        System.out.println(line.getActualPoints().size());

        map.getOverlays().add(line);
        map.invalidate(); // this is to refresh the display
        System.out.println("///////////////////////////////////////////////////////////////////");



        /*
        // then we handle the color :
        setColorForPolyline(route);

        line.getOutlinePaintLists().add(plBorder);
        line.getOutlinePaintLists().add(plInside);

        // this is to be able to identify the line later on
        line.setId(String.valueOf(i));


        // SETUP INFO WINDOW
        final View infoWindowView = inflater.inflate(R.layout.itinerary_infowindow, null);

        // find all the corresponding views in the infowindow
        TextView timeInfo = infoWindowView.findViewById(R.id.time_info);
        ImageView transportationInfo = infoWindowView.findViewById(R.id.transportation);
        final ImageView pollutionInfo = infoWindowView.findViewById(R.id.pollution_icon);

        // set values for time, transportation and pollution

        //time
        int t = Double.valueOf(route.getDuration()).intValue();
        String s = convertIntToHour(t);
        System.out.println(s);
        //String s = Integer.toString(t);
        timeInfo.setText(s);

        //transportation
        switch (route.getType()) {
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
        if ((route.getPollution() >= 0) && (route.getPollution() < 33)) {
            pollutionInfo.setImageResource(R.drawable.ic_pollution_good);
        } else if ((route.getPollution() >= 33) && (route.getPollution() < 66)) {
            pollutionInfo.setImageResource(R.drawable.ic_pollution_medium);
        } else if ((route.getPollution() >= 66) && (route.getPollution() <= 100)) {
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
        RelativeLayout layout = infoWindowView.findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDetails(route);
            }
        });




        // add line to map
        map.getOverlays().add(line);
        map.invalidate(); // this is to refresh the display
  */
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
        public void setColorForPolyline(float[][] route){
            System.out.println("pollution" + route.getPollution());

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