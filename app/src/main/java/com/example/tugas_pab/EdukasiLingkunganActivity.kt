package com.example.tugas_pab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class EdukasiLingkunganActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edukasi_lingkungan)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.navigation_education
        NavigationUtils.setupBottomNavigation(this, bottomNav)
        
        val clickListener = android.view.View.OnClickListener {
            startActivity(android.content.Intent(this, DetailArtikelActivity::class.java))
        }
        
        findViewById<android.view.View>(R.id.cv_article_1).setOnClickListener(clickListener)
        findViewById<android.view.View>(R.id.cv_article_2).setOnClickListener(clickListener)
        findViewById<android.view.View>(R.id.cv_article_3).setOnClickListener(clickListener)
        findViewById<android.view.View>(R.id.cv_article_4).setOnClickListener(clickListener)
        findViewById<android.view.View>(R.id.cv_article_5).setOnClickListener(clickListener)
    }
}
