package com.example.mint.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;

public class FaqActivity extends AppCompatActivity {
    String mAnswer;
    boolean isExpanded;
    TextView textView;
    ImageButton imageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        mAnswer = getResources().getString(R.string.lorem_ipsum);
        textView = (TextView) findViewById(R.id.expandableTextView);
        imageButton = (ImageButton) findViewById(R.id.expandBtn);

        imageButton.setImageResource(R.drawable.ic_chevron_right);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpanded = ! isExpanded;
                imageButton.setImageResource(isExpanded?R.drawable.ic_chevron_left:R.drawable.ic_chevron_right);
                textView.setText(isExpanded?mAnswer:mAnswer.substring(0,10));
            }
        });
    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}