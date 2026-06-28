package com.example.tugas_pab

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

object NavigationUtils {
    fun setupBottomNavigation(activity: Activity, bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            var intent: Intent? = null
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (activity !is DashboardActivity) {
                        intent = Intent(activity, DashboardActivity::class.java)
                    }
                }
                R.id.navigation_history -> {
                    if (activity !is RiwayatTransaksiActivity) {
                        intent = Intent(activity, RiwayatTransaksiActivity::class.java)
                    }
                }
                R.id.navigation_education -> {
                    if (activity !is EdukasiLingkunganActivity) {
                        intent = Intent(activity, EdukasiLingkunganActivity::class.java)
                    }
                }
                R.id.navigation_profile -> {
                    if (activity !is ProfilActivity) {
                        intent = Intent(activity, ProfilActivity::class.java)
                    }
                }
            }

            if (intent != null) {
                activity.startActivity(intent)
                activity.overridePendingTransition(0, 0)
                activity.finish()
            }
            true
        }
    }
}
