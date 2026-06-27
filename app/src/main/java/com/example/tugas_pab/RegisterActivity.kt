package com.example.tugas_pab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<android.view.View>(R.id.btn_register).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.ll_login).setOnClickListener {
            finish()
        }
    }
}
