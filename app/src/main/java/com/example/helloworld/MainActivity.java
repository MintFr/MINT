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
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map;
    IMapController mapController;
    private EditText startPoint;
    private EditText endPoint;
    private Button search;
    private int POSITION_PERMISSION_CODE = 1;

    ArrayList<String> lastAddressList;
    ArrayList<String> addressList;
    ListView addressListView;
    String start;
    String end;

    EditText buttonClicked;
    PopupWindow popUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Preferences.clearLastAddresses(this);

        startPoint = findViewById(R.id.PointDeDepart);
        endPoint = findViewById(R.id.PointDarrivee);
        search = findViewById(R.id.recherche);

        start = startPoint.getText().toString();
        end = endPoint.getText().toString();

        // check if the editText is empty and if so disable add button
        TextWatcher textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search.setEnabled(s.toString().length()!=0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        startPoint.setOnFocusChangeListener(this);
        startPoint.addTextChangedListener(textChangedListener);
        endPoint.setOnFocusChangeListener(this);
        endPoint.addTextChangedListener(textChangedListener);
        search.setOnClickListener(this);

        startPoint.setTag(0);
        endPoint.setTag(1);
        search.setTag(2);

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
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    private PopupWindow showFavoriteAddresses() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        lastAddressList = Preferences.getLastAddresses("lastAddress",this);
        addressList = Preferences.getPrefAddresses("Address", this);
        lastAddressList.add(0,"Mes dernières adresses :");
        addressList.add(0,"Mes adresses favorites :");

        addressList.addAll(0,lastAddressList);

        CustomListAdapter adapter = new CustomListAdapter(this, addressList);

        addressListView = new ListView(this);
        // add location button to the list
        TextView localisationRequest = new TextView(this);
        localisationRequest.setText(R.string.position_request);
        localisationRequest.setPadding(30,30,30,0);
        addressListView.addHeaderView(localisationRequest);
        addressListView.setHeaderDividersEnabled(false);

        // set our adapter and pass our pop up window contents
        addressListView.setAdapter(adapter);
        addressListView.setDivider(null);
        addressListView.setDividerHeight(0);

        // set on item selected
        addressListView.setOnItemClickListener(onItemClickListener());

        //User's position
        localisationRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the permission is already allowed, we use the user's position
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    buttonClicked.setText("Ma position");
                    buttonClicked.setSelection(buttonClicked.length()); // set cursor at end of text
                    popUp.dismiss();
                }
                // If not, we ask the permission to use his position
                else {
                    requestLocalisationPermission();
                }
            }
        });

        // some other visual settings for popup window
        popupWindow.setFocusable(false);
        popupWindow.setWidth((int)getResources().getDimension(R.dimen.start_point_width));
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
                            buttonClicked.setText("Ma position");
                            buttonClicked.setSelection(buttonClicked.length()); // set cursor at end of text
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

    // Callback when the user clicks on an item in the listView
    private AdapterView.OnItemClickListener onItemClickListener(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id>=0) {
                    buttonClicked.setText(addressList.get((int)id));
                    buttonClicked.setSelection(buttonClicked.length()); // set cursor at end of text
                    popUp.dismiss();
                }
            }
        };
    }

    // Method called when the user clicks on "search" (tag = 2)
    @Override
    public void onClick(View v){
        int i = (int) v.getTag();
        start = startPoint.getText().toString();
        end = endPoint.getText().toString();
        if(i==2){
            if (start.length() == 0 || end.length() == 0){
                // if nothing has been typed in, nothing happens and you get a message
                Toast.makeText(MainActivity.this, "Vous devez remplir les deux champs",Toast.LENGTH_SHORT).show();
            }
            else if (start.equals(end)){
                // if both addresses are the same, do nothing
                Toast.makeText(MainActivity.this, "Veuillez rentrer deux adresses différentes",Toast.LENGTH_SHORT).show();
            }
            else {
                int nbLastAdd = Preferences.getNumberOfLastAddresses("lastAddress",MainActivity.this); // get the number of addresses in the history
                int[] sameAddresses = getSameAddresses(start,end);
                if (sameAddresses[0]==-1&&sameAddresses[1]==-1) {
                    Preferences.addLastAddress("lastAddress", 0, end, MainActivity.this);
                    Preferences.addLastAddress("lastAddress", 0, start, MainActivity.this);
                    nbLastAdd = nbLastAdd + 2;
                }
                else if (sameAddresses[0]!=-1&&sameAddresses[1]==-1){
                    Preferences.addLastAddress("lastAddress", 0, end, MainActivity.this);
                    Preferences.moveAddressFirst(sameAddresses[0]+1,MainActivity.this);
                    nbLastAdd++;
                }
                else if (sameAddresses[0]==-1&&sameAddresses[1]!=-1) {
                    Preferences.moveAddressFirst(sameAddresses[1],MainActivity.this);
                    Preferences.addLastAddress("lastAddress", 0, start, MainActivity.this);
                    nbLastAdd++;
                }
                else {
                    Preferences.moveAddressFirst(sameAddresses[1], MainActivity.this);
                    Preferences.moveAddressFirst(sameAddresses[1]<sameAddresses[0]?sameAddresses[0]:sameAddresses[0]+1, MainActivity.this);
                }
                // check if number of addresses has gone over 3 and remove the ones over 3
                if (nbLastAdd == 5) {
                    Preferences.removeLastAddress("lastAddress", nbLastAdd + 1, MainActivity.this);
                    Preferences.removeLastAddress("lastAddress", nbLastAdd, MainActivity.this);
                } else if (nbLastAdd == 4) {
                    Preferences.removeLastAddress("lastAddress", nbLastAdd, MainActivity.this);
                }
            }
        }
    }

    // when the focus is on the edittext, display popupWindow, when the edittext loses focus, dismiss popupWindow
    @Override
    public void onFocusChange(View v, boolean hasFocus){
        int i = (int) v.getTag();
        buttonClicked = v.findViewWithTag(i);
        if(hasFocus) {
            popUp = showFavoriteAddresses();
            popUp.showAsDropDown(v, 0, 10); // show popup like dropdown list
        }
        if(!hasFocus){
            popUp.dismiss();
        }
    }

    // This is used to check when the user clicks outside of the edittext
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    public int[] getSameAddresses(String start, String end){
        int[] arr = new int[2];
        arr[0]=-1;
        arr[1]=-1;
        for (int j = 0; j < Preferences.getNumberOfLastAddresses("lastAddress",MainActivity.this); j++) {
            String lastAddress = Preferences.getLastAddresses("lastAddress", MainActivity.this).get(j);
            if (start.equals(lastAddress)) {
                arr[0]=j;
            }
            else if (end.equals(lastAddress)) {
                arr[1]=j;
            }
        }
        return arr;
    }
}
