package com.example.helloworld

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Bottom menu handler
 */
class ActivitySwitcher(private val activity: Activity) : BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem) : Boolean {
        val target = when (item.itemId) {
            R.id.itineraire -> MainActivity::class.java
            R.id.cartes -> MapActivity::class.java
            R.id.profil -> ProfileActivity::class.java
            else -> null
        }

        if (target != null && target != activity::class.java) {
            // Replace current activity
            val intent = Intent(activity, target)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            activity.startActivity(intent)
            activity.finish()
        }

        return false
    }
}
