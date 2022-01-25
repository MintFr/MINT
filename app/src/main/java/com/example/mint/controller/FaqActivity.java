package com.example.mint.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;

import java.util.ArrayList;

/**
 * Activity of FAQ, where you can drop down a selected question.
 */
public class FaqActivity extends AppCompatActivity implements View.OnClickListener {
    // Nb of questions in the FAQ. Locating in values/called_integers.xml
    int nbQuestions;
    // As we don't know the nb of questions, we use lists to store data
    // Because each button has the same role but for a different question
    ArrayList<String> mAnswers = new ArrayList<>();
    ArrayList<String> mQuestions = new ArrayList<>();
    ArrayList<Boolean> areExpanded = new ArrayList<>();
    ArrayList<TextView> textViews = new ArrayList<>();
    ArrayList<ImageButton> imageButtons = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // We need to fulfill the different arrays with data
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        // Getting from xml
        nbQuestions = getResources().getInteger(R.integer.nbQuestionFAQ);
        // For each question, we add the elements we need in arrays
        for (int i = 0; i < nbQuestions; i++) {
            // To get to the ids which are "faq_answer_1", "question3_button" etc...
            String idAndswer = "faq_answer_" + (i + 1);
            String idQuestion = "question" + (i + 1) + "_text";
            String idButton = "question" + (i + 1) + "_button";
            // Getting corresponding attributes
            mAnswers.add(
                    getResources().getString(
                            getResources().getIdentifier(
                                    idAndswer, "string", getPackageName()
                            )
                    )
            );
            textViews.add(
                    (TextView) findViewById(
                            getResources().getIdentifier(
                                    idQuestion, "id", getPackageName()
                            )
                    )
            );
            mQuestions.add(textViews.get(i).getText().toString());
            ImageButton buttonI = (ImageButton) findViewById(
                    getResources().getIdentifier(
                            idButton, "id", getPackageName()
                    )
            );
            buttonI.setOnClickListener(this);
            imageButtons.add(buttonI);

            // By default questions are not expanded
            areExpanded.add(false);
        }
    }

    /**
     * Method to get the index of the corresponding button clicked with the id
     *
     * @param buttonId:     Id of the corresponding button
     * @param imageButtons: List of all buttons
     * @return: index of the button in the list
     */
    public int getIndiceButton(int buttonId, ArrayList<ImageButton> imageButtons) {
        int res = 0;
        for (int i = 0; i < nbQuestions; i++) {
            ImageButton imageButtoni = imageButtons.get(i);
            // Check if the id corresponds to the current button of the list
            if (buttonId == imageButtoni.getId()) {
                res = i;
                break;
            }
        }
        return res;
    }

    @Override
    public void onClick(View v) {
        // Getting the index of the button clicked in the list
        int buttonId = v.getId();
        int indiceButton = getIndiceButton(buttonId, this.imageButtons);
        // Getting all attributes of the corresponding button
        // By construction, the indexes are equals
        boolean isExpanded = areExpanded.get(indiceButton);
        ImageButton imageButton = imageButtons.get(indiceButton);
        TextView textView = textViews.get(indiceButton);
        String mQuestion = mQuestions.get(indiceButton);
        String mAnswer = mAnswers.get(indiceButton);

        // When clicked, the button switches is expansion, to call it back
        isExpanded = !isExpanded;
        // The icon of the button changes : expand more if not expanded and conversely
        imageButton.setImageResource(
                isExpanded
                        ? R.drawable.ic_baseline_expand_less_24
                        : R.drawable.ic_baseline_expand_more_24
        );
        // If expanded we print the question and the answer. Otherwise only the question
        textView.setText(
                isExpanded
                        ? mQuestion + "\n \n" + mAnswer + "\n \n"
                        : mQuestion
        );

        // Modifying attributes of the list
        areExpanded.set(indiceButton, isExpanded);
        textViews.set(indiceButton, textView);

    }

    /**
     * When user wants to go back to settings activity
     */
    public void onClickBackButton(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}