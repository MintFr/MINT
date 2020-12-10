package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map;
    IMapController mapController;
    private EditText startPoint;
    private EditText endPoint;

    int buttonClicked;
    PopupWindow popUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startPoint = findViewById(R.id.PointDeDepart);
        endPoint = findViewById(R.id.PointDarrivee);

        startPoint.setOnClickListener(this);
        endPoint.setOnClickListener(this);

        startPoint.setTag(0);
        endPoint.setTag(1);

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
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    private PopupWindow showFavoriteAddresses() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        ArrayList<String> addressList = Preferences.getPrefAddresses("Address", this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item,
                addressList);
        // the drop down list is a list view
        ListView addressListView = new ListView(this);

        // add title to the list
        TextView title = new TextView(this);
        title.setText("Mes adresses favorites");
        title.setTextColor(getResources().getColor(R.color.colorAccent));
        title.setPadding(30,30,30,0);
        addressListView.addHeaderView(title);
        addressListView.setHeaderDividersEnabled(false);

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
                if (id>=0) {
                    switch (buttonClicked) {
                        case 0 :
                            startPoint.setText(Preferences.getPrefAddresses("Address", MainActivity.this).get((int) id));
                            startPoint.setSelection(startPoint.length()); // set cursor at end of text
                            popUp.dismiss(); //
                            break;
                        case 1 :
                            endPoint.setText(Preferences.getPrefAddresses("Address", MainActivity.this).get((int) id));
                            endPoint.setSelection(startPoint.length()); // set cursor at end of text
                            popUp.dismiss();
                            break;
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View v){
        buttonClicked = (int) v.getTag();
        popUp = showFavoriteAddresses();
        popUp.showAsDropDown(v, 0, 0); // show popup like dropdown list
    }
}
