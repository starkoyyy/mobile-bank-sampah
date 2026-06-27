package com.example.tugas_pab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.navigation_home
        NavigationUtils.setupBottomNavigation(this, bottomNav)
        
        findViewById<android.view.View>(R.id.iv_notification).setOnClickListener {
            startActivity(android.content.Intent(this, NotifikasiActivity::class.java))
        }
        
        findViewById<android.view.View>(R.id.cv_setor_sampah).setOnClickListener {
            startActivity(android.content.Intent(this, SetorSampahActivity::class.java))
        }
        
        findViewById<android.view.View>(R.id.cv_tarik_saldo).setOnClickListener {
            startActivity(android.content.Intent(this, TarikSaldoActivity::class.java))
        }
        
        findViewById<android.view.View>(R.id.tv_see_all).setOnClickListener {
            startActivity(android.content.Intent(this, RiwayatTransaksiActivity::class.java))
        }
    }
}
