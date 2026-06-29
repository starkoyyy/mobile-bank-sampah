package com.example.tugas_pab.ui.auth

import com.example.tugas_pab.R

import com.example.tugas_pab.network.RetrofitClient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<android.view.View>(R.id.btn_register).setOnClickListener {
            val nama = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_nama).text.toString().trim()
            val alamat = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_alamat).text.toString().trim()
            val email = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_email).text.toString().trim()
            val noTelepon = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_no_telepon).text.toString().trim()
            val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_username).text.toString().trim()
            val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_password).text.toString().trim()

            if (nama.isEmpty() || alamat.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || noTelepon.isEmpty()) {
                android.widget.Toast.makeText(this, "Semua field harus diisi", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = mapOf(
                "nama_lengkap" to nama,
                "alamat" to alamat,
                "email" to email,
                "no_hp" to noTelepon,
                "username" to username,
                "password" to password
            )

            RetrofitClient.instance.register(request).enqueue(object : retrofit2.Callback<Any> {
                override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                    if (response.isSuccessful) {
                        android.widget.Toast.makeText(this@RegisterActivity, "Registrasi berhasil, silakan login", android.widget.Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                        android.widget.Toast.makeText(this@RegisterActivity, "Registrasi gagal: ${response.code()} $errorMsg", android.widget.Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                    android.widget.Toast.makeText(this@RegisterActivity, "Koneksi error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            })
        }

        findViewById<android.view.View>(R.id.ll_login).setOnClickListener {
            finish()
        }
    }
}
