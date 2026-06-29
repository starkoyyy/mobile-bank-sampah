package com.example.tugas_pab.ui.admin

import com.example.tugas_pab.R

import com.example.tugas_pab.ui.auth.LoginActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tugas_pab.databinding.ActivityMenuAdminBinding

class MenuAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupProfile()
        setupMenus()
        setupBottomNavigation()
    }

    private fun setupProfile() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("user_data", "")
        if (userDataString?.isNotEmpty() == true) {
            try {
                val jsonObject = org.json.JSONObject(userDataString)
                val nama = jsonObject.optString("nama_lengkap", "Administrator")
                // Here we can set name instead of "Administrator" if desired
                // binding.tvRole.text = nama
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupMenus() {
        binding.menuKategori.setOnClickListener {
            startActivity(Intent(this, KelolaKategoriActivity::class.java))
        }

        binding.menuNasabah.setOnClickListener {
            startActivity(Intent(this, KelolaNasabahActivity::class.java))
        }


        binding.menuKeluar.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
                .setPositiveButton("Iya") { _, _ ->
                    val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().remove("user_data").remove("USER_ID").apply()
                    
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationAdmin.selectedItemId = R.id.navigation_more
        binding.bottomNavigationAdmin.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_admin_home -> {
                    startActivity(Intent(this, DashboardAdminActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    true
                }
                R.id.navigation_report -> {
                    startActivity(Intent(this, CetakLaporanActivity::class.java))
                    true
                }
                R.id.navigation_submission -> {
                    startActivity(Intent(this, DaftarPengajuanActivity::class.java))
                    true
                }
                R.id.navigation_more -> true
                else -> false
            }
        }
    }
}
