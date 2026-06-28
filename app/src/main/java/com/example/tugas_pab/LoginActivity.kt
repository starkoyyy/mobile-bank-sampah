package com.example.tugas_pab

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val prefs = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val savedIp = prefs.getString("server_ip", "192.168.1.13:3000")
        savedIp?.let { RetrofitClient.setServerIp(it) }
        
        // Auto Discover Server
        RetrofitClient.discoverServer { ip ->
            val newIp = "$ip:3000"
            prefs.edit().putString("server_ip", newIp).apply()
            RetrofitClient.setServerIp(newIp)
            // Toast dihapus agar tidak mengganggu
        }

        findViewById<TextView>(R.id.tv_set_ip).setOnClickListener {
            val input = EditText(this)
            input.setText(savedIp)
            AlertDialog.Builder(this)
                .setTitle("Set Server IP")
                .setMessage("Masukkan IP dan Port (Contoh: 192.168.1.13:3000)")
                .setView(input)
                .setPositiveButton("Simpan") { _, _ ->
                    val newIp = input.text.toString().trim()
                    if (newIp.isNotEmpty()) {
                        prefs.edit().putString("server_ip", newIp).apply()
                        RetrofitClient.setServerIp(newIp)
                        android.widget.Toast.makeText(this, "IP tersimpan: $newIp", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

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
                            
                            try {
                                val jsonObject = org.json.JSONObject(responseBodyString)
                                val userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                                val userId = userObj.optString("id", "")
                                editor.putString("USER_ID", userId)
                                
                                val role = userObj.optString("role", "nasabah").trim()
                                
                                editor.apply()
                                
                                android.widget.Toast.makeText(this@LoginActivity, "Login sukses", android.widget.Toast.LENGTH_SHORT).show()
                                
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
    }
}
