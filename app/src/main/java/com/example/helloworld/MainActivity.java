package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText startPoint;
    private EditText endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startPoint = findViewById(R.id.PointDeDepart);
        startPoint.setOnClickListener(this);
        
        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    private PopupWindow popupWindowsort() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        ArrayList<String> addressList = Preferences.getPrefAddresses("Address", this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                addressList);
        // the drop down list is a list view
        ListView addressListView = new ListView(this);

        // set our adapter and pass our pop up window contents
        addressListView.setAdapter(adapter);

        // set on item selected
        addressListView.setOnItemClickListener(onItemClickListener());

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(1000);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_bg_popup));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the listview as popup content
        popupWindow.setContentView(addressListView);

        return popupWindow;
    }

    private AdapterView.OnItemClickListener onItemClickListener(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startPoint.setText(Preferences.getPrefAddresses("Address",MainActivity.this).get((int)id));
            }
        };
    }

    @Override
    public void onClick(View v){
        PopupWindow popUp = popupWindowsort();
        popUp.showAsDropDown(v, 0, 0); // show popup like dropdown list
    }
}
