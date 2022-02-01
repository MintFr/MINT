package com.example.mint.model;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.TextView;

import com.example.mint.R;

public class CustomPopup extends Dialog {

    private String title;
    private String subTitle;
    private Button closeButton;
    private TextView titleView;
    private TextView subTitleView;

   //constructors
   public CustomPopup(Activity activity)
   {
       super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
       setContentView(R.layout.popup_pollen);
       this.title = "Alerte Pollen";
       this.subTitle = "Alerte pollen au niveau 4";
       this.titleView = findViewById(R.id.pollen_alert_title);
       //this.titleView = findViewById(R.id.pollen_alert2);

   }
   public void setTitle(String title)
   {this.title = title;
   }

    public void setSubTitle(String title) {
       this.subTitle = subTitle;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public void build(){
       show();
       titleView.setText(title);
       subTitleView.setText(subTitle);
    }
}
