package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
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
    private PopupWindow popupWindow;

    // addresses
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_favorite_adresses,null);

        // create the popup window
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup dismiss it

        popupWindow = new PopupWindow(this);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setFocusable(focusable);


        //remove background dimness when dismissed
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dim_popup.setVisibility(View.INVISIBLE);
            }
        });

        //link elements from popup window
        addButton = popupView.findViewById(R.id.button_add);
        enterAddress = popupView.findViewById(R.id.enter_adress);
        address1 = popupView.findViewById(R.id.adress1);
        address2 = popupView.findViewById(R.id.adress2);
        address3 = popupView.findViewById(R.id.adress3);
        address4 = popupView.findViewById(R.id.adress4);
        address5 = popupView.findViewById(R.id.adress5);
        remove1 = popupView.findViewById(R.id.remove_btn_1);
        remove2 = popupView.findViewById(R.id.remove_btn_2);
        remove3 = popupView.findViewById(R.id.remove_btn_3);
        remove4 = popupView.findViewById(R.id.remove_btn_4);
        remove5 = popupView.findViewById(R.id.remove_btn_5);

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

        View.OnClickListener onCLickRemove = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) v.getTag();
                popupView.findViewWithTag(i-5).setVisibility(View.GONE);
                popupView.findViewWithTag(i).setVisibility(View.GONE);
                Preferences.removeValue("Address",i-5,ProfileActivity.this);
            }
        };

        remove1.setOnClickListener(onCLickRemove);
        remove2.setOnClickListener(onCLickRemove);
        remove3.setOnClickListener(onCLickRemove);
        remove4.setOnClickListener(onCLickRemove);
        remove5.setOnClickListener(onCLickRemove);


        TextView address = new TextView(this);
        ImageButton remove = new ImageButton(this);
        if (Preferences.getNumberOfAddresses("Address",this)!=0) {
            for (int i = 0; i < Preferences.getNumberOfAddresses("Address", this); i++) {
                address = popupView.findViewWithTag(i);
                remove = popupView.findViewWithTag(i+5);
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
                    Toast.makeText(ProfileActivity.this, "Veuillez rentrer une adresse", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (Preferences.getNumberOfAddresses("Address",ProfileActivity.this)>=5){
                        Toast.makeText(ProfileActivity.this,"Vous ne pouvez pas ajouter plus de 5 adresses",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        int addressIndex = Preferences.getNumberOfAddresses("Address", ProfileActivity.this);
                        Preferences.addValue("Address",addressIndex,enterAddress.getText().toString(),ProfileActivity.this);
                        TextView newAddress = popupView.findViewWithTag(addressIndex);
                        ImageButton newButton = popupView.findViewWithTag(addressIndex + 5);
                        newAddress.setVisibility(View.VISIBLE);
                        newAddress.setText(enterAddress.getText().toString());
                        newButton.setVisibility(View.VISIBLE);
                        enterAddress.setText("");
                    }
                }
            }

        });



        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
    }

    @Override
    public void onClick(View v){
        int buttonClicked = (int) v.getTag();
        switch (buttonClicked) {
            case 0:
            //buttonView=R.layout.popup_sensibility;
            case 1:
            // dim background of popup
            dim_popup.setVisibility(View.VISIBLE);
            // show the popup window
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            case 2:
            //buttonView=R.layout.popup_favorite_transportation;
        }


    }

}





