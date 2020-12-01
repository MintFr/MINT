package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profil:
                        break;

                    case R.id.cartes:
                        Intent intentMap = new Intent(ProfileActivity.this, MapActivity.class);
                        startActivity(intentMap);
                        break;
                    case R.id.itineraire:
                        Intent intentProfil = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intentProfil);
                        break;
                }
                return false;
            }
        });
    }
}