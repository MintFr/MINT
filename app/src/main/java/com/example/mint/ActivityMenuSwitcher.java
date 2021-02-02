package com.example.mint;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Bottom menu listener that switches to the right activity when a menu item is selected.
 *
 * <p>
 * Usage:
 * <pre>{@code
 * public class MyActivity extends Activity {
 *     ... someFunction(...) {
 *         BottomNavigationView bottomNav = new BottomNavigationView(self);
 *         bottomNav.setOnNavigationItemSelectedListener(new ActivityMenuSwitcher(this));
 *     }
 * }
 * }</pre>
 * </p>
 */
class ActivityMenuSwitcher implements BottomNavigationView.OnNavigationItemSelectedListener {
    private final Activity activity;
    private BottomNavigationView bottomNav;

    ActivityMenuSwitcher(Activity currentActivity) {
        super();
        activity = currentActivity;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem targetItem) {

        // GET TARGET CLASS

        // item lets us know which activity will be our target activity
        // get the id of the item
        int targetItemId = targetItem.getItemId();
        // the target Activity is null at first
        Class<? extends Activity> target = null;

        //based on the id, attribute the right class to target
        if (targetItemId == R.id.itinerary) {
            target = MainActivity.class;
        } else if (targetItemId == R.id.maps) {
            target = MapActivity.class;
        } else if (targetItemId == R.id.profile) {
            target = ProfileActivity.class;
        }

        //TODO Previous version, should be deleted after checking everything works out
        /*
        //if the target has been attributed and isn't the current activity
        if (target != null && target != activity.getClass()) {
            // Replace current activity
            Intent intent = new Intent(activity, target);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);

            // starts the new activity and finishes the current one
            activity.startActivity(intent);
            activity.finish();
        }
        switch (item.getItemId()){
            case R.id.itinerary:
                return true;
            case R.id.maps:
                return true;
            case R.id.profile:
                return true;
        }
         */

        //TODO this does the transition from the current activity to the next with the right transitions
        //if the target has been attributed and isn't the current activity
        if (target != null && target != activity.getClass()) {

            //Create intent and start next activity
            Intent intent = new Intent(activity, target);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.putExtra("previousActivity", activity.getClass());
            activity.startActivity(intent);


            //For Left-To-Right transitions
            if(activity.getClass() == MainActivity.class && targetItemId == R.id.maps
                    || activity.getClass() == MainActivity.class && targetItemId == R.id.profile
                    || activity.getClass() == MapActivity.class && targetItemId == R.id.profile){

                //override the transition and finish the current activity
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                activity.finish();

                return true;
            }

            //For Right-To-Left transitions
            if(activity.getClass() == MapActivity.class && targetItemId == R.id.itinerary
                    || activity.getClass() == ProfileActivity.class && targetItemId == R.id.itinerary
                    || activity.getClass() == ProfileActivity.class && targetItemId == R.id.maps){

                //override the transition and finish the current activity
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                activity.finish();

                return true;
            }
        }

        return false;
    }

}

