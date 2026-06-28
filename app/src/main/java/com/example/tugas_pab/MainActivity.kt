package com.example.tugas_pab

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            
            // Load custom IP
            val savedIp = sharedPreferences.getString("server_ip", "192.168.1.13:3000")
            savedIp?.let { RetrofitClient.setServerIp(it) }
            
            // Auto Discover Server in background
            RetrofitClient.discoverServer { ip ->
                val newIp = "$ip:3000"
                sharedPreferences.edit().putString("server_ip", newIp).apply()
                RetrofitClient.setServerIp(newIp)
            }

            val userData = sharedPreferences.getString("user_data", null)

            val intent = if (userData != null) {
                try {
                    val jsonObject = org.json.JSONObject(userData)
                    val userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                    val role = if (userObj.has("role")) userObj.getString("role") else "nasabah"
                    
                    if (role.equals("admin", ignoreCase = true)) {
                        android.content.Intent(this, DashboardAdminActivity::class.java)
                    } else {
                        android.content.Intent(this, DashboardActivity::class.java)
                    }
                } catch (e: Exception) {
                    android.content.Intent(this, DashboardActivity::class.java)
                }
            } else {
                android.content.Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}