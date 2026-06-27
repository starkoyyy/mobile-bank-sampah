package com.example.tugas_pab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_admin)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_admin)
        bottomNav.selectedItemId = R.id.navigation_home
        
        val clickListener = android.view.View.OnClickListener {
            startActivity(android.content.Intent(this, DetailPenarikanActivity::class.java))
        }
        
        findViewById<android.view.View>(R.id.btn_setujui_1).setOnClickListener(clickListener)
        findViewById<android.view.View>(R.id.btn_setujui_2).setOnClickListener(clickListener)
        findViewById<android.view.View>(R.id.btn_setujui_3).setOnClickListener(clickListener)
    }
}
