package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sensibilityButton;
    private Button favoriteAdressesButton;
    private Button favoriteTransportationButton;

    private View dim_popup;

    private Button addButton;
    private EditText enterAddress;
    private String addedAddress;
    private TextView address1;

    private int buttonView;
    private PopupWindow popupWindow;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferences = getPreferences(MODE_PRIVATE);

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

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_favorite_adresses,null);

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

        addButton = popupView.findViewById(R.id.button_add);
        enterAddress = popupView.findViewById(R.id.enter_adress);
        address1 = popupView.findViewById(R.id.adress1);


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
                    address1.setText(enterAddress.getText().toString());
                    enterAddress.setText("");
                }
            }

        });



        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
    }

    @Override
    public void onClick(View v){
        int buttonClicked = (int) v.getTag();
        // dim background of popup
        dim_popup.setVisibility(View.VISIBLE);

        if (buttonClicked==0){
            //buttonView=R.layout.popup_sensibility;
        }
        else if (buttonClicked==1){
            // show the popup window
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
        else{
            //buttonView=R.layout.popup_favorite_transportation;
        }

    }

}





