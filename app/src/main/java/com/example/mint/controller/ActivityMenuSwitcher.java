package com.example.mint.controller;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;


import com.example.mint.R;
import com.example.mint.controller.MainActivity;
import com.example.mint.controller.MapActivity;
import com.example.mint.controller.ProfileActivity;
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

    /**
     * Constructor for ActivityMenuSwitcher
     * @param currentActivity Activity : current activity for each activity of the app
     */
    ActivityMenuSwitcher(Activity currentActivity) {
        super(); // I believe it can be erased
        activity = currentActivity;
    }

    /**
     * Start a new activity depending on what is clicked on the Menu
     * Configuration for the transition
     * @param targetItem Menu Item
     * @return boolean
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem targetItem) {

        //------- GET TARGET ACTIVITY -------

        // targetItem lets us know which activity will be our target activity
        // Get the id of the targetItem
        int targetItemId = targetItem.getItemId();
        // The target Activity is null at first
        Class<? extends Activity> target = null;

        // Based on the id, attribute the right class to target
        if (targetItemId == R.id.itinerary) { // don't understand le R.id
            target = MainActivity.class;
        } else if (targetItemId == R.id.maps) {
            target = MapActivity.class;
        } else if (targetItemId == R.id.profile) {
            target = ProfileActivity.class;
        }

        //-------TRANSITION TO NEXT ACTIVITY-------

        // This does the transition from the current activity to the next with the right transitions
        if (target != null && target != activity.getClass()) {

            // Create intent and start next activity
            Intent intent = new Intent(activity, target);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.putExtra("previousActivity", activity.getClass().getName());
            activity.startActivity(intent);


            //---------TRANSITIONS-----------
            // For Left-To-Right transitions
            if(activity.getClass() == MainActivity.class && targetItemId == R.id.maps || activity.getClass() == MainActivity.class || activity.getClass() == MapActivity.class && targetItemId == R.id.profile){

                // Override the transition and finish the current activity
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                activity.finish();

                return true;
            }

            // For Right-To-Left transitions
            if(activity.getClass() == MapActivity.class || activity.getClass() == ProfileActivity.class && targetItemId == R.id.itinerary || activity.getClass() == ProfileActivity.class){

                // Override the transition and finish the current activity
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                activity.finish();

                return true;
            }
        }

        return false;
    }

}

