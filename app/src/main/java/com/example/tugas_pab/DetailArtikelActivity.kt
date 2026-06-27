package com.example.tugas_pab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DetailArtikelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_artikel)
        
        findViewById<android.view.View>(R.id.iv_back).setOnClickListener {
            finish()
        }
    }
}
