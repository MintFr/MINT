package com.example.mint.controller;

import static android.graphics.Color.parseColor;
import static com.example.mint.model.PreferencesMaxPollen.getMaxPollen;
import static com.example.mint.model.PreferencesMaxPollen.setMaxPollen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.example.mint.R;
import com.example.mint.model.Coordinates;
import com.example.mint.model.CustomListAdapter;
import com.example.mint.model.Itinerary;
import com.example.mint.model.PreferencesAddresses;
import com.example.mint.model.PreferencesPollen;
import com.example.mint.model.PreferencesDate;
import com.example.mint.model.PreferencesSize;
import com.example.mint.model.PreferencesTransport;
import com.example.mint.model.fetchData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity is the activity for the front page of the app, where the user can select start and end points for an itinerary
 * among other things.
 */

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener, LocationListener {

    // For debug log
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final static double LATITUDE_DEFAULT_MAP = 47.21;
    private final static double LONGITUDE_DEFAULT_MAP = -1.55;

    // Nantes latitude and longitude boundaries
    private final static double LATITUDE_MAX = 47.4;
    private final static double LATITUDE_MIN = 47.0;
    private final static double LONGITUDE_MAX = -1.3;
    private final static double LONGITUDE_MIN = -1.8;
    public static int maxPollen;
    // get current date and time
    final Calendar cldr = Calendar.getInstance();
    /**
     * GEOLOC
     */
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private final int POSITION_PERMISSION_CODE = 1;
    boolean GpsStatus = false; //true if the user's location is activated on the phone
    LocationManager locationManager;
    Location locationUser;
    IMapController mapController;
    /**
     * Address suggestions
     */
    ArrayList<String> lastAddressList;
    ArrayList<String> addressList;
    ListView addressListView;
    String start;
    String end;
    String step;
    EditText buttonClicked;
    PopupWindow popUp;
    PopupWindow popUpCalendar;
    TimePicker timePicker;
    int year;
    int month;
    int day;
    int hour;
    int minutes;
    String dateText;
    String timeText;
    boolean starting;
    boolean fast;
    boolean healthy;
    /**
     * This activity handles the input of start and end points and the itinerary options
     *
     * @param savedInstanceState
     */

    SwitchCompat switchCompat;
    Button click;
    /**
     * POPUP POLLEN
     */
    TextView donneesPollen;
    String SAMPLE_URL, dataPollen;
    View v; // view in which to search the text view for the pollen
    TextView alertPollen;
    private View dimPopup;
    private int idButton; // We need this to know where we have to write the location of the user : in the startPoint or the endPoint
    private int positionId = -1; // where user's location is used : 0=startPoint, 1=endPoint, 2=stepPoint, -1 otherwise
    private Context contextPollen;
    /**
     * Map
     */
    private MapView map;
    private Marker positionMarker;
    /**
     * Start Address
     */
    private com.example.mint.model.Address startAddress;
    /**
     * End Address
     */
    private com.example.mint.model.Address endAddress;
    /**
     * Step Address
     */
    private com.example.mint.model.Address stepAddress;
    private EditText startPoint;
    private EditText endPoint;
    private EditText stepPoint;
    private boolean stepVisibility = false; // "true" is stepPoint is visible, "false" if not
    private boolean stepBool = false; // "true" if the user has chosen a stepPoint, "false" if not
    private ImageButton addStepPoint;
    private Button search;
    /**
     * Options
     */
    private Button option;
    private Button dateBtn;
    private ImageButton iconDateBtn;
    private ImageButton iconTimeBtn;
    private Button timeBtn;
    private ImageButton myPosition;
    private Button temporaryItineraryRealTime;
    private ImageButton pollen_button;
    /**
     * Temporary point for location changes
     */
    private GeoPoint tmpPoint;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private ArrayList<Itinerary> itineraries;

    /**
     * Method to read server response, which is as text file, and put it in a String object.
     *
     * @param is InputStream
     * @return String
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
     * Creation of the Popup pollen and fetch the data from the RNSA link
     */

    public void displayPollen() {
        if (CheckInternet()) {
            //creation of the popup
            dialogBuilder = new AlertDialog.Builder(this);
            final View pollenPopupView = getLayoutInflater().inflate(R.layout.popup_pollen, null);
            this.v = pollenPopupView; //initialisation of the view for the textView

            //Fetch data from RNSA url
            this.donneesPollen = v.findViewById(R.id.pollen_alert_text);   //initialisation of the text view for te pollen

            //Fetch RNSA data
            new fetchData(this.donneesPollen).execute();
            dataPollen = String.valueOf(this.donneesPollen.getText());
            dialogBuilder = dialogBuilder.setView(pollenPopupView);
            dialogBuilder.setNegativeButton("FERMER", null);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            //Set SharedPreferences
            setMaxPollen("maxPollen", maxPollen, contextPollen);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Connexion à Internet nécessaire pour obtenir les informations sur le pollen.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Debug
        Log.d(LOG_TAG, "------");
        Log.d(LOG_TAG, "Save State Main OnCreate");
        // Check for localisation permission
        requestLocalisationPermission();
        super.onCreate(savedInstanceState);
        String sizePolice = PreferencesSize.getSize("police", MainActivity.this);
        if (sizePolice.equals("big")) {
            setContentView(R.layout.activity_main_big);
        } else {
            setContentView(R.layout.activity_main);
        }

        //Popup Pollen when app starts
        contextPollen = getApplicationContext();
        SharedPreferences prefs = contextPollen.getSharedPreferences("isStarting", Context.MODE_PRIVATE);
        boolean isStartingPollen = prefs.getBoolean("isStartingPollen", true);
        if (isStartingPollen) {
            displayPollen();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isStartingPollen", false);
        editor.apply();

        // Highlighting selected favorite means of transportation chosen in Profile
        // (next and last step in "showOptions()")
        ArrayList<String> favoriteTrans = PreferencesTransport.getPrefTransportation("Transportation", MainActivity.this);
        int[] fav = {0, 0, 0, 0};
        for (int j = 0; j < 4; j++) {

            if (favoriteTrans.get(j).equals("car_button")) {
                fav[j] = 1;
            }
            if (favoriteTrans.get(j).equals("tram_button")) {
                fav[j] = 2;
            }
            if (favoriteTrans.get(j).equals("bike_button")) {
                fav[j] = 3;
            }
            if (favoriteTrans.get(j).equals("walk_button")) {
                fav[j] = 4;
            }
        }

        PreferencesTransport.setOptionTransportation(fav, this);

        // get current date and time
        this.year = cldr.get(Calendar.YEAR);
        this.month = cldr.get(Calendar.MONTH);
        this.day = cldr.get(Calendar.DAY_OF_MONTH);
        this.hour = cldr.get(Calendar.HOUR_OF_DAY);
        this.minutes = cldr.get(Calendar.MINUTE);

        // First step to set default date and time to current date and time
        // (next and last step in "showOptions()")
        this.dateText = String.format("%02d", day) + "/" + String.format("%02d", (month + 1)) + "/" + year;
        this.timeText = String.format("%02d", hour) + ":" + String.format("%02d", minutes);

        // First step to select if you want start time or end time. Start time is automatically selected
        // (next and last step in "showOptions()")
        this.starting = true;

        // Initialisation from layout activity_main
        this.startPoint = findViewById(R.id.startPoint);
        this.endPoint = findViewById(R.id.endPoint);
        this.stepPoint = findViewById(R.id.stepPoint);
        this.search = findViewById(R.id.search);
        this.addStepPoint = findViewById(R.id.addStepPoint);
        this.myPosition = findViewById(R.id.myPosition);
        this.option = findViewById(R.id.options);
        this.dimPopup = findViewById(R.id.dim_popup);
        this.pollen_button = findViewById(R.id.pollen_button);

        // Initializing Adresses with Adress Class
        this.endAddress = new com.example.mint.model.Address();
        this.startAddress = new com.example.mint.model.Address();
        this.stepAddress = new com.example.mint.model.Address();

        // startPoint/endPoint inversion
        ImageButton inversionButton = findViewById(R.id.inversion);

        // check if the editText is empty and if so disable add button
        TextWatcher textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * Set the search button enable if the length of the text written in the field
             * s (start or end) is not zero.
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search.setEnabled(s.toString().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        // sets the callbacks for when the prompts are selected
        startPoint.setOnFocusChangeListener(this);
        startPoint.addTextChangedListener(textChangedListener);
        endPoint.setOnFocusChangeListener(this);
        endPoint.addTextChangedListener(textChangedListener);
        stepPoint.setOnFocusChangeListener(this);
        stepPoint.addTextChangedListener(textChangedListener);

        // set the tags for when onClick is called
        startPoint.setTag(0);
        endPoint.setTag(1);
        stepPoint.setTag(10);

        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        // Map by default centered on Nantes Island
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(LATITUDE_DEFAULT_MAP, LONGITUDE_DEFAULT_MAP);
        mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(startPoint);

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


        // This code allows to change the color of the button depending on the sensibility of the user


        String sensibility = PreferencesPollen.getPollen("Pollen", MainActivity.this);

        int pollen_count = getMaxPollen("maxPollen", this.contextPollen);
        int colorZero = parseColor("#387D22");
        int colorOne = parseColor("#b0bb3a");
        int colorTwo = parseColor("#F1E952");
        int colorThree = parseColor("#EB3323");
        int threshold1 = 2;
        int threshold2 = 3;
        int threshold3 = 4;
        //We check the sensibility and set the according threshold for the colors
        switch (sensibility) {

            case "Pas sensible":
                threshold1 = 2;
                threshold2 = 3;
                threshold3 = 4;
                break;

            case "Peu sensible":
                threshold1 = 1;
                threshold2 = 2;
                threshold3 = 3;
                break;

            case "Sensible":
                threshold1 = 1;
                threshold2 = 2;
                threshold3 = 2;
                break;
            case "Très sensible":
                threshold1 = 1;
                threshold2 = 1;
                threshold3 = 1;
                break;
        }


        //We now choosing the color depending on the pollen and the threshold defined earlier
        int color = (
                (pollen_count >= threshold3) ?
                        colorThree :
                        (pollen_count == threshold2) ?
                                colorTwo :
                                (pollen_count == threshold1) ?
                                        colorOne :
                                        colorZero
        );


        VectorChildFinder vector = new VectorChildFinder(this, R.drawable.ic_pollen, pollen_button);

        VectorDrawableCompat.VFullPath path1 = vector.findPathByName("changingColor1");
        path1.setFillColor(color);
        VectorDrawableCompat.VFullPath path2 = vector.findPathByName("changingColor2");
        path2.setFillColor(color);
        VectorDrawableCompat.VFullPath path3 = vector.findPathByName("changingColor3");
        path3.setFillColor(color);
        VectorDrawableCompat.VFullPath path4 = vector.findPathByName("changingColor4");
        path4.setFillColor(color);
        VectorDrawableCompat.VFullPath path5 = vector.findPathByName("changingColor5");
        path5.setFillColor(color);

        // apply changes of colors
        pollen_button.invalidate();

    }


    /**
     * OnStart method, applied right after onCreate.
     * This method tries to center the map on the location of the user, if GPS granted.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Save State Main OnStart");


        /////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////// Centers the map on launch on the user's position ///////////
        /////////////////////////////////////////////////////////////////////////////////////////

        // We need this parameter to check if the phone's GPS is activated
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        // assert locationManager != null; // check if there the app is allowed to access location
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if the GPS is enabled

        // If the permission to access to the user's location is already given, we use it
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // We also need the phone's GPS to be activated. We check this here.
            if (GpsStatus) {

                getLocation();
                if (locationUser != null) {
                    //we put the marker on the map if the point returned is not null

                    Marker positionMarker = new Marker(map);
                    tmpPoint = new GeoPoint(locationUser.getLatitude(), locationUser.getLongitude());
                    positionMarker.setPosition(tmpPoint);
                    positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    positionMarker.setFlat(true);
                    positionMarker.setIcon(getResources().getDrawable(R.drawable.ic_position));
                    map.getOverlays().add(positionMarker);
                    mapController.setCenter(tmpPoint);
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

    /////////////////////////////////////////////////////////
    // BACK BUTTON //
    /////////////////////////////////////////////////////////

    /**
     * Overrides onBackPressed method so we can navigate to the previous activity
     * when the phone's back button is pressed
     */
    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    /////////////////////////////////////////////////////////
    // BACK BUTTON END //
    /////////////////////////////////////////////////////////


    /**
     * Method that creates the popup window on selection of adress' editTexts
     *
     * @return: PopupWindow
     */
    private PopupWindow showFavoriteAddresses() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);
        lastAddressList = PreferencesAddresses.getLastAddresses("lastAddress", this);
        addressList = PreferencesAddresses.getPrefAddresses("Address", this);
        lastAddressList.add(0, "Mes dernières adresses :");
        addressList.add(0, "Mes adresses favorites :");

        addressList.addAll(0, lastAddressList);

        addressList.add(0, "Ma position");

        // Adapter adapts the list of addresses for style
        CustomListAdapter adapter = new CustomListAdapter(this, addressList);

        addressListView = new ListView(this);

        // set our adapter and pass our pop up window contents
        addressListView.setAdapter(adapter);
        addressListView.setDivider(null);
        addressListView.setDividerHeight(0);

        // set on item selected
        addressListView.setOnItemClickListener(onItemClickListener());

        // some other visual settings for popup window
        popupWindow.setFocusable(false);
        popupWindow.setWidth((int) getResources().getDimension(R.dimen.start_point_width));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_bg_popup));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED); // To avoid that the popup hide the keyboard
        popupWindow.setOutsideTouchable(false); // To avoid that the popup hide the keyboard


        // set the listview as popup content
        popupWindow.setContentView(addressListView);

        return popupWindow;
    }

    /**
     * This method creates the popup window for the options
     *
     * @return PopupWindow : the options popup window
     */
    private PopupWindow showOptions() {
        // create the views for both popUpWindows
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View optionPopupView = inflater.inflate(R.layout.popup_activity_main_options, null);
        final View calendarPopupView = inflater.inflate(R.layout.popup_options_calendar, null);

        // create popUpWindows
        PopupWindow popupOptions = new PopupWindow(this);
        popupOptions.setFocusable(true);
        popupOptions.setBackgroundDrawable(null);
        popupOptions.setContentView(optionPopupView);

        // remove background dimness on dismiss
        popupOptions.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dimPopup.setVisibility(View.INVISIBLE);
            }
        });

        // get id of transportation buttons
        ImageButton carButton = optionPopupView.findViewById(R.id.car_button);
        ImageButton tramButton = optionPopupView.findViewById(R.id.tram_button);
        ImageButton bikeButton = optionPopupView.findViewById(R.id.bike_button);
        ImageButton walkButton = optionPopupView.findViewById(R.id.walk_button);

        // set tags for the onClick callback
        carButton.setTag(4);
        tramButton.setTag(5);
        bikeButton.setTag(6);
        walkButton.setTag(7);

        // Highlight already selected favorite means of transportation
        int[] favoriteTransportation = PreferencesTransport.getOptionTransportation(MainActivity.this);
        for (int i = 4; i < 8; i++) {
            ImageButton button = optionPopupView.findViewWithTag(i);
            if (favoriteTransportation[i - 4] != 0) {
                button.setActivated(true);
            }
        }

        // create the new onClick callback
        View.OnClickListener onTransportationClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag();

                //check which transportation button was clicked
                ImageButton buttonClicked = optionPopupView.findViewWithTag(i);
                buttonClicked.setActivated(!buttonClicked.isActivated());

                //add or remove transportation accordingly (if it was added or removed)
                if (buttonClicked.isActivated()) {
                    String key = (String) buttonClicked.getContentDescription();
                    int value = Integer.parseInt(key);
                    PreferencesTransport.addOptionTransportation(key, value, MainActivity.this);
                } else if (!buttonClicked.isActivated()) {
                    String key = (String) buttonClicked.getContentDescription();
                    PreferencesTransport.addOptionTransportation(key, 0, MainActivity.this);
                }
            }
        };

        // assign the onClick callback
        carButton.setOnClickListener(onTransportationClick);
        tramButton.setOnClickListener(onTransportationClick);
        bikeButton.setOnClickListener(onTransportationClick);
        walkButton.setOnClickListener(onTransportationClick);

        // set default date and time to current date and time
        dateBtn = optionPopupView.findViewById(R.id.date_calendar);
        dateBtn.setText(dateText);
        iconDateBtn = optionPopupView.findViewById(R.id.option_calendar_icon);

        timeBtn = optionPopupView.findViewById(R.id.time_text);
        iconTimeBtn = optionPopupView.findViewById(R.id.option_clock_icon);
        timeBtn.setText(timeText);

        // set date with both date buttons
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpCalendar = new PopupWindow(MainActivity.this);
                popUpCalendar.setFocusable(true);
                popUpCalendar.setBackgroundDrawable(null);
                popUpCalendar.setContentView(calendarPopupView);
                popUpCalendar.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

                CalendarView calendarView = calendarPopupView.findViewById(R.id.calendar);

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        dateText = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (month + 1)) + "/" + year;
                        dateBtn.setText(dateText);
                    }
                });
            }
        });


        iconDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpCalendar = new PopupWindow(MainActivity.this);
                popUpCalendar.setFocusable(true);
                popUpCalendar.setBackgroundDrawable(null);
                popUpCalendar.setContentView(calendarPopupView);
                popUpCalendar.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

                CalendarView calendarView = calendarPopupView.findViewById(R.id.calendar);

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        dateText = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (month + 1)) + "/" + year;
                        dateBtn.setText(dateText);
                    }
                });
            }
        });

        // set time with both buttons
        final Dialog timeDialog = new Dialog(this);
        timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timeDialog.setContentView(R.layout.popup_options_timepicker);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // time picker dialog
                timePicker = timeDialog.findViewById(R.id.time_picker);
                timePicker.setIs24HourView(true);
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        timeText = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                        timeBtn.setText(timeText);
                    }
                });
                timeDialog.show();
                timeDialog.setTitle("Choisissez une heure de départ");
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        });

        iconTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // time picker dialog
                timePicker = timeDialog.findViewById(R.id.time_picker);
                timePicker.setIs24HourView(true);
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        timeText = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                        timeBtn.setText(timeText);
                    }
                });
                timeDialog.show();
                timeDialog.setTitle("Choisissez une heure de départ");
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        });


        // the buttons for selecting if you want start time or end time. start time is automatically selected
        Button startTime = optionPopupView.findViewById(R.id.start_time);
        final Button endTime = optionPopupView.findViewById(R.id.end_time);
        startTime.setTag(8);
        endTime.setTag(9);

        // the button "start time" is selected by default
        if (starting) {
            startTime.setActivated(true);
        } else {
            endTime.setActivated(true);
        }

        // actions when either start or end time is clicked (unclicks the other one)
        View.OnClickListener onStartEndTimeClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag();
                Button button1 = optionPopupView.findViewWithTag(i);
                Button button2 = optionPopupView.findViewWithTag(i == 8 ? 9 : 8);
                button1.setActivated(true);
                button2.setActivated(false);

                // memorization of the selection of start or end time
                if (i == 8) {
                    starting = true;
                } else {
                    starting = false;
                }
            }
        };

        // the buttons for selecting if you want the healthier path or the fastest one.
        // the buttons for selecting if you want start time or end time.
        // the buttons for selecting if you want the healthier path or the fastest one. plusRapide is automatically selected
        Button fastest = optionPopupView.findViewById(R.id.fastest);
        Button healthier = optionPopupView.findViewById(R.id.healthier);
        fastest.setTag(10);
        healthier.setTag(11);

        // actions when either "plus rapide" or "plus sain" is clicked (unclicks the other one)
        View.OnClickListener onFastOrHealthyClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag();
                Button button1 = optionPopupView.findViewWithTag(i);
                Button button2 = optionPopupView.findViewWithTag(i == 10 ? 11 : 10);
                if (button1.isActivated()) {
                    button1.setActivated(false);
                    button2.setActivated(false);
                    fast = false;
                    healthy = false;
                } else if (!button1.isActivated()) {
                    button1.setActivated(true);
                    button2.setActivated(false);
                    // this is used to give the right values to the booleans "fast & healthy
                    switch (i) {
                        case 10:
                            fast = true;
                            healthy = false;
                            break;
                        case 11:
                            fast = false;
                            healthy = true;
                            break;
                        default:
                            break;
                    }
                }
            }
        };

        // memorization of the selection of fastest or healthier route
        if (fast) {
            fastest.setActivated(true);
            healthier.setActivated(false);
        } else if (healthy) {
            healthier.setActivated(true);
            fastest.setActivated(false);
        } else {
            fastest.setActivated(false);
            healthier.setActivated(false);
        }


        //allocation of the OnClickListeners
        startTime.setOnClickListener(onStartEndTimeClick);
        endTime.setOnClickListener(onStartEndTimeClick);

        fastest.setOnClickListener(onFastOrHealthyClick);
        healthier.setOnClickListener(onFastOrHealthyClick);

        return popupOptions;
    }

    /////////////////////////////////////////////////////////
    // LOCATION //
    /////////////////////////////////////////////////////////

    /**
     * Ask the permission to the user to use their location
     */
    private void requestLocalisationPermission() {
        // If the permission WAS DENIED PREVIOUSLY,
        // we open a dialog to ask for the permission to access to the user's location
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this) //create a dialog window to authorize access to location only if the user previously refused to grant location
                    .setTitle("Autorisation nécessaire")
                    .setMessage("Nous avons besoin de votre autorisation pour utiliser votre géolocalisation.")
                    .setPositiveButton("autoriser", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If the user clicks on this button, we ask the permission to use phone's position
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
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
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && GpsStatus) {

                    if (GpsStatus) {

                        getLocation();
                        if (locationUser != null) {
                            //We put the marker on the map
                            //TODO: refactor this in a function
                            Marker positionMarker = new Marker(map);
                            tmpPoint = new GeoPoint(locationUser.getLatitude(), locationUser.getLongitude());
                            positionMarker.setPosition(tmpPoint);
                            positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                            positionMarker.setFlat(true);
                            positionMarker.setIcon(getResources().getDrawable(R.drawable.ic_position));
                            map.getOverlays().add(positionMarker);
                            mapController.setCenter(tmpPoint);
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

    /**
     * Ask the user to turn on their location
     */
    private void showAlertMessageNoGps() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Echec de la localisation")
                .setMessage("Votre localisation n'est pas activée. Voulez-vous l'activer ?")
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //access to phone's settings to activate GPS
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
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
     * Return user's position in coordinates
     */
    @SuppressLint("MissingPermission")
    private void getLocation() {
        //Access user's location
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, MainActivity.this);
        locationUser = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
            tmpPoint = new GeoPoint(locationUser.getLatitude(), locationUser.getLongitude());
            //Deleting the previous marker
            if (map.getOverlays().size() != 0) {
                map.getOverlays().clear();
                map.postInvalidate();
            } else {
                // if there is no marker already we center the map on the new point
                mapController.setCenter(tmpPoint);
            }

            //printing a new position marker on the map
            if (map != null) {
                Marker positionMarker = new Marker(map);
                positionMarker.setPosition(tmpPoint);
                positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                positionMarker.setFlat(true);
                positionMarker.setIcon(getResources().getDrawable(R.drawable.ic_position));
                map.getOverlays().add(positionMarker);
            }
        }
    }

    /////////////////////////////////////////////////////////
    // LOCATION END //
    /////////////////////////////////////////////////////////


    /**
     * Callback when the user clicks on an item in the listView
     *
     * @return
     */
    private AdapterView.OnItemClickListener onItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id > 0) {
                    buttonClicked.setText(addressList.get((int) id));
                    buttonClicked.setSelection(buttonClicked.length()); // set cursor at end of text
                    popUp.dismiss();
                }
                // if we click on My Position, ask permission for geolocalisation
                //TODO : refactor this in a function
                if (id == 0) {
                    // We need this parameter to check if the phone's GPS is activated
                    locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                    assert locationManager != null; //check if there the app is allowed to access location
                    GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if the GPS is enabled

                    // If the permission to access to the user's location is already given, we use it
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        // We also need the phone's GPS to be activated. We check this here.
                        if (GpsStatus) {
                            popUp.dismiss();
                            getLocation();
                            if (idButton == startPoint.getId()) {
                                positionId = 0;
                                startPoint.setText(R.string.position_text);
                                startPoint.setSelection(buttonClicked.length()); // set cursor at end of text
                            }
                            if (idButton == endPoint.getId()) {
                                positionId = 1;
                                endPoint.setText(R.string.position_text);
                                endPoint.setSelection(buttonClicked.length()); // set cursor at end of text
                            }
                            if (idButton == stepPoint.getId()) {
                                positionId = 2;
                                stepPoint.setText(R.string.position_text);
                                stepPoint.setSelection(buttonClicked.length()); // set cursor at end of text
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
            }
        };
    }

    /**
     * Method called when the user clicks on "search"
     *
     * @param view
     */
    public void onClickSearch(View view) {
        start = startPoint.getText().toString();
        end = endPoint.getText().toString();
        step = stepPoint.getText().toString();

        Log.d(LOG_TAG, "TAGG : positionId = " + this.positionId);
        Log.d(LOG_TAG, "TAGG : Coordinates 1 : " + startAddress.getCoordinates().toString());
        Log.d(LOG_TAG, "TAGG : Coordinates 1 : " + endAddress.getCoordinates().toString());


        Log.d(LOG_TAG + "TAGG", "start : " + start + "; end : " + end);
        //First we check if the "Ma Position" is selected in the search, if So we take the last known position as the start or the end adress
        if (locationUser != null) {
            Coordinates coordinates = new Coordinates(locationUser.getLatitude(), locationUser.getLongitude());
            // We write the location in the good place : startPoint, stepPoint or endPoint
            if (positionId == 0) {
                startAddress.setLocationName(String.valueOf(R.string.position_text));
                startAddress.setCoordinates(coordinates);
            }
            if (positionId == 1) {
                endAddress.setLocationName(String.valueOf(R.string.position_text));
                endAddress.setCoordinates(coordinates);
            }
            if (positionId == 2) {
                stepAddress.setLocationName(String.valueOf(R.string.position_text));
                stepAddress.setCoordinates(coordinates);

            }
        }
        Log.d(LOG_TAG, "TAGG : Coordinates 2 : " + startAddress.getCoordinates().toString());
        Log.d(LOG_TAG, "TAGG : Coordinates 2 : " + endAddress.getCoordinates().toString());

        if (checkGoodAddressesForItinerary()) {
            updateLastAddresses(start, end);


            ////////////////////////////////////////////////////////////////////////////////////
            //Conversion addresses to spatial coordinates
            //For the start point
            Geocoder geocoderStart = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                Log.d(LOG_TAG, "TAGG : " + start + ", " + String.valueOf(R.string.position_text));
                Log.d(LOG_TAG, getString(R.string.position_text).toString());
                if (!start.equals(getString(R.string.position_text))) {  //check if location is not chosen
                    List addressListStart = geocoderStart.getFromLocationName(start, 1);
                    if (addressListStart != null && addressListStart.size() > 0) {
                        Address addressStart = (Address) addressListStart.get(0);
                        Coordinates coordinates = new Coordinates(addressStart.getLatitude(), addressStart.getLongitude());
                        startAddress.setCoordinates(coordinates);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            //For the end point
            Geocoder geocoderEnd = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                if (!end.equals(getString(R.string.position_text))) {       //check if location is not chosen
                    List addressListEnd = geocoderEnd.getFromLocationName(end, 1);
                    if (addressListEnd != null && addressListEnd.size() > 0) {
                        Address addressEnd = (Address) addressListEnd.get(0);
                        endAddress.setLocationName(end);
                        Coordinates coordinates = new Coordinates(addressEnd.getLatitude(), addressEnd.getLongitude());
                        endAddress.setCoordinates(coordinates);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //For the step point
            if (stepBool && stepPoint.isShown()) {
                Geocoder geocoderStep = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    if (!(step.equals(getString(R.string.position_text)))) {       //check if location is not chosen
                        List addressListStep = geocoderStep.getFromLocationName(step, 1);
                        if (addressListStep != null && addressListStep.size() > 0) {
                            Address addressStep = (Address) addressListStep.get(0);
                            stepAddress.setLocationName(step);
                            Coordinates coordinates = new Coordinates(addressStep.getLatitude(), addressStep.getLongitude());
                            stepAddress.setCoordinates(coordinates);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.d(LOG_TAG, "TAGG : Coordinates 3 : " + startAddress.getCoordinates().toString());
            Log.d(LOG_TAG, "TAGG : Coordinates 3 : " + endAddress.getCoordinates().toString());
            // TODO : debug when clicks "ma position" and then switches start and end
            ////////////////////////////////////////////////////////////////////////////////////
            //start itinerary calculation activity if the device has an internet connection
            int error = 0;
            if (!CheckInternet()) { //no internet connection
                error = 2;
            } else if (endAddress.getCoordinates().isZero() || startAddress.getCoordinates().isZero()) { //conversion impossible
                error = 1;
            } else if (startAddress.getCoordinates().getLatitude() < LATITUDE_MIN |
                    startAddress.getCoordinates().getLatitude() > LATITUDE_MAX |
                    startAddress.getCoordinates().getLongitude() < LONGITUDE_MIN |
                    startAddress.getCoordinates().getLongitude() > LONGITUDE_MAX) {
                error = 3;
            } else if (endAddress.getCoordinates().getLatitude() < LATITUDE_MIN |
                    endAddress.getCoordinates().getLatitude() > LATITUDE_MAX |
                    endAddress.getCoordinates().getLongitude() < LONGITUDE_MIN |
                    endAddress.getCoordinates().getLongitude() > LONGITUDE_MAX) {
                error = 4;
            } else if (stepBool && stepPoint.isShown() && stepAddress.getCoordinates().getLatitude() < LATITUDE_MIN |
                    stepAddress.getCoordinates().getLatitude() > LATITUDE_MAX |
                    stepAddress.getCoordinates().getLongitude() < LONGITUDE_MIN |
                    stepAddress.getCoordinates().getLongitude() > LONGITUDE_MAX) {
                error = 5;
            }

            Log.d(LOG_TAG, "TAGG error : " + error);
            switch (error) {
                case 0:
                    Intent intent = new Intent(getApplicationContext(), LoadingPageActivity.class);
                    intent.putExtra("starting", starting);
                    //intent.putExtra("date", dateText);
                    intent.putExtra("time", timeText);
                    intent.putExtra("latitudeStart", startAddress.getCoordinates().getLatitude());
                    intent.putExtra("longitudeStart", startAddress.getCoordinates().getLongitude());
                    intent.putExtra("latitudeEnd", endAddress.getCoordinates().getLatitude());
                    intent.putExtra("longitudeEnd", endAddress.getCoordinates().getLongitude());
                    intent.putExtra("stepInItinerary", stepBool && stepPoint.isShown()); // to know if there is a stepPoint or not

                    // Add stepPoint parameters if needed
                    if (stepBool && stepPoint.isShown()) {
                        intent.putExtra("latitudeStep", stepAddress.getCoordinates().getLatitude());
                        intent.putExtra("longitudeStep", stepAddress.getCoordinates().getLongitude());
                    }
                    startActivity(intent);
                    finish();
                    break;

                case 1:
                    Toast.makeText(this, "Conversion impossible, entrez une nouvelle adresse ou réessayez plus tard", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(getApplicationContext(), LoadingPageActivity.class);
                    intent1.putExtra("starting", starting);
                    intent1.putExtra("time", timeText);
                    intent1.putExtra("latitudeStart", 47.2484039066116);
                    intent1.putExtra("longitudeStart", -1.549636963829987);
                    intent1.putExtra("latitudeEnd", 47.212191574506164);
                    intent1.putExtra("longitudeEnd", -1.5535549386503666);
                    intent1.putExtra("stepInItinerary", stepBool); // to know if there is a stepPoint or not

                    // Add stepPoint parameters if needed
                    if (stepBool) {
                        intent1.putExtra("latitudeStep", 47.212191574506164);
                        intent1.putExtra("longitudeStep", -1.5535549386503666);
                    }

                    startActivity(intent1);
                    finish();
                    break;
                case 2:
                    Toast.makeText(this, "No Internet.", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(this, "Point de départ hors de Nantes Métropole. Précisez la localité ou entrez une nouvelle adresse", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(this, "Point d'arrivée hors de Nantes Métropole. Précisez la localité ou entrez une nouvelle adresse", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(this, "L'étape du trajet est hors de Nantes Métropole. Précisez la localité ou entrez une nouvelle adresse", Toast.LENGTH_SHORT).show();
                    break;

            }

            // We switch on positionId, to see if My position has been chosen. If yes, we don't add
            // it to Addresses.
            switch (positionId) {
                case 0:
                    PreferencesAddresses.addAddress("startAddress", "Ma position", MainActivity.this);
                    PreferencesAddresses.addAddress("endAddress", end, MainActivity.this);
                    PreferencesAddresses.addAddress("stepAddress", step, MainActivity.this);
                    break;
                case 1:
                    PreferencesAddresses.addAddress("startAddress", start, MainActivity.this);
                    PreferencesAddresses.addAddress("endAddress", "Ma position", MainActivity.this);
                    PreferencesAddresses.addAddress("stepAddress", step, MainActivity.this);
                case 2:
                    PreferencesAddresses.addAddress("startAddress", start, MainActivity.this);
                    PreferencesAddresses.addAddress("endAddress", end, MainActivity.this);
                    PreferencesAddresses.addAddress("stepAddress", "Ma position", MainActivity.this);
                default:
                    PreferencesAddresses.addAddress("startAddress", start, MainActivity.this);
                    PreferencesAddresses.addAddress("endAddress", end, MainActivity.this);
                    PreferencesAddresses.addAddress("stepAddress", step, MainActivity.this);
            }
        }
    }

    /**
     * This method
     *
     * @param start
     * @param end
     */
    private void updateLastAddresses(String start, String end) {
        ////////////////////////////////////////////////////////////////////////////////////
        // History's management
        ////////////// The history DOES NOT TAKE INTO ACCOUNT the stepPoint! //////////////

        // get the number of addresses in the history
        int nbLastAdd = PreferencesAddresses.getNumberOfLastAddresses("lastAddress", MainActivity.this);

        // returns which of the start or end address already exists in the list and its index in the list
        int[] sameAddresses = getSameAddresses(start, end);

        // if none of the addresses already exist, add them
        if (sameAddresses[0] == -1 && sameAddresses[1] == -1) {
            PreferencesAddresses.addLastAddress("lastAddress", 0, end, MainActivity.this);
            PreferencesAddresses.addLastAddress("lastAddress", 0, start, MainActivity.this);
            nbLastAdd = nbLastAdd + 2; // the number of addresses has increased by 2
        }

        // if the startpoint already exists, move it to first position and add endpoint
        else if (sameAddresses[0] > -1 && sameAddresses[1] == -1) {
            PreferencesAddresses.addLastAddress("lastAddress", 0, end, MainActivity.this);
            PreferencesAddresses.moveAddressFirst(sameAddresses[0] + 1, MainActivity.this);
            nbLastAdd++; // the number of addresses has increased by 1
        }

        // if the endpoint already exists, move it to first position and add startpoint
        else if (sameAddresses[0] == -1 && sameAddresses[1] > -1) {
            PreferencesAddresses.moveAddressFirst(sameAddresses[1], MainActivity.this);
            PreferencesAddresses.addLastAddress("lastAddress", 0, start, MainActivity.this);
            nbLastAdd++; // the number of addresses has increased by 1
        }

        // if both addresses already exists, we move both addresses to first position
        else if (sameAddresses[0] > -1 && sameAddresses[1] > -1) {
            PreferencesAddresses.moveAddressFirst(sameAddresses[1], MainActivity.this);
            // if the endpoint was after the startpoint in the list, the index at which we have to find the address is one higher
            PreferencesAddresses.moveAddressFirst(sameAddresses[1] < sameAddresses[0] ? sameAddresses[0] : sameAddresses[0] + 1, MainActivity.this);
        }

        // One of the parameters is equal to My position

        // The start point is user's position
        else if (sameAddresses[0] == -2) {
            // End address is not known
            if (sameAddresses[1] == -1) {
                PreferencesAddresses.addLastAddress("lastAddress", 0, end, MainActivity.this);
                nbLastAdd++;
            }
            // End address already known
            else {
                PreferencesAddresses.moveAddressFirst(sameAddresses[1], MainActivity.this);
            }
        }
        // The end point is user's location
        else if (sameAddresses[1] == -2) {
            // The start point is not known
            if (sameAddresses[0] == -1) {
                PreferencesAddresses.addLastAddress("lastAddress", 0, start, MainActivity.this);
                nbLastAdd++;
            }
            // Start address already in the list, we put it first
            else {
                PreferencesAddresses.moveAddressFirst(sameAddresses[0], MainActivity.this);
            }
        }

        // check if number of addresses has gone over 3 and remove the ones over 3
        if (nbLastAdd == 5) {
            PreferencesAddresses.removeLastAddress("lastAddress", nbLastAdd + 1, MainActivity.this);
            PreferencesAddresses.removeLastAddress("lastAddress", nbLastAdd, MainActivity.this);
        } else if (nbLastAdd == 4) {
            PreferencesAddresses.removeLastAddress("lastAddress", nbLastAdd, MainActivity.this);
        }
    }


    /**
     * This methods allows to check if the itinerary can be launched.
     * Particularly, it checks if the different adresses entered by the user are the same,
     * or if one is missing.
     *
     * @return boolean : true if everything is okay, false otherwise.
     */
    private boolean checkGoodAddressesForItinerary() {
        boolean goodStartAndEnd = true;
        // Check start and end adresses are not null
        if (start.length() == 0 || end.length() == 0) {
            goodStartAndEnd = false;
            // if nothing has been typed in, nothing happens and you get a message
            Toast.makeText(MainActivity.this, "Vous devez remplir les deux champs", Toast.LENGTH_SHORT).show();
        }

        // Check if start and end adresses are the same
        else if (start.equals(end)) {
            goodStartAndEnd = false;
            // if both addresses are the same, do nothing
            Toast.makeText(MainActivity.this, "Veuillez entrer deux adresses différentes", Toast.LENGTH_SHORT).show();
        }

        // stepPoint management: we check whether there is a stepPoint that is enabled (visible)
        boolean stepEqualStartOrEnd = false;
        if (step.length() > 0 && stepPoint.isShown()) {
            stepBool = true;
            // Check if start or end is the same than step address
            if (step.equals(start) || step.equals(end)) {
                Toast.makeText(MainActivity.this, "Veuillez entrer des adresses différentes", Toast.LENGTH_SHORT).show();
                stepEqualStartOrEnd = true;
            }
        }

        // If no step point : just check start and end.
        // If step point : check if it's different than start and end
        return ((!stepBool || (stepBool && !stepEqualStartOrEnd)) && goodStartAndEnd);
    }


    /**
     * when the focus is on the start or endpoint edittext, display popupWindow, when the edittext loses focus, dismiss popupWindow
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = (int) v.getTag();
        buttonClicked = v.findViewWithTag(i);
        idButton = buttonClicked.getId(); // We use this later to know where we have to write the location : in the startPoint, stepPoint endPoint
        if (hasFocus) {
            popUp = showFavoriteAddresses();
            popUp.showAsDropDown(v, 0, 10); // show popup like dropdown list
        }
        if (!hasFocus) {
            popUp.dismiss();
        }
    }

    /**
     * This is used to check when the user clicks outside of the start or endpoint edittext // DONT CHANGE \\
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * returns the index of the addresses that already exist in the history list.
     * Returns -2 if the String is "ma position" and -1 if the index isn't in the addresses
     *
     * @param start : String of the start address.
     * @param end   : String of the end address
     * @return res : The first value is for the start address, the second for end address.
     */
    public int[] getSameAddresses(String start, String end) {
        String myPositionText = getString(R.string.position_text);
        int[] res = new int[2];
        res[0] = start.equals(myPositionText) ? -2 : -1; // startpoint
        res[1] = end.equals(myPositionText) ? -2 : -1; // endpoint
        for (int j = 0; j < PreferencesAddresses.getNumberOfLastAddresses("lastAddress", MainActivity.this); j++) {
            String lastAddress = PreferencesAddresses.getLastAddresses("lastAddress", MainActivity.this).get(j);
            if (start.equals(lastAddress)) {
                res[0] = j;
            } else if (end.equals(lastAddress)) {
                res[1] = j;
            }
        }
        return res;
    }

    /**
     * Check if the device has an internet connection
     *
     * @return
     */
    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        return false;
    }//end of check int

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Save State Main OnPause");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "Save State Main OnRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Save State Main OnResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "Save State Main OnStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Save State Main OnDestroy");
    }

    /**
     * When users click on plus button to get a step during itinerary.
     *
     * @param view
     */
    public void onClickStepPointButton(View view) {
        // make the stepPoint visible when it is not
        if (!stepVisibility) {
            stepPoint.setVisibility(View.VISIBLE);
            addStepPoint.setActivated(true);
            stepVisibility = true;
        } // make the stepPoint Invisible when it is
        else {
            stepPoint.setVisibility(View.GONE);
            addStepPoint.setActivated(false);
            stepVisibility = false;
        }
    }

    /**
     * When users click on inversion button to reverse start and end.
     *
     * @param view
     */
    public void onClickInversionButton(View view) {
        String myPositionText = getString(R.string.position_text);
        Editable startText = startPoint.getText();
        Editable endText = endPoint.getText();
        endPoint.setText(startText);
        startPoint.setText(endText);

        // If ma position is set, we need to update the positionId to get user's location.
        if (startText.toString().equals(myPositionText)) {
            this.positionId = 1;
        } else if (endText.toString().equals(myPositionText)) {
            this.positionId = 0;
        }
    }

    /**
     * When users click on options button to choose transportation, date....
     *
     * @param view
     */
    public void onClickOptions(View view) {
        // things to do when user clicks options
        popUp = showOptions();
        dimPopup.setVisibility(View.VISIBLE);
        popUp.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    /**
     * When users click on location button to center the map on his location.
     *
     * @param view
     */
    public void onClickLocationButton(View view) {
        // We need this parameter to check if the phone's GPS is activated
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        assert locationManager != null; //check if there the app is allowed to access location
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if the GPS is enabled

        // If the permission to access to the user's location is already given, we use it
        if (ContextCompat.checkSelfPermission(MainActivity.this,
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
                    tmpPoint = new GeoPoint(locationUser.getLatitude(), locationUser.getLongitude());
                    positionMarker.setPosition(tmpPoint);
                    positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    positionMarker.setFlat(true);
                    positionMarker.setIcon(getResources().getDrawable(R.drawable.ic_position));
                    map.getOverlays().add(positionMarker);
                    mapController.setCenter(tmpPoint);
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
     * temporary function for testing real time itinerary
     */

    public void onClickPollen(View view) {
        displayPollen();
    }
}





