package com.example.mint;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This activity is used for the profile page of the app, in which the user can record their preferences, and access the settings
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * dim the screen behind the popup window
     */
    private View dim_popup;

    /**
     * parameters textView
     */
    private TextView parameters;

    /**
     * pollution profile
     */
    private TextView pollutionToday;
    private LineChart graph;

    ImageButton slideLeft;
    ImageButton slideRight;

    /**
     * favorite addresses
     */
    private Button addButton;
    private Button favoriteAddressesButton;
    private EditText enterAddress;
    private PopupWindow addressPopupWindow;

    /**
     * sensibility
     */
    private TextView setSensibility;
    private Button sensibilityButton;
    private PopupWindow sensibilityPopupWindow;

    /**
     * transportation
     */
    private ImageView carIcon;
    private ImageView tramIcon;
    private ImageView bikeIcon;
    private ImageView walkIcon;
    private Button favoriteTransportationButton;
    private PopupWindow transportationPopupWindow;


    //private static final String TAG = "ProfileActivity"; //--> for debugging

    /**
     * This activity handles the input of various preferences and the display of the pollution exposure throughout time
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Preferences.clearAddresses(this);
        //Preferences.clearSensibility(this);
        //Preferences.clearTransportation(this);

        //link layout elements to activity
        parameters = findViewById(R.id.parameters);
        sensibilityButton = findViewById(R.id.sensibility);
        favoriteAddressesButton = findViewById(R.id.favorite_addresses);
        favoriteTransportationButton = findViewById(R.id.favorite_transportation);

        dim_popup = findViewById(R.id.dim_popup);

        // handle the pollution
        pollutionToday = findViewById(R.id.exposure_today);
        // check whether a new day has started and if so reset pollution to 0 and store the last value
        resetPollutionNewDay();
        // Get the pollution from the last itineraries and change the pollution from today accordingly
        setPollutionToday();

        // UPDATE PREFERENCES
        // update the pollution for this month
        Preferences.addDayPollutionToMonth(Preferences.getCurrentDate(),Preferences.getPollutionToday(this),this);
        // update the pollution for this year
        Preferences.addMonthPollutionToYear(Preferences.getCurrentDate()[1],Preferences.getPollutionMonth(Preferences.getCurrentDate()[1],this),this);

        // THIS IS A TEST WITH RANDOM NUMBERS TO SEE IF DISPLAY WORKS CORRECTLY
//        ArrayList<Integer> valuesTest = new ArrayList<>();
//        ArrayList<Integer> valuesTest2 = new ArrayList<>();
//        for (int j=0;j<31;j++){
//            valuesTest.add((int) (Math.random() * 100));
//        }
//        for (int j=0;j<31;j++){
//            valuesTest2.add(0);
//        }
//        Preferences.setPollutionMonth(1,valuesTest2,this);
//        Preferences.setPollutionMonth(2,valuesTest2,this);

        // handle the graph
        graph = findViewById(R.id.chart);
        setUpGraph(0); // by default we display the week

        // handle the buttons
        Button weekBtn = findViewById(R.id.week);
        Button monthBtn = findViewById(R.id.month);
        Button yearBtn = findViewById(R.id.year);

        weekBtn.setTag(100);
        monthBtn.setTag(101);
        yearBtn.setTag(102);

        ArrayList<View> buttons = new ArrayList<>();
        buttons.add(weekBtn);
        buttons.add(monthBtn);
        buttons.add(yearBtn);

        weekBtn.setActivated(true); // by default we start with weekly graph

        final LinearLayout btnView = findViewById(R.id.graph_buttons_layout);


        View.OnClickListener onClickGraphButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag();
                v.setActivated(true);
                setUpGraph(i-100);
                for (int j=0;j<3;j++){
                    Button b = btnView.findViewWithTag(j+100);
                    if (!b.equals(v)){
                        b.setActivated(false);
                    }
                }

            }
        };

        weekBtn.setOnClickListener(onClickGraphButton);
        monthBtn.setOnClickListener(onClickGraphButton);
        yearBtn.setOnClickListener(onClickGraphButton);


        // set tags to know which button is pressed when launching onClick
        sensibilityButton.setTag(0);
        favoriteAddressesButton.setTag(1);
        favoriteTransportationButton.setTag(2);

        //launches "onClick" when one button is clicked
        sensibilityButton.setOnClickListener(this);
        favoriteAddressesButton.setOnClickListener(this);
        favoriteTransportationButton.setOnClickListener(this);

        // on click callback for parameters : open new activity
        parameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
            }
        });

        // used to call the layout which is going to be in the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View addressPopupView = inflater.inflate(R.layout.popup_favorite_adresses,null);
        final View sensibilityPopupView = inflater.inflate(R.layout.popup_sensibility,null);
        final View transportationPopupView = inflater.inflate(R.layout.popup_favorite_transportation,null);

        /////////////////////////////////////////////////////////
        // FAVORITE ADDRESSES POPUP//
        /////////////////////////////////////////////////////////

        // create the popup window
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets user tap outside the popup dismiss it
        addressPopupWindow = new PopupWindow(this);
        addressPopupWindow.setBackgroundDrawable(null);
        addressPopupWindow.setContentView(addressPopupView);
        addressPopupWindow.setWidth(width);
        addressPopupWindow.setHeight(height);
        addressPopupWindow.setFocusable(focusable);
        addressPopupWindow.setInputMethodMode(addressPopupWindow.INPUT_METHOD_NEEDED); // To avoid that the popup hide the keyboard
        addressPopupWindow.setOutsideTouchable(false); // To avoid that the popup hide the keyboard

        //remove background dimness when popup is dismissed
        addressPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dim_popup.setVisibility(View.INVISIBLE);
            }
        });

        //link elements from popup window
        addButton = addressPopupView.findViewById(R.id.button_add);
        enterAddress = addressPopupView.findViewById(R.id.enter_address);
        TextView address1 = addressPopupView.findViewById(R.id.address1);
        TextView address2 = addressPopupView.findViewById(R.id.address2);
        TextView address3 = addressPopupView.findViewById(R.id.address3);
        TextView address4 = addressPopupView.findViewById(R.id.address4);
        TextView address5 = addressPopupView.findViewById(R.id.address5);
        ImageButton remove1 = addressPopupView.findViewById(R.id.remove_btn_1);
        ImageButton remove2 = addressPopupView.findViewById(R.id.remove_btn_2);
        ImageButton remove3 = addressPopupView.findViewById(R.id.remove_btn_3);
        ImageButton remove4 = addressPopupView.findViewById(R.id.remove_btn_4);
        ImageButton remove5 = addressPopupView.findViewById(R.id.remove_btn_5);

        //set tags to know which address to display
        address1.setTag(0);
        address2.setTag(1);
        address3.setTag(2);
        address4.setTag(3);
        address5.setTag(4);
        remove1.setTag(5);
        remove2.setTag(6);
        remove3.setTag(7);
        remove4.setTag(8);
        remove5.setTag(9);

        // this is a callback that is set off every time a remove button is pressed
        View.OnClickListener onCLickRemove = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag() - 5; // to know which address has to be removed
                int numberOfAddresses = Preferences.getNumberOfAddresses("Address", ProfileActivity.this);
                Preferences.removeAddress("Address",i,ProfileActivity.this);
                for (int j=i;j<numberOfAddresses-1;j++){
                    TextView selectedText=addressPopupView.findViewWithTag(j);
                    TextView selectedTextAfter=addressPopupView.findViewWithTag(j+1);
                    selectedText.setText(selectedTextAfter.getText().toString()); // the text in each textView is set to the text that was in the next textView
                }
                // stop displaying the last address since we moved all the addresses up
                TextView deletedAddress = addressPopupView.findViewWithTag(numberOfAddresses-1);
                deletedAddress.setVisibility(View.GONE);
                ImageButton deletedButton = addressPopupView.findViewWithTag(numberOfAddresses+4);
                deletedButton.setVisibility(View.GONE);
            }
        };

        // assign callback to buttons
        remove1.setOnClickListener(onCLickRemove);
        remove2.setOnClickListener(onCLickRemove);
        remove3.setOnClickListener(onCLickRemove);
        remove4.setOnClickListener(onCLickRemove);
        remove5.setOnClickListener(onCLickRemove);

        // when the favorite addresses popup is opened, display all the addresses stored in preferences
        TextView address = new TextView(this);
        ImageButton remove = new ImageButton(this);
        if (Preferences.getNumberOfAddresses("Address",this)!=0) {
            for (int i = 0; i < Preferences.getNumberOfAddresses("Address", this); i++) {
                address = addressPopupView.findViewWithTag(i);
                remove = addressPopupView.findViewWithTag(i+5);
                address.setVisibility(View.VISIBLE);
                address.setText(Preferences.getPrefAddresses("Address", this).get(i));
                remove.setVisibility(View.VISIBLE);
            }
        }

        // check if the editText is empty and if so disable add button
        enterAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addButton.setEnabled(s.toString().length()!=0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // adds new address to preferences and displays it in the list below
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterAddress.getText().toString().length() == 0) {
                    //if nothing is typed in, requires you to type something
                    Toast.makeText(ProfileActivity.this, "Veuillez rentrer une adresse", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (Preferences.getNumberOfAddresses("Address",ProfileActivity.this)>=5){
                        // if you already have 5 addresses, tells you you cannot add anymore
                        Toast.makeText(ProfileActivity.this,"Vous ne pouvez pas ajouter plus de 5 adresses",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // add typed in address to the Preferences
                        int addressIndex = Preferences.getNumberOfAddresses("Address", ProfileActivity.this);
                        Preferences.addAddress("Address",addressIndex,enterAddress.getText().toString(),ProfileActivity.this);
                        // add the new address at the end of the list by making a new textview and remove button visible
                        TextView newAddress = addressPopupView.findViewWithTag(addressIndex);
                        ImageButton newButton = addressPopupView.findViewWithTag(addressIndex + 5);
                        newAddress.setVisibility(View.VISIBLE);
                        newAddress.setText(enterAddress.getText().toString());
                        newButton.setVisibility(View.VISIBLE);
                        // empty typing field
                        enterAddress.setText("");
                    }
                }
            }

        });

        /////////////////////////////////////////////////////////
        // FAVORITE ADDRESSES POPUP END //
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // SENSIBILITY POPUP //
        /////////////////////////////////////////////////////////

        // Create the Popup Window
        sensibilityPopupWindow = new PopupWindow(this);
        sensibilityPopupWindow.setBackgroundDrawable(null);
        sensibilityPopupWindow.setContentView(sensibilityPopupView);
        sensibilityPopupWindow.setWidth(width);
        sensibilityPopupWindow.setHeight(height);
        sensibilityPopupWindow.setFocusable(focusable);

        //link elements from popup window
        // sensibility //
        Button veryHighBtn = sensibilityPopupView.findViewById(R.id.very_high_sensibility_btn);
        Button highBtn = sensibilityPopupView.findViewById(R.id.high_sensibility_btn);
        Button moderateBtn = sensibilityPopupView.findViewById(R.id.moderate_sensibility_btn);
        Button lowBtn = sensibilityPopupView.findViewById(R.id.low_sensibility_btn);
        Button noSensibilityBtn = sensibilityPopupView.findViewById(R.id.no_sensibility_btn);
        setSensibility = findViewById(R.id.set_sensibility);

        // set Tags to use in "onClickSelect"
        veryHighBtn.setTag(10);
        highBtn.setTag(11);
        moderateBtn.setTag(12);
        lowBtn.setTag(13);
        noSensibilityBtn.setTag(14);

        // this is the sensibility that is displayed directly in the profile page
        setSensibility.setText(Preferences.getSensibility("Sensibility",this));

        // Highlight the sensibility if it has already been selected
        Button selectedButton = new Button(this);
        for (int i=10; i<=14; i++){
            selectedButton=sensibilityPopupView.findViewWithTag(i);
            if (selectedButton.getText().toString().equals(Preferences.getSensibility("Sensibility", this))){
                selectedButton.setActivated(true);
            }
        }

        // Highlight the sensibility when you click
        View.OnClickListener onClickSelect = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag();
                Button selectedButton = sensibilityPopupView.findViewWithTag(i);
                selectedButton.setActivated(!selectedButton.isActivated());
                String sensibility = selectedButton.getText().toString();
                if(selectedButton.isActivated()) {
                    Preferences.setSensibility("Sensibility", sensibility, ProfileActivity.this);
                    for (int j = 10; j<=14; j++){
                        if(j!=i) {
                            // uncheck all other buttons once a button is clicked
                            sensibilityPopupView.findViewWithTag(j).setActivated(false);
                        }
                    }
                    // dismiss the popup once you have selected a sensibility
                    sensibilityPopupWindow.dismiss(); // Remove popup
                }
                else {
                    // if clicking the button deactivates it, remove the sensibility from preferences
                    Preferences.removeSensibility("Sensibility", ProfileActivity.this);
                }
            }

        };

        veryHighBtn.setOnClickListener(onClickSelect);
        highBtn.setOnClickListener(onClickSelect);
        moderateBtn.setOnClickListener(onClickSelect);
        lowBtn.setOnClickListener(onClickSelect);
        noSensibilityBtn.setOnClickListener(onClickSelect);

        //callback when the popup is dismissed
        sensibilityPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dim_popup.setVisibility(View.INVISIBLE); // remove background dimness
                // display chosen sensibility on main profile page
                if(!setSensibility.getText().toString().equals(Preferences.getSensibility("Sensibility", ProfileActivity.this))){
                    setSensibility.setText(Preferences.getSensibility("Sensibility",ProfileActivity.this));
                }
            }
        });

        /////////////////////////////////////////////////////////
        // SENSIBILITY POPUP END //
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // TRANSPORTATION POPUP //
        /////////////////////////////////////////////////////////

        // Create the Popup Window
        transportationPopupWindow = new PopupWindow(this);
        transportationPopupWindow.setBackgroundDrawable(null);
        transportationPopupWindow.setContentView(transportationPopupView);
        transportationPopupWindow.setWidth(width);
        transportationPopupWindow.setHeight(height);
        transportationPopupWindow.setFocusable(focusable);

        // Link elements from the popup
        ImageButton carButton = transportationPopupView.findViewById(R.id.car_button);
        ImageButton tramButton = transportationPopupView.findViewById(R.id.tram_button);
        ImageButton bikeButton = transportationPopupView.findViewById(R.id.bike_button);
        ImageButton walkButton = transportationPopupView.findViewById(R.id.walk_button);
        carIcon = findViewById(R.id.car_icon);
        tramIcon = findViewById(R.id.tram_icon);
        bikeIcon = findViewById(R.id.bike_icon);
        walkIcon = findViewById(R.id.walk_icon);

        // on opening of profile page, display favorite means of transportation
        displayFavoriteTransportation();

        // Tags to determine which button is clicked in "onTransportationClick"
        carButton.setTag(15);
        tramButton.setTag(16);
        bikeButton.setTag(17);
        walkButton.setTag(18);

        // Highlight already selected favorite means of transportation
        ArrayList<String> favoriteTransportation = Preferences.getPrefTransportation("Transportation",this);
        for (int i = 15;i<19;i++){
            ImageButton button = transportationPopupView.findViewWithTag(i);
            String transportation = button.getContentDescription().toString();
            for (int j = 0;j<4;j++){
                if (transportation.equals(favoriteTransportation.get(j))){
                    button.setActivated(true);
                }
            }
        }


        // Change state of button once it is clicked
        View.OnClickListener onTransportationClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag();
                ImageButton buttonClicked = transportationPopupView.findViewWithTag(i);
                buttonClicked.setActivated(!buttonClicked.isActivated());
                int key = i-15;
                String value = buttonClicked.getContentDescription().toString();
                if (buttonClicked.isActivated()){
                    Preferences.addTransportation("Transportation",key,value,ProfileActivity.this);
                }
                else if (!buttonClicked.isActivated()){
                    Preferences.removeTransportation("Transportation",key,ProfileActivity.this);
                }
                System.out.println(Preferences.getPrefTransportation("Transportation",ProfileActivity.this));
            }
        };

        carButton.setOnClickListener(onTransportationClick);
        tramButton.setOnClickListener(onTransportationClick);
        bikeButton.setOnClickListener(onTransportationClick);
        walkButton.setOnClickListener(onTransportationClick);

        //callback when popup is dismissed
        transportationPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dim_popup.setVisibility(View.INVISIBLE); // remove background dimness
                displayFavoriteTransportation(); // check again which means of transportation have been selected
            }
        });

        /////////////////////////////////////////////////////////
        // TRANSPORTATION POPUP END //
        /////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        // BOTTOM MENU //
        /////////////////////////////////////////////////////////

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        /////////////////////////////////////////////////////////
        // BOTTOM MENU END //
        /////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////
        // SLIDE ANIMATION //
        /////////////////////////////////////////////////////////

        bottomNav.setSelectedItemId(R.id.profile);

        //TODO This is redundant with ActivityMenuSwitcher
        //Slide animation
        //bottomNav.setSelectedItemId(R.id.profile);

        /*
        bottomNav.setOnNavigationItemSelectedListener (new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.itinerary:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                        return true;
                    case R.id.maps:
                        startActivity(new Intent(getApplicationContext(),MapActivity.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        return true;
                    case R.id.profile:
                        return true;
                    default:
                }
                return false;
            }
        });
         */

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
        //For Right-To-Left transitions
        if(targetActivity.equals("com.example.mint.MainActivity") || targetActivity.equals("com.example.mint.MapActivity")){

            //override the transition and finish the current activity
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            this.finish();
        }
    }

    /////////////////////////////////////////////////////////
    // BACK BUTTON END //
    /////////////////////////////////////////////////////////

    /**
     * Displays the various popup windows when you click on each button
     * @param v : the view which has been clicked. We identify each one with a tag
     */
    @Override
    public void onClick(View v){
        int buttonClicked = (int) v.getTag();
        // dim background of popup
        dim_popup.setVisibility(View.VISIBLE);
        switch (buttonClicked) {
            case 0:
                // show the sensibility popup window
                sensibilityPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case 1:
                // show the addresses popup window
                addressPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case 2:
                // show the transportation popup window
                transportationPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
        }
    }

    /**
     * Get icon from int
     * @param i : index which corresponds to the icon
     * @return : the ImageView of the corresponding icon
     */
    public ImageView findIconFromInt(int i){
        ImageView icon = new ImageView(ProfileActivity.this);
        switch (i){
            case 0:
                icon=carIcon;
                break;
            case 1:
                icon=tramIcon;
                break;
            case 2:
                icon=bikeIcon;
                break;
            case 3:
                icon=walkIcon;
                break;
        }
        return icon;
    }

    /**
     * This method displays the already selected favorite means of transportation on the main profile page
     */
    public void displayFavoriteTransportation(){
        // we go through all the means of transportation
        for(int i = 0;i<4;i++){
            // gets the right icon from the index
            ImageView selectedIcon = findIconFromInt(i);
            if (Preferences.getPrefTransportation("Transportation",ProfileActivity.this).get(i).equals("--")) {
                // if this means of transporation is not in the preferences list, we do not display it
                selectedIcon.setVisibility(View.GONE);
            }
            else {
                // if it is, we display it
                selectedIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * This adds the pollution from the last itinerary to today's pollution
     */
    private void setPollutionToday(){
        // get the pollution from today
        int pollution = Preferences.getPollutionToday(this);
        // get the pollution from the last itinerary
        int lastPol = Preferences.getLastPollution(this);
        // add it to the pollution from today
        pollution+=lastPol;
        // save it in the preferences
        Preferences.setPollutionToday(pollution,this);
        // display new pollution
        pollutionToday.setText(Integer.toString(pollution));
        // reset the last pollution now that it has been saved (so that it doesn't keep adding it as soon as we reopen the profile)
        Preferences.setLastPollution(0,this);
    }

    /**
     * This method checks whether we have started a new day or not every time we open the profile activity
     * If so, we save the new date value in the preferences, to be used as a comparison for the next time this function is called
     * Then we clear the value for today's pollution and add it to an array with the values for the month's exposure
     */
    private void resetPollutionNewDay(){
        // first we check whether today is a new day or not
        // the date is in the format {day,month,year}, so currentDate[0] corresponds to the day
        int[] lastDate = Preferences.getLastDate(ProfileActivity.this); // this is the value that was set the last time the day changed (in if statement)
        int[] currentDate = Preferences.getCurrentDate(); // this is the current date

        if (currentDate[0]!=lastDate[0]){
            Preferences.setDate(ProfileActivity.this); // we replace the last saved date with today's date
            // then we add the pollution from the last day to the array of this month's pollution
            Preferences.addDayPollutionToMonth(lastDate,Preferences.getPollutionToday(ProfileActivity.this),ProfileActivity.this);
            Preferences.setPollutionToday(0,ProfileActivity.this); // we start over with a value of 0
        }
    }

    /**
     * This handles all the graph values and appearance settings
     * @param range : whether we want to display a week, a month or a year (0 for week, 1 for month, 2 for year)
     */
    private void setUpGraph(final int range){
        // we get the pollution data from preferences
        ArrayList<Integer> values = Preferences.getPollutionYear(Preferences.getCurrentDate()[2],this);

        // we convert it to a list of "entries" which is a class from the MPAndroidChart library
        List<Entry> entries = new ArrayList<>();
        // we use the java date format to calculate how many days have gone by since the beginning of the year
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String inputString1 = Preferences.getCurrentDate()[0]+" "+Preferences.getCurrentDate()[1]+" "+Preferences.getCurrentDate()[2];
        String inputString2 = "00 01 "+Preferences.getCurrentDate()[2];
        int diffDays=0;
        try {
            Date date1 = myFormat.parse(inputString1);
            Date date2 = myFormat.parse(inputString2);
            long diff = date1.getTime() - date2.getTime(); // number of days that have gone by in milliseconds
            diffDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            System.out.println ("Days: " + diffDays);
            for (int i=1;i<=diffDays;i++){
                entries.add(new Entry(i,values.get(i-1)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // LineDataSet allows for individual styling of this data (for if we have several data sets)
        LineDataSet dataSet = new LineDataSet(entries,null);
        dataSet.setColor(getResources().getColor(R.color.colorAccent));
        dataSet.setLineWidth(3);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(getResources().getColor(R.color.colorAccent));
        dataSet.setCircleRadius(5);
        dataSet.setCircleHoleRadius(3);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        // Axis styling
        // Y axis
        YAxis yAxisRight = graph.getAxisRight();
        final YAxis yAxisLeft = graph.getAxisLeft();
        yAxisRight.setEnabled(false);
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setGridLineWidth(0.5f);
        yAxisLeft.setGridColor(getResources().getColor(R.color.colorLightGrey));
        yAxisLeft.setTypeface(Typeface.DEFAULT);
        yAxisLeft.setTextColor(getResources().getColor(R.color.colorDarkGrey));

        // X axis
        final XAxis xAxis = graph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setTextColor(getResources().getColor(R.color.colorDarkGrey));

        // according to which button was clicked (week, month or year), we change the range of the x axis :
        switch (range){
            case 0 :
                // in the case that we want to show the week :
                // first we change the range of the x axis
                int currentDayOfWeek = (Preferences.getCurrentDate()[3])-1; // day of week starts with 1 for sunday, so we do -1 to start with monday
                int xAxisMin = (diffDays-currentDayOfWeek)+1; // we have to add one because if we dont we will have a one day difference
                xAxis.setAxisMinimum(xAxisMin);
                xAxis.setAxisMaximum(diffDays+(7-currentDayOfWeek));
                // then we change the labels of the x axis
                final ArrayList<String> xAxisLabelWeek = new ArrayList<>();
                xAxisLabelWeek.add("Lun");
                xAxisLabelWeek.add("Mar");
                xAxisLabelWeek.add("Mer");
                xAxisLabelWeek.add("Jeu");
                xAxisLabelWeek.add("Ven");
                xAxisLabelWeek.add("Sam");
                xAxisLabelWeek.add("Dim");
                // this formats the values to be the new ones we just created :
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return xAxisLabelWeek.get((int) (value - xAxis.getAxisMinimum()));
                    }
                });
                dataSet.setDrawCircles(true);
                graph.invalidate();
                break;
            case 1 :
                // in the case that we want to show the month :
                // first we change the range of the x axis
                int dayOfMonth = Preferences.getCurrentDate()[0]; // same principle as the day of week
                int lengthOfMonth = Preferences.getCurrentDate()[4]; // this is the number of days in the current month
                final int xAxisMinMonth = diffDays-dayOfMonth+1;
                xAxis.setAxisMinimum(xAxisMinMonth);
                xAxis.setAxisMaximum((diffDays-dayOfMonth)+lengthOfMonth);
                // then we change the labels of the x axis
                // this way we can set the array for the month we are about to draw
                final ArrayList<String> xAxisLabelMonth = daysInYear(Preferences.getCurrentDate()[2]);
                // this formats the values to be the new ones we just created :
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        System.out.println("value : "+value);
                        return xAxisLabelMonth.get((int) value);
                    }
                });
                dataSet.setDrawCircles(true);
                graph.invalidate();
                break;
            case 2 :
                // in the case that we want to show the month :
                // first we change the range of the x axis
                xAxis.setAxisMinimum(1);
                xAxis.setAxisMaximum(values.size());
                // then we change the labels of the x axis
                final ArrayList<String> xAxisLabelYear = new ArrayList<>();
                xAxisLabelYear.add("Jan");
                xAxisLabelYear.add("Fev");
                xAxisLabelYear.add("Mar");
                xAxisLabelYear.add("Avr");
                xAxisLabelYear.add("Mai");
                xAxisLabelYear.add("Jui");
                xAxisLabelYear.add("Jui");
                xAxisLabelYear.add("Aou");
                xAxisLabelYear.add("Sep");
                xAxisLabelYear.add("Oct");
                xAxisLabelYear.add("Nov");
                xAxisLabelYear.add("Dec");
                // this formats the values to be the new ones we just created :
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int month = getMonthFromDay((int)value);
                        return xAxisLabelYear.get(month);
                    }
                });
                // the circles make the graph illegible in the case of the yearly graph so we disable them
                dataSet.setDrawCircles(false);
                graph.invalidate();
                break;
        }

        // LineData allows for styling of the whole chart
        LineData lineData = new LineData(dataSet);

        //Set description non visible
        Description description = graph.getDescription();
        description.setEnabled(false);

        // Apply our data to the chart
        graph.setData(lineData);

        // refresh
        graph.invalidate();

        slideLeft = findViewById(R.id.slide_left);
        slideRight = findViewById(R.id.slide_right);

        slideLeft.setTag(110);
        slideRight.setTag(111);

        View.OnClickListener onSlideClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag();
                // first we check whether they are enabled or not
                if(xAxis.getAxisMinimum()<=1){
                    slideLeft.setEnabled(false);
                    switch (range) {
                        case 0 :
                            if (i==111) {
                                xAxis.setAxisMinimum(xAxis.getAxisMinimum() + 7);
                                xAxis.setAxisMaximum(xAxis.getAxisMaximum() + 7);
                                graph.fitScreen();
                                graph.setVisibleXRangeMaximum(6);
                                graph.moveViewToX(xAxis.getAxisMinimum());
                            }
                            break;
                        case 1 :
                            if(i==111) {
                                // We have to obtain the number of days in the months surrounding the current month, in order to move the minimum and maximum accordingly
                                int min = (int) xAxis.getAxisMinimum();
                                int month = getMonthFromDay(min); // this tells us which month is currently drawn on the graph
                                int[] monthDrawn = {1, month + 1, Preferences.getCurrentDate()[2]};
                                int[] monthNext = new int[]{monthDrawn[0], monthDrawn[1] + 1, monthDrawn[2]}; // same but with the next month
                                // then we get the length of each month
                                int lengthDrawn = nbOfDaysInMonth(monthDrawn);
                                int lengthNext = nbOfDaysInMonth(monthNext);
                                // then we move the minimum and maximum
                                xAxis.setAxisMinimum(xAxis.getAxisMinimum() + lengthDrawn);
                                xAxis.setAxisMaximum(xAxis.getAxisMaximum() + lengthNext);
                                graph.fitScreen();
                                graph.setVisibleXRangeMaximum(lengthNext); // setting the range according to whether we went back or forward
                                graph.moveViewToX(xAxis.getAxisMinimum());
                            }
                            break;
                        case 2 :
                            break;
                    }
                }
                else if(xAxis.getAxisMaximum()>=365){
                    slideRight.setEnabled(false);
                    switch (range) {
                        case 0 :
                            if (i==110) {
                                xAxis.setAxisMinimum(xAxis.getAxisMinimum() - 7);
                                xAxis.setAxisMaximum(xAxis.getAxisMaximum() - 7);
                                graph.fitScreen();
                                graph.setVisibleXRangeMaximum(6);
                                graph.moveViewToX(xAxis.getAxisMinimum());
                            }
                            break;
                        case 1 :
                            if(i==110) {
                                // We have to obtain the number of days in the months surrounding the current month, in order to move the minimum and maximum accordingly
                                int min = (int) xAxis.getAxisMinimum();
                                int month = getMonthFromDay(min); // this tells us which month is currently drawn on the graph
                                int[] monthDrawn = {1, month + 1, Preferences.getCurrentDate()[2]};
                                int[] monthLast = new int[]{monthDrawn[0], monthDrawn[1] - 1, monthDrawn[2]}; // same but with the next month
                                // then we get the length of each month
                                int lengthDrawn = nbOfDaysInMonth(monthDrawn);
                                int lengthLast = nbOfDaysInMonth(monthLast);
                                // then we move the minimum and maximum
                                xAxis.setAxisMinimum(xAxis.getAxisMinimum() - lengthDrawn);
                                xAxis.setAxisMaximum(xAxis.getAxisMaximum() - lengthLast);
                                graph.fitScreen();
                                graph.setVisibleXRangeMaximum(lengthLast); // setting the range according to whether we went back or forward
                                graph.moveViewToX(xAxis.getAxisMinimum());
                            }
                            break;
                        case 2 :
                            break;
                    }
                }
                else {
                    slideLeft.setEnabled(true);
                    slideRight.setEnabled(true);
                    // then we move the maximums and minimums
                    switch (range) {
                        case 0 :
                            xAxis.setAxisMinimum(i==110?xAxis.getAxisMinimum()-7:xAxis.getAxisMinimum()+7);
                            xAxis.setAxisMaximum(i==110?xAxis.getAxisMaximum()-7:xAxis.getAxisMaximum()+7);
                            graph.fitScreen();
                            graph.setVisibleXRangeMaximum(6);
                            graph.moveViewToX(xAxis.getAxisMinimum());
                            break;
                        case 1 :
                            System.out.println(slideLeft.isEnabled());
                            // We have to obtain the number of days in the months surrounding the current month, in order to move the minimum and maximum accordingly
                            int min = (int) xAxis.getAxisMinimum();
                            int month = getMonthFromDay(min); // this tells us which month is currently drawn on the graph
                            int[] monthDrawn = {1,month+1,Preferences.getCurrentDate()[2]};
                            int[] monthLast = new int[]{monthDrawn[0],monthDrawn[1]-1,monthDrawn[2]}; // we go back one month, that way we know how many days are in the previous month
                            int[] monthNext = new int[]{monthDrawn[0],monthDrawn[1]+1,monthDrawn[2]}; // same but with the next month
                            // then we get the length of each month
                            int lengthDrawn = nbOfDaysInMonth(monthDrawn);
                            int lengthLast = nbOfDaysInMonth(monthLast);
                            int lengthNext = nbOfDaysInMonth(monthNext);
                            // then we move the minimum and maximum
                            xAxis.setAxisMinimum(i==110?xAxis.getAxisMinimum()-lengthLast:xAxis.getAxisMinimum()+lengthDrawn);
                            xAxis.setAxisMaximum(i==110?xAxis.getAxisMaximum()-lengthDrawn:xAxis.getAxisMaximum()+lengthNext);
                            graph.fitScreen();
                            graph.setVisibleXRangeMaximum(i==110?lengthLast:lengthNext); // setting the range according to whether we went back or forward
                            graph.moveViewToX(xAxis.getAxisMinimum());
                            break;
                        case 2 :
                            break;
                    }
                }
            }
        };

        slideLeft.setOnClickListener(onSlideClick);
        slideRight.setOnClickListener(onSlideClick);

    }

    /**
     * Returns number of days in the month of the required date
     * @param date
     * @return
     */
    public int nbOfDaysInMonth(int[] date){
        Calendar cal = new GregorianCalendar(date[2],date[1]-1,date[0]);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns an array of all the dates in the year
     * Used for the labels on the "month" graph
     * @param year
     * @return
     */
    public ArrayList<String> daysInYear(int year){
        Calendar cal = new GregorianCalendar(year,0,0);
        ArrayList<String> days = new ArrayList<>();
        for (int i =0;i<365;i++){
            days.add(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            cal.add(Calendar.DATE,1);
            System.out.println(days.get(i));
        }
        return days;
    }

    public int getMonthFromDay(int day){
        if (day<=31){
            return 0;
        }
        else if (day<=59){
            return 1;
        }
        else if (day<=90){
            return 2;
        }
        else if (day<=120){
            return 3;
        }
        else if (day<=151){
            return 4;
        }
        else if (day<=181){
            return 5;
        }
        else if (day<=212){
            return 6;
        }
        else if (day<=243){
            return 7;
        }
        else if (day<=273){
            return 8;
        }
        else if (day<=304){
            return 9;
        }
        else if (day<=335){
            return 10;
        }
        else {
            return 11;
        }
    }





}