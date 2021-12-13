package com.example.mint.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;

public class TermsOfUseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);
    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }
}