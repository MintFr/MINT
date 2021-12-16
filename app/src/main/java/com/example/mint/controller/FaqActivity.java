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
    String mQuestion;
    boolean isExpanded;
    TextView textView;
    ImageButton imageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        mAnswer = getResources().getString(R.string.lorem_ipsum);
        textView = (TextView) findViewById(R.id.question1_text);
        mQuestion = textView.getText().toString();
        imageButton = (ImageButton) findViewById(R.id.question1_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpanded = !isExpanded;
                imageButton.setImageResource(
                        isExpanded
                                ? R.drawable.ic_baseline_expand_less_24
                                : R.drawable.ic_baseline_expand_more_24);
                textView.setText(isExpanded ? mQuestion + "\n \n" + mAnswer : mQuestion);
            }
        });
    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}