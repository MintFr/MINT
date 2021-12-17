package com.example.mint.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;

import java.util.ArrayList;

public class FaqActivity extends AppCompatActivity implements View.OnClickListener {
    int nbQuestions;
    ArrayList<String> mAnswers = new ArrayList<>();
    ArrayList<String> mQuestions = new ArrayList<>();
    ArrayList<Boolean> areExpanded = new ArrayList<>();
    ArrayList<TextView> textViews = new ArrayList<>();
    ArrayList<ImageButton> imageButtons = new ArrayList<>();

/*

    mAnswer2 = getResources().getString(R.string.lorem_ipsum);
    textView2 = (TextView) findViewById(R.id.question2_text);
    mQuestion2 = textView2.getText().toString();
    imageButton2 = (ImageButton) findViewById(R.id.question2_button);

    mAnswer3 = getResources().getString(R.string.lorem_ipsum);
    textView3= (TextView) findViewById(R.id.question3_text);
    mQuestion3 = textView3.getText().toString();
    imageButton3 = (ImageButton) findViewById(R.id.question3_button);


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
        nbQuestions = getResources().getInteger(R.integer.nbQuestionFAQ);
        for (int i=0; i<nbQuestions; i++){
            mAnswers.add(getResources().getString(R.string.lorem_ipsum));
            textViews.add((TextView) findViewById(R.id.question1_text));
            mQuestions.add(textViews.get(i).getText().toString());
            ImageButton buttonI = (ImageButton) findViewById(R.id.question1_button);
            buttonI.setOnClickListener(this);
            imageButtons.add(buttonI);

            areExpanded.add(false);
        }
    }

    public int getIndiceButton(int buttonId, ArrayList<ImageButton> imageButtons){
        int res=0;
        for (int i=0; i<nbQuestions; i++){
            ImageButton imageButtoni = imageButtons.get(i);
            if (buttonId == imageButtoni.getId()){
                res=i;
                break;
            }
        }
        return res;
    }

    @Override
    public void onClick(View v) {
        int buttonId = v.getId();
        int indiceButton = getIndiceButton(buttonId, this.imageButtons);
        boolean isExpanded = areExpanded.get(indiceButton);
        ImageButton imageButton = imageButtons.get(indiceButton);
        TextView textView = textViews.get(indiceButton);
        String mQuestion = mQuestions.get(indiceButton);
        String mAnswer = mAnswers.get(indiceButton);

        isExpanded = !isExpanded;
        imageButton.setImageResource(
                isExpanded
                        ? R.drawable.ic_baseline_expand_less_24
                        : R.drawable.ic_baseline_expand_more_24);
        textView.setText(isExpanded ? mQuestion + "\n \n" + mAnswer : mQuestion);

        areExpanded.set(indiceButton, isExpanded);
        textViews.set(indiceButton, textView);

    }






/*

        mQuestion = "";
        mAnswer = "";
        // Id of clicked button (int)
        int idButton = v.getId();
        imageButton = (ImageButton) findViewById(idButton);
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