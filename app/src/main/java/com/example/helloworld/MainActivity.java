package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map;
    IMapController mapController;
    private EditText startPoint;
    private EditText endPoint;
    private int POSITION_PERMISSION_CODE = 1;

    EditText buttonClicked;
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

        // add location button to the list
        TextView localisationRequest = new TextView(this);
        localisationRequest.setText("Ma position");
        localisationRequest.setTextColor(getResources().getColor(R.color.colorAccent));
        localisationRequest.setPadding(30,30,30,0);
        addressListView.addHeaderView(localisationRequest);

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

        //User's position
        localisationRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the permission is already allowed, we use the user's position
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startPoint.setText("Ma position");
                    startPoint.setSelection(startPoint.length()); // set cursor at end of text
                    popUp.dismiss();
                }
                // If not, we ask the permission to use his position
                else {
                    requestLocalisationPermission();
                }
            }
        });

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(1000);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_bg_popup));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the listview as popup content
        popupWindow.setContentView(addressListView);

        return popupWindow;
    }

    // Ask the permission to the user to use his geolocalisation
    private void requestLocalisationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Autorisation nécessaire")
                    .setMessage("Nous avons besoin de votre autorisation pour utiliser votre géolocalisation.")
                    .setPositiveButton("autoriser", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                                    Manifest.permission.ACCESS_FINE_LOCATION}, POSITION_PERMISSION_CODE);
                            startPoint.setText("Ma position");
                            startPoint.setSelection(startPoint.length()); // set cursor at end of text
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
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION}, POSITION_PERMISSION_CODE);
        }
    }

    // Return the answer of the localisation permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == POSITION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Autorisation ACCORDÉE", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Autorisation REFUSÉE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id>=0) {
                    buttonClicked.setText(Preferences.getPrefAddresses("Address", MainActivity.this).get((int) id));
                    buttonClicked.setSelection(buttonClicked.length()); // set cursor at end of text
                    popUp.dismiss();
                }
            }
        };
    }

    @Override
    public void onClick(View v){
        int i = (int) v.getTag();
        buttonClicked = v.findViewWithTag(i);
        popUp = showFavoriteAddresses();
        popUp.showAsDropDown(v, 0, 0); // show popup like dropdown list
    }
}
