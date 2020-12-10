package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sensibilityButton;
    private Button favoriteAdressesButton;
    private Button favoriteTransportationButton;

    private View dim_popup;

    private int buttonView;
    private PopupWindow addressPopupWindow;
    private PopupWindow sensibilityPopupWindow;
    private PopupWindow transportationPopupWindow;

    // addresses //
    private Button addButton;
    private EditText enterAddress;
    private String addedAddress;
    private TextView address1;
    private TextView address2;
    private TextView address3;
    private TextView address4;
    private TextView address5;
    private ImageButton remove1;
    private ImageButton remove2;
    private ImageButton remove3;
    private ImageButton remove4;
    private ImageButton remove5;

    // sensibility //
    private Button veryHighBtn;
    private Button highBtn;
    private Button moderateBtn;
    private Button lowBtn;
    private Button noSensibilityBtn;
    private TextView setSensibility;

    int activated =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Preferences.clearAddresses(this);
        //Preferences.clearSensibility(this);

        //link layout elements to activity
        sensibilityButton = findViewById(R.id.sensibility);
        favoriteAdressesButton = findViewById(R.id.favorite_adresses);
        favoriteTransportationButton = findViewById(R.id.favorite_transportation);

        dim_popup = findViewById(R.id.dim_popup);

        // set tags to know which button is pressed when launching onClick
        sensibilityButton.setTag(0);
        favoriteAdressesButton.setTag(1);
        favoriteTransportationButton.setTag(2);

        //launches "onClick" when one button is clicked
        sensibilityButton.setOnClickListener(this);
        favoriteAdressesButton.setOnClickListener(this);
        favoriteTransportationButton.setOnClickListener(this);

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
        boolean focusable = true; // lets taps outside the popup dismiss it
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
        enterAddress = addressPopupView.findViewById(R.id.enter_adress);
        address1 = addressPopupView.findViewById(R.id.adress1);
        address2 = addressPopupView.findViewById(R.id.adress2);
        address3 = addressPopupView.findViewById(R.id.adress3);
        address4 = addressPopupView.findViewById(R.id.adress4);
        address5 = addressPopupView.findViewById(R.id.adress5);
        remove1 = addressPopupView.findViewById(R.id.remove_btn_1);
        remove2 = addressPopupView.findViewById(R.id.remove_btn_2);
        remove3 = addressPopupView.findViewById(R.id.remove_btn_3);
        remove4 = addressPopupView.findViewById(R.id.remove_btn_4);
        remove5 = addressPopupView.findViewById(R.id.remove_btn_5);

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

        sensibilityPopupWindow = new PopupWindow(this);
        sensibilityPopupWindow.setBackgroundDrawable(null);
        sensibilityPopupWindow.setContentView(sensibilityPopupView);
        sensibilityPopupWindow.setWidth(width);
        sensibilityPopupWindow.setHeight(height);
        sensibilityPopupWindow.setFocusable(focusable);

        //link elements from popup window
        veryHighBtn = sensibilityPopupView.findViewById(R.id.very_high_sensibility_btn);
        highBtn = sensibilityPopupView.findViewById(R.id.high_sensibility_btn);
        moderateBtn = sensibilityPopupView.findViewById(R.id.moderate_sensibility_btn);
        lowBtn = sensibilityPopupView.findViewById(R.id.low_sensibility_btn);
        noSensibilityBtn = sensibilityPopupView.findViewById(R.id.no_sensibility_btn);
        setSensibility = findViewById(R.id.set_sensibility);

        // set Tags to use in onClick
        veryHighBtn.setTag(10);
        highBtn.setTag(11);
        moderateBtn.setTag(12);
        lowBtn.setTag(13);
        noSensibilityBtn.setTag(14);

        setSensibility.setText(Preferences.getSensibility("Sensibility",this));

        // Highlight the sensibility if it has already been selected
        Button selectedButton = new Button(this);
        for (int i=10; i<=14; i++){
            selectedButton=sensibilityPopupView.findViewWithTag(i);
            if (selectedButton.getText().toString().equals(Preferences.getSensibility("Sensibility", this))){
                selectedButton.setActivated(true);
            }
        }


        // Highlight the sensibility that is clicked
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
                            sensibilityPopupView.findViewWithTag(j).setActivated(false);
                        }
                    }
                    sensibilityPopupWindow.dismiss(); // Remove popup
                }
                else {
                    Preferences.removeSensibility("Sensibility", ProfileActivity.this);
                }
            }

        };

        veryHighBtn.setOnClickListener(onClickSelect);
        highBtn.setOnClickListener(onClickSelect);
        moderateBtn.setOnClickListener(onClickSelect);
        lowBtn.setOnClickListener(onClickSelect);
        noSensibilityBtn.setOnClickListener(onClickSelect);

        //remove background dimness when popup is dismissed
        sensibilityPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dim_popup.setVisibility(View.INVISIBLE);
                if(!setSensibility.getText().toString().equals(Preferences.getSensibility("Sensibility", ProfileActivity.this))){
                    setSensibility.setText(Preferences.getSensibility("Sensibility",ProfileActivity.this));
                }
            }
        });

        /////////////////////////////////////////////////////////
        // SENSIBILITY POPUP END //
        /////////////////////////////////////////////////////////

        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
    }

    @Override
    public void onClick(View v){
        int buttonClicked = (int) v.getTag();
        // dim background of popup
        dim_popup.setVisibility(View.VISIBLE);
        switch (buttonClicked) {
            case 0:
                // show the popup window
                sensibilityPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case 1:
                // show the popup window
                addressPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case 2:
                //buttonView=R.layout.popup_favorite_transportation;
                break;
        }


    }

}