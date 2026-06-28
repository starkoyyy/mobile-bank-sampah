package com.example.tugas_pab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.navigation_profile
        NavigationUtils.setupBottomNavigation(this, bottomNav)
        
        findViewById<android.view.View>(R.id.btn_logout).setOnClickListener {
            val intent = android.content.Intent(this, LoginActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            
            // Clear shared preferences on logout
            val sharedPreferences = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            
            startActivity(intent)
            finish()
        }

        // Load user data and display name
        val sharedPreferences = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("user_data", null)
        if (userDataString != null) {
            try {
                val jsonObject = org.json.JSONObject(userDataString)
                val userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                
                val name = if (userObj.has("nama_lengkap") && !userObj.isNull("nama_lengkap") && userObj.getString("nama_lengkap").isNotBlank()) {
                    userObj.getString("nama_lengkap")
                } else if (userObj.has("username") && !userObj.isNull("username")) {
                    userObj.getString("username")
                } else {
                    "Pengguna"
                }
                
                val email = if (userObj.has("email") && !userObj.isNull("email") && userObj.getString("email").isNotBlank()) {
                    userObj.getString("email")
                } else {
                    "Belum diatur"
                }
                
                val phone = if (userObj.has("no_telepon") && !userObj.isNull("no_telepon") && userObj.getString("no_telepon").isNotBlank()) {
                    userObj.getString("no_telepon")
                } else {
                    "Belum diatur"
                }
                
                val address = if (userObj.has("alamat_lengkap") && !userObj.isNull("alamat_lengkap") && userObj.getString("alamat_lengkap").isNotBlank()) {
                    userObj.getString("alamat_lengkap")
                } else {
                    "Belum diatur"
                }
                
                val role = if (userObj.has("role") && !userObj.isNull("role")) {
                    val r = userObj.getString("role")
                    r.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
                } else {
                    "Nasabah"
                }
                
                val createdAt = if (userObj.has("created_at") && !userObj.isNull("created_at")) {
                    val dateStr = userObj.getString("created_at")
                    try {
                        dateStr.substring(0, 4) // extract year "YYYY"
                    } catch (e: Exception) {
                        "2023"
                    }
                } else {
                    java.util.Calendar.getInstance().get(java.util.Calendar.YEAR).toString()
                }
                
                findViewById<android.widget.TextView>(R.id.tv_profile_name).text = name
                findViewById<android.widget.TextView>(R.id.tv_profile_status).text = "Anggota aktif sejak $createdAt"
                findViewById<android.widget.TextView>(R.id.tv_profile_badge).text = "Peran: $role"
                findViewById<android.widget.TextView>(R.id.tv_val_email).text = email
                findViewById<android.widget.TextView>(R.id.tv_val_phone).text = phone
                findViewById<android.widget.TextView>(R.id.tv_val_address).text = address
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
