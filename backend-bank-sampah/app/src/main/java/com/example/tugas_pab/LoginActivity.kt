package com.example.tugas_pab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<android.view.View>(R.id.btn_login).setOnClickListener {
            val intent = android.content.Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<android.view.View>(R.id.ll_register).setOnClickListener {
            val intent = android.content.Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
