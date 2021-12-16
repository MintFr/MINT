package com.example.mint.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;

import java.lang.reflect.Field;
import java.util.Arrays;

public class FaqActivity extends AppCompatActivity implements View.OnClickListener {
    String mAnswer;
    String mQuestion;
    boolean isExpanded;
    TextView textView;
    ImageButton imageButton;

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /*
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
            }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

    }

    @Override
    public void onClick(View v) {
        // Id of clicked button (int)
        int idButton = v.getId();
        // String of the id
        String name = getResources().getResourceEntryName(idButton);
        String[] names = name.split("_");
        Log.d("FAQ_class", Arrays.toString(names));
        Log.d("FAQ_class", names[0] + "    " + names[1]);
        String idBeginning = names[0] + "_text";


        // Getting id of the text view from this button number
        int idQuestionFromButton = getResources().getIdentifier(idBeginning, "id", getPackageName());
        textView = findViewById(idQuestionFromButton);
        mQuestion = textView.getText().toString();
        mAnswer = getResources().getString(R.string.lorem_ipsum);

        isExpanded = !isExpanded;
        imageButton.setImageResource(
                isExpanded ?
                        R.drawable.ic_baseline_expand_less_24 :
                        R.drawable.ic_baseline_expand_more_24
        );
        textView.setText(
                isExpanded ?
                        mQuestion + "\n \n" + mAnswer :
                        mQuestion
        );

    }
/*
    @Override
    protected void onStart() {
        super.onStart();
        imageButton.setOnClickListener(this);
    }
 */

    public void onClickBackButton(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}