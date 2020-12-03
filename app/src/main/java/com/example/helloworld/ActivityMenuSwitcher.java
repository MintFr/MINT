package com.example.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Bottom menu listener that switches to the right activity given the current activity
 */
class ActivityMenuSwitcher implements BottomNavigationView.OnNavigationItemSelectedListener {
    private final Activity activity;

    ActivityMenuSwitcher(Activity currentActivity) {
        super();
        activity = currentActivity;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Class<? extends Activity> target = null;
        int itemId = item.getItemId();

        if (itemId == R.id.itineraire) {
            target = MainActivity.class;
        } else if (itemId == R.id.cartes) {
            target = MapActivity.class;
        } else if (itemId == R.id.profil) {
            target = ProfileActivity.class;
        }

        if (target != null && target != activity.getClass()) {
            // Replace current activity
            Intent intent = new Intent(activity, target);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            activity.startActivity(intent);
            activity.finish();
        }

        return false;
    }
}
