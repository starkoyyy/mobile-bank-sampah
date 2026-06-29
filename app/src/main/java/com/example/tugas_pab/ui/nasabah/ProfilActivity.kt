package com.example.tugas_pab.ui.nasabah

import com.example.tugas_pab.R

import com.example.tugas_pab.ui.auth.LoginActivity
import com.example.tugas_pab.data.model.Nasabah
import com.example.tugas_pab.utils.NavigationUtils

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

class ProfilActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.navigation_profile
        NavigationUtils.setupBottomNavigation(this, bottomNav)

        findViewById<android.view.View>(R.id.iv_back).setOnClickListener {
            val intent = android.content.Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        
        findViewById<View>(R.id.btn_logout).setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
                .setPositiveButton("Iya") { _, _ ->
                    val intent = android.content.Intent(this, LoginActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    
                    val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().remove("user_data").remove("USER_ID").apply()
                    
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        // Load user data and display name
        loadUserData()
        
        // Setup Edit Button
        findViewById<View>(R.id.btn_edit_profile).setOnClickListener {
            showEditProfilDialog()
        }
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("user_data", null)
        
        val ivProfileAvatar = findViewById<ImageView>(R.id.iv_profile_avatar)
        
        if (userDataString != null) {
            try {
                val jsonObject = JSONObject(userDataString)
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
                    r.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
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
                    Calendar.getInstance().get(Calendar.YEAR).toString()
                }
                
                // Load dynamic avatar
                val avatarUrl = "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=1B5E20&color=fff&size=200"
                Glide.with(this).load(avatarUrl).circleCrop().into(ivProfileAvatar)
                
                findViewById<TextView>(R.id.tv_profile_name).text = name
                findViewById<TextView>(R.id.tv_profile_status).text = "Anggota aktif sejak $createdAt"
                findViewById<TextView>(R.id.tv_profile_badge).text = "Peran: $role"
                findViewById<TextView>(R.id.tv_val_email).text = email
                findViewById<TextView>(R.id.tv_val_phone).text = phone
                findViewById<TextView>(R.id.tv_val_address).text = address
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showEditProfilDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profil, null)
        val etNama = dialogView.findViewById<EditText>(R.id.et_nama_lengkap)
        val etEmail = dialogView.findViewById<EditText>(R.id.et_email)
        val etPhone = dialogView.findViewById<EditText>(R.id.et_no_hp)
        val etAlamat = dialogView.findViewById<EditText>(R.id.et_alamat)
        val btnSimpan = dialogView.findViewById<Button>(R.id.btn_simpan)
        val btnBatal = dialogView.findViewById<Button>(R.id.btn_batal)
        


        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("user_data", null)
        var jsonObject = JSONObject()
        var userObj = JSONObject()
        

        
        if (userDataString != null) {
            try {
                jsonObject = JSONObject(userDataString)
                userObj = if (jsonObject.has("user")) jsonObject.getJSONObject("user") else jsonObject
                
                etNama.setText(if (userObj.has("nama_lengkap") && !userObj.isNull("nama_lengkap")) userObj.getString("nama_lengkap") else "")
                etEmail.setText(if (userObj.has("email") && !userObj.isNull("email")) userObj.getString("email") else "")
                etPhone.setText(if (userObj.has("no_telepon") && !userObj.isNull("no_telepon")) userObj.getString("no_telepon") else "")
                etAlamat.setText(if (userObj.has("alamat_lengkap") && !userObj.isNull("alamat_lengkap")) userObj.getString("alamat_lengkap") else "")
                

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }



        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
            
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnBatal.setOnClickListener { dialog.dismiss() }
        
        btnSimpan.setOnClickListener {
            try {
                userObj.put("nama_lengkap", etNama.text.toString().trim())
                userObj.put("email", etEmail.text.toString().trim())
                userObj.put("no_telepon", etPhone.text.toString().trim())
                userObj.put("alamat_lengkap", etAlamat.text.toString().trim())
                

                
                if (jsonObject.has("user")) {
                    jsonObject.put("user", userObj)
                } else {
                    jsonObject = userObj
                }
                
                sharedPreferences.edit().putString("user_data", jsonObject.toString()).apply()
                
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                loadUserData()
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
