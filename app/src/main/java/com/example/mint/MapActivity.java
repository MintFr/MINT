package com.example.mint;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

/**
 * MapActivity handles the Maps page of the app, letting the user consult various maps of Nantes
 */
public class MapActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    IMapController mapController;

    //private static final String TAG = "MapActivity"; //--> for debugging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_map);
        MapView map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(47.21, -1.55);
        mapController = map.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(startPoint);


        Spinner spinner = (Spinner) findViewById(R.id.map_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.map_array, R.layout.custom_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        /////////////////////////////////////////////////////////
        // BOTTOM MENU //
        /////////////////////////////////////////////////////////
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        /////////////////////////////////////////////////////////
        // BOTTOM MENU END //
        /////////////////////////////////////////////////////////
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
        if(targetActivity == "ProfileActivity"){

            //override the transition and finish the current activity
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //this.finish();
        }

        //For Right-To-Left transitions
        if(targetActivity.equals("com.example.mint.MainActivity") ){

            //override the transition and finish the current activity
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            this.finish();
        }
    }
    /////////////////////////////////////////////////////////
    // BACK BUTTON END //
    /////////////////////////////////////////////////////////

}
