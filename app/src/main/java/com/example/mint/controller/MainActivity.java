package com.example.mint.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mint.R;
import com.example.mint.model.Coordinates;
import com.example.mint.model.CustomListAdapter;
import com.example.mint.model.Preferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity is the activity for the front page of the app, where the user can select start and end points for an itinerary
 * among other things.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, LocationListener {


    private View dimPopup;

    /**
     * GEOLOC
     */
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private int idButton; // We need this to know where we have to write the location of the user : in the startPoint or the endPoint
    private int idInt = 0; // We need this to know where we have to write the location of the user : in the startPoint, stepPoint or endPoint
    boolean GpsStatus = false; //true if the user's location is activated on the phone
    private final int POSITION_PERMISSION_CODE = 1;
    LocationManager locationManager;

    /**
     * Map
     */
    private MapView map;
    IMapController mapController;

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
    private EditText startPoint2; // for starPoint/endPoint inversion
    private EditText endPoint;
    private EditText stepPoint;
    private boolean stepVisibility = false; // "true" is stepPoint is visible, "false" if not
    private boolean stepBool = false; // "true" if the user has chosen a stepPoint, "false" if not
    private ImageButton addStepPoint;
    private EditText latitude;
    private EditText longitude;
    private ImageButton inversionButton;
    private Button search;
    //private final int POSITION_PERMISSION_CODE = 1;

    //boolean GpsStatus = false; //true if the user's location is activated on the phone

    //private static final String TAG = "MainActivity"; //--> for debugging

    ArrayList<String> lastAddressList;
    ArrayList<String> addressList;
    ListView addressListView;
    String start;
    String end;
    String step;
    EditText buttonClicked;



    /**
     * Options
     */
    private Button option;
    private Button dateBtn;
    private ImageButton iconDateBtn;
    private ImageButton iconTimeBtn;
    private Button timeBtn;
    PopupWindow popUp;
    PopupWindow popUpCalendar;
    TimePicker timePicker;
    // get current date and time
    final Calendar cldr = Calendar.getInstance();
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
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // First step to highlight already selected favorite means of transportation
        // (next and last step in "showOptions()"
        ArrayList<String> favoriteTrans = Preferences.getPrefTransportation("Transportation",MainActivity.this);
        int[] fav = {0,0,0,0};
        for (int j = 0;j<4;j++) {
            if (favoriteTrans.get(j).equals("car_button")) {
                fav[j] = 1;
            }
            if (favoriteTrans.get(j).equals("tram_button")) {
                fav[j] = 2;
            }
            if (favoriteTrans.get(j).equals("bike_button")) {
                fav[j] =3;
            }
            if (favoriteTrans.get(j).equals("walk_button")) {
                fav[j] = 4;
            }
        }
        System.out.println("{"+fav[0]+","+fav[1]+","+fav[2]+","+fav[3]+"}");
        Preferences.setOptionTransportation(fav,this);

        // get current date and time
        year = cldr.get(Calendar.YEAR);
        month = cldr.get(Calendar.MONTH);
        day = cldr.get(Calendar.DAY_OF_MONTH);
        hour = cldr.get(Calendar.HOUR_OF_DAY);
        minutes = cldr.get(Calendar.MINUTE);

        // First step to set default date and time to current date and time
        // (next and last step in "showOptions()"
        dateText = String.format("%02d",day) + "/" + String.format("%02d",(month+1)) + "/" + year;
        timeText = String.format("%02d",hour) + ":" + String.format("%02d",minutes);

        // First step to select if you want start time or end time. Start time is automatically selected
        // (next and last step in "showOptions()")
        starting = true;

        // Initialisation
        startPoint = findViewById(R.id.startPoint);
        endPoint = findViewById(R.id.endPoint);
        stepPoint = findViewById(R.id.stepPoint);
        search = findViewById(R.id.search);
        addStepPoint = findViewById(R.id.addStepPoint);

        option = findViewById(R.id.options);
        dimPopup = findViewById(R.id.dim_popup);

        start = startPoint.getText().toString();
        end = endPoint.getText().toString();

        this.endAddress = new com.example.mint.model.Address();
        this.startAddress = new com.example.mint.model.Address();
        this.stepAddress = new com.example.mint.model.Address();

        // check if the editText is empty and if so disable add button
        TextWatcher textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * Set the search button enable if the length of the text written in the field s (start or end) is not zero
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search.setEnabled(s.toString().length()!=0);
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
        search.setOnClickListener(this);
        option.setOnClickListener(this);

        // set the tags for when onClick is called
        startPoint.setTag(0);
        endPoint.setTag(1);
        search.setTag(2);
        option.setTag(3);
        stepPoint.setTag(10);

        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        //Map
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(47.21, -1.55);
        mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(startPoint);

        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        addStepPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make the stepPoint visible when it is not
                if (!stepVisibility){
                    stepPoint.setVisibility(View.VISIBLE);
                    addStepPoint.setActivated(true);
                    stepVisibility = true;
                } // make the stepPoint INvisible when it is
                else {
                    stepPoint.setVisibility(View.GONE);
                    addStepPoint.setActivated(false);
                    stepVisibility = false;
                }
            }
        });


    }

    /////////////////////////////////////////////////////////
    // BACK BUTTON //
    /////////////////////////////////////////////////////////
    /**
     * Overrides onBackPressed method so we can navigate to the previous activity when the phone's back button is pressed
     */
    @Override
    public void onBackPressed(){

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
        if(targetActivity.equals("com.example.mint.MapsActivity") || targetActivity.equals("com.example.mint.controller.ProfileActivity")){

        // hide/show stepPoint when the user clicks on the addStepPoint button

            //override the transition and finish the current activity
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            this.finish();
        }
    }

    /////////////////////////////////////////////////////////
    // BACK BUTTON END //
    /////////////////////////////////////////////////////////


    /**
     * function that creates the popup window on selection of editTexts
     * @return
     */
    private PopupWindow showFavoriteAddresses() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);
        lastAddressList = Preferences.getLastAddresses("lastAddress",this);
        addressList = Preferences.getPrefAddresses("Address", this);
        lastAddressList.add(0,"Mes dernières adresses :");
        addressList.add(0,"Mes adresses favorites :");

        addressList.addAll(0,lastAddressList);

        addressList.add(0,"Ma position");

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
        popupWindow.setWidth((int)getResources().getDimension(R.dimen.start_point_width));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_bg_popup));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED); // To avoid that the popup hide the keyboard
        popupWindow.setOutsideTouchable(false); // To avoid that the popup hide the keyboard


        // set the listview as popup content
        popupWindow.setContentView(addressListView);

        // startPoint/endPoint inversion
        inversionButton = findViewById(R.id.inversion);
        inversionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startPoint2.setText(startPoint.getText());$
                Editable startText = startPoint.getText();
                Editable endText = endPoint.getText();
                endPoint.setText(startText);
                startPoint.setText(endText);
            }
        });

        return popupWindow;
    }

    /**
     * This method creates the popup window for the options
     * @return PopupWindow : the options popup window
     */
    private PopupWindow showOptions() {
        // create the views for both popUpWindows
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View optionPopupView = inflater.inflate(R.layout.popup_activity_main_options,null);
        final View calendarPopupView = inflater.inflate(R.layout.popup_options_calendar,null);

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
        int[] favoriteTransportation = Preferences.getOptionTransportation(MainActivity.this);
        for (int i = 4;i<8;i++){
            ImageButton button = optionPopupView.findViewWithTag(i);
            if (favoriteTransportation[i-4]!=0){
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
                if (buttonClicked.isActivated()){
                    String key = (String) buttonClicked.getContentDescription();
                    int value = Integer.parseInt(key);
                    Preferences.addOptionTransportation(key,value,MainActivity.this);
                }
                else if (!buttonClicked.isActivated()){
                    String key = (String) buttonClicked.getContentDescription();
                    Preferences.addOptionTransportation(key,0,MainActivity.this);
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
                popUpCalendar.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);

                CalendarView calendarView = calendarPopupView.findViewById(R.id.calendar);

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        dateText =  String.format("%02d",dayOfMonth) + "/" +  String.format("%02d",(month+1)) + "/" + year;
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
                popUpCalendar.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);

                CalendarView calendarView = calendarPopupView.findViewById(R.id.calendar);

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        dateText =  String.format("%02d",dayOfMonth) + "/" +  String.format("%02d",(month+1)) + "/" + year;
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
                        timeText = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
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
                        timeText = String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute);
                        timeBtn.setText(timeText);
                    }
                });
                timeDialog.show();
                timeDialog.setTitle("Choisissez une heure de départ");
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        });


        // the buttons for selecting if you want start time or end time.
        // the buttons for selecting if you want the healthier path or the fastest one. plusRapide is automatically selected
        Button plusRapide = optionPopupView.findViewById(R.id.fastest);
        Button plusSain = optionPopupView.findViewById(R.id.healthier);
        plusRapide.setTag(10);
        plusSain.setTag(11);

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
                Button button2 = optionPopupView.findViewWithTag(i==8?9:8);
                button1.setActivated(true);
                button2.setActivated(false);

                // memorization of the selection of start or end time
                if (i == 8){
                    starting = true;
                } else {
                    starting = false;
                }
            }
        };

        // the buttons for selecting if you want the healthier path or the fastest one.
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
                Button button2 = optionPopupView.findViewWithTag(i==10?11:10);
                if (button1.isActivated()) {
                    button1.setActivated(false);
                    button2.setActivated(false);
                    fast = false;
                    healthy = false;
                }
                else if(!button1.isActivated()) {
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
                        default: break;
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
        }
        else {
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

    /** Ask the permission to the user to use their location
     *
     */
    private void requestLocalisationPermission(){
        // If the permission WAS DENIED PREVIOUSLY,
        // we open a dialog to ask for the permission to access to the user's location
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this) //create a dialog window to autorise access to location only if the user previously refused to grant location
                    .setTitle("Autorisation nécessaire")
                    .setMessage("Nous avons besoin de votre autorisation pour utiliser votre géolocalisation.")
                    .setPositiveButton("autoriser", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If the user click on this button, we ask her/him the permission to use her/his position
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION}, POSITION_PERMISSION_CODE);
                            popUp.dismiss();
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
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION}, POSITION_PERMISSION_CODE);
        }
    }

    /**Return the answer of the location permission request in a "short toast window" at the bottom
     * of the screen and print the user's position if we have the permission
     * @param requestCode
     * @param grantResults
     * @param permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == POSITION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Autorisation ACCORDÉE", Toast.LENGTH_SHORT).show();
                // If the permission to access to the user's location is  allowed AND if the GPS' phone is activated,
                // we use this location
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && GpsStatus) {
                    popUp.dismiss();
                    getLocation(); //getLocation to avoid clicking again on "Ma position"
                }
            }
            else {
                Toast.makeText(this, "Autorisation REFUSÉE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Ask the user to turn on their location
     *
     */
    private void showAlertMessageNoGps() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Echec de la localisation")
                .setMessage("Votre localisation n'est pas activée. Voulez-vous l'activer ?")
                .setPositiveButton("oui", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //access to phone's settings to activate GPS
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION}, POSITION_PERMISSION_CODE);
                        popUp.dismiss();
                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener(){ //refuse to activate GPS
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    /** Return user's position in coordinates
     *
     */
    @SuppressLint("MissingPermission")
    private void getLocation(){
        //Access user's location
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);

        // We now need to know where we have to write the location : in the startPoint, stepPoint or endPoint
        if (idButton == startPoint.getId()){
            idInt = 0;
        }
        if (idButton == endPoint.getId()) {
            idInt = 1;
        }
        if (idButton == stepPoint.getId()) {
            idInt = 2;
        }
    }

    /** Print user's position If we need to convert the
     * coordinates in an address, we need to do it here with a "geocoder"
     *
     * @param location
     */
    public void onLocationChanged(Location location) {
        //String position = location.getLatitude() + "," + location.getLongitude();
        Coordinates coordinates = new Coordinates(location.getLatitude(),location.getLongitude());
        // We write the location in the good place : startPoint, stepPoint or endPoint
        if (idInt == 0){
            startAddress.setLocationName(String.valueOf(R.string.position_text));
            startAddress.setCoordinates(coordinates);
            startPoint.setText("Ma position");
            //startPoint.setSelection(buttonClicked.length()-1); // set cursor at end of text
        } if (idInt == 1) {
            endAddress.setLocationName(String.valueOf(R.string.position_text));
            endAddress.setCoordinates(coordinates);
            endPoint.setText("Ma position");
            //endPoint.setSelection(buttonClicked.length()-1); // set cursor at end of text
        } if (idInt == 2) {
            stepAddress.setLocationName(String.valueOf(R.string.position_text));
            stepAddress.setCoordinates(coordinates);
            stepPoint.setText("Ma position");
            //stepPoint.setSelection(buttonClicked.length()-1); // set cursor at end of text
        }
    }

    /////////////////////////////////////////////////////////
    // LOCATION END //
    /////////////////////////////////////////////////////////


    /**
     * Callback when the user clicks on an item in the listView
     * @return
     */
    private AdapterView.OnItemClickListener onItemClickListener(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id>0) {
                    buttonClicked.setText(addressList.get((int)id));
                    buttonClicked.setSelection(buttonClicked.length()); // set cursor at end of text
                    popUp.dismiss();
                }
                // if we click on My Position, ask permission for geolocalisation
                if (id==0) {
                    // We need this parameter to check if the phone's GPS is activated
                    locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                    assert locationManager != null; //check if there the app is allowed to access location
                    GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if the GPS is enabled

                    // If the permission to access to the user's location is already given, we use it
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        // We also need the phone's GPS to be activated. We check this here.
                        if (GpsStatus){
                            popUp.dismiss();
                            getLocation();
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
     * Method called when the user clicks on "search" or "option"
     * @param v
     */
    @Override
    public void onClick(View v){
        int i = (int) v.getTag(); //if 2 : search, if 3 : option
        start = startPoint.getText().toString();
        end = endPoint.getText().toString();
        step = stepPoint.getText().toString();

        // things to do when user clicks search
        if(i==2){
            if (start.length() == 0 || end.length() == 0){
                // if nothing has been typed in, nothing happens and you get a message
                Toast.makeText(MainActivity.this, "Vous devez remplir les deux champs", Toast.LENGTH_SHORT).show();
            } // stepPoint management: we check whether there is a stepPoint
            else if (step.length() > 0) {
                stepBool = true;
                // if at least two addresses are the same, do nothing
                if (step.equals(start) || step.equals(end) || start.equals(end)) {
                    Toast.makeText(MainActivity.this, "Veuillez rentrer des adresses différentes",Toast.LENGTH_SHORT).show();
                }
            } else if (start.equals(end)) {
                // if both addresses are the same, do nothing
                Toast.makeText(MainActivity.this, "Veuillez entrer deux adresses différentes",Toast.LENGTH_SHORT).show();
            }
            else {

                // stepPoint management: we check whether there is a stepPoint
                boolean stepEqualStartOrEnd = false;
                if (step.length() > 0) {
                    stepBool = true;
                    // if at least two addresses are the same, do nothing
                    if (step.equals(start) || step.equals(end) || start.equals(end)) {
                        Toast.makeText(MainActivity.this, "Veuillez entrer des adresses différentes",Toast.LENGTH_SHORT).show();
                        stepEqualStartOrEnd = true;
                    }
                }

                // Checking if there is a stepPoint:
                //      if yes : stepPoint must be different than startPoint and endPoint
                //      if not : go
                if ( !stepBool || (stepBool && !stepEqualStartOrEnd) ) {

                    ////////////////////////////////////////////////////////////////////////////////////
                    // History's management
                    ////////////// The history DOES NOT TAKE INTO ACCOUNT the stepPoint! //////////////

                    // get the number of addresses in the history
                    int nbLastAdd = Preferences.getNumberOfLastAddresses("lastAddress", MainActivity.this);

                    // returns which of the start or end address already exists in the list and its index in the list
                    int[] sameAddresses = getSameAddresses(start, end);

                    // if none of the addresses already exist, add them
                    if (sameAddresses[0] == -1 && sameAddresses[1] == -1) {
                        Preferences.addLastAddress("lastAddress", 0, end, MainActivity.this);
                        Preferences.addLastAddress("lastAddress", 0, start, MainActivity.this);
                        nbLastAdd = nbLastAdd + 2; // the number of addresses has increased by 2
                    }

                    // if the startpoint already exists, move it to first position and add endpoint
                    else if (sameAddresses[0] != -1 && sameAddresses[1] == -1) {
                        Preferences.addLastAddress("lastAddress", 0, end, MainActivity.this);
                        Preferences.moveAddressFirst(sameAddresses[0] + 1, MainActivity.this);
                        nbLastAdd++; // the number of addresses has increased by 1
                    }

                    // if the endpoint already exists, move it to first position and add startpoint
                    else if (sameAddresses[0] == -1 && sameAddresses[1] != -1) {
                        Preferences.moveAddressFirst(sameAddresses[1], MainActivity.this);
                        Preferences.addLastAddress("lastAddress", 0, start, MainActivity.this);
                        nbLastAdd++; // the number of addresses has increased by 1
                    }

                    // if both addresses already exist, we move both addresses to first position
                    else {
                        Preferences.moveAddressFirst(sameAddresses[1], MainActivity.this);
                        // if the endpoint was after the startpoint in the list, the index at which we have to find the address is one higher
                        Preferences.moveAddressFirst(sameAddresses[1] < sameAddresses[0] ? sameAddresses[0] : sameAddresses[0] + 1, MainActivity.this);
                    }

                    // check if number of addresses has gone over 3 and remove the ones over 3
                    if (nbLastAdd == 5) {
                        Preferences.removeLastAddress("lastAddress", nbLastAdd + 1, MainActivity.this);
                        Preferences.removeLastAddress("lastAddress", nbLastAdd, MainActivity.this);
                    } else if (nbLastAdd == 4) {
                        Preferences.removeLastAddress("lastAddress", nbLastAdd, MainActivity.this);
                    }


                    ////////////////////////////////////////////////////////////////////////////////////
                    //Conversion addresses to spatial coordinates
                    //For the start point
                    Geocoder geocoderStart = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        if (!start.equals(R.string.position_text)) {  //check if location is not chosen
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
                        if (!end.equals(R.string.position_text)) {       //check if location is not chosen
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
                    if (stepBool) {
                        Geocoder geocoderStep = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            if (!step.equals(R.string.position_text)) {       //check if location is not chosen
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

                    ////////////////////////////////////////////////////////////////////////////////////
                    //start itinerary calculation activity if the device has an internet connection
                    int error = 0;
                    if (!CheckInternet()) { //no internet connection
                        error = 2;
                    } else if (endAddress.getCoordinates().isZero() || startAddress.getCoordinates().isZero()) { //conversion impossible
                        error = 1;
                    } else if (startAddress.getCoordinates().getLatitude() < 47.0 |
                            startAddress.getCoordinates().getLatitude() > 47.4 |
                            startAddress.getCoordinates().getLongitude() < -1.8 |
                            startAddress.getCoordinates().getLongitude() > -1.3) {
                        //System.out.pri
                        error = 3;
                    } else if (endAddress.getCoordinates().getLatitude() < 47.0 |
                            endAddress.getCoordinates().getLatitude() > 47.4 |
                            endAddress.getCoordinates().getLongitude() < -1.8 |
                            endAddress.getCoordinates().getLongitude() > -1.3) {
                        error = 4;
                    } else if (stepBool && stepAddress.getCoordinates().getLatitude() < 47.0 |
                            stepAddress.getCoordinates().getLatitude() > 47.4 |
                            stepAddress.getCoordinates().getLongitude() < -1.8 |
                            stepAddress.getCoordinates().getLongitude() > -1.3) {
                        error = 5;
                    }

                    System.out.println("error" + error);
                    switch (error) {
                        case 0:
                            Intent intent = new Intent(getApplicationContext(), LoadingPageActivity.class);
                            intent.putExtra("starting", starting);
                            //intent.putExtra("date", dateText);
                            intent.putExtra("time", timeText);
                            intent.putExtra("param1", startAddress.getCoordinates().getLatitude());
                            intent.putExtra("param2", startAddress.getCoordinates().getLongitude());
                            intent.putExtra("param3", endAddress.getCoordinates().getLatitude());
                            intent.putExtra("param4", endAddress.getCoordinates().getLongitude());
                            intent.putExtra("param5", stepBool); // to know if there is a stepPoint or not

                            // Add stepPoint parameters if needed
                            if (stepBool) {
                                intent.putExtra("param6", stepAddress.getCoordinates().getLatitude());
                                intent.putExtra("param7", stepAddress.getCoordinates().getLongitude());
                            }
                            System.out.println("recherche");
                            startActivity(intent);
                            finish();
                            break;

                        case 1:
                            Toast.makeText(this, "Conversion impossible, entrez une nouvelle adresse ou réessayez plus tard", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(getApplicationContext(), LoadingPageActivity.class);
                            intent1.putExtra("param1", 47.2484039066116);
                            intent1.putExtra("param2", -1.549636963829987);
                            intent1.putExtra("param3", 47.212191574506164);
                            intent1.putExtra("param4", -1.5535549386503666);
                            intent1.putExtra("param5", stepBool); // to know if there is a stepPoint or not

                            // Add stepPoint parameters if needed
                            if (stepBool) {
                                intent1.putExtra("param6", 47.212191574506164);
                                intent1.putExtra("param7", -1.5535549386503666);
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
                }
            }



                /*if (CheckInternet()){
                    if (!endAddress.getCoordinates().isZero() & !startAddress.getCoordinates().isZero()){
                    Intent intent = new Intent(getApplicationContext(),LoadingPageActivity.class);
                    intent.putExtra("param1", endAddress.getCoordinates().getLatitude());
                    intent.putExtra("param2", endAddress.getCoordinates().getLongitude());
                    intent.putExtra("param3", startAddress.getCoordinates().getLatitude());
                    intent.putExtra("param4", startAddress.getCoordinates().getLongitude());
                    startActivity(intent);
                    finish();
                }
                    else{
                        Toast.makeText(this, "Conversion impossible, entrez une nouvelle adresse ou réessayez plus tard", Toast.LENGTH_SHORT).show();
                    }

                    }
                else{
                    Toast.makeText(this, "No Internet.", Toast.LENGTH_SHORT).show();
                }*/

                Preferences.addAddress("startAddress",start,MainActivity.this);
                Preferences.addAddress("endAddress",end,MainActivity.this);
                Preferences.addAddress("stepAddress",step,MainActivity.this);

            }

        // things to do when user clicks options
        else if (i==3){
                    popUp = showOptions();
                    dimPopup.setVisibility(View.VISIBLE);
                    popUp.showAtLocation(v,Gravity.CENTER,0,0);
                }
    }

    /**
     * when the focus is on the start or endpoint edittext, display popupWindow, when the edittext loses focus, dismiss popupWindow
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus){
        int i = (int) v.getTag();
        buttonClicked = v.findViewWithTag(i);
        idButton = buttonClicked.getId(); // We use this later to know where we have to write the location : in the startPoint, stepPoint endPoint
        if(hasFocus) {
            popUp = showFavoriteAddresses();
            popUp.showAsDropDown(v, 0, 10); // show popup like dropdown list
        }
        if(!hasFocus){
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
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    /**
     * returns the index of the addresses that already exist in the history list, returns -1 if doesnt exist
     * @param start
     * @param end
     * @return
     */
    public int[] getSameAddresses(String start, String end){
        int[] arr = new int[2];
        arr[0]=-1; // startpoint
        arr[1]=-1; // endpoint
        for (int j = 0; j < Preferences.getNumberOfLastAddresses("lastAddress",MainActivity.this); j++) {
            String lastAddress = Preferences.getLastAddresses("lastAddress", MainActivity.this).get(j);
            if (start.equals(lastAddress)) {
                arr[0]=j;
            }
            else if (end.equals(lastAddress)) {
                arr[1]=j;
            }
        }
        return arr;
    }

    /**
     * Check if the device has an internet connection
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


}



