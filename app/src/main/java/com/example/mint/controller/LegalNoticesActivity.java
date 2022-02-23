package com.example.mint.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;
import com.example.mint.model.PreferencesSize;

public class LegalNoticesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sizePolice = PreferencesSize.getSize("police", LegalNoticesActivity.this);
        if (sizePolice.equals("big")) {
            setContentView(R.layout.activity_legal_notices_big);
        } else {
            setContentView(R.layout.activity_legal_notices);
        }
    }

    /**
     * Methods which returns to the previous activity which is Settings Activity.
     *
     * @param view
     */
    public void onClickBackButtonLegal(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }
}