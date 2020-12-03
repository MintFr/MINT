package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sensibilityButton;
    private Button favoriteAdressesButton;
    private Button favoriteTransportationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //link layout elements to activity
        sensibilityButton = findViewById(R.id.sensibility);
        favoriteAdressesButton = findViewById(R.id.favorite_adresses);
        favoriteTransportationButton = findViewById(R.id.favorite_transportation);

        // set tags to know which button is pressed when launching onClick
        sensibilityButton.setTag(0);
        favoriteAdressesButton.setTag(1);
        favoriteTransportationButton.setTag(2);

        //launches "onClick" when one button is clicked
        sensibilityButton.setOnClickListener(this);
        favoriteAdressesButton.setOnClickListener(this);
        favoriteTransportationButton.setOnClickListener(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
    }

    @Override
    public void onClick(View v){
        int buttonView; // indicates which view to display according to button
        int buttonClicked = (int) v.getTag();
        if (buttonClicked==0){
            buttonView=R.layout.popup_sensibility;
        }
        else if (buttonClicked==1){
            buttonView=R.layout.popup_favorite_adresses;
        }
        else{
            buttonView=R.layout.popup_favorite_transportation;
        }

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(buttonView, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

    }

}
