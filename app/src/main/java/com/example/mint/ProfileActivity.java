package com.example.mint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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


    // TODO javadoc comments
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
        resetPollutionNewDay(); // check whether a new day has started and if so reset pollution to 0 and store the last value
        setPollutionToday(); // Get the pollution from the last itineraries and changes the pollution from today accordingly

        // handle the graph
        // THIS IS A TEST WITH RANDOM NUMBERS TO SEE IF DISPLAY WORKS CORRECTLY
        ArrayList<Integer> valuesTest = new ArrayList<>();
        for (int j=0;j<31;j++){
            valuesTest.add((int) (Math.random() * 100));
        }
        Preferences.setPollutionMonth(1,valuesTest,this);
        graph = findViewById(R.id.chart);
        setUpGraph();

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
        // SLIDE ANIMATION //
        /////////////////////////////////////////////////////////

        bottomNav.setSelectedItemId(R.id.profile);

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

    }

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
        int pollution = Preferences.getPollutionToday(this);
        int lastPol = Preferences.getLastPollution(this);
        System.out.println(lastPol);
        pollution+=lastPol;
        Preferences.setPollutionToday(pollution,this);
        pollutionToday.setText(Integer.toString(pollution));
    }

    /**
     * what this function does is check whether we have started a new day or not everytime we open the profile activity
     * If so, we save the new day value in the preferences, to be used as a comparison for the next time this function is called
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
     */
    private void setUpGraph(){
        // MONTH GRAPH

        // we get the pollution data from preferences
        ArrayList<Integer> values = Preferences.getPollutionMonth(1,this);

        // we convert it a list of "entries" which is a class from the MPAndroidChart library
        List<Entry> entries = new ArrayList<>();
        for (int i=0;i<values.size();i++){
            entries.add(new Entry(i+1,values.get(i)));
        }

        // LineDataSet allows for individual styling of this data (for if we have several data sets)
        LineDataSet dataSet = new LineDataSet(entries,"Janvier");

        // LineData allows for styling of the whole chart
        LineData lineData = new LineData(dataSet);

        // Apply our data to the chart
        graph.setData(lineData);

        // refresh
        graph.invalidate();
    }




}