package com.example.mint.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    /**
     * This activity handles the various application settings (FAQ, language...)
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Bottom Menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new MenuSwitcherActivity(this));
        bottomNav.setItemIconTintList(null);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
    }

    /**
     * Method applying when user clicks on "Mentions légales". Launches the TermsofUse Activity.
     *
     * @param view
     */
    public void onClickTermsOfUse(View view) {
        Intent intent = new Intent(this, TermsOfUseActivity.class);
        startActivity(intent);
    }

    /**
     * Method applying when user clicks on "FAQ". Launches the FAQActivity.
     *
     * @param view
     */
    public void onClickFAQ(View view) {
        Intent intent = new Intent(this, FaqActivity.class);
        startActivity(intent);
    }
  
  
    /**
     * Method applying when user clicks on "Mentions légales". Launches the LegalNotices Activity.
     *
     * @param view
     */
    public void onClickLegalNotices(View view) {
        Intent intent = new Intent(this, LegalNoticesActivity.class);
        startActivity(intent);
    }
}
