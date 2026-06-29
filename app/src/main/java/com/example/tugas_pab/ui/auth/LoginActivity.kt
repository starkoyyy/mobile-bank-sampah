package com.example.tugas_pab.ui.auth

import com.example.tugas_pab.R

import com.example.tugas_pab.ui.nasabah.DashboardActivity
import com.example.tugas_pab.ui.admin.DashboardAdminActivity
import com.example.tugas_pab.network.RetrofitClient

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        findViewById<android.view.View>(R.id.btn_login).setOnClickListener {
            val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_username).text.toString().trim()
            val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_password).text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Username dan password tidak boleh kosong", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = mapOf("username" to username, "password" to password)
            RetrofitClient.instance.login(request).enqueue(object : retrofit2.Callback<Any> {
                override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                    if (response.isSuccessful) {
                        try {
                            val responseBody = response.body()
                            val responseBodyString = com.google.gson.Gson().toJson(responseBody)
                            
                            val sharedPreferences = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("user_data", responseBodyString)
                            
                            com.example.tugas_pab.utils.CustomToast.success(this@LoginActivity, "Login Berhasil!")
                            
                            try {
                                val jsonObject = org.json.JSONObject(responseBodyString)
                                val userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                                val userId = userObj.optString("id", "")
                                editor.putString("USER_ID", userId)
                                
                                val role = userObj.optString("role", "nasabah").trim()
                                
                                editor.apply()
                                
                                val intent = if (role.equals("admin", ignoreCase = true)) {
                                    android.content.Intent(this@LoginActivity, DashboardAdminActivity::class.java)
                                } else {
                                    android.content.Intent(this@LoginActivity, DashboardActivity::class.java)
                                }
                                startActivity(intent)
                                finish()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                android.widget.Toast.makeText(this@LoginActivity, "Error parsing data", android.widget.Toast.LENGTH_SHORT).show()
                                val intent = android.content.Intent(this@LoginActivity, DashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            android.widget.Toast.makeText(this@LoginActivity, "Error parsing data", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                        android.widget.Toast.makeText(this@LoginActivity, "Login gagal: ${response.code()} $errorMsg", android.widget.Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                    android.widget.Toast.makeText(this@LoginActivity, "Koneksi error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            })
        }

        findViewById<android.view.View>(R.id.ll_register).setOnClickListener {
            val intent = android.content.Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<android.view.View>(R.id.tv_forgot_password_bottom).setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Lupa Password")
                .setMessage("Silakan hubungi administrator bank sampah untuk mereset password Anda.")
                .setPositiveButton("Tutup", null)
                .show()
        }
    }
}
